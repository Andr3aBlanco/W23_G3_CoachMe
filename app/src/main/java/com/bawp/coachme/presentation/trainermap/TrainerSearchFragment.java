package com.bawp.coachme.presentation.trainermap;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.model.User;
import com.bawp.coachme.presentation.order.OrdersFragment;
import com.bawp.coachme.utils.DBHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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

/**
* Class TrainerSearchFragment.java
 *
 * Fragment that contains the logic to retrieve the trainers
 * and pass them to TrainerMapFragment or to the TrainerListFragment
 * It also get the options from the search filter and passes it to
 * the TrainerListFragment
 *
 * @author Andrea Blanco
 */
public class TrainerSearchFragment extends Fragment implements LocationListener {

//
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final double DEFAULT_LATITUDE = 49.2586;
    private static final double DEFAULT_LONGITUDE = -123.0947;


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


    Button search;

    private DatePickerDialog datePickerDialog;

    //Variables for the search in firebase
    long initialDate;
    long endDate;
    List<String> selectedServices = new ArrayList<>();

    List<String> trainerIDs = new ArrayList<>();

    //Arrays to store filter results
//    private HashMap<String, Trainer> unfilteredTrainers = new HashMap<>();

    private List<Trainer> filteredTrainersList = new ArrayList<>();
    private HashMap<String, Trainer> filteredTrainersHM = new HashMap<>();

//    Fragment fragmentMapList = new TrainerMapFragment();

    private LocationManager locationManager;
    private String provider;
    private double latitude;
    private double longitude;

    //
    int sortingOption = 1;
    String selectedValue = "";
    //
    DBHelper dbHelper;

    public TrainerSearchFragment() {
        // Required empty public constructor
    }


    public static TrainerSearchFragment newInstance(String param1, String param2) {
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

        dbHelper = new DBHelper(getContext());

    }


    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.trainer_search_fragment, container, false);

        //Using Fused Location Provider Client to get the Current Location
        FusedLocationProviderClient fusedLocationClient;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }else{
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    //getting latitude and longitude
                    if (location != null){
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }else{
                        latitude = DEFAULT_LATITUDE;
                        longitude = DEFAULT_LONGITUDE;
                    }

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

                    dateFrom.setText(dayOfMonth + "/" + month + "/" + year);
                    dateTo.setText(dayOfMonth + "/" + month + "/" + (year  + 1));

                    //set initial dates
                    calendar.set(year, month, dayOfMonth, 0, 0, 0);
                    initialDate = calendar.getTime().getTime();
                    calendar.set(year + 1, month, dayOfMonth, 23, 59, 59);
                    endDate = calendar.getTime().getTime();

                    System.out.println("INSIDE THE TRAINER SEARCH from " + initialDate + "  to: " + endDate);
                    // Get the first round of trainers with list empty and current day today + 1 year
                    filteredTrainersList = dbHelper.getTrainersByServicesAndDate(selectedServices, initialDate, endDate);

                    // create the hashmap -> pass to map
                    for(Trainer trainer : filteredTrainersList){

                        filteredTrainersHM.put(trainer.getId(), trainer);
                    }

                    System.out.println("Total trainers found " + filteredTrainersHM.size());

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

                    // In the first load -> map selected
                    System.out.println("RIGHT BEFORE GENERATING MAP FOR THE FIRST TIME latitude longitude " + latitude + " " + longitude);
                    //Pass the data to MapSearchFragment
                    TrainerMapFragment childMapFragment = new TrainerMapFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("FILTERED_TRAINERS", filteredTrainersHM);  //how to update this
                    args.putDouble("LATITUDE",latitude);
                    args.putDouble("LONGITUDE",longitude);
                    childMapFragment.setArguments(args);

                    //At the beginning replace for TrainerMapFragment
                    replaceFragment(childMapFragment);

                    // Listener for the spinner

                    spinSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            selectedValue = parent.getItemAtPosition(position).toString();

                            switch (selectedValue){
                                case "Rating":
                                    sortingOption = 1;
                                    break;

                                case "Distance":
                                    sortingOption = 2;
                                    break;

                                case "Price":
                                    sortingOption = 3;
                                    break;
                                default:
                                    sortingOption = 1;
                                    break;
                            }

                            // If in list View generate the new view of the List parring the option
                            // Get the selected raadio button  --> create new fragment with the new filtering options
                            int selectedRb = rgMapList.getCheckedRadioButtonId();

                            switch(selectedRb){
                                case R.id.rbListView:
                                    //Pass the data to MapSearchFragment
                                    TrainerListFragment childMapFragment2 = new TrainerListFragment();
                                    Bundle args2 = new Bundle();
                                    args2.putSerializable("FILTERED_TRAINERS", filteredTrainersHM);  //how to update this
                                    args2.putInt("SORTING_OPTION", sortingOption);
                                    args2.putDouble("LATITUDE", latitude);
                                    args2.putDouble("LONGITUDE",longitude);
                                    childMapFragment2.setArguments(args2);

                                    //At the beginning replace for TrainerMapFragment
                                    replaceFragment(childMapFragment2);

                                    break;

                                case R.id.rbMapView:
                                    //Do nothing
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            sortingOption = 1;
                        }
                    });

                    rgMapList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                            switch (checkedId) {
                                case R.id.rbMapView:
                                    //Pass the data to MapSearchFragment
                                    TrainerMapFragment childMapFragment = new TrainerMapFragment();
                                    Bundle args = new Bundle();
                                    args.putSerializable("FILTERED_TRAINERS", filteredTrainersHM);  //how to update this
                                    args.putDouble("LONGITUDE", longitude);
                                    args.putDouble("LATITUDE", latitude);
                                    //sort before pass
                                    childMapFragment.setArguments(args);

                                    //At the beginning replace for TrainerMapFragment
                                    replaceFragment(childMapFragment);
                                    break;
                                case R.id.rbListView:

                                    //Pass the data to MapSearchFragment
                                    TrainerListFragment childMapFragment2 = new TrainerListFragment();
                                    Bundle args2 = new Bundle();
                                    args2.putSerializable("FILTERED_TRAINERS", filteredTrainersHM);  //how to update this
                                    args2.putInt("SORTING_OPTION", sortingOption);
                                    childMapFragment2.setArguments(args2);

                                    //At the beginning replace for TrainerMapFragment
                                    replaceFragment(childMapFragment2);
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

                            selectedServices = new ArrayList<>();
                            // Reset the filtered list
                            filteredTrainersHM = new HashMap<>();

                            if (crossfit.isChecked()) {
                                selectedServices.add("Crossfit");
                            }
                            if (yoga.isChecked()) {
                                selectedServices.add("Yoga");
                            }
                            if (pilates.isChecked()) {
                                selectedServices.add("Pilates");
                            }
                            if (martials.isChecked()) {
                                selectedServices.add("Martials");
                            }

                            // On search - dates and services migh change or not
                            filteredTrainersList = dbHelper.getTrainersByServicesAndDate(selectedServices, initialDate, endDate); // Filter ok

                            // create the hashmap -> pass to map
                            for(Trainer trainer : filteredTrainersList){

                                filteredTrainersHM.put(trainer.getId(), trainer);
                            }


                            // Get the selected raadio button  --> create new fragment with the new filtering options
                            int selectedRb = rgMapList.getCheckedRadioButtonId();

                            switch(selectedRb){
                                case R.id.rbListView:
                                    //Pass the data to MapSearchFragment
                                    TrainerListFragment childMapFragment2 = new TrainerListFragment();
                                    Bundle args2 = new Bundle();
                                    args2.putSerializable("FILTERED_TRAINERS", filteredTrainersHM);  //how to update this
                                    args2.putInt("SORTING_OPTION", sortingOption);
                                    childMapFragment2.setArguments(args2);

                                    //At the beginning replace for TrainerMapFragment
                                    replaceFragment(childMapFragment2);

                                    break;

                                case R.id.rbMapView:

                                    TrainerMapFragment childMapFragment = new TrainerMapFragment();
                                    Bundle args = new Bundle();
                                    args.putSerializable("FILTERED_TRAINERS", filteredTrainersHM);  //how to update this
                                    args.putDouble("LONGITUDE", longitude);
                                    args.putDouble("LATITUDE", latitude);
                                    //sort before pass
                                    childMapFragment.setArguments(args);

                                    //At the beginning replace for TrainerMapFragment
                                    replaceFragment(childMapFragment);


                                    // TO HERE

                                    //sort before pass
                                    childMapFragment.setArguments(args);

                                    //At the beginning replace for TrainerMapFragment
                                    replaceFragment(childMapFragment);


                                    break;
                            }
                        }
                    });
                }
            });

        }


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
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                Toast.makeText(getContext(),"LOCATION SERVICES : OUT OF SERVICE ", Toast.LENGTH_SHORT ).show();
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Toast.makeText(getContext(),"LOCATION SERVICES : TEMPORARILY UNAVAILABLE ", Toast.LENGTH_SHORT ).show();
                break;
            case LocationProvider.AVAILABLE:
                Toast.makeText(getContext(),"LOCATION SERVICES : AVAILABLE ", Toast.LENGTH_SHORT ).show();
                break;
        }
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
                            initialDate = calendar.getTime().getTime();

                        } else {
                            //fix end date in miliseconds
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year1, monthOfYear, dayOfMonth, 23, 59, 59);
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