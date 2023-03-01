package com.bawp.coachme.presentation.trainersearch;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bawp.coachme.R;
import com.bawp.coachme.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;




public class TrainerSearchFragment extends Fragment implements FragmentFilterTrainer.FilterListener {

    //Imports
    FragmentManager fm;
    FragmentTransaction ft;

    //List of trainers
    List<User> theTrainers; //filtered

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public TrainerSearchFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment TrainerSearchFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static TrainerSearchFragment newInstance(String param1, String param2) {
//        TrainerSearchFragment fragment = new TrainerSearchFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trainer_search, container, false);

        Log.d("ANDREA", "Inside Trainer Search Fragment");

        fm = getParentFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.filtercontainer, new FragmentFilterTrainer());

        //Get the Filter Fragment

        //Get the Trainer List Fragment
        // AB- Pulling trainers data from Firebase
//        FirebaseApp.initializeApp(TrainerSearchFragment.this);

        //Reference to users
        DatabaseReference trainersRef = FirebaseDatabase.getInstance().getReference("users");

        //Query for trainers
        Query trainerQuery = trainersRef.orderByChild("role/id").equalTo(2);

        //Task list
        //add each query to tasks
        //whenAllSuccess

        //Lista de listas getResults(0) o (1) order is important
        //alltask oncomplete listener

        trainerQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for(DataSnapshot trainerSnapshot : snapshot.getChildren()){

                        User trainer = trainerSnapshot.getValue(User.class);

                        //Print

                        assert trainer != null;
                        Log.d("ANDREA", "Value is: " + trainer.getFirstName());
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                    //TODO
            }
        });

        //Filter by type

        return view;
    }

    @Override
    public void onFilterSelected(String option1, String option2) {


    }

    //AB - Method for pulling trainers data from Firebase

}