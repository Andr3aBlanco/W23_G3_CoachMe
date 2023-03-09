package com.bawp.coachme.model;

import java.util.List;

public class Trainer extends User{

    private String trainerID;
    private List<Long> slots;
    private double rating;

    // constructor

    public Trainer(String trainerID, List<Long> slots, double rating) {
        this.trainerID = trainerID;
        this.slots = slots;
        this.rating = rating;
    }

    public Trainer(String trainerID, String firstName, String lastName, String email, double latitudeCoord, double longitudeCoord, double radius, double flatPrice, String phoneNumber, String address, Role role, List<String> serviceTypes,  List<Long> slots, double rating) {
        super(firstName, lastName, email, latitudeCoord, longitudeCoord, radius, flatPrice, phoneNumber, address, role, serviceTypes);
        this.trainerID = trainerID;
        this.slots = slots;
        this.rating = rating;
    }


    //Methods from trainer

    public List<Long> getSlots() {
        return slots;
    }

    public void setSlots(List<Long> slots) {
        this.slots = slots;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getTrainerID() {
        return trainerID;
    }

    public void setTrainerID(String trainerID) {
        this.trainerID = trainerID;
    }


    // Methods from user


}
