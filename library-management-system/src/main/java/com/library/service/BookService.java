package com.library.service;

import com.library.exception.*;
import com.library.model.Book;
import com.library.pattern.observer.*;
import com.library.pattern.strategy.SearchStrategy;
import com.library.repository.BookRepository;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service responsible for book management operations.
 * Single Responsibility: only manages book lifecycle (add/remove/update/search).
 */
public class BookService {

    private static final Logger logger = Logger.getLogger(BookService.class.getName());

    private final BookRepository bookRepository;
    private final LibraryEventPublisher eventPublisher;
    private SearchStrategy searchStrategy;

    public BookService(BookRepository bookRepository,
                       LibraryEventPublisher eventPublisher,
                       SearchStrategy defaultSearchStrategy) {
        this.bookRepository = bookRepository;
        this.eventPublisher = eventPublisher;
        this.searchStrategy = defaultSearchStrategy;
    }

    /**
     * Add a new book to the library.
     */
    public Book addBook(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new DuplicateBookException(book.getIsbn());
        }
        bookRepository.save(book);
        logger.info("Book added: " + book);
        eventPublisher.publish(new LibraryEvent(
                LibraryEvent.EventType.BOOK_ADDED,
                book.getIsbn(), null,
                "Book added: " + book.getTitle()));
        return book;
    }

    /**
     * Remove a book from the library by ISBN.
     */
    public void removeBook(String isbn) {
        getBookOrThrow(isbn);
        bookRepository.deleteByIsbn(isbn);
        logger.info("Book removed: " + isbn);
        eventPublisher.publish(new LibraryEvent(
                LibraryEvent.EventType.BOOK_REMOVED,
                isbn, null,
                "Book removed with ISBN: " + isbn));
    }

    /**
     * Update book details. ISBN cannot be changed.
     */
    public Book updateBook(String isbn, String title, String author, int year, String genre) {
        Book book = getBookOrThrow(isbn);
        if (title != null) book.setTitle(title);
        if (author != null) book.setAuthor(author);
        if (year > 0) book.setPublicationYear(year);
        if (genre != null) book.setGenre(genre);
        bookRepository.save(book);
        logger.info("Book updated: " + book);
        return book;
    }

    /**
     * Find a book by ISBN.
     */
    public Book findByIsbn(String isbn) {
        return getBookOrThrow(isbn);
    }

    /**
     * Search books using the currently configured strategy.
     */
    public List<Book> search(String query) {
        return searchStrategy.search(bookRepository.findAll(), query);
    }

    /**
     * Search books using a specific strategy (Strategy pattern - runtime switching).
     */
    public List<Book> searchWith(SearchStrategy strategy, String query) {
        return strategy.search(bookRepository.findAll(), query);
    }

    /**
     * Set the default search strategy.
     */
    public void setSearchStrategy(SearchStrategy strategy) {
        this.searchStrategy = strategy;
    }

    /**
     * List all books.
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Get all available books (not checked out).
     */
    public List<Book> getAvailableBooks() {
        return bookRepository.findAll().stream()
                .filter(Book::isAvailable)
                .collect(java.util.stream.Collectors.toList());
    }

    Book getBookOrThrow(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
    }
}
