package com.library.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a reservation placed by a patron for a checked-out book.
 */
public class Reservation {

    public enum ReservationStatus {
        PENDING, NOTIFIED, FULFILLED, CANCELLED
    }

    private final String reservationId;
    private final String isbn;
    private final String patronId;
    private final LocalDate reservationDate;
    private ReservationStatus status;

    public Reservation(String isbn, String patronId) {
        this.reservationId = UUID.randomUUID().toString();
        this.isbn = Objects.requireNonNull(isbn, "ISBN cannot be null");
        this.patronId = Objects.requireNonNull(patronId, "Patron ID cannot be null");
        this.reservationDate = LocalDate.now();
        this.status = ReservationStatus.PENDING;
    }

    public String getReservationId() { return reservationId; }
    public String getIsbn() { return isbn; }
    public String getPatronId() { return patronId; }
    public LocalDate getReservationDate() { return reservationDate; }
    public ReservationStatus getStatus() { return status; }

    public void setStatus(ReservationStatus status) { this.status = status; }

    public boolean isActive() {
        return status == ReservationStatus.PENDING || status == ReservationStatus.NOTIFIED;
    }

    @Override
    public String toString() {
        return String.format("Reservation{id='%s', isbn='%s', patronId='%s', status=%s}",
                reservationId, isbn, patronId, status);
    }
}
