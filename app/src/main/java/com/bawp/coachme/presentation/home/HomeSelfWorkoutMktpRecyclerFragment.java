package com.bawp.coachme.presentation.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.coachme.R;
import com.bawp.coachme.model.SelfWorkoutPlan;
import com.bawp.coachme.model.SelfWorkoutPlanByUser;
import com.bawp.coachme.presentation.selfworkout.SelfworkoutFragment;
import com.bawp.coachme.utils.DBHelper;
import com.bawp.coachme.utils.UserSingleton;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HomeSelfWorkoutMktpRecyclerFragment extends Fragment {

    private static List<SelfWorkoutPlan> selfWorkoutPlanList;
    private HomeSelfWorkoutMktpRecyclerFragment.RecyclerViewAdapter marketplaceAdapter;
    private RecyclerView recyclerView;
    private static HomeSelfWorkoutMarketplaceFragment mktpFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        marketplaceAdapter = new HomeSelfWorkoutMktpRecyclerFragment.RecyclerViewAdapter(selfWorkoutPlanList,mktpFragment);
        recyclerView.setAdapter(marketplaceAdapter);
        return view;
    }

    public static Fragment newInstance( List<SelfWorkoutPlan> swpList, HomeSelfWorkoutMarketplaceFragment parentFragment) {
        selfWorkoutPlanList = swpList;
        mktpFragment = parentFragment;
        return new HomeSelfWorkoutMktpRecyclerFragment();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder{

        CardView mCardView;
        TextView mTxtViewMktWorkoutTitle;
        TextView mTxtViewMktWorkoutLevel;
        TextView mTxtViewMktWorkoutPrice;
        TextView mTxtViewMktWorkoutDescription;
        ImageView mImgViewMktWorkoutLogo;
        Button mBtnAddWorkoutToCart;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container){
            super(inflater.inflate(R.layout.selfworkout_marketplace_cardview_layout, container, false));
            mCardView = itemView.findViewById(R.id.card_container_sw_marketplace);

            mTxtViewMktWorkoutTitle = itemView.findViewById(R.id.txtViewMktWorkoutTitle);
            mTxtViewMktWorkoutLevel = itemView.findViewById(R.id.txtViewMktWorkoutLevel);
            mTxtViewMktWorkoutPrice = itemView.findViewById(R.id.txtViewMktWorkoutPrice);
            mTxtViewMktWorkoutDescription = itemView.findViewById(R.id.txtViewMktWorkoutDescription);
            mImgViewMktWorkoutLogo = itemView.findViewById(R.id.imgViewMktWorkoutLogo);
            mBtnAddWorkoutToCart = itemView.findViewById(R.id.btnAddWorkoutToCart);

        }
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<HomeSelfWorkoutMktpRecyclerFragment.RecyclerViewHolder>{

        private List<SelfWorkoutPlan> selfWorkoutPlans;
        private HomeSelfWorkoutMarketplaceFragment parentFragment;
        private DBHelper dbHelper;

        public RecyclerViewAdapter(List<SelfWorkoutPlan> selfWorkoutPlans, HomeSelfWorkoutMarketplaceFragment parentFragment) {
            this.selfWorkoutPlans = selfWorkoutPlans;
            this.parentFragment = parentFragment;
            this.dbHelper = new DBHelper(getContext());
        }

        @NonNull
        @Override
        public HomeSelfWorkoutMktpRecyclerFragment.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new HomeSelfWorkoutMktpRecyclerFragment.RecyclerViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull HomeSelfWorkoutMktpRecyclerFragment.RecyclerViewHolder holder, int position) {

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
                    holder.mTxtViewMktWorkoutLevel.setTextColor(ContextCompat.getColor(getContext(),R.color.easy_workout_color));
                    break;
                case "Medium":
                    holder.mTxtViewMktWorkoutLevel.setTextColor(ContextCompat.getColor(getContext(),R.color.medium_workout_color));
                    break;
                case "Intense":
                    holder.mTxtViewMktWorkoutLevel.setTextColor(ContextCompat.getColor(getContext(),R.color.intense_workout_color));
                    break;
                default:
                    break;
            }

            //Downloading the logo from firebase storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            String imageURL = selfWorkoutPlans.get(position).getPosterUrlFirestore();
            StorageReference imageRef = storage.getReferenceFromUrl(imageURL);
            Glide.with(getContext())
                    .load(imageRef)
                    .into(holder.mImgViewMktWorkoutLogo);

            holder.mBtnAddWorkoutToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Cart");
                    builder.setMessage("Are you sure you want to add the Workout Plan to your cart?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String selfWorkoutPlanId = selfWorkoutPlans.get(position).getId();
                            dbHelper.createWorkoutPlanByUser(UserSingleton.getInstance().getUserId(),
                                    selfWorkoutPlanId);
                            dialog.dismiss();
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

        }

        @Override
        public int getItemCount() {

            return selfWorkoutPlans.size();

        }
    }

}
