package com.bawp.coachme.presentation.trainermap;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrainerDetailsFragment extends Fragment {

    public static final String ARG_TRAINER_NAME = "trainer_name";
    public static final String ARG_TRAINER_EMAIL = "trainer_email";

    private String trainerName;
    private String trainerEmail;

    //Variables for the trainer info
    User currentTrainer;

    public TrainerDetailsFragment() {
        // Required empty public constructor
    }

    public static TrainerDetailsFragment newInstance(String name, String email) {
        TrainerDetailsFragment fragment = new TrainerDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRAINER_NAME, name);
        args.putString(ARG_TRAINER_EMAIL, email);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trainerName = getArguments().getString(ARG_TRAINER_NAME);
            trainerEmail = getArguments().getString(ARG_TRAINER_EMAIL);
                            }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_trainer_details, container, false);

        TextView name = view.findViewById(R.id.tvTrainerName);

        name.setText(trainerName);

        return view;
    }

    ///MEthod to get the data for the current trainer
    private void getTrainerAppointment(TrainerMapFragment.GetTrainersCallback callback) {

//        List<User> trainers = new ArrayList<>();
        // Change to data from Firebase
        DatabaseReference trainerRef = FirebaseDatabase.getInstance().getReference().child("users");
        DatabaseReference appRef = FirebaseDatabase.getInstance().getReference().child("appointments");

        List<Task<DataSnapshot>> tasks = new ArrayList<>();

        Query queryGetTrainer = trainerRef.orderByChild("email").equalTo(trainerEmail); //check this
        Query queryGetAppointments = appRef.orderByChild("email").equalTo(trainerEmail);

        tasks.add(queryGetTrainer.get());
        tasks.add(queryGetAppointments.get());

        Task<List<DataSnapshot>> allTasks = Tasks.whenAllSuccess(tasks);

        //Pull with no filter
        trainerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                List<User> trainers = new ArrayList<>();
                HashMap<String, User> trainers = new HashMap<>();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    String userKey = userSnapshot.getKey();
                    trainers.put(userKey, user);
                    assert user != null;
                    Log.d("Andrea", "Retrived " + trainers.get(userKey).getFirstName());
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


}