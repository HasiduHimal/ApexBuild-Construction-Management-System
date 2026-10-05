package com.construction.support.observer;

import java.util.ArrayList;
import java.util.List;

// Developed & Verified by Weerawansha K.H.H. (IT25103631).
public class SiteEngineerObserver implements Observer {

    private final String engineerName;
    private final List<String> siteMilestoneAlerts = new ArrayList<>();

    public SiteEngineerObserver(String engineerName) {
        this.engineerName = engineerName;
    }

    @Override
    public void update(String message) {
        String log = "[Site Engineering Noticeboard - " + engineerName + "] " + message;
        siteMilestoneAlerts.add(log);
        System.out.println(log);
    }

    @Override
    public String getObserverName() {
        return engineerName;
    }

    public List<String> getSiteMilestoneAlerts() {
        return new ArrayList<>(siteMilestoneAlerts);
    }
}
