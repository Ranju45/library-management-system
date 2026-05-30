package com.library.pattern.observer;

import java.util.logging.Logger;

/**
 * Concrete Observer that handles patron notification events.
 * In a real system, this would send emails, SMS, or push notifications.
 */
public class NotificationObserver implements LibraryObserver {

    private static final Logger logger = Logger.getLogger(NotificationObserver.class.getName());

    @Override
    public void onEvent(LibraryEvent event) {
        switch (event.getType()) {
            case RESERVATION_AVAILABLE:
                notifyPatron(event.getPatronId(),
                        "Good news! The book with ISBN " + event.getIsbn() +
                        " you reserved is now available for pickup.");
                break;
            case BOOK_CHECKED_OUT:
                logger.fine("Checkout notification for patron: " + event.getPatronId());
                break;
            case BOOK_RETURNED:
                logger.fine("Return notification for ISBN: " + event.getIsbn());
                break;
            default:
                break;
        }
    }

    private void notifyPatron(String patronId, String message) {
        // In production: send email/SMS/push. Here we log it.
        logger.info(String.format("📬 NOTIFICATION → Patron [%s]: %s", patronId, message));
    }
}
