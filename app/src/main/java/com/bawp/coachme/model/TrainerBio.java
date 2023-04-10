package com.bawp.coachme.model;


/**
 * Class TrainerBio.java
 *
 * class associated with the table trainerbio
 *
 * @author Andrea Blanco
 *
 * **/
public class TrainerBio {

    String trainerId;
    String tainerBio;

    public TrainerBio() {
    }

    public TrainerBio(String trainerId, String tainerBio) {
        this.trainerId = trainerId;
        this.tainerBio = tainerBio;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
    }

    public String getTainerBio() {
        return tainerBio;
    }

    public void setTainerBio(String tainerBio) {
        this.tainerBio = tainerBio;
    }
}
