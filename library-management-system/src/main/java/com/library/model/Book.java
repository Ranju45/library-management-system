package com.library.model;

import java.util.Objects;

/**
 * Represents a book in the library system.
 * Encapsulates all book-related attributes and provides a fluent builder pattern.
 */
public class Book {

    private final String isbn;
    private String title;
    private String author;
    private int publicationYear;
    private String genre;
    private BookStatus status;
    private String currentBranchId;

    private Book(Builder builder) {
        this.isbn = builder.isbn;
        this.title = builder.title;
        this.author = builder.author;
        this.publicationYear = builder.publicationYear;
        this.genre = builder.genre;
        this.status = BookStatus.AVAILABLE;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getPublicationYear() { return publicationYear; }
    public String getGenre() { return genre; }
    public BookStatus getStatus() { return status; }
    public String getCurrentBranchId() { return currentBranchId; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setStatus(BookStatus status) { this.status = status; }
    public void setCurrentBranchId(String branchId) { this.currentBranchId = branchId; }

    public boolean isAvailable() {
        return this.status == BookStatus.AVAILABLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return String.format("Book{isbn='%s', title='%s', author='%s', year=%d, status=%s}",
                isbn, title, author, publicationYear, status);
    }

    public static class Builder {
        private final String isbn;
        private String title;
        private String author;
        private int publicationYear;
        private String genre;

        public Builder(String isbn) {
            this.isbn = isbn;
        }

        public Builder title(String title) { this.title = title; return this; }
        public Builder author(String author) { this.author = author; return this; }
        public Builder publicationYear(int year) { this.publicationYear = year; return this; }
        public Builder genre(String genre) { this.genre = genre; return this; }

        public Book build() {
            Objects.requireNonNull(isbn, "ISBN cannot be null");
            Objects.requireNonNull(title, "Title cannot be null");
            Objects.requireNonNull(author, "Author cannot be null");
            return new Book(this);
        }
    }
}
