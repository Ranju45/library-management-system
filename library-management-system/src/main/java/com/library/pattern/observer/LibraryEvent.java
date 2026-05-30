package com.library.pattern.observer;

import java.time.LocalDateTime;

/**
 * Represents an event that occurred in the library system.
 * Used by the Observer pattern to notify subscribers.
 */
public class LibraryEvent {

    public enum EventType {
        BOOK_CHECKED_OUT,
        BOOK_RETURNED,
        BOOK_RESERVED,
        RESERVATION_AVAILABLE,
        BOOK_TRANSFERRED,
        BOOK_ADDED,
        BOOK_REMOVED,
        PATRON_REGISTERED
    }

    private final EventType type;
    private final String isbn;
    private final String patronId;
    private final String message;
    private final LocalDateTime timestamp;

    public LibraryEvent(EventType type, String isbn, String patronId, String message) {
        this.type = type;
        this.isbn = isbn;
        this.patronId = patronId;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public EventType getType() { return type; }
    public String getIsbn() { return isbn; }
    public String getPatronId() { return patronId; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] %s | ISBN: %s | Patron: %s | %s",
                timestamp, type, isbn != null ? isbn : "-", patronId != null ? patronId : "-", message);
    }
}
