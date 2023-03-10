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

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.bawp.coachme.HomeFragment;
import com.bawp.coachme.R;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.model.User;
import com.bawp.coachme.presentation.order.OrdersFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


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

    CheckBox crossfit;
    CheckBox yoga;
    CheckBox martials;
    CheckBox pilates;


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



    // New Implementation with Trainer Class
    private HashMap<String, Trainer> trainersFiltered = new HashMap<String, Trainer>();



    public TrainerSearchFragment() {
        // Required empty public constructor
    }


    public static TrainerSearchFragment newInstance() {
        TrainerSearchFragment fragment = new TrainerSearchFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

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

        //Initialize the filtered trainers
        trainersFiltered = new HashMap<>();

        // Get the views from this layout
        rgMapList = view.findViewById(R.id.rgMapListSelector);
        outletMapList = view.findViewById(R.id.searchOptContainer);
        spinSort = view.findViewById(R.id.spinOrderByOptions);
        dateFrom = view.findViewById(R.id.tvDateStart);
        dateTo = view.findViewById(R.id.tvDateEnd);
        search = view.findViewById(R.id.ivSearch);
        crossfit = view.findViewById(R.id.cbCrossfit);
        yoga = view.findViewById(R.id.cbYoga);
        pilates = view.findViewById(R.id.cbPilates);
        martials = view.findViewById(R.id.cbMartials);

        //Initialize datePickers
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Set the initial dates in view
        dateFrom.setText(dayOfMonth + "/" + month + "/" + year);
        dateTo.setText(dayOfMonth + "/" + month + "/" + year + 1);

        //set initial dates
        calendar.set(year, month, dayOfMonth, 0, 0, 0);
        initialDate = calendar.getTime().getTime();
        calendar.set(year + 1, month, dayOfMonth, 23, 59, 59);
        endDate = calendar.getTime().getTime();



        //Cick Listener for the dateTo and dateFrom
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
//        getTrainers(new GetTrainersCallback() {
//            @Override
//            public void onTrainersReceived(HashMap<String, User> trainers) {
//                Log.d("Andrea", "Trainers received in the TrainerSearchFragment");
//
//                //Pass the data to MapSearchFragment
//                TrainerMapFragment childMapFragment = new TrainerMapFragment();
//                Bundle args = new Bundle();
//                args.putSerializable("FILTERED_TRAINERS", filteredTrainers);  //Change thid to the filtered trainers
//                childMapFragment.setArguments(args);
//
//                //At the beginning replace for TrainerMapFragment
//                replaceFragment(childMapFragment);
//            }
//        });

        getTrainers(new GetTrainersCallback() {
            @Override
            public void onTrainersReceived(HashMap<String, Trainer> trainers) {
                Log.d("Andrea", "Trainers received in the TrainerSearchFragment");

                //Pass the data to MapSearchFragment
                TrainerMapFragment childMapFragment = new TrainerMapFragment();
                Bundle args = new Bundle();
                args.putSerializable("FILTERED_TRAINERS", filteredTrainers);  //Change thid to the filtered trainers
                args.putSerializable("TRAINERS", trainersFiltered);
                childMapFragment.setArguments(args);

                //At the beginning replace for TrainerMapFragment
                replaceFragment(childMapFragment);
            }
        });


        // Listener for Displaying Map or List

        rgMapList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                switch (checkedId) {
                    case R.id.rbMapView:
                        // Changing Back to MapView Requires this
                        //Pass the data to MapSearchFragment
                        TrainerMapFragment childMapFragment = new TrainerMapFragment();
                        Bundle args = new Bundle();
                        args.putSerializable("FILTERED_TRAINERS", filteredTrainers);  //how to update this
                        args.putSerializable("TRAINERS", trainersFiltered); //HM class trainer
                        childMapFragment.setArguments(args);

                        replaceFragment(childMapFragment);
                        break;
                    case R.id.rbListView:

                        // Put here the List -> RecyclerView
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
                trainerIDs = new ArrayList<>(); // - HM < TrainerID, List<Appointment> > // only time slots
                ArrayList<String> selectedServices = new ArrayList<>();


                //Create the references
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

                DatabaseReference trainerRef = FirebaseDatabase.getInstance().getReference().child("users");
                DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference().child("trainerSchedules");

                //Query based on date
                Query availAppQuery = scheduleRef
                        .orderByChild("time")
                        .startAt(initialDate)
                        .endAt(endDate);


                if (crossfit.isChecked()) {
                    selectedServices.add("Crossfit");
                    Log.d("Andrea", "Checkbox selected");
                }
                if (yoga.isChecked()) {
                    selectedServices.add("Yoga");
                    Log.d("Andrea", "Checkbox selected");
                }
                if (pilates.isChecked()) {
                    selectedServices.add("Pilates");
                    Log.d("Andrea", "Checkbox selected");
                }
                if (martials.isChecked()) {
                    selectedServices.add("Martials");
                    Log.d("Andrea", "Checkbox selected");
                }


                Log.d("Andrea", "Inside click search  this is the end time " + endDate + "initial date: " + initialDate);
                availAppQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //Iterating over the results from trainerShedule
                        for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                            String trainerId = appointmentSnapshot.child("trainerID").getValue(String.class);
                            if (!trainerIDs.contains(trainerId)) {
                                trainerIDs.add(trainerId); //
                                // Do i need the list of appointments here?  - pulling again in trainer details -- check again

                                //Add available appointments info for each trainer
                                // change from list to HM
                            }
                        }
                        Log.d("Andrea", "Retrieved " + trainerIDs.size() + " trainers");

                        //Create query for trainers with services
                        Query trainerQuery = trainerRef.orderByChild("servicesTypes");
                        //Listener for
                        trainerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                // Iterate through the results of the trainer query
                                for (DataSnapshot trainerSnapshot : snapshot.getChildren()) {
                                    // Check if the trainer has appointments within the desired date range
                                    if (trainerIDs.contains(trainerSnapshot.getKey())) {
                                        // Check if the trainer's "services" field contains at least one of the values in the "services" list
                                     List<String> trainerServices = trainerSnapshot.child("serviceTypes").getValue(new GenericTypeIndicator<List<String>>() {
                                        });

                                        System.out.println("selectedServices " + selectedServices.size());

                                        if (selectedServices.isEmpty()) {
                                            // Add all trainers to the filtered trainers list
                                            User trainer = trainerSnapshot.getValue(User.class);
                                            String key = trainerSnapshot.getKey();
                                            System.out.println("retrieving all trainers");
                                            System.out.println("Retrieved " + trainer.getFirstName());
                                            filteredTrainers.put(key, trainer);
                                        } else {
                                            for (String service : selectedServices) {
                                                if (trainerServices.contains(service)) {
                                                    // Add the trainer to the filtered trainers list
                                                    User trainer = trainerSnapshot.getValue(User.class);
                                                    String key = trainerSnapshot.getKey();
                                                    System.out.println("retrieving with checked box");
                                                    System.out.println("Retrieved " + trainer.getFirstName());
                                                    filteredTrainers.put(key, trainer);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

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
//    public interface GetTrainersCallback {
//        void onTrainersReceived(HashMap<String, User> trainersMap); //Chck this - change in trainer details
//    }

    public interface GetTrainersCallback {
        void onTrainersReceived(HashMap<String, Trainer> trainersMap); //Chck this - change in trainer details
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
                        if (tv.getId() == R.id.tvDateStart) {
                            //Fix start date in miliseconds
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year1, monthOfYear, dayOfMonth, 0, 0, 0);
                            Log.d("Andrea", "This is the date picked " + calendar.getTime() + " " + calendar.getTime().getTime());
                            initialDate = calendar.getTime().getTime();

                        } else {
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


    //Method for getting the trainers No filter - initial load
    // All trainers with appointments available from today to 1 year later
    // Create initial list
    private void getTrainers(GetTrainersCallback callback) {

        HashMap<String, List<Long>> slotsByTrainer = new HashMap<>();
        List<String> trainerIDs = new ArrayList<>();

        // References to the necessary collections -> Users, Schedule, Trainer raitings
        DatabaseReference trainerRef = FirebaseDatabase.getInstance().getReference().child("users");
        DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference().child("trainerSchedules");
        DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference().child("trainerRatings");

        // 1. Get the slots by date
        Query availAppQuery = scheduleRef
                .orderByChild("time")
                .startAt(initialDate)
                .endAt(endDate);

        // 2. Get the trainers from Users
        Query trainersFromUser = trainerRef.orderByChild("role/roleName").equalTo("trainer");


        availAppQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Iterating over the time slots to get the trainerIDs
                for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                    String trainerId = appointmentSnapshot.child("trainerID").getValue(String.class);
                    long slotTime = appointmentSnapshot.child("time").getValue(Long.class);

                    if (slotsByTrainer.containsKey(trainerId)) {
                       slotsByTrainer.get(trainerId).add(slotTime);
                        // Do i need the list of appointments here?  - pulling again in trainer details -- check again

                        //Add available appointments info for each trainer
                        // change from list to HM
                    }else{
                        trainerIDs.add(trainerId); // Add to the list of IDs - remove later
                        List<Long> list = new ArrayList<>();
                        list.add(slotTime);
                        slotsByTrainer.put(trainerId,list);
                    }
                }
                            // Inside listen for the trainer query

                trainersFromUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // Iterate through the results of the trainer query
                        for (DataSnapshot trainerSnapshot : snapshot.getChildren()) {
                            // Check if the trainer has appointments within the desired date range
                            if (slotsByTrainer.containsKey(trainerSnapshot.getKey())) { // HM contains the key
                                // Check if the trainer's "services" field contains at least one of the values in the "services" list
                                List<String> trainerServices = trainerSnapshot.child("serviceTypes").getValue(new GenericTypeIndicator<List<String>>() {
                                });  // This ok because is needed in the creation of trainer
                                double trainerRating = 4.5;
                                    // Add all trainers to the filtered trainers list
                                    User trainer = trainerSnapshot.getValue(User.class);
                                    String key = trainerSnapshot.getKey();
                                    System.out.println("retrieving all trainers for the first time");
                                    System.out.println("Retrieved " + trainer.getFirstName());
                                    filteredTrainers.put(key, trainer);

                                    //Creating object of Trainer Class
                                    Trainer eachTrainer = new Trainer(key, trainer.getFirstName(), trainer.getLastName(),
                                            trainer.getEmail(), trainer.getLatitudeCoord(), trainer.getLongitudeCoord(),
                                            trainer.getRadius(), trainer.getFlatPrice(), trainer.getPhoneNumber(),
                                            trainer.getAddress(), trainer.getRole(),
                                            trainer.getServiceTypes(), slotsByTrainer.get(key),trainerRating);

                                    // Put trainer in the trainersFiltered
                                trainersFiltered.put(key, eachTrainer);

                            }

                        }
                        System.out.println("trainers Trainer in Search " + trainersFiltered.size());
                        // New
//                        callback.onTrainersReceived(filteredTrainers);
                        callback.onTrainersReceived(trainersFiltered);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("FIREBASE", "Error while retrieving from trainerSchedule");
            }
        });












//        trainerRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                HashMap<String, User> trainers = new HashMap<>();
//
//                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                    User user = userSnapshot.getValue(User.class);
//                    String userKey = userSnapshot.getKey();
//                    trainers.put(userKey, user); //
//                    unfilteredTrainers.put(userKey, user);
//                    assert user != null;
//                    Log.d("Andrea", "Retrieved " + user.getFirstName());
//                }
//
//                filteredTrainers = unfilteredTrainers;
//                // New
//                callback.onTrainersReceived(trainers);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.d("Andrea", "onCancelled: " + error.getMessage());
//            }
//        });

    }


}