package com.bawp.coachme.model;

import java.io.Serializable;

public class SelfWorkoutSessionType implements Serializable {

    private String id;
    private String sessionType;
    private SelfWorkoutPlan selfWorkoutPlan;

    private String sessionTypeIconURLFirestore;

    public SelfWorkoutSessionType(){

    }

    public SelfWorkoutSessionType(String id,String sessionType, SelfWorkoutPlan selfWorkoutPlan, String sessionTypeIconURLFirestore) {
        this.id = id;
        this.sessionType = sessionType;
        this.selfWorkoutPlan = selfWorkoutPlan;
        this.sessionTypeIconURLFirestore = sessionTypeIconURLFirestore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public SelfWorkoutPlan getSelfWorkoutPlan() {
        return selfWorkoutPlan;
    }

    public void setSelfWorkoutPlan(SelfWorkoutPlan selfWorkoutPlan) {
        this.selfWorkoutPlan = selfWorkoutPlan;
    }

    public String getSessionTypeIconURLFirestore() {
        return sessionTypeIconURLFirestore;
    }

    public void setSessionTypeIconURLFirestore(String sessionTypeIconURLFirestore) {
        this.sessionTypeIconURLFirestore = sessionTypeIconURLFirestore;
    }
}
