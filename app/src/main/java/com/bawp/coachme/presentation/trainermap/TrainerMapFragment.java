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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TrainerMapFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_LOCATION = 1;

    private GoogleMap googleMap;
    private List<User> theFilteredTrainers;
    private HashMap<String, User> trainersMapFiltered = new HashMap<>();

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
//                getTrainers(new GetTrainersCallback() {
//                    @Override
//                    public void onTrainersReceived(List<User> trainers) {
//                        Log.d("Andrea", "Trainers received");
//                        Log.d("Andrea", "This is the name of the first: " + trainers.get(0).getFirstName());
//                        addMarkersForTrainers(trainers);
//                    }
//                });

//                getTrainers(new GetTrainersCallback() {
//                    @Override
//                    public void onTrainersReceived(List<User> trainers) {
//                        Log.d("Andrea", "Trainers received");
//                        Log.d("Andrea", "This is the name of the first: " + trainers.get(0).getFirstName());
//                        addMarkersForTrainers(trainers);
//                    }
//                });

                getTrainers(new GetTrainersCallback() {
                    @Override
                    public void onTrainersReceived(HashMap<String,User> trainers) {
                        Log.d("Andrea", "Trainers received");
//                        Log.d("Andrea", "This is the name of the first: " + trainers.get(0).getFirstName());
                        addMarkersForTrainers(trainers);
                    }
                });


                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        Log.d("ANDREA", "Clicked marker");

                        //Parameters for the object creation
                        String trainerID =marker.getSnippet();
                        String trainerName = trainersMapFiltered.get(trainerID).getFirstName() + " " + trainersMapFiltered.get(trainerID).getLastName();
                        double flatPrice = trainersMapFiltered.get(trainerID).getFlatPrice();
//                        double rating;

                        //Bundle
                        Bundle bundle = new Bundle();
                        bundle.putString(TrainerDetailsFragment.ARG_TRAINER_NAME, trainerName );
                        bundle.putDouble(TrainerDetailsFragment.ARG_TRAINER_FLATPRICE, flatPrice );
                        bundle.putString(TrainerDetailsFragment.ARG_TRAINER_ID, trainerID);

                        TrainerDetailsFragment fragment = TrainerDetailsFragment.newInstance(trainerName, flatPrice, trainerID);

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

//    private void addMarkersForTrainers(List<User> trainers) {
//
//        for (User trainer : trainers) {
//            LatLng position = new LatLng(trainer.getLatitudeCoord(), trainer.getLongitudeCoord());
//            MarkerOptions markerOptions = new MarkerOptions()
//                    .position(position)
//                    .title(trainer.getFirstName())
//                    .snippet(trainer.getEmail())
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//
//
//
//            googleMap.addMarker(markerOptions);
//        }
//    }

    private void addMarkersForTrainers(HashMap<String, User> trainers) {

        for (Map.Entry<String, User> trainer : trainers.entrySet()) { //ok
            String key = trainer.getKey();
            User theTrainer = trainer.getValue();

            LatLng position = new LatLng(theTrainer.getLatitudeCoord(), theTrainer.getLongitudeCoord());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(position)
                    .title(theTrainer.getFirstName())
                    .snippet(key) //To pass to trainer details
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));



            googleMap.addMarker(markerOptions);
        }
    }

    //method for filling theFilteredTrainers
//    private void getTrainers(GetTrainersCallback callback) {
//
//        // Change to data from Firebase
//        DatabaseReference trainerRef = FirebaseDatabase.getInstance().getReference().child("users");
//
//        //Pull with no filter
//        trainerRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                List<User> trainers = new ArrayList<>();
//
//                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                    User user = userSnapshot.getValue(User.class);
//                    trainers.add(user);
//                    assert user != null;
//                    Log.d("Andrea", "Retrieved " + user.getFirstName());
//                }
//                // New
//                callback.onTrainersReceived(trainers);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.d("Andrea", "onCancelled: " + error.getMessage());
//            }
//        });
//
//    }


    private void getTrainers(GetTrainersCallback callback) {

        // Change to data from Firebase
        DatabaseReference trainerRef = FirebaseDatabase.getInstance().getReference().child("users");

        //Pull with no filter
        trainerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap<String,User> trainers = new HashMap<>();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    String userKey = userSnapshot.getKey();
                    trainers.put(userKey, user); //
                    trainersMapFiltered.put(userKey,user);
                    assert user != null;
                    Log.d("Andrea", "Retrieved " + user.getFirstName());
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

//    public interface GetTrainersCallback {
//        void onTrainersReceived(List<User> trainers);
//    }

    public interface GetTrainersCallback {
        void onTrainersReceived(HashMap<String, User> trainersMap); //Chck this - change in trainer details
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