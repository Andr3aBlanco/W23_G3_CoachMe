/**
 * Class: SelfWorkoutPlan.java
 *
 * Class associated with the information of the Self-workout plans.
 *
 * Fields:
 * - title
 * - description
 * - planPrice
 * - posterUrlFirestore (this is the file location from Firebase Firestore storage)
 * - mainGoals
 * - duration
 * - daysPerWeek
 * - level
 *
 * @author Luis Miguel Miranda
 * @version 1.0.1
 */
package com.bawp.coachme.model;

import java.io.Serializable;

public class SelfWorkoutPlan implements Serializable {

    private String id;
    private String title;
    private String description;
    private double planPrice;
    private String posterUrlFirestore;
    private String mainGoals;
    private String duration;
    private int daysPerWeek;
    private String level;

    public SelfWorkoutPlan(){

    }

    public SelfWorkoutPlan(String id,String title, String description, double planPrice, String posterUrlFirestore, String mainGoals, String duration, int daysPerWeek, String level) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.planPrice = planPrice;
        this.posterUrlFirestore = posterUrlFirestore;
        this.mainGoals = mainGoals;
        this.duration = duration;
        this.daysPerWeek = daysPerWeek;
        this.level = level;
    }

    public SelfWorkoutPlan(String title, String description, double planPrice, String posterUrlFirestore) {
        this.title = title;
        this.description = description;
        this.planPrice = planPrice;
        this.posterUrlFirestore = posterUrlFirestore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPlanPrice() {
        return planPrice;
    }

    public void setPlanPrice(double planPrice) {
        this.planPrice = planPrice;
    }

    public String getPosterUrlFirestore() {
        return posterUrlFirestore;
    }

    public void setPosterUrlFirestore(String posterUrlFirestore) {
        this.posterUrlFirestore = posterUrlFirestore;
    }

    public String getMainGoals() {
        return mainGoals;
    }

    public void setMainGoals(String mainGoals) {
        this.mainGoals = mainGoals;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getDaysPerWeek() {
        return daysPerWeek;
    }

    public void setDaysPerWeek(int daysPerWeek) {
        this.daysPerWeek = daysPerWeek;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString(){
        return title + " " + description + mainGoals;
    }
}
