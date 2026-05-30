package com.library.pattern.strategy;

import com.library.model.Book;
import java.util.List;

/**
 * Strategy interface for book search algorithms.
 * Enables different search strategies (by title, author, ISBN, genre).
 */
public interface SearchStrategy {
    /**
     * Search for books matching the given query.
     *
     * @param books the collection of books to search
     * @param query the search term
     * @return list of matching books
     */
    List<Book> search(List<Book> books, String query);
}
