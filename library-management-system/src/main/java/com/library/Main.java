package com.library;

import com.library.model.*;
import com.library.pattern.factory.BookFactory;
import com.library.pattern.observer.LibraryEvent;
import com.library.pattern.strategy.*;

import java.util.List;

/**
 * Entry point demonstrating all features of the Library Management System.
 */
public class Main {

    public static void main(String[] args) {
        LibrarySystem library = new LibrarySystem();

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   Library Management System — Demo       ║");
        System.out.println("╚══════════════════════════════════════════╝\n");

        // ── 1. Setup: branches ────────────────────────────────────────────────
        section("1. Setting up branches");
        library.branches().addBranch("MAIN", "Main Branch", "123 Library Ave");
        library.branches().addBranch("EAST", "East Branch", "456 East St");
        System.out.println("Created branches: MAIN, EAST");

        // ── 2. Add books ──────────────────────────────────────────────────────
        section("2. Adding books");
        Book b1 = library.books().addBook(BookFactory.createBook("978-0-06-112008-4",
                "To Kill a Mockingbird", "Harper Lee", 1960, "Fiction"));
        Book b2 = library.books().addBook(BookFactory.createBook("978-0-7432-7356-5",
                "The Great Gatsby", "F. Scott Fitzgerald", 1925, "Fiction"));
        Book b3 = library.books().addBook(BookFactory.createBook("978-0-452-28423-4",
                "1984", "George Orwell", 1949, "Dystopian Fiction"));
        Book b4 = library.books().addBook(BookFactory.createBook("978-0-316-76948-0",
                "The Catcher in the Rye", "J.D. Salinger", 1951, "Fiction"));
        Book b5 = library.books().addBook(BookFactory.createFictionBook("978-0-14-028329-7",
                "Of Mice and Men", "John Steinbeck", 1937));
        Book b6 = library.books().addBook(BookFactory.createNonFictionBook("978-0-671-46499-1",
                "How to Win Friends", "Dale Carnegie", 1936));

        System.out.println("Added " + library.books().getAllBooks().size() + " books.");

        // Add books to branches
        library.branches().addBookToBranch("MAIN", b1);
        library.branches().addBookToBranch("MAIN", b2);
        library.branches().addBookToBranch("MAIN", b3);
        library.branches().addBookToBranch("EAST", b4);
        library.branches().addBookToBranch("EAST", b5);

        // ── 3. Register patrons ───────────────────────────────────────────────
        section("3. Registering patrons");
        Patron alice = library.patrons().registerPatron("P001", "Alice Johnson", "alice@email.com", "555-0101");
        Patron bob   = library.patrons().registerPatron("P002", "Bob Smith",     "bob@email.com",   "555-0102");
        Patron carol = library.patrons().registerPatron("P003", "Carol White",   "carol@email.com", "555-0103");
        library.patrons().addPreferredGenre("P001", "Fiction");
        library.patrons().addPreferredGenre("P001", "Dystopian Fiction");
        System.out.println("Registered: " + alice.getName() + ", " + bob.getName() + ", " + carol.getName());

        // ── 4. Search books ───────────────────────────────────────────────────
        section("4. Searching books");

        // Default strategy: title search
        List<Book> titleResults = library.books().search("Great");
        System.out.println("Title search 'Great': " + titleResults);

        // Strategy pattern: switch to author search
        library.books().setSearchStrategy(new SearchStrategies.AuthorSearchStrategy());
        List<Book> authorResults = library.books().search("Orwell");
        System.out.println("Author search 'Orwell': " + authorResults);

        // One-shot search with explicit strategy
        List<Book> isbnResults = library.books().searchWith(
                new SearchStrategies.IsbnSearchStrategy(), "978-0-06-112008-4");
        System.out.println("ISBN search: " + isbnResults);

        // ── 5. Checkout ───────────────────────────────────────────────────────
        section("5. Checking out books");
        LoanRecord loan1 = library.lending().checkOut("978-0-06-112008-4", "P001");
        LoanRecord loan2 = library.lending().checkOut("978-0-7432-7356-5", "P002");
        LoanRecord loan3 = library.lending().checkOut("978-0-452-28423-4", "P001");
        System.out.println("Alice checked out: Mockingbird and 1984");
        System.out.println("Bob checked out: Great Gatsby");
        System.out.println("Available books: " + library.lending().countAvailableBooks());
        System.out.println("Checked-out books: " + library.lending().countCheckedOutBooks());

        // ── 6. Reservation system ─────────────────────────────────────────────
        section("6. Reservation system");
        Reservation res = library.lending().reserveBook("978-0-06-112008-4", "P003");
        System.out.println("Carol reserved 'Mockingbird': " + res);

        // ── 7. Return + auto-notification ────────────────────────────────────
        section("7. Returning a book (triggers reservation notification)");
        library.lending().returnBook("978-0-06-112008-4", "P001");
        System.out.println("Alice returned Mockingbird — Carol should be notified.");

        // ── 8. Borrowing history ─────────────────────────────────────────────
        section("8. Borrowing history for Alice");
        library.lending().getBorrowingHistory("P001").forEach(System.out::println);

        // ── 9. Book transfer between branches ────────────────────────────────
        section("9. Transferring a book between branches");
        library.lending().returnBook("978-0-7432-7356-5", "P002");
        library.branches().transferBook("MAIN", "EAST", "978-0-7432-7356-5");
        System.out.println("Transferred Great Gatsby from MAIN → EAST");
        System.out.println("EAST branch books: " + library.branches().getBranch("EAST").getTotalBooks());

        // ── 10. Recommendations ───────────────────────────────────────────────
        section("10. Book recommendations for Alice");
        List<Book> recs = library.recommendations().getBlendedRecommendations("P001", 3);
        System.out.println("Blended recommendations:");
        recs.forEach(r -> System.out.println("  → " + r.getTitle() + " by " + r.getAuthor()));

        // ── 11. Update / remove ───────────────────────────────────────────────
        section("11. Updating and removing a book");
        library.books().updateBook("978-0-316-76948-0", "The Catcher in the Rye (Updated)", null, 0, null);
        System.out.println("Updated: " + library.books().findByIsbn("978-0-316-76948-0"));
        library.books().removeBook("978-0-671-46499-1");
        System.out.println("Removed 'How to Win Friends'. Total books now: " + library.books().getAllBooks().size());

        // ── 12. Audit log ─────────────────────────────────────────────────────
        section("12. Audit log (last 5 events)");
        List<LibraryEvent> log = library.auditLog().getAuditLog();
        log.stream().skip(Math.max(0, log.size() - 5)).forEach(e -> System.out.println(e));

        System.out.println("\n✅ Demo complete.");
    }

    private static void section(String title) {
        System.out.println("\n── " + title + " ──────────────────────────────");
    }
}
