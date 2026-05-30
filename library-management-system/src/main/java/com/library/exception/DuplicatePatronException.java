package com.library.exception;

public class DuplicatePatronException extends LibraryException {
    public DuplicatePatronException(String patronId) {
        super("Patron already exists with ID: " + patronId);
    }
}
