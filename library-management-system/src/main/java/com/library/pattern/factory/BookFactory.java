package com.library.pattern.factory;

import com.library.model.Book;

/**
 * Factory class for creating Book instances.
 * Implements the Factory design pattern.
 * Centralises object creation logic and can be extended with object pools or caching.
 */
public class BookFactory {

    private BookFactory() { /* utility class */ }

    /**
     * Creates a standard book with all required fields.
     */
    public static Book createBook(String isbn, String title, String author, int year, String genre) {
        return new Book.Builder(isbn)
                .title(title)
                .author(author)
                .publicationYear(year)
                .genre(genre)
                .build();
    }

    /**
     * Creates a book with minimal required fields.
     */
    public static Book createBook(String isbn, String title, String author, int year) {
        return new Book.Builder(isbn)
                .title(title)
                .author(author)
                .publicationYear(year)
                .build();
    }

    /**
     * Creates a fiction book.
     */
    public static Book createFictionBook(String isbn, String title, String author, int year) {
        return new Book.Builder(isbn)
                .title(title)
                .author(author)
                .publicationYear(year)
                .genre("Fiction")
                .build();
    }

    /**
     * Creates a non-fiction book.
     */
    public static Book createNonFictionBook(String isbn, String title, String author, int year) {
        return new Book.Builder(isbn)
                .title(title)
                .author(author)
                .publicationYear(year)
                .genre("Non-Fiction")
                .build();
    }
}
