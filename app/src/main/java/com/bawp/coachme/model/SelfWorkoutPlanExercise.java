package com.bawp.coachme.model;

import java.io.Serializable;

public class SelfWorkoutPlanExercise implements Serializable {

    private String id;
    private String selfWorkoutSessionTypeId;
    private String exerciseName;
    private int numSets;
    private String repetitions;
    private String restTime;
    private String exerciseImageURLFirestore;

    public SelfWorkoutPlanExercise(){

    }

    public SelfWorkoutPlanExercise(String id,String selfWorkoutSessionTypeId, String exerciseName, int numSets, String repetitions, String restTime, String exerciseImageURLFirestore) {
        this.id = id;
        this.selfWorkoutSessionTypeId = selfWorkoutSessionTypeId;
        this.exerciseName = exerciseName;
        this.numSets = numSets;
        this.repetitions = repetitions;
        this.restTime = restTime;
        this.exerciseImageURLFirestore = exerciseImageURLFirestore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSelfWorkoutSessionTypeId() {
        return selfWorkoutSessionTypeId;
    }

    public void setSelfWorkoutSessionTypeId(String selfWorkoutSessionTypeId) {
        this.selfWorkoutSessionTypeId = selfWorkoutSessionTypeId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public int getNumSets() {
        return numSets;
    }

    public void setNumSets(int numSets) {
        this.numSets = numSets;
    }

    public String getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(String repetitions) {
        this.repetitions = repetitions;
    }

    public String getRestTime() {
        return restTime;
    }

    public void setRestTime(String restTime) {
        this.restTime = restTime;
    }

    public String getExerciseImageURLFirestore() {
        return exerciseImageURLFirestore;
    }

    public void setExerciseImageURLFirestore(String exerciseImageURLFirestore) {
        this.exerciseImageURLFirestore = exerciseImageURLFirestore;
    }
}
