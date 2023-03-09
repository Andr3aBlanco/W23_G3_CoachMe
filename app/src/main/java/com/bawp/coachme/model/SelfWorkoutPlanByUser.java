/**
 * Class: SelfWorkoutPlanByUser.java
 *
 * Class associated with the information of which user has which self-workout plan
 * (Many to many relationship)
 *
 * Fields:
 * - customerId
 * - selfworkoutplanId
 * - requestedDate: date when the user requested the self-workout plan (added into the shopping cart)
 * - status: current status of the appointment
 *      -> 1: pending (waiting for payment)
 *      -> 2: cancelled
 *      -> 3: active
 * - paymentDate: date when the user paid for the self-workout plan
 * - paymentId: payment id (payment intent id from Stripe)
 *
 * @author Luis Miguel Miranda
 * @version 1.0.1
 */
package com.bawp.coachme.model;

import java.util.Date;

public class SelfWorkoutPlanByUser {

    private String customerId;
    private String selfworkoutplanId;
    private long requestedDate;
    private int status;
    private long paymentDate;
    private String paymentId;

    public SelfWorkoutPlanByUser(){

    }

    public SelfWorkoutPlanByUser(String customerId,String selfworkoutplanId, long requestedDate, int status) {
        this.customerId = customerId;
        this.selfworkoutplanId = selfworkoutplanId;
        this.requestedDate = requestedDate;
        this.status = status;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSelfworkoutplanId() {
        return selfworkoutplanId;
    }

    public void setSelfworkoutplanId(String selfworkoutplanId) {
        this.selfworkoutplanId = selfworkoutplanId;
    }

    public long getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(long requestedDate) {
        this.requestedDate = requestedDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(long paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    @Override
    public String toString(){
        return customerId + " " + selfworkoutplanId;
    }
}
