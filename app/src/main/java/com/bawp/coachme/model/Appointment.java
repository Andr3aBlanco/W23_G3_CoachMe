/**
 * Class: Appointment.java
 *
 * Class associated with the appointments made by the users with trainers
 *
 * Fields:
 * - bookedDate: date when the training session will occur.
 * - serviceType: type of training session (Crossfit, Cycling, etc)
 * - status: current status of the appointment
 *      -> 1: pending (waiting for payment)
 *      -> 2: cancelled
 *      -> 3: active
 *      -> 4: in progress
 *      -> 5: finished
 *      -> 6: no show
 * - totalPrice: price of the training session
 * - location: place where the training session will occur (address)
 * - trainerId: id of the trainer (comes from the Users table)
 * - customerId: id of the customer (comes from the Users table)
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */
package com.bawp.coachme.model;

import java.util.Date;

public class Appointment {

    private Date bookedDate;
    private String serviceType;
    private int status;
    private double totalPrice;
    private String location;
    private String trainerId;
    private String customerId;

    public Appointment(){

    }

    public Appointment(Date bookedDate, String serviceType, int status,
                       double totalPrice, String location,
                       String trainerId, String customerId) {
        this.bookedDate = bookedDate;
        this.serviceType = serviceType;
        this.status = status;
        this.totalPrice = totalPrice;
        this.location = location;
        this.trainerId = trainerId;
        this.customerId = customerId;
    }

    public Date getBookedDate() {
        return bookedDate;
    }

    public void setBookedDate(Date bookedDate) {
        this.bookedDate = bookedDate;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString(){
        return this.customerId+" - "+this.trainerId+" - "+this.bookedDate;
    }
}
