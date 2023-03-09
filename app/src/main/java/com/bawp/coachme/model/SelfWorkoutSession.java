package com.bawp.coachme.model;

import java.io.Serializable;

/**
 * Class: SelfWorkoutSession.java
 *
 * This class will help us to track all the interactions of the user in the workoutplan.
 * That means that every time a user starts a session, the app will record that interaction
 * so the user will not start it once it has been initiated.
 *
 * Fields:
 * - selfWorkoutPlanByUserid -> to reduce the name it'll become swpUserId
 * - selfWorkoutSessionType -> type of session of the workout
 * Example:
 * Let's say we have a workout plan called "Extreme Workout". Within this workout plan,
 * we have 3 types (for example): Legs, biceps & triceps, cardio.
 * That means that a user must only complete one type per day.
 *
 * - sessionDate: date when the session has been started
 * - sessionStatus: current status of the session
 *      -> 1: started
 *      -> 2: cancelled
 *      -> 3: finished
 *
 * @author Luis Miguel Miranda
 * @version 1.0.0
 */

public class SelfWorkoutSession implements Serializable {

    private String swpUserId;
    private String selfworkoutSessionType;
    private long sessionDate;
    private int sessionStatus;

    public SelfWorkoutSession(){

    }

    public SelfWorkoutSession(String swpUserId, String selfworkoutSessionType, long sessionDate, int sessionStatus) {
        this.swpUserId = swpUserId;
        this.selfworkoutSessionType = selfworkoutSessionType;
        this.sessionDate = sessionDate;
        this.sessionStatus = sessionStatus;
    }

    public String getSwpUserId() {
        return swpUserId;
    }

    public void setSwpUserId(String swpUserId) {
        this.swpUserId = swpUserId;
    }

    public String getSelfworkoutSessionType() {
        return selfworkoutSessionType;
    }

    public void setSelfworkoutSessionType(String selfworkoutSessionType) {
        this.selfworkoutSessionType = selfworkoutSessionType;
    }

    public long getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(long sessionDate) {
        this.sessionDate = sessionDate;
    }

    public int getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(int sessionStatus) {
        this.sessionStatus = sessionStatus;
    }
}
