package com.bawp.coachme.model;

import java.io.Serializable;

public class SelfWorkoutSessionType implements Serializable {

    private String sessionType;
    private String selfWorkoutPlanId;

    private String sessionTypeIconURLFirestore;

    public SelfWorkoutSessionType(){

    }

    public SelfWorkoutSessionType(String sessionType, String selfWorkoutPlanId, String sessionTypeIconURLFirestore) {
        this.sessionType = sessionType;
        this.selfWorkoutPlanId = selfWorkoutPlanId;
        this.sessionTypeIconURLFirestore = sessionTypeIconURLFirestore;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public String getSelfWorkoutPlanId() {
        return selfWorkoutPlanId;
    }

    public void setSelfWorkoutPlanId(String selfWorkoutPlanId) {
        this.selfWorkoutPlanId = selfWorkoutPlanId;
    }

    public String getSessionTypeIconURLFirestore() {
        return sessionTypeIconURLFirestore;
    }

    public void setSessionTypeIconURLFirestore(String sessionTypeIconURLFirestore) {
        this.sessionTypeIconURLFirestore = sessionTypeIconURLFirestore;
    }
}
