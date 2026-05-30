package com.library.exception;

public class BookNotAvailableException extends LibraryException {
    public BookNotAvailableException(String isbn) {
        super("Book is not available for checkout: " + isbn);
    }
}
