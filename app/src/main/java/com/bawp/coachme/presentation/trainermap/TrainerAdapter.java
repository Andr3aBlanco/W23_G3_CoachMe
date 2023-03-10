package com.bawp.coachme.presentation.trainermap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrainerAdapter extends RecyclerView.Adapter<TrainerViewHolder> {

    private List<Trainer> sortedFilteredTrainers;

    public TrainerAdapter(List<Trainer> sortedFilteredTrainers) {
        this.sortedFilteredTrainers = sortedFilteredTrainers;
    }

    @NonNull
    @Override
    public TrainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Layout from TrainerDetailsFragment used in the map

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trainer_details_fragment, parent, false);

        return new TrainerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainerViewHolder holder, int position) {
        Trainer trainer = sortedFilteredTrainers.get(position);
        // Bind to the holder
        holder.bind(trainer);

        // create the fragment


    }

    @Override
    public int getItemCount() {
        return sortedFilteredTrainers.size();
    }
}
