package com.bawp.coachme.presentation.trainermap;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.bawp.coachme.HomeFragment;
import com.bawp.coachme.R;
import com.bawp.coachme.model.User;
import com.bawp.coachme.presentation.order.OrdersFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrainerSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrainerSearchFragment extends Fragment implements LocationListener {


    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;


    //Get Elements from the Layout
    RadioGroup rgMapList;
    FrameLayout outletMapList;

    //For the filter
    Spinner spinSort;
    TextView dateFrom;
    TextView dateTo;

    ImageButton search;

    private DatePickerDialog datePickerDialog;

    //Variables for the search in firebase
    long initialDate;
    long endDate;

    List<String> trainerIDs = new ArrayList<>();

    //Arrays to store filter results
    private HashMap<String, User> unfilteredTrainers = new HashMap<>();
    private HashMap<String, User> filteredTrainers = new HashMap<>();

//    Fragment fragmentMapList = new TrainerMapFragment();

    private LocationManager locationManager;
    private String provider;
    private double latitude;
    private double longitude;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TrainerSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrainerSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrainerSearchFragment newInstance(String param1, String param2) {
        TrainerSearchFragment fragment = new TrainerSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Check if the app has permission to access location information
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission if it hasn't been granted
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        }
    }


    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.trainer_search_fragment, container, false);

        rgMapList = view.findViewById(R.id.rgMapListSelector);
        outletMapList = view.findViewById(R.id.searchOptContainer);
        spinSort = view.findViewById(R.id.spinOrderByOptions);
        dateFrom = view.findViewById(R.id.tvDateStart);
        dateTo = view.findViewById(R.id.tvDateEnd);
        search = view.findViewById(R.id.ivSearch);

        //Initialize datePickers
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        dateFrom.setText(dayOfMonth+"/"+month+"/"+year);
        dateTo.setText(dayOfMonth+"/"+month+"/"+year);

        //set initial dates
        calendar.set(year,month,dayOfMonth,0,0,0);
        initialDate = calendar.getTime().getTime();
        calendar.set(year,month,dayOfMonth,23,59,59);
        endDate = calendar.getTime().getTime();

        //Cick Listener for the date
        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showDatePickerDialog(dateTo);
            }
        });

        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(dateFrom);
            }
        });

        // Get the location manager and provider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        }
        provider = LocationManager.GPS_PROVIDER;

        // Request location updates
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);

        // Get the last known location
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d("TrainerSearch", "Latitude: " + latitude + ", Longitude: " + longitude); //My location ok :)
        }


        //get trainers
        getTrainers(new GetTrainersCallback() {
            @Override
            public void onTrainersReceived(HashMap<String, User> trainers) {
                Log.d("Andrea", "Trainers received in the TrainerSearchFragment");

                //Pass the data to MapSearchFragment
                TrainerMapFragment childMapFragment = new TrainerMapFragment();
                Bundle args = new Bundle();
                args.putSerializable("FILTERED_TRAINERS", unfilteredTrainers);
                childMapFragment.setArguments(args);

                //At the beginning replace for TrainerMapFragment
                replaceFragment(childMapFragment);
            }
        });


        rgMapList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                switch (checkedId) {
                    case R.id.rbMapView:
                        replaceFragment(new TrainerMapFragment());
                        break;
                    case R.id.rbListView:
                        replaceFragment(new HomeFragment()); //Replace this for the list later
                        break;
                    default:
                        replaceFragment(new TrainerMapFragment());
                        break;
                }
            }
        });


        //Listener for the search button
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initialize again the list of trainerIDs
                trainerIDs = new ArrayList<>();
                Log.d("Andrea", "This is the leghtn os trainer IDs in the onCLick " + trainerIDs.size());
                //Create the references
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

                //Logic for between dates
                Query availAppQuery = dbRef.child("trainerSchedules")
                        .orderByChild("time")
                        .startAt(initialDate)
                        .endAt(endDate);

                    Log.d("Andrea", "Inside click search  this is the end time " + endDate + "initial date: " + initialDate);
                availAppQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                            String trainerId = appointmentSnapshot.child("trainerID").getValue(String.class);
                            if (!trainerIDs.contains(trainerId)) {
                                trainerIDs.add(trainerId); //
                                // Do i need the list of appointments here?  - pulling again in trainer details -- check again
                    }}
                        Log.d("Andrea", "Retrieved " + trainerIDs.size() + " trainers");

                        for(String trainerID : trainerIDs){
                            dbRef.child("users").child(trainerID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    String key = snapshot.getKey();
                                    User trainer = snapshot.getValue(User.class);
                                    filteredTrainers.put(key, trainer); //  this is filtered


                                    System.out.println("Retrieved " + trainer.getFirstName());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                        Log.d("FIREBASE", "Trainer Search - On Trainer by ID CANCELLED");
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("FIREBASE", "Trainer Search - On Appointment By Time CANCELLED");
                    }
                });

            }
        });

        return view;
    }


    //Replace Fragment method
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.searchOptContainer, fragment);
        transaction.commit();
    }

    //Method for getting the trainers based on the filters
    private void getTrainers(GetTrainersCallback callback) {

        // References to the necessary collections -> Users, Schedule, Trainer raitings
        DatabaseReference trainerRef = FirebaseDatabase.getInstance().getReference().child("users");

        //Pull with no filter
        trainerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap<String, User> trainers = new HashMap<>();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    String userKey = userSnapshot.getKey();
                    trainers.put(userKey, user); //
                    unfilteredTrainers.put(userKey, user);
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

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }


    // Interface for GetTrainers
    public interface GetTrainersCallback {
        void onTrainersReceived(HashMap<String, User> trainersMap); //Chck this - change in trainer details
    }


    //Method for creating the date picker dialog
    private void showDatePickerDialog(TextView tv) {
        // Get the current date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new DatePickerDialog and show it
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year1, int monthOfYear, int dayOfMonth) {
                        // Handle the selected date --- check which one is selected
                        if(tv.getId() == R.id.tvDateStart){
                                //Fix start date in miliseconds
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year1, monthOfYear, dayOfMonth, 0, 0, 0);
                            Log.d("Andrea", "This is the date picked " + calendar.getTime() + " " + calendar.getTime().getTime());
                            initialDate = calendar.getTime().getTime();

                        }else{
                                //fix end date in miliseconds
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year1, monthOfYear, dayOfMonth, 23, 59, 59);
                            Log.d("Andrea", "This is the date picked" + calendar.getTime().getTime());
                            endDate = calendar.getTime().getTime();
                        }
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        tv.setText(selectedDate);
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }




}