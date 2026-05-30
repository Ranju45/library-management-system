package com.library.repository;

import com.library.model.Book;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of BookRepository.
 * Uses a HashMap for O(1) ISBN lookups.
 */
public class InMemoryBookRepository implements BookRepository {

    private final Map<String, Book> store = new HashMap<>();

    @Override
    public void save(Book book) {
        store.put(book.getIsbn(), book);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return Optional.ofNullable(store.get(isbn));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Book> findByTitle(String title) {
        String lower = title.toLowerCase();
        return store.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByAuthor(String author) {
        String lower = author.toLowerCase();
        return store.values().stream()
                .filter(b -> b.getAuthor().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByGenre(String genre) {
        String lower = genre.toLowerCase();
        return store.values().stream()
                .filter(b -> b.getGenre() != null && b.getGenre().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        return store.containsKey(isbn);
    }

    @Override
    public void deleteByIsbn(String isbn) {
        store.remove(isbn);
    }
}
