package com.bawp.coachme.presentation.trainermap;

import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.bawp.coachme.R;
import com.bawp.coachme.model.TrainerSchedule;
import com.bawp.coachme.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class TrainerDetailsFragment extends Fragment {

    public static final String ARG_TRAINER_NAME = "trainer_name";
    public static final String ARG_TRAINER_EMAIL = "trainer_email";
    public static final String ARG_TRAINER_FLATPRICE = "trainer_price";
    public static final String ARG_TRAINER_ID = "trainer_id";

    private String trainerName;
    private String trainerEmail;
    private double flatPrice;
    private String trainerID;


    //Variables for the trainer info
    User currentTrainer;


    //Map of appointments
    HashMap<String, TrainerSchedule> trainerScheduleHashMap = new HashMap<>();



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
        CalendarView calendar = view.findViewById(R.id.cvDates);



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
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {


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

        scheduleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()){

                    TrainerSchedule schedule = ds.getValue(TrainerSchedule.class);
                    Log.d("Andrea", schedule.getTrainerID());
                    String scheduleKey = ds.getKey();

                    trainerScheduleHashMap.put(scheduleKey,schedule);
            }

        }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("Andrea", "Cancelled get schedules");

            }
            });
//        exQqueryGetTrainer.addOnCompleteListener(new OnCompleteListener() {
//            @Override
//            public void onComplete(@NonNull Task task) {
//
//                if(exQqueryGetTrainer.isSuccessful()){
//                    DataSnapshot dsResult = (DataSnapshot) exQqueryGetTrainer.getResult();
//
//                    for(DataSnapshot ds : dsResult.getChildren()){
//
//                        TrainerSchedule schedule = ds.getValue(TrainerSchedule.class);
//                        Log.d("Andrea", schedule.getTrainerID());
//                        String scheduleKey = ds.getKey();
//
//                        trainerScheduleHashMap.put(scheduleKey,schedule);
//                    }
//                } else{
//                    Exception error = task.getException();
//                    Log.d("TRAINER", "Firebase - get trainer Schedule failed");
//                }
//
//
//            }
//        });


    }


}