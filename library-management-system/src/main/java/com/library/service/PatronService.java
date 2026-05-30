package com.library.service;

import com.library.exception.*;
import com.library.model.Patron;
import com.library.pattern.observer.*;
import com.library.repository.PatronRepository;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service responsible for patron management operations.
 */
public class PatronService {

    private static final Logger logger = Logger.getLogger(PatronService.class.getName());

    private final PatronRepository patronRepository;
    private final LibraryEventPublisher eventPublisher;

    public PatronService(PatronRepository patronRepository, LibraryEventPublisher eventPublisher) {
        this.patronRepository = patronRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Register a new patron.
     */
    public Patron registerPatron(String patronId, String name, String email, String phone) {
        if (patronRepository.existsById(patronId)) {
            throw new DuplicatePatronException(patronId);
        }
        Patron patron = new Patron(patronId, name, email, phone);
        patronRepository.save(patron);
        logger.info("Patron registered: " + patron);
        eventPublisher.publish(new LibraryEvent(
                LibraryEvent.EventType.PATRON_REGISTERED,
                null, patronId,
                "Patron registered: " + name));
        return patron;
    }

    /**
     * Update patron details.
     */
    public Patron updatePatron(String patronId, String name, String email, String phone) {
        Patron patron = getPatronOrThrow(patronId);
        if (name != null) patron.setName(name);
        if (email != null) patron.setEmail(email);
        if (phone != null) patron.setPhone(phone);
        patronRepository.save(patron);
        logger.info("Patron updated: " + patron);
        return patron;
    }

    /**
     * Get patron by ID.
     */
    public Patron findById(String patronId) {
        return getPatronOrThrow(patronId);
    }

    /**
     * Get all patrons.
     */
    public List<Patron> getAllPatrons() {
        return patronRepository.findAll();
    }

    /**
     * Add preferred genre for recommendations.
     */
    public void addPreferredGenre(String patronId, String genre) {
        Patron patron = getPatronOrThrow(patronId);
        patron.addPreferredGenre(genre);
        patronRepository.save(patron);
    }

    Patron getPatronOrThrow(String patronId) {
        return patronRepository.findById(patronId)
                .orElseThrow(() -> new PatronNotFoundException(patronId));
    }
}
