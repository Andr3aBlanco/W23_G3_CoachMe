package com.bawp.coachme.presentation.trainermap;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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


public class TrainerMapFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_LOCATION = 1;

    private GoogleMap googleMap;
    private List<User> theFilteredTrainers;

    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.trainer_map_fragment, container, false);

        mapView = (MapView) view.findViewById(R.id.mapTrainersF); //
        mapView.onCreate(savedInstanceState); //

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap map) {
                googleMap = map;

                Log.d("Andrea", "Map Ready");
                checkLocationPermissionAndEnableMyLocation();


                //Chnage
                getTrainers(new GetTrainersCallback() {
                    @Override
                    public void onTrainersReceived(List<User> trainers) {
                        Log.d("Andrea", "Trainers received");
                        Log.d("Andrea", "This is the name of the first: " + trainers.get(0).getFirstName());
                        addMarkersForTrainers(trainers);
                    }
                });


                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        Log.d("ANDREA", "Clicked marker");

                        String trainerName = marker.getTitle();
                        String trainerEmail =marker.getSnippet();

                        //Bundle
                        Bundle bundle = new Bundle();
                        bundle.putString(TrainerDetailsFragment.ARG_TRAINER_NAME, trainerName );

                        TrainerDetailsFragment fragment = TrainerDetailsFragment.newInstance(trainerName, trainerEmail);

                        // Replace the map fragment with the TrainerFragment
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.mapFragmentContainer, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();


                        return true;
                    }
                });
            }
        });


        return view;
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

    //ADDITIONAL METHODS

    private void addMarkersForTrainers(List<User> trainers) {

        for (User trainer : trainers) {
            LatLng position = new LatLng(trainer.getLatitudeCoord(), trainer.getLongitudeCoord());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(position)
                    .title(trainer.getFirstName())
                    .snippet(trainer.getEmail())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));



            googleMap.addMarker(markerOptions);
        }
    }

    //method for filling theFilteredTrainers
    private void getTrainers(GetTrainersCallback callback) {

//        List<User> trainers = new ArrayList<>();
        // Change to data from Firebase
        DatabaseReference trainerRef = FirebaseDatabase.getInstance().getReference().child("users");


        //Pull with no filter
        trainerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<User> trainers = new ArrayList<>();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    trainers.add(user);
                    assert user != null;
                    Log.d("Andrea", "Retrived " + user.getFirstName());
                }
                // New
                callback.onTrainersReceived(trainers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Andrea", "onCancelled: " + error.getMessage());
            }
        });

    }


    public interface GetTrainersCallback {
        void onTrainersReceived(List<User> trainers);
    }

    private void checkLocationPermissionAndEnableMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
//            googleMap.setMyLocationEnabled(true);

        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        }
    }


    //Method to get the clicked trainer
    private User getClickedTrainer(Marker marker) {
        for (User trainer : theFilteredTrainers) {
            if (trainer.getFirstName().equals(marker.getTitle())) {
                return trainer;
            }
        }
        return null;
    }

}