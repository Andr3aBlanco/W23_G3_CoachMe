package com.bawp.coachme.model;

import java.util.List;

public class Payment {

    private String paymentId;
    private long paymentDate;

    private double finalPrice;
    private List<Appointment> appointmentList;
    private List<SelfWorkoutPlanByUser> selfWorkoutPlanByUserList;

    public Payment(){

    }

    public Payment(String paymentId, long paymentDate, double finalPrice) {
        this.paymentId = paymentId;
        this.paymentDate = paymentDate;
        this.finalPrice = finalPrice;
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

    public List<Appointment> getAppointmentList() {
        return appointmentList;
    }

    public void setAppointmentList(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    public List<SelfWorkoutPlanByUser> getSelfWorkoutPlanByUserList() {
        return selfWorkoutPlanByUserList;
    }

    public void setSelfWorkoutPlanByUserList(List<SelfWorkoutPlanByUser> selfWorkoutPlanByUserList) {
        this.selfWorkoutPlanByUserList = selfWorkoutPlanByUserList;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }
}
