package com.bawp.coachme.presentation.trainermap;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TrainerMapFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_LOCATION = 1;

    private GoogleMap googleMap;
    private List<Trainer> theFilteredTrainers;
    private HashMap<String, Trainer> trainersMapFiltered = new HashMap<>();

    private MapView mapView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
          trainersMapFiltered = (HashMap<String, Trainer>) getArguments().getSerializable("FILTERED_TRAINERS");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.trainer_map_fragment, container, false);

        Log.d("Andrea", "The map was loaded");
        mapView = (MapView) view.findViewById(R.id.mapTrainersF); //
        mapView.onCreate(savedInstanceState); //

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap map) {
                googleMap = map;

                // Set the initial position and zoom level
                LatLng initialPosition = new LatLng(49.18308405089783, -122.95854180247775); // Vancouver Metro 49.18308405089783, -122.95854180247775
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 10f));

                Log.d("Andrea", "Map Ready");
                checkLocationPermissionAndEnableMyLocation();

                        addMarkersForTrainers(trainersMapFiltered);

                //Print unfiltered trainers
//                for (Map.Entry<String, User> entry : trainersMapFiltered.entrySet()) {
//                    String key = entry.getKey();
//                    User value = entry.getValue();
//                    System.out.println(key + " = " + value.getFirstName());
//                }


                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        Log.d("ANDREA", "Clicked marker");

                        //Parameters for the object creation
                        String trainerID =marker.getSnippet();
                        Trainer userToPass = trainersMapFiltered.get(trainerID);

                        //Bundle
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("TRAINER_TO_DISPLAY", userToPass);
                        TrainerDetailsFragment detailsFragment = new TrainerDetailsFragment();
                        detailsFragment.setArguments(bundle);

                        // Replace the map fragment with the TrainerFragment
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.mapFragmentContainer, detailsFragment);
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
    private void addMarkersForTrainers(HashMap<String, Trainer> trainers) {

        for (Map.Entry<String, Trainer> trainer : trainers.entrySet()) { //ok
            String key = trainer.getKey();
            Trainer theTrainer = trainer.getValue();

            LatLng position = new LatLng(theTrainer.getLatitudeCoord(), theTrainer.getLongitudeCoord());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(position)
                    .title(theTrainer.getFirstName())
                    .snippet(key) //To pass to trainer details
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));



            googleMap.addMarker(markerOptions);
        }
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
    private Trainer getClickedTrainer(Marker marker) {
        for (Trainer trainer : theFilteredTrainers) {
            if (trainer.getFirstName().equals(marker.getTitle())) {
                return trainer;
            }
        }
        return null;
    }



}