package com.library.pattern.strategy;

import com.library.model.Book;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy implementations.
 */
public class SearchStrategies {

    /** Searches books by title (case-insensitive, partial match). */
    public static class TitleSearchStrategy implements SearchStrategy {
        @Override
        public List<Book> search(List<Book> books, String query) {
            String lowerQuery = query.toLowerCase();
            return books.stream()
                    .filter(b -> b.getTitle().toLowerCase().contains(lowerQuery))
                    .collect(Collectors.toList());
        }
    }

    /** Searches books by author name (case-insensitive, partial match). */
    public static class AuthorSearchStrategy implements SearchStrategy {
        @Override
        public List<Book> search(List<Book> books, String query) {
            String lowerQuery = query.toLowerCase();
            return books.stream()
                    .filter(b -> b.getAuthor().toLowerCase().contains(lowerQuery))
                    .collect(Collectors.toList());
        }
    }

    /** Searches books by exact ISBN. */
    public static class IsbnSearchStrategy implements SearchStrategy {
        @Override
        public List<Book> search(List<Book> books, String query) {
            return books.stream()
                    .filter(b -> b.getIsbn().equalsIgnoreCase(query.trim()))
                    .collect(Collectors.toList());
        }
    }

    /** Searches books by genre (case-insensitive, partial match). */
    public static class GenreSearchStrategy implements SearchStrategy {
        @Override
        public List<Book> search(List<Book> books, String query) {
            String lowerQuery = query.toLowerCase();
            return books.stream()
                    .filter(b -> b.getGenre() != null && b.getGenre().toLowerCase().contains(lowerQuery))
                    .collect(Collectors.toList());
        }
    }
}
