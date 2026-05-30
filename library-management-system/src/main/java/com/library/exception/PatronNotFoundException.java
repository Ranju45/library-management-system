package com.library.exception;

public class PatronNotFoundException extends LibraryException {
    public PatronNotFoundException(String patronId) {
        super("Patron not found with ID: " + patronId);
    }
}
