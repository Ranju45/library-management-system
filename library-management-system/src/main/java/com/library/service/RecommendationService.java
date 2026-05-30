package com.library.service;

import com.library.model.*;
import com.library.pattern.strategy.RecommendationStrategy;
import com.library.pattern.strategy.RecommendationStrategies;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Service that generates personalised book recommendations for patrons.
 * Uses the Strategy pattern to swap recommendation algorithms at runtime.
 */
public class RecommendationService {

    private static final Logger logger = Logger.getLogger(RecommendationService.class.getName());
    private static final int DEFAULT_MAX = 5;

    private final BookService bookService;
    private final PatronService patronService;

    public RecommendationService(BookService bookService, PatronService patronService) {
        this.bookService = bookService;
        this.patronService = patronService;
    }

    /**
     * Get recommendations using a specific strategy.
     */
    public List<Book> getRecommendations(String patronId, RecommendationStrategy strategy, int maxResults) {
        Patron patron = patronService.getPatronOrThrow(patronId);
        List<Book> allBooks = bookService.getAllBooks();
        List<Book> recommendations = strategy.recommend(patron, allBooks, maxResults);
        logger.info(String.format("Generated %d recommendations for patron [%s] using %s",
                recommendations.size(), patronId, strategy.getClass().getSimpleName()));
        return recommendations;
    }

    /**
     * Get blended recommendations from all strategies (deduped).
     */
    public List<Book> getBlendedRecommendations(String patronId, int maxResults) {
        Patron patron = patronService.getPatronOrThrow(patronId);
        List<Book> allBooks = bookService.getAllBooks();

        // Build borrow count map for popularity strategy
        Map<String, Long> borrowCount = patronService.getAllPatrons().stream()
                .flatMap(p -> p.getBorrowingHistory().stream())
                .collect(Collectors.groupingBy(LoanRecord::getIsbn, Collectors.counting()));

        // Build isbn->book map for author strategy
        Map<String, Book> bookMap = allBooks.stream()
                .collect(Collectors.toMap(Book::getIsbn, b -> b));

        List<RecommendationStrategy> strategies = Arrays.asList(
                new RecommendationStrategies.GenreBasedRecommendation(),
                new RecommendationStrategies.AuthorBasedRecommendation(bookMap),
                new RecommendationStrategies.PopularityBasedRecommendation(borrowCount)
        );

        Set<String> seen = new LinkedHashSet<>();
        List<Book> results = new ArrayList<>();

        for (RecommendationStrategy strategy : strategies) {
            for (Book b : strategy.recommend(patron, allBooks, maxResults)) {
                if (seen.add(b.getIsbn())) {
                    results.add(b);
                    if (results.size() >= maxResults) return results;
                }
            }
        }
        return results;
    }

    public List<Book> getRecommendations(String patronId, RecommendationStrategy strategy) {
        return getRecommendations(patronId, strategy, DEFAULT_MAX);
    }
}
