package com.library.model;

import java.util.*;

/**
 * Represents a library branch (multi-branch extension).
 * Maintains its own inventory of books.
 */
public class LibraryBranch {

    private final String branchId;
    private String branchName;
    private String address;
    private final Map<String, Book> inventory; // ISBN -> Book

    public LibraryBranch(String branchId, String branchName, String address) {
        this.branchId = Objects.requireNonNull(branchId, "Branch ID cannot be null");
        this.branchName = Objects.requireNonNull(branchName, "Branch name cannot be null");
        this.address = address;
        this.inventory = new HashMap<>();
    }

    public String getBranchId() { return branchId; }
    public String getBranchName() { return branchName; }
    public String getAddress() { return address; }

    public void setBranchName(String branchName) { this.branchName = branchName; }
    public void setAddress(String address) { this.address = address; }

    public Map<String, Book> getInventory() {
        return Collections.unmodifiableMap(inventory);
    }

    public void addBook(Book book) {
        inventory.put(book.getIsbn(), book);
        book.setCurrentBranchId(branchId);
    }

    public Optional<Book> removeBook(String isbn) {
        Book removed = inventory.remove(isbn);
        return Optional.ofNullable(removed);
    }

    public Optional<Book> findBook(String isbn) {
        return Optional.ofNullable(inventory.get(isbn));
    }

    public boolean hasBook(String isbn) {
        return inventory.containsKey(isbn);
    }

    public List<Book> getAvailableBooks() {
        List<Book> available = new ArrayList<>();
        for (Book b : inventory.values()) {
            if (b.isAvailable()) available.add(b);
        }
        return Collections.unmodifiableList(available);
    }

    public int getTotalBooks() {
        return inventory.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LibraryBranch)) return false;
        LibraryBranch that = (LibraryBranch) o;
        return Objects.equals(branchId, that.branchId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(branchId);
    }

    @Override
    public String toString() {
        return String.format("LibraryBranch{id='%s', name='%s', books=%d}", branchId, branchName, inventory.size());
    }
}
