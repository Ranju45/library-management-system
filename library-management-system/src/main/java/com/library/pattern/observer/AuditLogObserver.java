package com.library.pattern.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Concrete Observer that maintains a full audit log of all library events.
 */
public class AuditLogObserver implements LibraryObserver {

    private static final Logger logger = Logger.getLogger(AuditLogObserver.class.getName());
    private final List<LibraryEvent> auditLog = new ArrayList<>();

    @Override
    public void onEvent(LibraryEvent event) {
        auditLog.add(event);
        logger.info("AUDIT: " + event);
    }

    public List<LibraryEvent> getAuditLog() {
        return Collections.unmodifiableList(auditLog);
    }

    public void printAuditLog() {
        System.out.println("\n===== AUDIT LOG =====");
        auditLog.forEach(System.out::println);
        System.out.println("=====================\n");
    }
}
