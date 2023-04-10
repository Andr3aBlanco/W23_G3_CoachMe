package com.bawp.coachme.model;

import java.io.Serializable;

/**
 * Class: SelfWorkoutSessionLog.java
 *
 * This class will hold the information of which exercises have been completed in a session
 *
 * Fields:
 * - selfWorkoutSessionId
 * - selfworkoutExercise
 * - sessionExerciseStatus: current status of the session's exercise
 *      -> 1: started
 *      -> 2: cancelled
 *      -> 3: finished
 *
 * @author Luis Miguel Miranda
 * @version 1.0.0
 */

public class SelfWorkoutSessionLog implements Serializable {

    private int id;
    private int selfWorkoutSessionId;
    private SelfWorkoutPlanExercise selfWorkoutExercise;
    private int sessionExerciseStatus;

    public SelfWorkoutSessionLog(){

    }

    public SelfWorkoutSessionLog(int id,int selfWorkoutSessionId, SelfWorkoutPlanExercise selfWorkoutExercise, int sessionExerciseStatus) {
        this.id = id;
        this.selfWorkoutSessionId = selfWorkoutSessionId;
        this.selfWorkoutExercise = selfWorkoutExercise;
        this.sessionExerciseStatus = sessionExerciseStatus;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSelfWorkoutSessionId() {
        return selfWorkoutSessionId;
    }

    public void setSelfWorkoutSessionId(int selfWorkoutSessionId) {
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
