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
 * - paymentDate: date when the user paid for the self-workout plan
 * - paymentId: payment id (payment intent id from Stripe)
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */
package com.bawp.coachme.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Appointment implements Parcelable {

    private String id;
    private long bookedDate;
    private long registeredDate;
    private String serviceType;
    private int status;
    private double totalPrice;
    private String location;
    private String trainerId;
    private String customerId;
    private String paymentId;
    private long paymentDate;
    private String deviceToken;

    // modification to simplify the model
    private int rating;  // one per appointment
    private String comment;

    public Appointment(){

    }

    public Appointment(String id, long bookedDate, long registeredDate, String serviceType, int status, double totalPrice, String location, String trainerId, String customerId, String paymentId, long paymentDate, String deviceToken) {
        this.id = id;
        this.bookedDate = bookedDate;
        this.registeredDate = registeredDate;
        this.serviceType = serviceType;
        this.status = status;
        this.totalPrice = totalPrice;
        this.location = location;
        this.trainerId = trainerId;
        this.customerId = customerId;
        this.paymentId = paymentId;
        this.paymentDate = paymentDate;
        this.deviceToken = deviceToken;
        // new attribute
        this.rating = 0;
        this.comment = "";
    }

    // another constructor needed for the DBHelper when getting the appointments from DB


    public Appointment(String id, long bookedDate, long registeredDate, String serviceType, int status, double totalPrice, String location, String trainerId, String customerId, String paymentId, long paymentDate, String deviceToken, int rating, String comment) {
        this.id = id;
        this.bookedDate = bookedDate;
        this.registeredDate = registeredDate;
        this.serviceType = serviceType;
        this.status = status;
        this.totalPrice = totalPrice;
        this.location = location;
        this.trainerId = trainerId;
        this.customerId = customerId;
        this.paymentId = paymentId;
        this.paymentDate = paymentDate;
        this.deviceToken = deviceToken;
        this.rating = rating;
        this.comment = comment;
    }

    protected Appointment(Parcel in) {
        id = in.readString();
        bookedDate = in.readLong();
        registeredDate = in.readLong();
        serviceType = in.readString();
        status = in.readInt();
        totalPrice = in.readDouble();
        location = in.readString();
        trainerId = in.readString();
        customerId = in.readString();
        paymentId = in.readString();
        paymentDate = in.readLong();
        deviceToken = in.readString();
        rating = in.readInt();
        comment = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(bookedDate);
        dest.writeLong(registeredDate);
        dest.writeString(serviceType);
        dest.writeInt(status);
        dest.writeDouble(totalPrice);
        dest.writeString(location);
        dest.writeString(trainerId);
        dest.writeString(customerId);
        dest.writeString(paymentId);
        dest.writeLong(paymentDate);
        dest.writeString(deviceToken);
        dest.writeInt(rating);
        dest.writeString(comment);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getBookedDate() {
        return bookedDate;
    }

    public void setBookedDate(long bookedDate) {
        this.bookedDate = bookedDate;
    }

    public long getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(long registeredDate) {
        this.registeredDate = registeredDate;
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

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public long getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(long paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString(){
        return this.customerId+" - "+this.trainerId+" - "+this.bookedDate;
    }
}
