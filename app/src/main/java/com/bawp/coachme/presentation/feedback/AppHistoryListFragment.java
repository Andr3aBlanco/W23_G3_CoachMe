package com.bawp.coachme.presentation.feedback;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Appointment;

import java.util.ArrayList;
import java.util.List;

public class AppHistoryListFragment extends Fragment {

    List<Appointment> prevAppForThisUser = new ArrayList<>();
    RecyclerView prevAppRecycler;

    public AppHistoryListFragment() {
        // Required empty public constructor
    }


    public static AppHistoryListFragment newInstance(String param1, String param2) {
        AppHistoryListFragment fragment = new AppHistoryListFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           List<Parcelable> prevApp = getArguments().getParcelableArrayList("PREVAPPLIST");
            for (Parcelable parcelable : prevApp) {
                prevAppForThisUser.add((Appointment) parcelable);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.appointment_history_recycler_fragment, container, false); // OK
        prevAppRecycler = view.findViewById(R.id.appHistoryRecyclerView);
        // Instantiate the custom adapter
        GridLayoutManager gm = new GridLayoutManager(getContext(), 1);
        prevAppRecycler.setLayoutManager(gm);

        // Adapter
        AppRecyclerAdapter theAdapter = new AppRecyclerAdapter(prevAppForThisUser);
        prevAppRecycler.setAdapter(theAdapter);


        return view;
    }
}