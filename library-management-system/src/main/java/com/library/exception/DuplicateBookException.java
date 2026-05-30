package com.library.exception;

public class DuplicateBookException extends LibraryException {
    public DuplicateBookException(String isbn) {
        super("Book already exists with ISBN: " + isbn);
    }
}
