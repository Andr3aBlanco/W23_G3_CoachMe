package com.bawp.coachme.presentation.trainermap;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrainerListFragment extends Fragment {

private RecyclerView recyclerView;
private List<Trainer> filteredTrainers;

private TrainerAdapter theTrainerAdapter;

    public TrainerListFragment() {
        // Required empty public constructor
    }

    public static TrainerListFragment newInstance(String param1, String param2) {
        TrainerListFragment fragment = new TrainerListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filteredTrainers = (List<Trainer>) getArguments().getSerializable("FILTEREDTRAINERS"); //Make sure of creating this hashmap in Trainer Search
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.trainer_list_fragment, container, false);

        recyclerView = view.findViewById(R.id.rvTrainerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adapter for the recycler
        theTrainerAdapter = new TrainerAdapter(filteredTrainers); //cange the class in the adapter
        recyclerView.setAdapter(theTrainerAdapter);

        return view;
    }
}