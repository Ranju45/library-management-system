package com.library.service;

import com.library.exception.*;
import com.library.model.*;
import com.library.pattern.observer.*;

import java.util.*;
import java.util.logging.Logger;

/**
 * Service for managing multiple library branches and book transfers between them.
 */
public class BranchService {

    private static final Logger logger = Logger.getLogger(BranchService.class.getName());

    private final Map<String, LibraryBranch> branches = new HashMap<>();
    private final LibraryEventPublisher eventPublisher;

    public BranchService(LibraryEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Register a new library branch.
     */
    public LibraryBranch addBranch(String branchId, String branchName, String address) {
        if (branches.containsKey(branchId)) {
            throw new LibraryException("Branch already exists with ID: " + branchId);
        }
        LibraryBranch branch = new LibraryBranch(branchId, branchName, address);
        branches.put(branchId, branch);
        logger.info("Branch added: " + branch);
        return branch;
    }

    /**
     * Get a branch by ID.
     */
    public LibraryBranch getBranch(String branchId) {
        return Optional.ofNullable(branches.get(branchId))
                .orElseThrow(() -> new BranchNotFoundException(branchId));
    }

    /**
     * List all branches.
     */
    public List<LibraryBranch> getAllBranches() {
        return new ArrayList<>(branches.values());
    }

    /**
     * Add a book to a specific branch inventory.
     */
    public void addBookToBranch(String branchId, Book book) {
        LibraryBranch branch = getBranch(branchId);
        branch.addBook(book);
        logger.info(String.format("Book [%s] added to branch [%s]", book.getIsbn(), branchId));
    }

    /**
     * Transfer a book from one branch to another.
     */
    public void transferBook(String fromBranchId, String toBranchId, String isbn) {
        LibraryBranch fromBranch = getBranch(fromBranchId);
        LibraryBranch toBranch = getBranch(toBranchId);

        Book book = fromBranch.findBook(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        if (!book.isAvailable()) {
            throw new BookNotAvailableException(isbn);
        }

        fromBranch.removeBook(isbn);
        book.setStatus(BookStatus.AVAILABLE);
        toBranch.addBook(book);

        logger.info(String.format("Book [%s] transferred from [%s] to [%s]", isbn, fromBranchId, toBranchId));
        eventPublisher.publish(new LibraryEvent(
                LibraryEvent.EventType.BOOK_TRANSFERRED,
                isbn, null,
                String.format("Transferred from %s to %s", fromBranchId, toBranchId)));
    }

    /**
     * Search for a book across all branches.
     */
    public Map<String, Book> findBookAcrossBranches(String isbn) {
        Map<String, Book> results = new LinkedHashMap<>();
        for (LibraryBranch branch : branches.values()) {
            branch.findBook(isbn).ifPresent(b -> results.put(branch.getBranchId(), b));
        }
        return results;
    }
}
