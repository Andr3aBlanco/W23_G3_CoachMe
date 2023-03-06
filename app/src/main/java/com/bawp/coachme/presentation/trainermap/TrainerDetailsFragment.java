package com.bawp.coachme.presentation.trainermap;

import android.os.Bundle;

import androidx.annotation.NonNull;
//import androidx.compose.ui.node.LookaheadDelegate;
import androidx.compose.ui.unit.Density;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bawp.coachme.R;
import com.bawp.coachme.model.CustomCalendarView;
import com.bawp.coachme.model.TrainerSchedule;
import com.bawp.coachme.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TrainerDetailsFragment extends Fragment {

    public static final String ARG_TRAINER_NAME = "trainer_name";
    public static final String ARG_TRAINER_EMAIL = "trainer_email";
    public static final String ARG_TRAINER_FLATPRICE = "trainer_price";
    public static final String ARG_TRAINER_ID = "trainer_id";

    private String trainerName;
    private String trainerEmail;
    private double flatPrice;
    private String trainerID;


    CalendarView calendarView;
    //Variables for the trainer info
    User currentTrainer;


    //Map of appointments
    HashMap<String, TrainerSchedule> trainerScheduleHashMap = new HashMap<>();

    HashSet<Date> highlightedDates = new HashSet<>();

    HashMap<String, List<String>> timesForEachDay = new HashMap<>();


    public TrainerDetailsFragment() {
        // Required empty public constructor
    }

    public static TrainerDetailsFragment newInstance(String name, double flatPrice,  String trainerID ) {
        TrainerDetailsFragment fragment = new TrainerDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRAINER_NAME, name);
        args.putDouble(ARG_TRAINER_FLATPRICE, flatPrice);
        args.putString(ARG_TRAINER_ID, trainerID);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trainerName = getArguments().getString(ARG_TRAINER_NAME);
            trainerEmail = getArguments().getString(ARG_TRAINER_EMAIL);
            trainerID = getArguments().getString(ARG_TRAINER_ID);
            flatPrice = getArguments().getDouble(ARG_TRAINER_FLATPRICE);
                            }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.trainer_details_fragment, container, false);

        //Things from the layout
        TextView name = view.findViewById(R.id.tvTrainerName);
        ImageButton seeMore = view.findViewById(R.id.btnTrainerSeeAppTable);
        LinearLayout calendarLayout = view.findViewById(R.id.calLayout); //ok
        calendarView = view.findViewById(R.id.cvDates); //check this


        Calendar currentCalendar = Calendar.getInstance();

        calendarView.setMinDate(currentCalendar.getTimeInMillis());

        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.add(Calendar.YEAR, 1);
        calendarView.setMaxDate(maxCalendar.getTimeInMillis());
        calendarView.invalidate();
//        calendarView.setTrainerScheduleHashMap(trainerScheduleHashMap);


        name.setText(trainerName);

        getTrainerSchedule();





        //click listener for the button
        seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarLayout.setVisibility(View.VISIBLE);
            }
        });

        //on Change listener for the calendar view
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Log.d("Andrea", "changed date");
            }
        });

        return view;
    }

    ///MEthod to get the data for the current trainer
    private void getTrainerSchedule() {

        DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference().child("trainerSchedules");


        Query queryGetTrainer = scheduleRef.orderByChild("trainerID").equalTo(trainerID); //check this
//        Task exQqueryGetTrainer = queryGetTrainer.get();
        //Pull with no filter

        queryGetTrainer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                trainerScheduleHashMap.clear();
                highlightedDates.clear();
                timesForEachDay.clear();

                for(DataSnapshot ds : snapshot.getChildren()) {

                    TrainerSchedule schedule = ds.getValue(TrainerSchedule.class);
                    Log.d("Andrea", "this is the id " + schedule.getTrainerID());
                    Log.d("Andrea", "this is the slot" + schedule.getTime().toString());
                    String scheduleKey = ds.getKey();
                    trainerScheduleHashMap.put(scheduleKey, schedule);
                    highlightedDates.add(schedule.getTime()); //Why the null pointer?
                }


                    //Convert to an array of Date to loop and find if date is contained
                    Date[] trainerDates = new Date[highlightedDates.size()];
                    trainerDates = highlightedDates.toArray(trainerDates);

                    //Fill the map for times to each date = date repeats and is the key
                    for(Date date : trainerDates){

                        LocalDateTime dateTime = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                        }
                        String dateStr = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            dateStr = dateTime.toLocalDate().toString();
                        }
                        String timeStr = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            timeStr = dateTime.toLocalTime().toString();
                        }

                        if (!timesForEachDay.containsKey(dateStr)) {
                            timesForEachDay.put(dateStr, new ArrayList<>());
                        }
                        timesForEachDay.get(dateStr).add(timeStr);

                    }

                    // Print the map
                    for (Map.Entry<String, List<String>> entry : timesForEachDay.entrySet()) {
                        System.out.println("Date: " + entry.getKey() + ", Times: " + entry.getValue());
                    }
                    //this HashMap is perfect - Beautiful :)
        }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("Andrea", "Cancelled get schedules");

            }
            });


    }


}