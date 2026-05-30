package com.library.repository;

import com.library.model.Book;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Book persistence operations.
 * Follows the Repository pattern to decouple data access from business logic.
 */
public interface BookRepository {
    void save(Book book);
    Optional<Book> findByIsbn(String isbn);
    List<Book> findAll();
    List<Book> findByTitle(String title);
    List<Book> findByAuthor(String author);
    List<Book> findByGenre(String genre);
    boolean existsByIsbn(String isbn);
    void deleteByIsbn(String isbn);
}
