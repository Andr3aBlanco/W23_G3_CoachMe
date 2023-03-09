package com.bawp.coachme.model;

import java.io.Serializable;

/**
 * Class: SelfWorkoutSessionLog.java
 *
 * This class will hold the information of which exercises have been completed in a session
 *
 * Fields:
 * - selfWorkoutSessionId
 * - exerciseId
 * - sessionExerciseStatus: current status of the session's exercise
 *      -> 1: started
 *      -> 2: cancelled
 *      -> 3: finished
 *
 * @author Luis Miguel Miranda
 * @version 1.0.0
 */

public class SelfWorkoutSessionLog implements Serializable {

    private String selfWorkoutSessionId;
    private SelfWorkoutPlanExercise selfWorkoutExercise;
    private int sessionExerciseStatus;

    public SelfWorkoutSessionLog(){

    }

    public SelfWorkoutSessionLog(String selfWorkoutSessionId, SelfWorkoutPlanExercise selfWorkoutExercise, int sessionExerciseStatus) {
        this.selfWorkoutSessionId = selfWorkoutSessionId;
        this.selfWorkoutExercise = selfWorkoutExercise;
        this.sessionExerciseStatus = sessionExerciseStatus;
    }

    public String getSelfWorkoutSessionId() {
        return selfWorkoutSessionId;
    }

    public void setSelfWorkoutSessionId(String selfWorkoutSessionId) {
        this.selfWorkoutSessionId = selfWorkoutSessionId;
    }

    public SelfWorkoutPlanExercise getSelfWorkoutExercise() {
        return selfWorkoutExercise;
    }

    public void setSelfWorkoutExercise(SelfWorkoutPlanExercise selfWorkoutExercise) {
        this.selfWorkoutExercise = selfWorkoutExercise;
    }

    public int getSessionExerciseStatus() {
        return sessionExerciseStatus;
    }

    public void setSessionExerciseStatus(int sessionExerciseStatus) {
        this.sessionExerciseStatus = sessionExerciseStatus;
    }
}
