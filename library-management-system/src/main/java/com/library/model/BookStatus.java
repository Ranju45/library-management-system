package com.library.model;

/**
 * Represents the current status of a book in the library system.
 */
public enum BookStatus {
    AVAILABLE,
    CHECKED_OUT,
    RESERVED,
    TRANSFERRED,
    LOST
}
