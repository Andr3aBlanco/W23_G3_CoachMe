package com.bawp.coachme;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.ConversationActions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bawp.coachme.databinding.ActivityMainBinding;
import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.AppointmentReminder;
import com.bawp.coachme.model.SelfWorkoutPlan;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.model.User;
import com.bawp.coachme.presentation.order.OrdersFragment;
import com.bawp.coachme.utils.UserSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //After loging, we have set the User Id
        UserSingleton.getInstance().setUserId("-NOjpL1jiGcc80qBrFIl");

        /*
            Activate the messaging service (Push Notification) by getting the
            device token
         */

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        Log.d("FIREBASE MESSAGING","Failed token");
                        return;
                    }
                    String token = task.getResult();
                    // Store the FCM registration token in your database
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference usersRef = database.child("users");
                    UserSingleton.getInstance().setUserDeviceToken(token);
                    usersRef.child(UserSingleton.getInstance().getUserId()).child("deviceToken").setValue(token);

                    //sendPushNotification();
                });

        //addWorkoutPlans();
        //addWorkoutPlanToUser("-NOuQesyIu4gk6Qsu3Ti","-NOjpL1jiGcc80qBrFIl");
        //addWorkoutPlanToUser("-NOuQet72VtKaw2AO3qb","-NOjpL1jiGcc80qBrFIl");

        //Testing a Push Notification Sending
        addAppointments();


        //bind
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){

                case R.id.menu_home:
                    replaceFragment(new HomeFragment());
                    break;

                case R.id.menu_orders:
                    replaceFragment(new OrdersFragment());

                    break;

                case R.id.menu_profile:
                    replaceFragment(new ProfileFragment());

                    break;

                case R.id.menu_stats:
                    replaceFragment(new StatsFragment());

                    break;

                case R.id.menu_add:
                    replaceFragment(new HomeFragment());

                    break;

            }

            return true;
        });
    }

    private void addWorkoutPlans(){
        FirebaseDatabase CoachMeDatabaseInstance = FirebaseDatabase.getInstance();
        DatabaseReference CoachMeDatabaseRef = CoachMeDatabaseInstance.getReference();
        DatabaseReference swpRef = CoachMeDatabaseRef.child("selfWorkoutPlans");

        SelfWorkoutPlan swp1 = new SelfWorkoutPlan("Crossfit Workout Plan",
                                                    "Crossfit Workout Plan for everyone",
                                                    190.99);

        SelfWorkoutPlan swp2 = new SelfWorkoutPlan("Cycling Workout Plan",
                "Cycling Workout Plan - 30days for everyone",
                89.99);
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
                        userId, selfworkoutId, new Date(), 1);
                swpByUserRef.push().setValue(obj);
            }
        });

    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.barFrame, fragment);
        fragmentTransaction.commit();

    }

    private void addAppointments(){
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
                AppointmentReminder appR = new AppointmentReminder(
                        "Appointment Comming Up!",
                        "Appointment with "+trainer.getFirstName(),
                        app.getBookedDate(),
                        UserSingleton.getInstance().getUserDeviceToken()
                        );

                appReminderRef.push().setValue(appR);
            }
        });




    }

}