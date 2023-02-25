package com.bawp.coachme.model;

public class AppointmentReminder {

    private String title;
    private String description;
    private long bookedDate;
    private String deviceToken;

    public AppointmentReminder(){

    }

    public AppointmentReminder(String title, String description, long bookedDate, String deviceToken) {
        this.title = title;
        this.description = description;
        this.bookedDate = bookedDate;
        this.deviceToken = deviceToken;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDate() {
        return bookedDate;
    }

    public void setDate(long bookedDate) {
        this.bookedDate = bookedDate;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
