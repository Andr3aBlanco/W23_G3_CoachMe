package com.bawp.coachme.presentation.feedback;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
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

    float newRating = 0;
    String newComment = "";
    OnItemClickListener onItemClickListener; //this is the one to be manipulated


    // position to be used in the onCreate
    int currentPosition = -1;

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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public AppointmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_history_item, parent, false);
        AppointmentHolder holder = new AppointmentHolder(itemView);

        dbHelper = new DBHelper(parent.getContext()); // Check this and change if necessary

        holder.ratingFive.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                newRating = ratingBar.getRating();
                System.out.println("NEW RATING " + newRating);
            }
        });

        holder.starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPosition = holder.getBindingAdapterPosition();
                // If appointment has not been rated yet show the rating card
                if(pastAppointments.get(currentPosition).getRating() == 0 ){

                    holder.ratingCard.setVisibility(View.VISIBLE);
                } else {

                    Toast.makeText(parent.getContext(),"You have already rated this session. ", Toast.LENGTH_SHORT).show();
                }


                System.out.println("This is the current position " + currentPosition);
            }
        });

        holder.btnSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.print("NEW RATING ON SUBMIT " + newRating);
                if(TextUtils.isEmpty(holder.txtComment.getText())){

                    Toast.makeText(parent.getContext(), "Please leave us a comment ", Toast.LENGTH_SHORT).show();

                } else {

                    newComment = holder.txtComment.getText().toString();


                    if(newRating == 0){

                        Toast.makeText(parent.getContext(), "Please choose a rating ", Toast.LENGTH_SHORT).show();

                    } else{

                        // get the current appointment and save the new rating and save to trainer
                        pastAppointments.get(currentPosition).setRating((int)newRating); // nor refreshing
                        pastAppointments.get(currentPosition).setComment(newComment);
                        dbHelper.updateAppointmentRating(pastAppointments.get(currentPosition).getId(),(int)newRating);
                        dbHelper.updateTrainerRating(pastAppointments.get(currentPosition).getTrainerId());

                        holder.ratingOne.setRating(newRating/5);

                        // disappear on submit again
                        holder.ratingCard.setVisibility(View.GONE);

                        setPastAppointments(pastAppointments);



                    }

                }
            }
        });


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentHolder holder, int position) {

        currentTrainer = dbHelper.getTrainerById(pastAppointments.get(position).getTrainerId());
        //Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate = sdf.format(new Date(pastAppointments.get(position).getBookedDate()));

        System.out.println("Rating for " + pastAppointments.get(position).getRating());

        // rated or not yet
        if(pastAppointments.get(position).getRating() == 0) {

            String toDisplay = "Rate your session: <b>" + pastAppointments.get(position).getServiceType() + "</b> with <b>" +
                    currentTrainer.getFirstName() + "</b> on the <b>" + formattedDate + "</b>";

            holder.message.setText(HtmlCompat.fromHtml(toDisplay,  HtmlCompat.FROM_HTML_MODE_LEGACY));


//            System.out.println("rating for position " + position + " is " + pastAppointments.get(position).getRating());

            holder.ratingOne.setRating((float)pastAppointments.get(position).getRating()/5);


        } else {

            String toDisplay = "Your <b>" + pastAppointments.get(position).getServiceType() + "</b> session with <b>" +
                    currentTrainer.getFirstName() + "</b> on the " + formattedDate + " was <b>" + pastAppointments.get(position).getComment() + "</b>";


            holder.message.setText(HtmlCompat.fromHtml(toDisplay,  HtmlCompat.FROM_HTML_MODE_LEGACY));
            // fill the star depending on the value
            holder.ratingOne.setRating((float)pastAppointments.get(position).getRating()/5);
//            Log.d("ANDREA", "This is the rating for appointment in " + position + " " +pastAppointments.get(position).getRating() );
        }


    }

    @Override
    public int getItemCount() {
        return pastAppointments.size();
    }

    public class AppointmentHolder extends RecyclerView.ViewHolder {

        // From the layout
        TextView message;
        TextView trainerDate;
        TextView comment;
        RatingBar ratingOne;
        RatingBar ratingFive;
        CardView ratingCard;

        Button starButton;

        EditText txtComment;
        Button btnSubmitReview;

        public AppointmentHolder(@NonNull View itemView) {
            super(itemView);

            message = itemView.findViewById(R.id.txtTrainerNameAppRV);
//            trainerDate = itemView.findViewById(R.id.txtTrainerNameDate);
//            comment = itemView.findViewById(R.id.txtRatingComment);
            ratingOne = itemView.findViewById(R.id.ratingOne);
            ratingFive = itemView.findViewById(R.id.ratingFive);
            ratingCard = itemView.findViewById(R.id.ratingCard);
            starButton = itemView.findViewById(R.id.btnCoverStar);
            txtComment = itemView.findViewById(R.id.txtAppDateRV); // comment
            btnSubmitReview = itemView.findViewById(R.id.btnSubmitReview);

        }
    }

    public interface OnItemClickListener {
        public void onItemClick(int i);  // where does this come from

    }
}
