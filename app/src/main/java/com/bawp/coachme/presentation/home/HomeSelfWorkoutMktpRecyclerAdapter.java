package com.bawp.coachme.presentation.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.SelfWorkoutPlan;
import com.bawp.coachme.utils.DBHelper;
import com.bawp.coachme.utils.UserSingleton;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HomeSelfWorkoutMktpRecyclerAdapter extends RecyclerView.Adapter<HomeSelfWorkoutMktpRecyclerAdapter.HomeSelfWorkoutMktpHolder>{

    Context context;
    List<SelfWorkoutPlan> selfWorkoutPlans;
    DBHelper dbHelper;
    SetOnItemClickListener listener;

    public HomeSelfWorkoutMktpRecyclerAdapter(List<SelfWorkoutPlan> selfWorkoutPlans, Context context){
        this.selfWorkoutPlans = selfWorkoutPlans;
        this.context = context;
        this.dbHelper = new DBHelper(context);
    }

    public SetOnItemClickListener getListener() {
        return listener;
    }

    public void setListener(SetOnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeSelfWorkoutMktpHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.selfworkout_marketplace_cardview_layout, parent, false);
        HomeSelfWorkoutMktpHolder holder = new HomeSelfWorkoutMktpHolder(view);

        holder.mBtnAddWorkoutToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Cart");
                builder.setMessage("Are you sure you want to add the Workout Plan to your cart?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        listener.onItemClick(holder.getAdapterPosition());
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeSelfWorkoutMktpHolder holder, int position) {
        if (position != -1){
            String title = selfWorkoutPlans.get(position).getTitle();
            String description = selfWorkoutPlans.get(position).getDescription();
            String mainGoal = selfWorkoutPlans.get(position).getMainGoals();
            String duration = selfWorkoutPlans.get(position).getDuration();
            String level = selfWorkoutPlans.get(position).getLevel();
            int daysPerWeek = selfWorkoutPlans.get(position).getDaysPerWeek();

            //Designing the message in the description slot
            String finalDescription =
                    "Description:\n"+ description +"\n"+
                            "Main Goals:\n"+mainGoal + "\n"+
                            "Duration:\n"+ duration + " ( "+daysPerWeek+" per week)\n";

            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
            String currencyTotalPrice = formatter.format(selfWorkoutPlans.get(position).getPlanPrice());

            holder.mTxtViewMktWorkoutTitle.setText(title);
            holder.mTxtViewMktWorkoutDescription.setText(finalDescription);
            holder.mTxtViewMktWorkoutPrice.setText(currencyTotalPrice);
            holder.mTxtViewMktWorkoutLevel.setText(level);

            switch (level){
                case "Easy":
                    holder.mTxtViewMktWorkoutLevel.setTextColor(ContextCompat.getColor(context,R.color.easy_workout_color));
                    break;
                case "Medium":
                    holder.mTxtViewMktWorkoutLevel.setTextColor(ContextCompat.getColor(context,R.color.medium_workout_color));
                    break;
                case "Intense":
                    holder.mTxtViewMktWorkoutLevel.setTextColor(ContextCompat.getColor(context,R.color.intense_workout_color));
                    break;
                default:
                    break;
            }

            //Downloading the logo from firebase storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            String imageURL = selfWorkoutPlans.get(position).getPosterUrlFirestore();
            StorageReference imageRef = storage.getReferenceFromUrl(imageURL);
            Glide.with(context)
                    .load(imageRef)
                    .into(holder.mImgViewMktWorkoutLogo);
        }
    }

    @Override
    public int getItemCount() {
        return selfWorkoutPlans.size();
    }

    public class HomeSelfWorkoutMktpHolder extends RecyclerView.ViewHolder{

        CardView mCardView;
        TextView mTxtViewMktWorkoutTitle;
        TextView mTxtViewMktWorkoutLevel;
        TextView mTxtViewMktWorkoutPrice;
        TextView mTxtViewMktWorkoutDescription;
        ImageView mImgViewMktWorkoutLogo;
        Button mBtnAddWorkoutToCart;

        public HomeSelfWorkoutMktpHolder(@NonNull View itemView) {
            super(itemView);
            mTxtViewMktWorkoutTitle = itemView.findViewById(R.id.txtViewMktWorkoutTitle);
            mTxtViewMktWorkoutLevel = itemView.findViewById(R.id.txtViewMktWorkoutLevel);
            mTxtViewMktWorkoutPrice = itemView.findViewById(R.id.txtViewMktWorkoutPrice);
            mTxtViewMktWorkoutDescription = itemView.findViewById(R.id.txtViewMktWorkoutDescription);
            mImgViewMktWorkoutLogo = itemView.findViewById(R.id.imgViewMktWorkoutLogo);
            mBtnAddWorkoutToCart = itemView.findViewById(R.id.btnAddWorkoutToCart);
        }
    }

    public interface SetOnItemClickListener{
        public void onItemClick(int i);
    }

}
