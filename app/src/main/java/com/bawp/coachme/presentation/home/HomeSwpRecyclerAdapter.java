package com.bawp.coachme.presentation.home;

/**
 * Class: HomeSwpRecyclerAdapter.java
 *
 * Recycler View Adapter that will handle all the generation of each current
 * self-workout plan a user has purchased.
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.presentation.selfworkout.SelfworkoutFragment;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class HomeSwpRecyclerAdapter extends RecyclerView.Adapter<HomeSwpRecyclerAdapter.HomeSwpViewHolder>{

    Context context;
    List<SelfWorkoutPlanByUser> selfWorkoutPlanByUsers;
    SetOnItemClickListener listener;

    public HomeSwpRecyclerAdapter(List<SelfWorkoutPlanByUser> selfWorkoutPlanByUsers, Context context, SetOnItemClickListener listener){
        this.selfWorkoutPlanByUsers = selfWorkoutPlanByUsers;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeSwpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.home_swp_cardview_layout, parent, false);
        HomeSwpViewHolder holder = new HomeSwpViewHolder(view);

        holder.mBtnCheckSelfWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.getAdapterPosition());
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeSwpViewHolder holder, int position) {
        String title = selfWorkoutPlanByUsers.get(position).getSelfworkoutplan().getTitle();
        String mainGoal = selfWorkoutPlanByUsers.get(position).getSelfworkoutplan().getMainGoals();
        holder.mTxtViewProductTitle.setText(title);
        holder.mTxtViewProductDetail.setText(mainGoal);

        //Downloading the logo from firebase storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String imageURL = selfWorkoutPlanByUsers.get(position).getSelfworkoutplan().getPosterUrlFirestore();
        StorageReference imageRef = storage.getReferenceFromUrl(imageURL);
        Glide.with(context)
                .load(imageRef)
                .into(holder.mImgViewSelfWorkoutLogo);
    }

    @Override
    public int getItemCount() {
        return selfWorkoutPlanByUsers.size();
    }

    public class HomeSwpViewHolder extends RecyclerView.ViewHolder{

        CardView mCardView;
        TextView mTxtViewProductTitle;
        TextView mTxtViewProductDetail;
        ImageView mImgViewSelfWorkoutLogo;
        ImageButton mBtnCheckSelfWorkout;

        public HomeSwpViewHolder(@NonNull View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.home_swp_card_container);
            mTxtViewProductTitle = itemView.findViewById(R.id.txtViewProductTitle);
            mTxtViewProductDetail = itemView.findViewById(R.id.txtViewProductDetail);
            mImgViewSelfWorkoutLogo = itemView.findViewById(R.id.imgViewSelfWorkoutLogo);
            mBtnCheckSelfWorkout = itemView.findViewById(R.id.btnCheckSelfWorkout);
        }
    }

    public interface SetOnItemClickListener{
        public void onItemClick(int i);
    }

    public SetOnItemClickListener getListener() {
        return listener;
    }

    public void setListener(SetOnItemClickListener listener) {
        this.listener = listener;
    }
}
