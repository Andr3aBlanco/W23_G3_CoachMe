package com.bawp.coachme.presentation.trainermap;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bawp.coachme.R;

public class TrainerDetailsFragment extends Fragment {

    public static final String ARG_TRAINER_NAME = "trainer_name";

    private String trainerName;

    public TrainerDetailsFragment() {
        // Required empty public constructor
    }

    public static TrainerDetailsFragment newInstance(String name) {
        TrainerDetailsFragment fragment = new TrainerDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRAINER_NAME, name);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trainerName = getArguments().getString(ARG_TRAINER_NAME);
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
}