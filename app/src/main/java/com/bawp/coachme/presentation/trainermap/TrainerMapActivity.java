package com.bawp.coachme.presentation.trainermap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.model.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TrainerMapActivity extends AppCompatActivity {

    private MapView mapView;
    private GoogleMap googleMap;

    List<User> theFilteredTrainers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_map);

        mapView = (MapView) findViewById(R.id.mapTrainers);
        mapView.onCreate(savedInstanceState);

        getTrainers(new GetTrainersCallback() {
            @Override
            public void onTrainersReceived(List<User> trainers) {
                addMarkersForTrainers();
            }
        });

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;

                Log.d("Andrea", "Map ready");

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        Log.d("ANDREA", "Clicked marker");
                        return false;
                    }
                });
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    private void addMarkersForTrainers() {

        for (User trainer : theFilteredTrainers) {
            LatLng position = new LatLng(trainer.getLatitudeCoord(), trainer.getLongitudeCoord());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(position)
                    .title(trainer.getFirstName())
                    .snippet(String.valueOf(trainer.getFlatPrice()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            googleMap.addMarker(markerOptions);
        }
    }


    private void getTrainers(GetTrainersCallback callback) {


        // Change to data from Firebase
        DatabaseReference trainerRef = FirebaseDatabase.getInstance().getReference().child("users");


        //Pull with no filter
        trainerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    theFilteredTrainers.add(user);
                    assert user != null;
                    Log.d("Andrea", "Retrived " + user.getFirstName());
                }
                // D
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Andrea", "onCancelled: " + error.getMessage());
            }
        });

            callback.onTrainersReceived(theFilteredTrainers);

    }

    public interface GetTrainersCallback {
        void onTrainersReceived(List<User> trainers);
    }

}