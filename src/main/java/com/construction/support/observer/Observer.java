package com.construction.support.observer;

/**
 * Observer interface for notification subscribers.
 * Follows the Observer Design Pattern (SE2030 Lecture Part I, Slide 35).
 */
public interface Observer {
    void update(String message);
    String getObserverName();
}
