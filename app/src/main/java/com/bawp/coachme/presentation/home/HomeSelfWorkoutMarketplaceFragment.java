package com.bawp.coachme.presentation.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bawp.coachme.R;
import com.bawp.coachme.model.SelfWorkoutPlan;
import com.bawp.coachme.utils.DBHelper;
import com.bawp.coachme.utils.UserSingleton;

import java.util.List;

public class HomeSelfWorkoutMarketplaceFragment extends Fragment {

    DBHelper dbHelper;
    FragmentManager fm;
    Fragment fragment;
    ProgressBar pbSelfworkoutMarketplace;
    LinearLayout llSelfworkoutMarketplace;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_self_workout_marketplace, container, false);

        HomeSelfWorkoutMarketplaceFragment currentFragment = this;

        pbSelfworkoutMarketplace = view.findViewById(R.id.pbSelfworkoutMarketplace);
        llSelfworkoutMarketplace = view.findViewById(R.id.llSelfworkoutMarketplace);

        pbSelfworkoutMarketplace.setVisibility(View.VISIBLE);
        llSelfworkoutMarketplace.setVisibility(View.GONE);

        //Get the list of self workout plans available for the user
        String customerId = UserSingleton.getInstance().getUserId();

        dbHelper = new DBHelper(getContext());

        List<SelfWorkoutPlan> selfWorkoutPlanList = dbHelper.getSelfWorkoutPlanAvailable(customerId);

        HomeSelfWorkoutMktpRecyclerAdapter mktpAdapter = new HomeSelfWorkoutMktpRecyclerAdapter(selfWorkoutPlanList, getContext());
        RecyclerView recyclerViewMktp = view.findViewById(R.id.marketplaceRecyclerView);
        recyclerViewMktp.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMktp.setAdapter(mktpAdapter);

        /*
        fm = getActivity().getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.marketplaceFragmentContainer);
        if (fragment == null){
            fragment = HomeSelfWorkoutMktpRecyclerFragment.newInstance(selfWorkoutPlanList,currentFragment );

            fm.beginTransaction()
                    .add(R.id.marketplaceFragmentContainer,fragment)
                    .commit();
        }else{
            fragment = HomeSelfWorkoutMktpRecyclerFragment.newInstance(selfWorkoutPlanList,currentFragment);

            fm.beginTransaction()
                    .replace(R.id.marketplaceFragmentContainer,fragment)
                    .commit();
        }
        */
        pbSelfworkoutMarketplace.setVisibility(View.GONE);
        llSelfworkoutMarketplace.setVisibility(View.VISIBLE);

        return view;
    }
}