package com.bawp.coachme.presentation.feedback;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.model.Trainer;
import com.bawp.coachme.utils.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AppRecyclerAdapter extends RecyclerView.Adapter<AppRecyclerAdapter.AppointmentHolder> {

    List<Appointment> pastAppointments = new ArrayList<>();
    int clickedIndex = -1;


    // Aux variables to get from the other tables
    String TrainerFullName = "";
    DBHelper dbHelper;
    Trainer currentTrainer;
    OnItemClickListener onItemClickListener;


    public AppRecyclerAdapter(List<Appointment> pastAppointments, OnItemClickListener onItemClickListener) {
        this.pastAppointments = pastAppointments;
        this.onItemClickListener = onItemClickListener;
    }

    public AppRecyclerAdapter(List<Appointment> pastAppointments) {
        this.pastAppointments = pastAppointments;
    }


    // setters and getters


    public List<Appointment> getPastAppointments() {
        return pastAppointments;
    }

    public void setPastAppointments(List<Appointment> pastAppointments) {
        this.pastAppointments = pastAppointments;
        notifyDataSetChanged();
    }

    public int getClickedIndex() {
        return clickedIndex;
    }

    public void setClickedIndex(int clickedIndex) {
        this.clickedIndex = clickedIndex;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_history_item, parent, false);
        AppointmentHolder holder = new AppointmentHolder(itemView);

        // Get the current adapter position
        int position = holder.getBindingAdapterPosition();
        System.out.println(position);
        dbHelper = new DBHelper(parent.getContext()); // Check this and change if necessary

        // click listener to update the selected index
        holder.starRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                holder.ratingCard.setVisibility(View.VISIBLE);
                System.out.println("CLICKED ONE STAR");
            }
        });

        // Set the click listener on the five-star RatingBar
//        holder.barRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//
//
//                float newRating = ratingBar.getRating();
//                System.out.println("NEW RATING " + newRating);
//                // get the current appointment and save the new rating and save to trainer
//                pastAppointments.get(position).setRating((int)newRating);
//
//                dbHelper.updateTrainerRating(pastAppointments.get(position).getTrainerId());
//
//                holder.starRating.setRating(rating);
//                holder.barRating.setVisibility(View.GONE);
//
//                notifyDataSetChanged();
//            }
//        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentHolder holder, int position) {

        int positionH = holder.getBindingAdapterPosition();
        currentTrainer = dbHelper.getTrainerById(pastAppointments.get(position).getTrainerId());
        //Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate = sdf.format(new Date(pastAppointments.get(position).getBookedDate()));

        System.out.println("Rating for " + pastAppointments.get(position).getRating());
        // rated or not yet


        if(pastAppointments.get(position).getRating() == 0) {

            String toDisplay = "Rate your " + pastAppointments.get(position).getServiceType() + " session with " +
                    currentTrainer.getFirstName() + " on the " + formattedDate;

            holder.trainerName.setText(toDisplay);


        } else {

            String toDisplay = "Your " + pastAppointments.get(position).getServiceType() + "session with " +
                    currentTrainer.getFirstName() + " on the " + formattedDate;



        }


        // no access to adapter position
        holder.barRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {


                float newRating = ratingBar.getRating();
                newRating = Math.round(newRating);
                System.out.println("NEW RATING " + newRating);
                // get the current appointment and save the new rating and save to trainer
                pastAppointments.get(positionH).setRating((int)newRating);
                dbHelper.updateAppointmentRating(pastAppointments.get(positionH).getId(),(int)newRating);
                dbHelper.updateTrainerRating(pastAppointments.get(positionH).getTrainerId());

                holder.starRating.setRating(rating);
                holder.ratingCard.setVisibility(View.GONE);

                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return pastAppointments.size();
    }

    public class AppointmentHolder extends RecyclerView.ViewHolder {

        // From the layout
        TextView trainerName;
        TextView appDate;
        RatingBar starRating;
        RatingBar barRating;
        CardView ratingCard;

        public AppointmentHolder(@NonNull View itemView) {
            super(itemView);

            trainerName = itemView.findViewById(R.id.txtTrainerNameAppRV);
            appDate = itemView.findViewById(R.id.txtAppDateRV);
            starRating = itemView.findViewById(R.id.appRatingStar);
            barRating = itemView.findViewById(R.id.ratingBar);
            ratingCard = itemView.findViewById(R.id.ratingCard);

        }
    }

    public interface OnItemClickListener {
        public void onItemClick(int i);

    }
}
