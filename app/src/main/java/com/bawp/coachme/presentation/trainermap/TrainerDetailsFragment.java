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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bawp.coachme.R;
import com.bawp.coachme.model.CustomCalendarView;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.model.TrainerSchedule;
import com.bawp.coachme.model.User;
import com.bawp.coachme.utils.DBHelper;
import com.bawp.coachme.utils.UserSingleton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.text.NumberFormat;
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
import java.util.UUID;

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

    List<Long> availApp = new ArrayList<>();
    HashMap<String, List<Integer>> appointmentsMap = new HashMap<>();

    /* For saving into database -> appointment
     * for deleting from schedule */
    int selectedYear = 0;
    int selectedMonth = 0;
    int selectedHour = 0;
    int selectedDay = 0;

    private int cardStatus = 0;


    /* Variables for saving in appointment table
        * */
    String appId;
    long bookedDate;
    long registeredDate;
    String serviceType;
    int status;
    double totalPrice;
    String location;
    String trainerId;
    String customerId;
    String trainerBio;

    DBHelper dbHelper;



    List<Integer> hourList = new ArrayList<>();
    // Adapter
    ArrayAdapter<String> theAdapter;
    TrainerCustomList trainerCustomListAdapter;
    public TrainerDetailsFragment() {
        // Required empty public constructor
    }

    public static TrainerDetailsFragment newInstance() {
        TrainerDetailsFragment fragment = new TrainerDetailsFragment();
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

        Button seeMore = view.findViewById(R.id.btnTrainerSeeAppTable);
        RelativeLayout calendarLayout = view.findViewById(R.id.calLayout); //ok
        ListView listViewHours = view.findViewById(R.id.lvTimes);
        Button closeBtn = view.findViewById(R.id.btnCloseCard);
        CardView wholeCard = view.findViewById(R.id.cardViewTrainerDetails);
        Button addCart = view.findViewById(R.id.btnAddCart);
//        calendarView = view.findViewById(R.id.cvDates); //check this
        Spinner spinServices = view.findViewById(R.id.spinService);


        calendarView = view.findViewById(R.id.cvDates);

        List<Long> disabledDates = new ArrayList<>();
        Date newDate = new Date();
        Long newDateMilli = newDate.getTime();
        disabledDates.add(newDateMilli);
        System.out.println("NEW DATE IN TRAINER DETAILS " + newDateMilli);

        calendarView.setDisabledDates(disabledDates);

        Calendar currentCalendar = Calendar.getInstance();
        calendarView.setMinDate(currentCalendar.getTimeInMillis());

        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.add(Calendar.YEAR, 1);
        calendarView.setMaxDate(maxCalendar.getTimeInMillis());
        calendarView.invalidate();


        dbHelper = new DBHelper(getContext());
//        calendarView.setTrainerScheduleHashMap(trainerScheduleHashMap);
        Locale locate = new Locale("en", "CA");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locate);
        //Set the info in card
        tvName.setText(currentTrainer.getFirstName() + " " + currentTrainer.getLastName());
        tvPrice.setText(formatter.format(currentTrainer.getFlatPrice()));
        tvRating.setText(String.format("%.2f", currentTrainer.getRating()));


        double rating = currentTrainer.getRating();
        availApp =   dbHelper.getTimesByTrainerID(currentTrainer.getId());
        trainerBio = dbHelper.getTrainerBioSummary(currentTrainer.getId());


        tvBio.setText(trainerBio);
        //Create hashmap of date and times
        // Loop through the appointment times in the List
        for (long timeInMillis : availApp) {
            // Create a Calendar object for the appointment time
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeInMillis);

            // Get the year, month, and day of the appointment
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // Add 1 to get the month in range 1-12
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Get the hour of the appointment
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            // Create the key for the HashMap
            String key = String.format("%04d-%02d-%02d", year, month, day); // Format the date as "yyyy-MM-dd"

            // Check if the key is already in the HashMap
            if (appointmentsMap.containsKey(key)) {
                // Add the hour to the value List
               hourList = appointmentsMap.get(key);
                hourList.add(hour);
                appointmentsMap.put(key, hourList);
            } else {
                // Create a new value List with the hour
                 hourList = new ArrayList<>();
                hourList.add(hour);
                appointmentsMap.put(key, hourList);
            }
        }

        // get services
        List<String> currentServices = dbHelper.getServicesByTrainerId(currentTrainer.getId());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, currentServices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinServices.setAdapter(adapter);


        //click listener for the button
        seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cardStatus == 0 ) {
                    availApp =   dbHelper.getTimesByTrainerID(currentTrainer.getId());
                    calendarLayout.setVisibility(View.VISIBLE);
                    cardStatus = 1;
                } else{
                    calendarLayout.setVisibility(View.GONE);
                    cardStatus = 0;
                }
//                disableDates();
            }
        });

        //on Change listener for the calendar view
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Set current and print
                selectedDay = dayOfMonth;
                selectedMonth = month;
                selectedYear = year;
                selectedHour = 100;


                Calendar clickedDate = Calendar.getInstance();
                clickedDate.set(year, month, dayOfMonth, 0, 0, 0);
                clickedDate.set(Calendar.MILLISECOND, 0);

                // Create the key for the HashMap
                String key = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth); // Add 1 to get the month in range 1-12


                // Check if the clicked date is in the available appointment list
                if (appointmentsMap.containsKey(key)) {
                    // Do nothing: the date is available
                    // Get the List of hours for the key
                    hourList = appointmentsMap.get(key); // this is the one to pass
                    Collections.sort(hourList);


                    // Populate the ListView with the hourList values
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, hoursString);
                     trainerCustomListAdapter = new TrainerCustomList(hourList);
                     listViewHours.setAdapter(trainerCustomListAdapter);

                } else {

//                    hoursString = new ArrayList<>();
//                    theAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, hoursString);

                    hourList = new ArrayList<>();
                    trainerCustomListAdapter = new TrainerCustomList(hourList);
                    listViewHours.setAdapter(trainerCustomListAdapter);


                    // Disable click on the date
                    Toast.makeText(getContext(), "This date is not available", Toast.LENGTH_SHORT).show();
//                                holder.calendarView.setDate(holder.calendarView.getDate()); // Reset the selected date
                }
            }

        });
         /// Missing method for disabling the click on dates not available
        // and change the color


        // For list view
        //Moved the color and am pm display to the custom adapter
        listViewHours.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(trainerCustomListAdapter.getSelectedHour() == position ){
                    // If clicked is the same
                    System.out.println("Index == tp selected position ");
                    trainerCustomListAdapter.setSelectedHour(-1);
                    trainerCustomListAdapter.notifyDataSetChanged();
                } else
                {
                    System.out.println("Index != tp selected position ");
                    trainerCustomListAdapter.setSelectedHour(position);
                    trainerCustomListAdapter.notifyDataSetChanged();
                }



                selectedHour = hourList.get(position); // printing date OK;
                System.out.println("For appointment " + selectedYear + "/" + selectedMonth + "/" + selectedDay + "/" + selectedHour);
            }
        });


        // add to cart
        addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(selectedHour == 100){
                    Toast.makeText(getContext(), "Select an appointment to add to Cart", Toast.LENGTH_SHORT).show();

                }else{

                    UUID uuid = UUID.randomUUID();

                    customerId = UserSingleton.getInstance().getUserId();
                    appId = uuid.toString();

                    //Create the booked date
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, 0, 0);
                    bookedDate = calendar.getTimeInMillis();


                    Date today = new Date();
                    calendar.setTime(today);
                    registeredDate = calendar.getTimeInMillis();

                    location = currentTrainer.getAddress();
                    trainerId = currentTrainer.getId();

                    // modifying in layout
                    serviceType = spinServices.getSelectedItem().toString();
                    totalPrice = currentTrainer.getFlatPrice();

                    // create query to delete appointment by time
                    // find it by time
                    long endOfHour = 0;
                    calendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, 59, 59);
                    endOfHour = calendar.getTimeInMillis();

                    dbHelper.removeFromSchedule(trainerId, bookedDate, endOfHour);
                    String deviceToken = UserSingleton.getInstance().getUserDeviceToken();

                    // Create query to add appointment by time
                    dbHelper.addAppToCart(appId, bookedDate, registeredDate, serviceType, 1, totalPrice, location, trainerId, customerId, deviceToken);


                    int currentPosition = trainerCustomListAdapter.getSelectedHour();
                    hourList.remove(currentPosition);
                    trainerCustomListAdapter = new TrainerCustomList(hourList);
                    listViewHours.setAdapter(trainerCustomListAdapter);
                    selectedHour = 100;

                }
            }
        });


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wholeCard.setVisibility(View.GONE);
                System.out.println("TRAINER DETAILS IN MAP Click on close" );
            }
        });

        return view;
    }

}