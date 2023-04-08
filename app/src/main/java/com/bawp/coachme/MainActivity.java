package com.bawp.coachme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bawp.coachme.databinding.ActivityMainBinding;
import com.bawp.coachme.presentation.home.HomeFragment;
import com.bawp.coachme.presentation.order.OrdersFragment;
import com.bawp.coachme.presentation.stats.StatsFragment;
import com.bawp.coachme.presentation.trainermap.TrainerSearchFragment;
import com.bawp.coachme.presentation.userAuthentication.LoginActivity;
import com.bawp.coachme.utils.UserSingleton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.bawp.coachme.presentation.userAuthentication.ProfileFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: MainActivity.java
 *
 * This is the activity that hosts the FrameLayout where the sections included
 * in the navigation bar are hosted
 *
 * It also hosts the Bottom Navigation Bar
 *
 * @author Andrea Blanco / Jaydipkumar Mulani
 * **/



public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private static final int NOTIFICATION_PERMISSION_CODE = 123;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    private String provider;

    private double latitude;
    private double longitude;



    ActivityMainBinding binding;
    CountDownTimer countDownTimer;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String current_User;
    DatabaseReference databaseRef;
    private String myDeviceId;
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        replaceFragment(new HomeFragment());
        fragmentBinding();

        // Requesting permissions (LOCATION and POST NOTIFICATIONS)
        List<String> requestPermissions = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            Log.d("Permissions","Location is required");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions.add(Manifest.permission.POST_NOTIFICATIONS);
            Log.d("Permissions","Post Notifications is required");
        }

        if (requestPermissions.size() > 0){
            Log.d("Permissions","We required at least 1 permission");
            //let's ask for the permissions
            ActivityCompat.requestPermissions(this,
                    requestPermissions.toArray(new String[requestPermissions.size()]),
                    PERMISSIONS_REQUEST_LOCATION);
        }

    }

    private void fragmentBinding(){

        //bind
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            getLoginStatus();
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

        binding.floatingAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new TrainerSearchFragment());
            }
        });
    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.barFrame, fragment);
        fragmentTransaction.commit();

    }
//    checking every 30 second that user device id is still same
    private void getLoginStatus() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        current_User = user.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(current_User);


        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                countDownTimer.start();
                databaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String deviceToken = snapshot.child("deviceToken").getValue(String.class);
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (!deviceToken.equals(UserSingleton.getInstance().getUserDeviceToken())){
                            FirebaseAuth.getInstance().signOut();


                            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            countDownTimer.cancel();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        }.start();
    }

}