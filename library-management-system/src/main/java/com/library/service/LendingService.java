package com.library.service;

import com.library.exception.*;
import com.library.model.*;
import com.library.pattern.observer.*;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service responsible for the lending process — checkout, return, and reservations.
 */
public class LendingService {

    private static final Logger logger = Logger.getLogger(LendingService.class.getName());
    private static final int DEFAULT_LOAN_DAYS = 14;
    private static final int MAX_LOANS_PER_PATRON = 5;

    private final BookService bookService;
    private final PatronService patronService;
    private final LibraryEventPublisher eventPublisher;

    // ISBN -> ordered list of reservations (queue)
    private final Map<String, LinkedList<Reservation>> reservationsByIsbn = new HashMap<>();

    public LendingService(BookService bookService,
                          PatronService patronService,
                          LibraryEventPublisher eventPublisher) {
        this.bookService = bookService;
        this.patronService = patronService;
        this.eventPublisher = eventPublisher;
    }

    // ── Checkout ──────────────────────────────────────────────────────────────

    /**
     * Check out a book to a patron for the default loan period.
     */
    public LoanRecord checkOut(String isbn, String patronId) {
        return checkOut(isbn, patronId, DEFAULT_LOAN_DAYS);
    }

    /**
     * Check out a book to a patron for a specified number of days.
     */
    public LoanRecord checkOut(String isbn, String patronId, int loanDays) {
        Book book = bookService.getBookOrThrow(isbn);
        Patron patron = patronService.getPatronOrThrow(patronId);

        if (!book.isAvailable()) {
            throw new BookNotAvailableException(isbn);
        }

        if (patron.getActiveLoanCount() >= MAX_LOANS_PER_PATRON) {
            throw new LibraryException("Patron has reached the maximum loan limit of " + MAX_LOANS_PER_PATRON);
        }

        LocalDate checkoutDate = LocalDate.now();
        LocalDate dueDate = checkoutDate.plusDays(loanDays);

        LoanRecord record = new LoanRecord(isbn, patronId, checkoutDate, dueDate);
        patron.addLoanRecord(record);
        book.setStatus(BookStatus.CHECKED_OUT);

        logger.info(String.format("Book [%s] checked out to patron [%s], due: %s", isbn, patronId, dueDate));
        eventPublisher.publish(new LibraryEvent(
                LibraryEvent.EventType.BOOK_CHECKED_OUT,
                isbn, patronId,
                "Checked out until " + dueDate));

        return record;
    }

    // ── Return ────────────────────────────────────────────────────────────────

    /**
     * Return a book that was previously checked out.
     */
    public LoanRecord returnBook(String isbn, String patronId) {
        Book book = bookService.getBookOrThrow(isbn);
        Patron patron = patronService.getPatronOrThrow(patronId);

        LoanRecord record = patron.getBorrowingHistory().stream()
                .filter(r -> r.getIsbn().equals(isbn) && r.isActive())
                .findFirst()
                .orElseThrow(() -> new LibraryException(
                        "No active loan found for ISBN " + isbn + " and patron " + patronId));

        record.setReturnDate(LocalDate.now());

        // Check for pending reservations
        LinkedList<Reservation> queue = reservationsByIsbn.get(isbn);
        if (queue != null && !queue.isEmpty()) {
            Reservation next = queue.peek();
            book.setStatus(BookStatus.RESERVED);
            next.setStatus(Reservation.ReservationStatus.NOTIFIED);

            // Notify the next patron in line
            eventPublisher.publish(new LibraryEvent(
                    LibraryEvent.EventType.RESERVATION_AVAILABLE,
                    isbn, next.getPatronId(),
                    "Your reserved book is now available for pickup."));
        } else {
            book.setStatus(BookStatus.AVAILABLE);
        }

        logger.info(String.format("Book [%s] returned by patron [%s]", isbn, patronId));
        eventPublisher.publish(new LibraryEvent(
                LibraryEvent.EventType.BOOK_RETURNED,
                isbn, patronId,
                "Book returned on " + record.getReturnDate()));

        return record;
    }

    // ── Reservations ─────────────────────────────────────────────────────────

    /**
     * Reserve a checked-out book. Adds the patron to the reservation queue.
     */
    public Reservation reserveBook(String isbn, String patronId) {
        Book book = bookService.getBookOrThrow(isbn);
        Patron patron = patronService.getPatronOrThrow(patronId);

        if (book.isAvailable()) {
            throw new LibraryException("Book ISBN " + isbn + " is available — no need to reserve, just check it out!");
        }

        // Ensure patron doesn't already have a reservation for this book
        boolean alreadyReserved = getReservationQueue(isbn).stream()
                .anyMatch(r -> r.getPatronId().equals(patronId) && r.isActive());
        if (alreadyReserved) {
            throw new LibraryException("Patron " + patronId + " already has a reservation for ISBN " + isbn);
        }

        Reservation reservation = new Reservation(isbn, patronId);
        getReservationQueue(isbn).add(reservation);
        patron.addReservation(isbn);

        logger.info(String.format("Reservation created: patron [%s] for ISBN [%s]", patronId, isbn));
        eventPublisher.publish(new LibraryEvent(
                LibraryEvent.EventType.BOOK_RESERVED,
                isbn, patronId,
                "Reservation placed. Queue position: " + getReservationQueue(isbn).size()));

        return reservation;
    }

    /**
     * Cancel a reservation.
     */
    public void cancelReservation(String isbn, String patronId) {
        Patron patron = patronService.getPatronOrThrow(patronId);
        LinkedList<Reservation> queue = getReservationQueue(isbn);

        Reservation toRemove = queue.stream()
                .filter(r -> r.getPatronId().equals(patronId) && r.isActive())
                .findFirst()
                .orElseThrow(() -> new LibraryException("No active reservation found for patron " + patronId + " and ISBN " + isbn));

        toRemove.setStatus(Reservation.ReservationStatus.CANCELLED);
        queue.remove(toRemove);
        patron.removeReservation(isbn);
        logger.info(String.format("Reservation cancelled: patron [%s] for ISBN [%s]", patronId, isbn));
    }

    /**
     * Get all active loans.
     */
    public List<LoanRecord> getAllActiveLoans() {
        return patronService.getAllPatrons().stream()
                .flatMap(p -> p.getBorrowingHistory().stream())
                .filter(LoanRecord::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Get all overdue loans.
     */
    public List<LoanRecord> getOverdueLoans() {
        return getAllActiveLoans().stream()
                .filter(LoanRecord::isOverdue)
                .collect(Collectors.toList());
    }

    /**
     * Get patron's borrowing history.
     */
    public List<LoanRecord> getBorrowingHistory(String patronId) {
        Patron patron = patronService.getPatronOrThrow(patronId);
        return new ArrayList<>(patron.getBorrowingHistory());
    }

    // ── Inventory ─────────────────────────────────────────────────────────────

    /**
     * Get available books count.
     */
    public long countAvailableBooks() {
        return bookService.getAllBooks().stream().filter(Book::isAvailable).count();
    }

    /**
     * Get checked-out books count.
     */
    public long countCheckedOutBooks() {
        return bookService.getAllBooks().stream()
                .filter(b -> b.getStatus() == BookStatus.CHECKED_OUT)
                .count();
    }

    private LinkedList<Reservation> getReservationQueue(String isbn) {
        return reservationsByIsbn.computeIfAbsent(isbn, k -> new LinkedList<>());
    }
}
