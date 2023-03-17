package com.bawp.coachme.presentation.feedback;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bawp.coachme.R;

public class AppHistoryList extends Fragment {

    public AppHistoryList() {
        // Required empty public constructor
    }


    public static AppHistoryList newInstance(String param1, String param2) {
        AppHistoryList fragment = new AppHistoryList();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.appointment_history_recycler_fragment, container, false);
    }
}