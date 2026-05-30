package com.library.pattern.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Subject in the Observer pattern.
 * Manages observers and dispatches library events.
 */
public class LibraryEventPublisher {

    private static final Logger logger = Logger.getLogger(LibraryEventPublisher.class.getName());
    private final List<LibraryObserver> observers = new ArrayList<>();

    public void subscribe(LibraryObserver observer) {
        observers.add(observer);
        logger.fine("Observer subscribed: " + observer.getClass().getSimpleName());
    }

    public void unsubscribe(LibraryObserver observer) {
        observers.remove(observer);
    }

    public void publish(LibraryEvent event) {
        logger.info("Publishing event: " + event);
        for (LibraryObserver observer : observers) {
            try {
                observer.onEvent(event);
            } catch (Exception e) {
                logger.warning("Observer failed to handle event: " + e.getMessage());
            }
        }
    }
}
