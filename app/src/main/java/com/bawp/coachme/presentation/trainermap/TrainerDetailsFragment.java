package com.bawp.coachme.presentation.trainermap;

import android.os.Bundle;

import androidx.annotation.NonNull;
//import androidx.compose.ui.node.LookaheadDelegate;
import androidx.cardview.widget.CardView;
import androidx.compose.ui.unit.Density;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bawp.coachme.R;
import com.bawp.coachme.model.CustomCalendarView;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.model.TrainerSchedule;
import com.bawp.coachme.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TrainerDetailsFragment extends Fragment {

    public static final User TRAINER_TO_DISPLAY = new User();

    private String trainerName;
    private String trainerEmail;
    private double flatPrice;
    private String trainerID;

    CustomCalendarView calendarView;
    //Variables for the trainer info
    Trainer currentTrainer;


    //Map of appointments
    HashMap<String, TrainerSchedule> trainerScheduleHashMap = new HashMap<>();

    HashSet<Date> highlightedDates = new HashSet<>();

    HashMap<String, List<String>> timesForEachDay = new HashMap<>();

    Date[] trainerDates;
    String[] trainerDatesString;

    List<String> hourList = new ArrayList<>();
    public TrainerDetailsFragment() {
        // Required empty public constructor
    }

    public static TrainerDetailsFragment newInstance() {
        TrainerDetailsFragment fragment = new TrainerDetailsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_TRAINER_NAME, name);
//        args.putDouble(ARG_TRAINER_FLATPRICE, flatPrice);
//        args.putString(ARG_TRAINER_ID, trainerID);
//
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            currentTrainer = (Trainer) bundle.getSerializable("TRAINER_TO_DISPLAY");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.trainer_details_fragment, container, false);

        //Things from the layout
        TextView tvName = view.findViewById(R.id.tvTrainerName);
        TextView tvPrice = view.findViewById(R.id.tvPriceHour);
        TextView tvBio = view.findViewById(R.id.tvTrainerBio);
        TextView tvRating = view.findViewById(R.id.tvTrainerRating);

        ImageButton seeMore = view.findViewById(R.id.btnTrainerSeeAppTable);
        LinearLayout calendarLayout = view.findViewById(R.id.calLayout); //ok
        ListView listViewHours = view.findViewById(R.id.lvTimes);
        ImageButton closeBtn = view.findViewById(R.id.btnCloseCard);
        CardView wholeCard = view.findViewById(R.id.cardViewTrainerDetails);
//        calendarView = view.findViewById(R.id.cvDates); //check this

        calendarView = view.findViewById(R.id.cvDates);
        List<Date> disabledDates = new ArrayList<>();
        Date newDate = new Date();
        disabledDates.add(newDate);
        disabledDates.add(new Date());
        calendarView.setDisabledDates(disabledDates);

        Calendar currentCalendar = Calendar.getInstance();
        calendarView.setMinDate(currentCalendar.getTimeInMillis());

        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.add(Calendar.YEAR, 1);
        calendarView.setMaxDate(maxCalendar.getTimeInMillis());
        calendarView.invalidate();


//        calendarView.setTrainerScheduleHashMap(trainerScheduleHashMap);

        //Set the info in card
        tvName.setText(currentTrainer.getFirstName() + " " + currentTrainer.getLastName());
        tvPrice.setText(Double.toString(currentTrainer.getFlatPrice()));


//        getTrainerSchedule();

        //click listener for the button
        seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarLayout.setVisibility(View.VISIBLE);
//                disableDates();
            }
        });

        //on Change listener for the calendar view
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                Log.d("Andrea", "this is the selected day " + year + "-" + month + "-" + dayOfMonth); //this is ok

                List<String> availableTimes = new ArrayList<>();

                Calendar calToSearch = Calendar.getInstance();
                calToSearch.set(year,month,dayOfMonth);
                Date dateToSearch = calToSearch.getTime();

                LocalDateTime dateTime = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    dateTime = LocalDateTime.ofInstant(dateToSearch.toInstant(), ZoneId.systemDefault());
                }
                String dateStr = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    dateStr = dateTime.toLocalDate().toString();
                }

//                System.out.println("This is the date to search " + dateStr);

                 if(timesForEachDay.containsKey(dateStr)){
//                     Log.d("Andrea", "Trainer has slots this day ");
                     //Fill the list of times
                     availableTimes = timesForEachDay.get(dateStr); //ok
                 }

                 //fill the listview with each of the dates
                List<Integer> timesInNumber = new ArrayList<>();
                 for(String s: availableTimes){
                     timesInNumber.add(Integer.parseInt(s.substring(0,1))); //only the hour
                 }

                Collections.sort(timesInNumber); //sorted in number
                for(int s : timesInNumber){
                    hourList.add(Integer.toString(s)); //in sring
                }

                //Create the adapter for the listview
                ArrayAdapter<String> slotsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, hourList );
                listViewHours.setAdapter(slotsAdapter);
            }
        });

        // Click Listener for the ListView Hours
        listViewHours.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedValue = hourList.get(position);
            }
        });

        //Click listener for the close button
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wholeCard.setVisibility(View.GONE);
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

                for (DataSnapshot ds : snapshot.getChildren()) {

                    TrainerSchedule schedule = ds.getValue(TrainerSchedule.class);
//                    Log.d("Andrea", "this is the id " + schedule.getTrainerID());
//                    Log.d("Andrea", "this is the slot" + schedule.getTime().toString());
                    String scheduleKey = ds.getKey();
                    trainerScheduleHashMap.put(scheduleKey, schedule);
                    highlightedDates.add(schedule.getTime()); //Why the null pointer?
                }


                //Convert to an array of Date to loop and find if date is contained
                trainerDates = new Date[highlightedDates.size()];
                trainerDates = highlightedDates.toArray(trainerDates);

                //Fill the map for times to each date = date repeats and is the key
                for (Date date : trainerDates) {

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

                    if (!timesForEachDay.containsKey(dateStr)) { //Key does not exist
                        timesForEachDay.put(dateStr, new ArrayList<>());
                    }
                    timesForEachDay.get(dateStr).add(timeStr);

                }

                // Print the map
                for (Map.Entry<String, List<String>> entry : timesForEachDay.entrySet()) {
                    System.out.println("Date: " + entry.getKey() + ", Times: " + entry.getValue());
                }
                //Print the HashSet
                for(Date d: highlightedDates){

                    System.out.println(d);
                }

                //this HashMap is perfect - Beautiful :)

                //Create string of the date
                Calendar calendar2 = Calendar.getInstance();
                trainerDatesString = new String[trainerDates.length];
                for(int i = 0; i < trainerDates.length; i++){

                    calendar2.setTime(trainerDates[i]); //set to the date in the array
                    int year = calendar2.get(Calendar.YEAR);
                    int month = calendar2.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int day = calendar2.get(Calendar.DAY_OF_MONTH);

//                    System.out.println("Andrea This is from Firebase");
//                    System.out.println("Year: " + year);
//                    System.out.println("Month: " + month);
//                    System.out.println("Day: " + day);

                    trainerDatesString[i] = year+"-"+month+"-"+day;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("Andrea", "Cancelled get schedules");

            }
        });

    }

    //Method for disabling dates
    private void disableDates() {

            //Get the current date
        Calendar currentCalendar = Calendar.getInstance();
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentYear = currentCalendar.get(Calendar.YEAR);

        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.set(currentYear, currentMonth, 1);
        for (int day = 1; day <= dateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); day++) {
            dateCalendar.set(currentYear, currentMonth, day);
            Date date = dateCalendar.getTime();

            //Extract year, motnh and day
            int year  = dateCalendar.get(Calendar.YEAR);
            int month = dateCalendar.get(Calendar.MONTH) + 1;
            int dayC = dateCalendar.get(Calendar.DAY_OF_MONTH);
            String dateToCompare = year+"-"+month+"-"+dayC;

//            Log.d("Andrea", "This is the date of the calendar " + dateCalendar.getTime());
//            Log.d("Andrea", "This is the part of date " + year + "-" + month + " " + dayC );
            // If the HashSet does not contain the date, gray it out and disable it
            for(int j = 0; j < trainerDatesString.length; j++){
//                Log.d("Andrea", "Inside date for comp calendar " + dateToCompare + " trainerDateString " + trainerDatesString[j]);
                if (!trainerDatesString[j].equals(dateToCompare)) {
                    //it gets here but does not set clickable to false
//                    Log.d("Andrea", "Inside date if comp calendar " + dateToCompare + " trainerDateString " + trainerDatesString[j]);
                    long timeInMillis = dateCalendar.getTimeInMillis();
//                    Log.d("Andrea", "Time in mils " + timeInMillis);
                    calendarView.setClickable(false);
                    calendarView.setDateTextAppearance(R.style.CalendarDateGrayedOut);
                }
            }

        }

    }
}