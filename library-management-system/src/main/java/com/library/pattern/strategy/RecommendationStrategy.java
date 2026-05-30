package com.library.pattern.strategy;

import com.library.model.Book;
import com.library.model.LoanRecord;
import com.library.model.Patron;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Strategy interface for book recommendations.
 */
public interface RecommendationStrategy {
    List<Book> recommend(Patron patron, List<Book> allBooks, int maxResults);
}

