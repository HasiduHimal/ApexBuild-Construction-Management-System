package com.construction.support.observer;

// Developed & Verified by Weerawansha K.H.H. (IT25103631).
public interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String message);
}
