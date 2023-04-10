package com.bawp.coachme.presentation.userAuthentication;
/**
 * Clsss: NewUserForm.java
 * class which helps user to fill out their personal information and store them to firebase user table and user singleton
 * @author Jaydip mulani
 * @version 1.0
 */
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bawp.coachme.LoadingDBSplashActivity;
import com.bawp.coachme.MainActivity;
import com.bawp.coachme.R;
import com.bawp.coachme.model.Role;
import com.bawp.coachme.model.User;
import com.bawp.coachme.utils.UserSingleton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NewUserForm extends AppCompatActivity {
    EditText firstNameTxt;
    EditText lastNameTxt;
    EditText addressTxt;
    EditText emailTxt;
    EditText phoneNumberTxt;
    Button confirmDataBtn;
    FirebaseAuth auth;
TextView btnCurrent_location;
    FirebaseUser user;
    String current_User;


    String myDeviceToken;

    DatabaseReference databaseRef;
    private LocationRequest locationRequest;
    private Geocoder geocoder;
    List<Address> addresses=null;
    public void onStart() {
        super.onStart();

        current_User=user.getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(current_User);
//Checking in the data base that if user's first name is present in the table meant user already have submitted his data
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("firstName").exists()) {
                     //user exists
                    UserSingleton.getInstance().setUserId(current_User);
                     //here i have to change to main activity when i create one
                     Intent intent = new Intent(getApplicationContext(), LoadingDBSplashActivity.class);
                     startActivity(intent);
                     Toast.makeText(NewUserForm.this, "Welcome Back ", Toast.LENGTH_SHORT).show();
                     finish();
                }
            };

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//creating user in database and setting up new
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newuserform);
        // getting current user from firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        // saving current user ID
        current_User = user.getUid();
        // creating database reference
        databaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(current_User);
        confirmDataBtn = findViewById(R.id.btnConfirm);
        firstNameTxt = findViewById(R.id.txtFirstName);
        lastNameTxt = findViewById(R.id.txtLastName);
        emailTxt=findViewById(R.id.txt_email_new_UF);
        btnCurrent_location=findViewById(R.id.txtGetCurrentLocation);
        addressTxt = findViewById(R.id.txtAddress);
        phoneNumberTxt = findViewById(R.id.txtPhoneNumber);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        // if user is not logged in we will open login activity
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
       //getting current location
        btnCurrent_location.setOnClickListener((View v)-> {
        getCurrentLocation();
        });
        confirmDataBtn.setOnClickListener((View v) -> {
            String dbFirstname, dbLastname, dbAddress, dbPhoneNumber,dbEmail;
            // creating role as a customer ID=1 and role customer
            Role newRole = new Role(1, "Customer");
            dbFirstname = String.valueOf(firstNameTxt.getText());
            dbLastname = String.valueOf(lastNameTxt.getText());
            dbPhoneNumber = String.valueOf(phoneNumberTxt.getText());
            dbAddress = String.valueOf(addressTxt.getText());
            dbEmail= String.valueOf(emailTxt.getText());

            if (TextUtils.isEmpty(dbFirstname)) {
                Toast.makeText(NewUserForm.this, "Please Enter first name", Toast.LENGTH_SHORT).show();
                return;
            }if (TextUtils.isEmpty(dbEmail)) {
                Toast.makeText(NewUserForm.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher (dbEmail).matches()) {

                Toast.makeText(NewUserForm.this, "Please provide a valid Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(dbLastname)) {
                Toast.makeText(NewUserForm.this, "Please Enter last name", Toast.LENGTH_SHORT).show();
                return;
            }if (TextUtils.isEmpty(dbAddress)) {
                Toast.makeText(NewUserForm.this, "Please Enter address", Toast.LENGTH_SHORT).show();
                return;
            }if (TextUtils.isEmpty(dbPhoneNumber)) {
                Toast.makeText(NewUserForm.this, "Please Enter phone number", Toast.LENGTH_SHORT).show();
                return;
            }

           //creating user object to store in firebase realtime database
         User newUser= new User(dbFirstname,dbLastname,dbEmail,dbAddress,dbPhoneNumber);


            databaseRef.setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        Intent intent = new Intent(getApplicationContext(), LoadingDBSplashActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(NewUserForm.this, "unable to add user details ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });



    }
    //making sure that user has enabled access to the location
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if (isGPSEnabled()) {

                    getCurrentLocation();

                }else {

                    turnOnGPS();
                }
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentLocation();
            }
        }
    }
//getting current location for the user
    private void getCurrentLocation() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(NewUserForm.this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(NewUserForm.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(NewUserForm.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();

                                        geocoder = new Geocoder(NewUserForm.this, Locale.getDefault());

                                        try {
                                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        addressTxt.setText(addresses.get(0).getAddressLine(0));
                                        Toast.makeText(NewUserForm.this, addresses.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION}, 1);
            }
        }
    }
//creating popup for asking permission to allow user for current location
    private void turnOnGPS() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(NewUserForm.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(NewUserForm.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }
// checking   that gps is enabled or not
    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

}