package com.construction.support.observer;

import java.util.ArrayList;
import java.util.List;

// Developed & Verified by Weerawansha K.H.H. (IT25103631).
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
