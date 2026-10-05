package com.construction.support.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Subject that notifies observers about progress milestones and inquiry updates.
 * (SE2030 Lecture Part I, Slide 38).
 */
public class QualityProgressSubject implements Subject {

    private final List<Observer> observers = new ArrayList<>();
    private final List<String> notificationHistory = new ArrayList<>();

    @Override
    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        notificationHistory.add(message);
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    public List<Observer> getObservers() {
        return new ArrayList<>(observers);
    }

    public List<String> getNotificationHistory() {
        return new ArrayList<>(notificationHistory);
    }
}
