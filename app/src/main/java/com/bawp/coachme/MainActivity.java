package com.bawp.coachme;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bawp.coachme.databinding.ActivityMainBinding;
import com.bawp.coachme.model.SelfWorkoutPlan;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.model.User;
import com.bawp.coachme.presentation.order.OrdersFragment;
import com.bawp.coachme.presentation.trainermap.TrainerMapFragment;
import com.bawp.coachme.presentation.trainermap.TrainerSearchFragment;
import com.bawp.coachme.utils.UserSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        UserSingleton.getInstance().setUserId("-NOjpL1jiGcc80qBrFIl");

        //addWorkoutPlans();
        //addWorkoutPlanToUser("-NOuQesyIu4gk6Qsu3Ti","-NOjpL1jiGcc80qBrFIl");
        //addWorkoutPlanToUser("-NOuQet72VtKaw2AO3qb","-NOjpL1jiGcc80qBrFIl");

        //bind
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.floatingAdd.setOnClickListener(v -> replaceFragment(new TrainerMapFragment())); //Change to trainer search working on map now

        binding.bottomNavigationView.setBackground(null);



        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){

                case R.id.menu_home:
                    replaceFragment(new TrainerSearchFragment());
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
        swpRef.push().setValue(swp2);

    }

    private void addWorkoutPlanToUser(String selfworkoutId, String userId){
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
                        userId,selfworkoutId,new Date(),1);
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

}