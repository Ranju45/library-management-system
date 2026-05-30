package com.library;

import com.library.pattern.observer.*;
import com.library.pattern.strategy.SearchStrategies;
import com.library.repository.*;
import com.library.service.*;

/**
 * LibrarySystem is the top-level Facade that wires all services, repositories,
 * and observers together. Clients interact with the system through this class.
 *
 * Design patterns used here:
 *  - Facade: simplifies access to the subsystem
 *  - Observer: event publishing / notification
 *  - Strategy: search and recommendation algorithms
 *  - Factory: BookFactory (in factory package)
 *  - Builder: Book.Builder
 *  - Repository: InMemoryBookRepository / InMemoryPatronRepository
 */
public class LibrarySystem {

    private final LibraryEventPublisher eventPublisher;
    private final AuditLogObserver auditLog;

    private final BookService bookService;
    private final PatronService patronService;
    private final LendingService lendingService;
    private final BranchService branchService;
    private final RecommendationService recommendationService;

    public LibrarySystem() {
        // Infrastructure
        eventPublisher = new LibraryEventPublisher();

        // Wire observers
        auditLog = new AuditLogObserver();
        eventPublisher.subscribe(auditLog);
        eventPublisher.subscribe(new NotificationObserver());

        // Repositories
        BookRepository bookRepo = new InMemoryBookRepository();
        PatronRepository patronRepo = new InMemoryPatronRepository();

        // Services
        bookService = new BookService(bookRepo, eventPublisher, new SearchStrategies.TitleSearchStrategy());
        patronService = new PatronService(patronRepo, eventPublisher);
        lendingService = new LendingService(bookService, patronService, eventPublisher);
        branchService = new BranchService(eventPublisher);
        recommendationService = new RecommendationService(bookService, patronService);
    }

    public BookService books() { return bookService; }
    public PatronService patrons() { return patronService; }
    public LendingService lending() { return lendingService; }
    public BranchService branches() { return branchService; }
    public RecommendationService recommendations() { return recommendationService; }
    public AuditLogObserver auditLog() { return auditLog; }
    public LibraryEventPublisher eventPublisher() { return eventPublisher; }
}
