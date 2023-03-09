/**
 * Class: Helpers.java
 *
 * Class that will hold some methods that can guide us to insert some dummy data into the
 * Database without passing through all the activities and fragments
 *
 * Methods:
 * - addAppointments: add appointment with dummy data
 * - addWorkoutPlan: add the self-workout plan with dummy data
 * - addWorkoutPlanToUser: assign a workout plan to a user
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */

package com.bawp.coachme.utils;

import androidx.annotation.NonNull;

import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.AppointmentReminder;
import com.bawp.coachme.model.SelfWorkoutPlan;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Helpers {

    public void addAppointments(){
        String userId = UserSingleton.getInstance().getUserId();

        Appointment app = new Appointment(
                new Date().getTime(),
                new Date().getTime(),
                "Cycling",
                1,
                100.99,
                "At home",
                "-NOjpL1w8KF6d4kWh_yB",
                userId
        );

        FirebaseDatabase CoachMeDatabaseInstance = FirebaseDatabase.getInstance();
        DatabaseReference CoachMeDatabaseRef = CoachMeDatabaseInstance.getReference();
        DatabaseReference appRef = CoachMeDatabaseRef
                .child("appointments");
        appRef.push().setValue(app);

        DatabaseReference userRef = CoachMeDatabaseRef
                .child("users");

        DatabaseReference appReminderRef = CoachMeDatabaseRef
                .child("appointmentReminders");



        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                User trainer = task.getResult().child(app.getTrainerId()).getValue(User.class);
                //Converting long date into Date
                SimpleDateFormat df = new SimpleDateFormat("HH:mm");
                String hour = df.format(app.getBookedDate());


                AppointmentReminder appR = new AppointmentReminder(
                        "Appointment Comming Up!",
                        "Today you have a new appointment! \n" +
                                "Your trainer is "+trainer.getFirstName() + " " + trainer.getLastName()+"\n" +
                                "Location: "+app.getLocation()+"\n" +
                                "Date: Today at "+ hour,
                        app.getBookedDate(),
                        UserSingleton.getInstance().getUserDeviceToken()
                );

                appReminderRef.push().setValue(appR);
            }
        });

    }

    private void addWorkoutPlans(){
        FirebaseDatabase CoachMeDatabaseInstance = FirebaseDatabase.getInstance();
        DatabaseReference CoachMeDatabaseRef = CoachMeDatabaseInstance.getReference();
        DatabaseReference swpRef = CoachMeDatabaseRef.child("selfWorkoutPlans");

        SelfWorkoutPlan swp1 = new SelfWorkoutPlan("Crossfit Workout Plan",
                "Crossfit Workout Plan for everyone",
                190.99,"");

        SelfWorkoutPlan swp2 = new SelfWorkoutPlan("Cycling Workout Plan",
                "Cycling Workout Plan - 30days for everyone",
                89.99,"");
        swpRef.push().setValue(swp1);
        swpRef.push().setValue(swp1);
        swpRef.push().setValue(swp2);

    }

    private void addWorkoutPlanToUser(String selfworkoutId, String userId) {
        FirebaseDatabase CoachMeDatabaseInstance = FirebaseDatabase.getInstance();
        DatabaseReference CoachMeDatabaseRef = CoachMeDatabaseInstance.getReference();
        DatabaseReference swpByUserRef = CoachMeDatabaseRef
                .child("selfWorkoutPlansByUser");

        Task swpByUserRefTask = swpByUserRef.get();
        swpByUserRefTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                DataSnapshot ds = (DataSnapshot) task.getResult();
                SelfWorkoutPlanByUser obj = new SelfWorkoutPlanByUser(
                        userId, selfworkoutId, 1677354923545L, 1);
                swpByUserRef.push().setValue(obj);
            }
        });

    }


}
