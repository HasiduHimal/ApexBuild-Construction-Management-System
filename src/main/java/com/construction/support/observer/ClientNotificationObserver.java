package com.construction.support.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete Observer that receives client updates and inquiry resolutions.
 * (SE2030 Lecture Part I, Slide 40).
 */
public class ClientNotificationObserver implements Observer {

    private final String observerName;
    private final List<String> receivedAlerts = new ArrayList<>();

    public ClientNotificationObserver(String observerName) {
        this.observerName = observerName;
    }

    @Override
    public void update(String message) {
        String log = "[Client Notification Center - " + observerName + "] " + message;
        receivedAlerts.add(log);
        System.out.println(log);
    }

    @Override
    public String getObserverName() {
        return observerName;
    }

    public List<String> getReceivedAlerts() {
        return new ArrayList<>(receivedAlerts);
    }
}
