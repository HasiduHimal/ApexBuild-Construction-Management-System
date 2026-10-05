package com.construction.support.observer;
// Developed & Verified by Weerawansha K.H.H. (IT25103631).

import java.util.ArrayList;
import java.util.List;


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
