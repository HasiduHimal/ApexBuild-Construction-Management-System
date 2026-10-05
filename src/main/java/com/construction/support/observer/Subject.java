package com.construction.support.observer;

/**
 * Subject interface for managing and notifying observers.
 * Follows the Observer Design Pattern (SE2030 Lecture Part I, Slide 34).
 */
public interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String message);
}
