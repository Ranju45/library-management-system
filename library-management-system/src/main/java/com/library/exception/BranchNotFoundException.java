package com.library.exception;

public class BranchNotFoundException extends LibraryException {
    public BranchNotFoundException(String branchId) {
        super("Branch not found with ID: " + branchId);
    }
}
