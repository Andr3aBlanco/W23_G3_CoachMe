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

    private int id;
    private SelfWorkoutPlan selfworkoutplan;
    private long requestedDate;
    private int status;
    private long paymentDate;
    private String paymentId;

    public SelfWorkoutPlanByUser(){

    }

    public SelfWorkoutPlanByUser(int id,SelfWorkoutPlan selfworkoutplan, long requestedDate, int status) {
        this.id = id;
        this.selfworkoutplan = selfworkoutplan;
        this.requestedDate = requestedDate;
        this.status = status;
    }

    public SelfWorkoutPlanByUser(int id, SelfWorkoutPlan selfworkoutplan, long requestedDate, int status, long paymentDate, String paymentId) {
        this.id = id;
        this.selfworkoutplan = selfworkoutplan;
        this.requestedDate = requestedDate;
        this.status = status;
        this.paymentDate = paymentDate;
        this.paymentId = paymentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SelfWorkoutPlan getSelfworkoutplan() {
        return selfworkoutplan;
    }

    public void setSelfworkoutplan(SelfWorkoutPlan selfworkoutplan) {
        this.selfworkoutplan = selfworkoutplan;
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

}
