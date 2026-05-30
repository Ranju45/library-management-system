package com.library.exception;

/**
 * Thrown when a requested book is not found in the system.
 */
public class BookNotFoundException extends LibraryException {
    public BookNotFoundException(String isbn) {
        super("Book not found with ISBN: " + isbn);
    }
}
