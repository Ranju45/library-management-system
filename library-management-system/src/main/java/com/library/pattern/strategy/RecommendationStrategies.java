package com.library.pattern.strategy;

import com.library.model.Book;
import com.library.model.LoanRecord;
import com.library.model.Patron;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Concrete implementations of recommendation strategies.
 */
public class RecommendationStrategies {

    /**
     * Recommends books based on genres the patron has previously borrowed.
     */
    public static class GenreBasedRecommendation implements RecommendationStrategy {
        @Override
        public List<Book> recommend(Patron patron, List<Book> allBooks, int maxResults) {
            // Gather ISBNs already borrowed
            Set<String> borrowedIsbns = patron.getBorrowingHistory().stream()
                    .map(LoanRecord::getIsbn)
                    .collect(Collectors.toSet());

            // Use preferred genres and also infer from borrowing history
            List<String> genres = new ArrayList<>(patron.getPreferredGenres());

            return allBooks.stream()
                    .filter(b -> !borrowedIsbns.contains(b.getIsbn()))
                    .filter(b -> b.getGenre() != null && genres.contains(b.getGenre()))
                    .filter(Book::isAvailable)
                    .limit(maxResults)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Recommends popular books (most borrowed) that the patron hasn't read.
     */
    public static class PopularityBasedRecommendation implements RecommendationStrategy {
        private final Map<String, Long> borrowCountByIsbn;

        public PopularityBasedRecommendation(Map<String, Long> borrowCountByIsbn) {
            this.borrowCountByIsbn = borrowCountByIsbn;
        }

        @Override
        public List<Book> recommend(Patron patron, List<Book> allBooks, int maxResults) {
            Set<String> borrowedIsbns = patron.getBorrowingHistory().stream()
                    .map(LoanRecord::getIsbn)
                    .collect(Collectors.toSet());

            return allBooks.stream()
                    .filter(b -> !borrowedIsbns.contains(b.getIsbn()))
                    .filter(Book::isAvailable)
                    .sorted((a, b) -> Long.compare(
                            borrowCountByIsbn.getOrDefault(b.getIsbn(), 0L),
                            borrowCountByIsbn.getOrDefault(a.getIsbn(), 0L)))
                    .limit(maxResults)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Recommends books by the same authors the patron has borrowed before.
     */
    public static class AuthorBasedRecommendation implements RecommendationStrategy {
        private final Map<String, Book> booksByIsbn;

        public AuthorBasedRecommendation(Map<String, Book> booksByIsbn) {
            this.booksByIsbn = booksByIsbn;
        }

        @Override
        public List<Book> recommend(Patron patron, List<Book> allBooks, int maxResults) {
            Set<String> borrowedIsbns = patron.getBorrowingHistory().stream()
                    .map(LoanRecord::getIsbn)
                    .collect(Collectors.toSet());

            Set<String> readAuthors = borrowedIsbns.stream()
                    .map(isbn -> booksByIsbn.get(isbn))
                    .filter(Objects::nonNull)
                    .map(Book::getAuthor)
                    .collect(Collectors.toSet());

            return allBooks.stream()
                    .filter(b -> !borrowedIsbns.contains(b.getIsbn()))
                    .filter(b -> readAuthors.contains(b.getAuthor()))
                    .filter(Book::isAvailable)
                    .limit(maxResults)
                    .collect(Collectors.toList());
        }
    }
}
