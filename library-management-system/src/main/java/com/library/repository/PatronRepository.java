package com.library.repository;

import com.library.model.Patron;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository interface for Patron operations.
 */
public interface PatronRepository {
    void save(Patron patron);
    Optional<Patron> findById(String patronId);
    List<Patron> findAll();
    boolean existsById(String patronId);
    void deleteById(String patronId);
}
