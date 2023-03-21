package com.bawp.coachme.presentation.trainermap;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.model.User;

import java.util.ArrayList;
import java.util.List;

public class TrainerAdapter extends RecyclerView.Adapter<TrainerViewHolder> {

    private List<User> theFilteredTrainers = new ArrayList<>();
    private List<Double> theTrainerRatings = new ArrayList<>();

    @NonNull
    @Override
    public TrainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TrainerViewHolder holder, int position) {
        User trainer = theFilteredTrainers.get(position);
        //asign the thing in the holder
        holder.tvName.setText(trainer.getFirstName() + " " + trainer.getLastName());

    }

    @Override
    public int getItemCount() {
        return theFilteredTrainers.size();
    }
}
