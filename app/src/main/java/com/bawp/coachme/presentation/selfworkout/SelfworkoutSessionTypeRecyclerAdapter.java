/**
 * Class: SelfworkoutSessionTypeRecyclerAdapter.java
 *
 * Recycler Adapter that handles the display of each SESSION TYPE based
 * on a Selfworkout plan selected (bought) by a user.
 *
 * Each element is going to be represented as a card element, and if the user
 * select one it will go to the next fragment related to the exercise's list fragment.
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */

package com.bawp.coachme.presentation.selfworkout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.SelfWorkoutSession;
import com.bawp.coachme.model.SelfWorkoutSessionType;
import com.bawp.coachme.utils.DBHelper;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class SelfworkoutSessionTypeRecyclerAdapter extends RecyclerView.Adapter<SelfworkoutSessionTypeRecyclerAdapter.SelfworkoutSessionTypeViewHolder>{

    List<SelfWorkoutSessionType> swpSTList;
    SelfWorkoutSession session;
    Context context;
    DBHelper dbHelper;
    SetOnItemClickListener listener;

    public SelfworkoutSessionTypeRecyclerAdapter(List<SelfWorkoutSessionType> swpSTList,SelfWorkoutSession session,Context context,SetOnItemClickListener listener){
        this.swpSTList = swpSTList;
        this.session = session;
        this.context = context;
        this.dbHelper = new DBHelper(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public SelfworkoutSessionTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.selfworkout_session_cardview_layout, parent, false);
        SelfworkoutSessionTypeRecyclerAdapter.SelfworkoutSessionTypeViewHolder holder = new SelfworkoutSessionTypeRecyclerAdapter.SelfworkoutSessionTypeViewHolder(view);

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickItem(holder.getAdapterPosition());
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SelfworkoutSessionTypeViewHolder holder, int position) {
        holder.mTxtViewSessionType.setText(swpSTList.get(position).getSessionType().toUpperCase());
        if(session != null) {
            if (!session.getSelfworkoutSessionType().getId().equals(swpSTList.get(position).getId())) {
                holder.mCardView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.disabled_color));
            }
        }

        //getting image from session type
        FirebaseStorage storage = FirebaseStorage.getInstance();

        //Setting the image of the workout plan
        StorageReference imageRef = storage.getReferenceFromUrl(swpSTList.get(position).getSessionTypeIconURLFirestore());

        //Download image
        Glide.with(context)
                .load(imageRef)
                .into(holder.mImgViewSessionType);
    }

    @Override
    public int getItemCount() {
        return swpSTList.size();
    }

    public class SelfworkoutSessionTypeViewHolder extends RecyclerView.ViewHolder{

        CardView mCardView;
        TextView mTxtViewSessionType;
        ImageView mImgViewSessionType;

        public SelfworkoutSessionTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.card_container_sw_st);
            mTxtViewSessionType = itemView.findViewById(R.id.txtViewSessionType);
            mImgViewSessionType = itemView.findViewById(R.id.imgViewSessionType);
        }
    }

    public interface SetOnItemClickListener{
        public void onClickItem(int i);
    }

}
