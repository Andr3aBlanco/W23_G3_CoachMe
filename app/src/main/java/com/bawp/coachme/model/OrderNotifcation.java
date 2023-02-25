/**
 * Class: OrderNotification.java
 *
 * Class associated with the message notifications for the orders purchased
 *
 * Fields:
 * - title: title of the notification
 * - description: description of the notification
 * - deviceToken: device Id which we are going to send the notification
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */
package com.bawp.coachme.model;

public class OrderNotifcation {

    private String title;
    private String description;
    private String deviceToken;

    public OrderNotifcation(){

    }

    public OrderNotifcation(String title, String description, String deviceToken) {
        this.title = title;
        this.description = description;
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

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
