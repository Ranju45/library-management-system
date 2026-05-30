package com.library.pattern.observer;

/**
 * Observer interface for the Observer design pattern.
 * Implementors receive notifications about library events.
 */
public interface LibraryObserver {
    /**
     * Called when a library event occurs.
     *
     * @param event the event that occurred
     */
    void onEvent(LibraryEvent event);
}
