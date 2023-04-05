package com.bawp.coachme.presentation.trainermap;

import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.utils.DBHelper;
import com.bawp.coachme.utils.UserSingleton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TrainerListFragment extends Fragment {


    // This is for displaying trainers and test logic for recyclerView

    DBHelper dbHelper;  // Call trainers move this to trainersearch
    // Remove up here

     private RecyclerView recyclerView;
     private TrainerListFragment.TrainerViewAdapter trainerListAdapter;
     private static TrainerSearchFragment trainerSearchFragment;

     // List of trainers
    private static List<Trainer> trainerList = new ArrayList<>(); // This is unsorted
    private static HashMap<String, Trainer> unsortTrainers = new HashMap<>();
    private int sortOption = 1;

    private double currentLatitude = 0;
    private double currentLongitude = 0;

    TrainerCustomList trainerCustomListAdapter;
    public TrainerListFragment() {
        // Required empty public constructor
    }

    public static TrainerListFragment newInstance(String param1, String param2) {
        TrainerListFragment fragment = new TrainerListFragment();
        Bundle args = new Bundle();
// GET THE TRAINERS FROM THE TRIANER SEARCH
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trainerList = new ArrayList<>();
        if (getArguments() != null) {
            unsortTrainers = (HashMap<String, Trainer>) getArguments().getSerializable("FILTERED_TRAINERS");
            sortOption = getArguments().getInt("SORTING_OPTION");
            currentLatitude = getArguments().getDouble("LATITUDE");
            currentLongitude = getArguments().getDouble("LONGITUDE");

            for(Trainer trainer: unsortTrainers.values()){
                trainerList.add(trainer); // This is ok
            }

        }
        // Create an instance of your database helper class
        dbHelper = new DBHelper(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.trainer_list_fragment, container, false);
        recyclerView = view.findViewById(R.id.trainer_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//        trainerList = dbHelper.getTrainers(); // chnage this to pass the  parameters
        Log.d("TRAINERLISTFRAGMENT", "Number of trainers: " + trainerList.size());

        //set the adapter
        trainerListAdapter = new TrainerListFragment.TrainerViewAdapter(trainerList, trainerSearchFragment, sortOption, currentLatitude, currentLongitude );
        recyclerView.setAdapter(trainerListAdapter);
        return view;
    }

    //Class for the ViewHolder
    private class TrainerViewHolder extends RecyclerView.ViewHolder{

        // Get the things from the layout
       private TextView tvName;
        private TextView tvPrice;
        private TextView tvBio;
        private TextView tvRating;

        private Button seeMore;
        private Button closeCard;
        private RelativeLayout calendarLayout;
        private  ListView listViewHours;
        private  Button closeBtn;
        private Button addCart;
        private CalendarView calendarView;
        private Spinner spinServices;

        // constructor OK
        public TrainerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public TrainerViewHolder(LayoutInflater inflater, ViewGroup container){
            super(inflater.inflate(R.layout.trainer_details_fragment, container, false));

            // Find the things from the layout TrainerDetails with logic
            tvName = itemView.findViewById(R.id.tvTrainerName);
            tvPrice = itemView.findViewById(R.id.tvPriceHour);
            tvBio = itemView.findViewById(R.id.tvTrainerBio);
            tvRating  = itemView.findViewById(R.id.tvTrainerRating);

            seeMore = itemView.findViewById(R.id.btnTrainerSeeAppTable);
            calendarLayout = itemView.findViewById(R.id.calLayout); //ok
            listViewHours = itemView.findViewById(R.id.lvTimes);
            calendarView = itemView.findViewById(R.id.cvDates);

            closeCard = itemView.findViewById(R.id.btnCloseCard);
            addCart = itemView.findViewById(R.id.btnAddCart);
            spinServices = itemView.findViewById(R.id.spinService);

        }
    }

    public class TrainerViewAdapter extends RecyclerView.Adapter<TrainerListFragment.TrainerViewHolder>{

        //Adapter with new model
        private List<Trainer> unsortedTrainers = new ArrayList<>();
        //Parent fragment
        private TrainerSearchFragment parentFragment;
        private int sortingOpt = 0;
        int cardStatus = 0;
        double yourLatitude = 0;
        double yourLongitude = 0;
        private DBHelper dbHelper;

        /* For saving into database -> appointment
        * for deleting from schedule */
        int selectedYear = 0;
        int selectedMonth = 0;
        int selectedHour = 0;
        int selectedDay = 0;

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

        List<Integer> hourListOnAddCart = new ArrayList<>();

        int currentCard = -1;
//
//        List<Long> availApp = new ArrayList<>();
//        HashMap<String, List<Integer>> appointmentsMap = new HashMap<>();

        /*
        /*
        * Sorting options comes from the filter static parent
        * 1 -> Distance
        * 2 -> Rating
        * 3 -> Price
        * */

        // Constructor OK
        public TrainerViewAdapter(List<Trainer> unsortedTrainers, TrainerSearchFragment parentFragment,  int sortingOpt, double yourLatitude, double yourLongitude) {
            this.unsortedTrainers = unsortedTrainers;
            this.sortingOpt = sortingOpt;
            this.parentFragment = parentFragment; //This is calling the parent - container for both map and tlist
            this.dbHelper = new DBHelper(getContext());
            this.yourLatitude = yourLatitude;
            this.yourLongitude = yourLongitude;
        }

        @NonNull
        @Override
        public TrainerListFragment.TrainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new TrainerListFragment.TrainerViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull TrainerViewHolder holder, int position) {
            // sorting the trainers
//            final List<Integer> hourList = new ArrayList<>();
            if(sortingOpt == 1){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    unsortedTrainers.sort(Comparator.comparingDouble(Trainer::getRating));
                }
            } else if (sortingOpt == 2) {

                TrainerDistanceSorter sorter = new TrainerDistanceSorter();
                unsortedTrainers = sorter.sortByDistance(unsortedTrainers, yourLatitude, yourLongitude);

            } else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    unsortedTrainers.sort(Comparator.comparingDouble(Trainer::getFlatPrice));
                }
            }


            // Fomrater for the price
            Locale locate = new Locale("en", "CA");
            NumberFormat formatter = NumberFormat.getCurrencyInstance(locate);
            double rating = unsortedTrainers.get(position).getRating();
            List<Long> availApp =   dbHelper.getTimesByTrainerID(unsortedTrainers.get(position).getId());


            //Create hashmap of date and times
            HashMap<String, List<Integer>> appointmentsMap = new HashMap<>();
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
                    List<Integer> hourList2 = appointmentsMap.get(key);
                    hourList2.add(hour);
                    appointmentsMap.put(key, hourList2);
                } else {
                    // Create a new value List with the hour
                    List<Integer> hourList2 = new ArrayList<>();
                    hourList2.add(hour);
                    appointmentsMap.put(key, hourList2);
                }
            }


            // Set the things in the layout with the holder
            holder.closeCard.setVisibility(View.INVISIBLE);
            holder.tvName.setText(unsortedTrainers.get(position).getFirstName() + " " + unsortedTrainers.get(position).getLastName()); //Only one for testing
            holder.tvPrice.setText(formatter.format(unsortedTrainers.get(position).getFlatPrice()));
            holder.tvRating.setText(String.format("%.2f", unsortedTrainers.get(position).getRating())); //Ok

            String trainerBio = dbHelper.getTrainerBioSummary(unsortedTrainers.get(position).getId());
            holder.tvBio.setText(trainerBio);


            // for the spinner
            List<String> currentServices = dbHelper.getServicesByTrainerId(unsortedTrainers.get(position).getId());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, currentServices);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            if(position == currentCard ) {
                holder.calendarLayout.setVisibility(View.VISIBLE);

            } else{
                holder.calendarLayout.setVisibility(View.GONE);

            }

            holder.spinServices.setAdapter(adapter);
            //Here goes all the logic
            holder.seeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(currentCard != holder.getBindingAdapterPosition() ) {
                        currentCard = holder.getBindingAdapterPosition();
                        notifyDataSetChanged();
                    } else{
                        currentCard = -1;
                        notifyDataSetChanged();
                    }


                }
            });



            holder.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                    // Set current
                    selectedDay = dayOfMonth;
                    selectedMonth = month;
                    selectedYear = year;
                    selectedHour = 100;

                    List<String> hoursString = new ArrayList<>();


                    Calendar clickedDate = Calendar.getInstance();
                    clickedDate.set(year, month, dayOfMonth, 0, 0, 0);
                    clickedDate.set(Calendar.MILLISECOND, 0);

                    // Create the key for the HashMap
                    String key = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth); // Add 1 to get the month in range 1-12


                    // Check if the clicked date is in the available appointment list
                    if (appointmentsMap.containsKey(key)) {
                        // Get the List of hours for the key
                       List<Integer> hourList = appointmentsMap.get(key);
                        Collections.sort(hourList);

                        trainerCustomListAdapter = new TrainerCustomList(hourList);
                        holder.listViewHours.setAdapter(trainerCustomListAdapter);
                        hourListOnAddCart = hourList;

                    } else {

                        List<Integer> hourList = new ArrayList<>();
                        trainerCustomListAdapter = new TrainerCustomList(hourList);
                        holder.listViewHours.setAdapter(trainerCustomListAdapter);
                        hourListOnAddCart = new ArrayList<>();

                        Toast.makeText(getContext(), "This date is not available", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Click listener for the ListView
            holder.listViewHours.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if(trainerCustomListAdapter.getSelectedHour() == position ){
                        // If clicked is the same
                        System.out.println("Index == tp selected position ");
                        trainerCustomListAdapter.setSelectedHour(-1);
                        trainerCustomListAdapter.notifyDataSetChanged();
                        selectedHour = 100;

                    } else
                    {
                        System.out.println("Index != tp selected position ");
                        trainerCustomListAdapter.setSelectedHour(position);
                        trainerCustomListAdapter.notifyDataSetChanged();


                        String selectedItemText = parent.getItemAtPosition(position).toString();
                        String timeString = selectedItemText.split(" ")[0]; // extract the time part
                        int timeInt = Integer.parseInt(timeString); // parse the time to integer
                        System.out.println("TRAINER LIST click on custom list time: " + timeInt);
                        selectedHour = timeInt;
                    }





                    System.out.println("Time for appointment " + selectedYear + "/" + selectedMonth + "/" + selectedDay + " : " + selectedHour);
                }
            });

            holder.addCart.setOnClickListener((View v) -> {


                if(selectedHour == 100 ){

                    Toast.makeText(getContext(), "Select an appointment to add to Cart", Toast.LENGTH_SHORT).show();
                } else {

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

                    location = unsortedTrainers.get(position).getAddress();
                    trainerId = unsortedTrainers.get(position).getId();
                    serviceType = holder.spinServices.getSelectedItem().toString();
                    totalPrice = unsortedTrainers.get(position).getFlatPrice();

                    // create query to delete appointment by time
                    // find it by time
                    long endOfHour = 0;
                    calendar.set(selectedYear, selectedMonth, selectedDay, selectedHour, 59, 59);
                    endOfHour = calendar.getTimeInMillis();

                    dbHelper.removeFromSchedule(trainerId, bookedDate, endOfHour);
                    String deviceToken = UserSingleton.getInstance().getUserDeviceToken();

                    Log.d("IN TRAINER LIST", "Booked date " + bookedDate + "  YYYY/MM/DD : HH " + selectedYear + "/" + selectedMonth + "/" + selectedDay + " : " + selectedHour);
                    // Create query to add appointment by time
                    dbHelper.addAppToCart(appId, bookedDate, registeredDate, serviceType, 1, totalPrice, location, trainerId, customerId, deviceToken);

                    // call method to update the listHours and generate adapter
                    int currentPosition = trainerCustomListAdapter.getSelectedHour();
                    hourListOnAddCart.remove(currentPosition);
                    trainerCustomListAdapter = new TrainerCustomList(hourListOnAddCart);
                    holder.listViewHours.setAdapter(trainerCustomListAdapter);
                    selectedHour = 100;
                }
            });

        }

        @Override
        public int getItemCount() {
            return unsortedTrainers.size();
        }


    }

    /// To sort the trainers based on distance
    public class TrainerDistanceSorter {

        private static final int EARTH_RADIUS_KM = 6371;

        public List<Trainer> sortByDistance(List<Trainer> trainers, double userLatitude, double userLongitude) {
            Collections.sort(trainers, new Comparator<Trainer>() {
                @Override
                public int compare(Trainer t1, Trainer t2) {
                    double distanceToT1 = calculateDistance(userLatitude, userLongitude, t1.getLatitudeCoord(), t1.getLongitudeCoord());
                    double distanceToT2 = calculateDistance(userLatitude, userLongitude, t2.getLatitudeCoord(), t2.getLongitudeCoord());
                    return Double.compare(distanceToT1, distanceToT2);
                }
            });
            return trainers;
        }

        private double calculateDistance(double userLat, double userLng, double trainerLat, double trainerLng) {
            double dLat = Math.toRadians(trainerLat - userLat);
            double dLng = Math.toRadians(trainerLng - userLng);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(userLat))
                    * Math.cos(Math.toRadians(trainerLat))
                    * Math.sin(dLng / 2) * Math.sin(dLng / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return EARTH_RADIUS_KM * c;
        }
    }

}