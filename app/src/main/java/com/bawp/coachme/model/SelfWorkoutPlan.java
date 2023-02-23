/**
 * Class: SelfWorkoutPlan.java
 *
 * Class associated with the information of the Self-workout plans.
 *
 * Fields:
 * - title
 * - description
 * - planPrice
 *
 * @author Luis Miguel Miranda
 * @version 1.0.1
 */
package com.bawp.coachme.model;

public class SelfWorkoutPlan {

    private String title;
    private String description;
    private double planPrice;

    public SelfWorkoutPlan(){

    }

    public SelfWorkoutPlan(String title, String description, double planPrice) {
        this.title = title;
        this.description = description;
        this.planPrice = planPrice;
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
}
