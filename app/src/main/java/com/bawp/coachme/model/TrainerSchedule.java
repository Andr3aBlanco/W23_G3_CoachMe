package com.bawp.coachme.model;

import java.util.Date;


/**
 * Class TrainerSchedule.java
 *
 * Class associated with the table trainer schedule
 *
 * @author Andrea Blanco
 *
 * **/
public class TrainerSchedule {

    private Date time;
    private String trainerID;

    public TrainerSchedule() {
    }

    public TrainerSchedule(Date time, String trainerID) {
        this.time = time;
        this.trainerID = trainerID;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time =  new Date(time);
    }

    public String getTrainerID() {
        return trainerID;
    }

    public void setTrainerID(String trainerID) {
        this.trainerID = trainerID;
    }
}