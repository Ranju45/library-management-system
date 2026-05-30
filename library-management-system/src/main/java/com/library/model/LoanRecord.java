package com.library.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a single lending transaction — either active or historical.
 */
public class LoanRecord {

    private final String loanId;
    private final String isbn;
    private final String patronId;
    private final LocalDate checkoutDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public LoanRecord(String isbn, String patronId, LocalDate checkoutDate, LocalDate dueDate) {
        this.loanId = UUID.randomUUID().toString();
        this.isbn = Objects.requireNonNull(isbn, "ISBN cannot be null");
        this.patronId = Objects.requireNonNull(patronId, "Patron ID cannot be null");
        this.checkoutDate = Objects.requireNonNull(checkoutDate, "Checkout date cannot be null");
        this.dueDate = Objects.requireNonNull(dueDate, "Due date cannot be null");
    }

    public String getLoanId() { return loanId; }
    public String getIsbn() { return isbn; }
    public String getPatronId() { return patronId; }
    public LocalDate getCheckoutDate() { return checkoutDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isActive() {
        return returnDate == null;
    }

    public boolean isOverdue() {
        return isActive() && LocalDate.now().isAfter(dueDate);
    }

    @Override
    public String toString() {
        return String.format("LoanRecord{loanId='%s', isbn='%s', patronId='%s', checkout=%s, due=%s, returned=%s}",
                loanId, isbn, patronId, checkoutDate, dueDate, returnDate != null ? returnDate : "N/A");
    }
}
