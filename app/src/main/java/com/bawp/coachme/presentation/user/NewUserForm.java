package com.bawp.coachme.presentation.user;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.tasks.OnSuccessListener;
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

import adapter.PlaceAutoSuggestionAdapter;

public class NewUserForm extends AppCompatActivity {
    EditText firstNameTxt;
    EditText lastNameTxt;
    EditText addressTxt;
    EditText phoneNumberTxt;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;
    String emailTxt;

    Button confirmDataBtn;
    FirebaseAuth auth;
Button btnCurrent_location;
    FirebaseUser user;
    String current_User;
    String fname;
    String lname;
    String phonenum;
private String city;
    Button btn;
    DatabaseReference databaseRef;
    private LocationRequest locationRequest;
    private Geocoder geocoder;
    List<Address> addresses=null;
    public void onStart() {
        super.onStart();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(current_User);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                     //user exists

                     //here i have to change to main activity when i create one
                     Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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

        } else {
            emailTxt = user.getEmail();
            fname = user.getDisplayName();
            phonenum = user.getPhoneNumber();
            firstNameTxt.setText(fname);
            phoneNumberTxt.setText(phonenum);

        }
       //getting current location
btnCurrent_location.setOnClickListener((View v)-> {
getCurrentLocation();
});
        confirmDataBtn.setOnClickListener((View v) -> {


            String Fname, Lname, address, phoneNumber;

            // creating role as a customer ID=1 and role customer
            Role newRole = new Role(1, "Customer");
            Fname = String.valueOf(firstNameTxt.getText());
            Lname = String.valueOf(lastNameTxt.getText());
            phoneNumber = String.valueOf(phoneNumberTxt.getText());
            address = String.valueOf(addressTxt.getText());


            if (TextUtils.isEmpty(Fname)) {
                Toast.makeText(NewUserForm.this, "Please Enter first name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(Lname)) {
                Toast.makeText(NewUserForm.this, "Please Enter last name", Toast.LENGTH_SHORT).show();
                return;
            }if (TextUtils.isEmpty(address)) {
                Toast.makeText(NewUserForm.this, "Please Enter address", Toast.LENGTH_SHORT).show();
                return;
            }if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(NewUserForm.this, "Please Enter phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            User newUser = new User(Fname, Lname, emailTxt, address, phoneNumber, newRole);

            //Because the user is a new user, let's add it into firebase
            databaseRef.setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        UserSingleton.getInstance().setUserId(current_User);
                        Toast.makeText(NewUserForm.this, "Welcome to CoachMe ", Toast.LENGTH_SHORT).show();
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