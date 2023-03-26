package com.bawp.coachme.presentation.home;

/**
 * Class: HomeSelfWorkoutMarketplaceFragment.java
 *
 * Fragment with the list of self-workout plans available for the user.
 *
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bawp.coachme.R;
import com.bawp.coachme.model.SelfWorkoutPlan;
import com.bawp.coachme.presentation.selfworkout.SelfworkoutFragment;
import com.bawp.coachme.utils.DBHelper;
import com.bawp.coachme.utils.UserSingleton;

import java.util.List;

public class HomeSelfWorkoutMarketplaceFragment extends Fragment {

    DBHelper dbHelper;
    ProgressBar pbSelfworkoutMarketplace;
    LinearLayout llSelfworkoutMarketplace;
    LinearLayout llNoMktpItemsAvailable;
    Button btnBackToHome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_self_workout_marketplace, container, false);

        HomeSelfWorkoutMarketplaceFragment currentFragment = this;

        pbSelfworkoutMarketplace = view.findViewById(R.id.pbSelfworkoutMarketplace);
        llSelfworkoutMarketplace = view.findViewById(R.id.llSelfworkoutMarketplace);
        llNoMktpItemsAvailable = view.findViewById(R.id.llNoMktpItemsAvailable);
        btnBackToHome = view.findViewById(R.id.btnBackToHome);

        pbSelfworkoutMarketplace.setVisibility(View.VISIBLE);
        llSelfworkoutMarketplace.setVisibility(View.GONE);

        //Get the list of self workout plans available for the user
        String customerId = UserSingleton.getInstance().getUserId();

        dbHelper = new DBHelper(getContext());

        List<SelfWorkoutPlan> selfWorkoutPlanList = dbHelper.getSelfWorkoutPlanAvailable(customerId);

        llNoMktpItemsAvailable.setVisibility(View.GONE);

        HomeSelfWorkoutMktpRecyclerAdapter mktpAdapter = new HomeSelfWorkoutMktpRecyclerAdapter(selfWorkoutPlanList, getContext());

        RecyclerView recyclerViewMktp = view.findViewById(R.id.marketplaceRecyclerView);
        recyclerViewMktp.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMktp.setAdapter(mktpAdapter);

        mktpAdapter.setListener(new HomeSelfWorkoutMktpRecyclerAdapter.SetOnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                String selfWorkoutPlanId = selfWorkoutPlanList.get(i).getId();
                dbHelper.createWorkoutPlanByUser(UserSingleton.getInstance().getUserId(),
                        selfWorkoutPlanId);

                selfWorkoutPlanList.remove(i);
                mktpAdapter.notifyDataSetChanged();

                if(selfWorkoutPlanList.size()==0){
                    llNoMktpItemsAvailable.setVisibility(View.VISIBLE);
                    recyclerViewMktp.setVisibility(View.GONE);
                }

            }
        });

        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = new HomeFragment();

                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();

                // Replace the current fragment with the new one
                fragmentTransaction.replace(R.id.barFrame, homeFragment);

                // Add the transaction to the back stack
                fragmentTransaction.addToBackStack("main");

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

        pbSelfworkoutMarketplace.setVisibility(View.GONE);
        llSelfworkoutMarketplace.setVisibility(View.VISIBLE);

        return view;
    }
}