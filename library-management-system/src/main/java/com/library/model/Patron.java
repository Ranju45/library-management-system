package com.library.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a library patron (member).
 * Tracks borrowing history and current loans.
 */
public class Patron {

    private final String patronId;
    private String name;
    private String email;
    private String phone;
    private final List<LoanRecord> borrowingHistory;
    private final List<String> reservedIsbns;
    private final List<String> preferredGenres;

    public Patron(String patronId, String name, String email, String phone) {
        Objects.requireNonNull(patronId, "Patron ID cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        this.patronId = patronId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.borrowingHistory = new ArrayList<>();
        this.reservedIsbns = new ArrayList<>();
        this.preferredGenres = new ArrayList<>();
    }

    public String getPatronId() { return patronId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<LoanRecord> getBorrowingHistory() {
        return Collections.unmodifiableList(borrowingHistory);
    }

    public List<String> getReservedIsbns() {
        return Collections.unmodifiableList(reservedIsbns);
    }

    public List<String> getPreferredGenres() {
        return Collections.unmodifiableList(preferredGenres);
    }

    public void addLoanRecord(LoanRecord record) {
        borrowingHistory.add(record);
    }

    public void addReservation(String isbn) {
        if (!reservedIsbns.contains(isbn)) {
            reservedIsbns.add(isbn);
        }
    }

    public void removeReservation(String isbn) {
        reservedIsbns.remove(isbn);
    }

    public void addPreferredGenre(String genre) {
        if (!preferredGenres.contains(genre)) {
            preferredGenres.add(genre);
        }
    }

    public boolean hasActiveLoans() {
        return borrowingHistory.stream().anyMatch(LoanRecord::isActive);
    }

    public long getActiveLoanCount() {
        return borrowingHistory.stream().filter(LoanRecord::isActive).count();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patron)) return false;
        Patron patron = (Patron) o;
        return Objects.equals(patronId, patron.patronId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patronId);
    }

    @Override
    public String toString() {
        return String.format("Patron{id='%s', name='%s', email='%s'}", patronId, name, email);
    }
}
