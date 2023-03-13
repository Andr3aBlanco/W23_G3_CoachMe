package com.bawp.coachme.presentation.selfworkout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bawp.coachme.R;
import com.bawp.coachme.model.SelfWorkoutSession;
import com.bawp.coachme.model.SelfWorkoutSessionType;
import com.bawp.coachme.presentation.order.OrderPaymentOptionsFragment;
import com.bawp.coachme.presentation.order.OrdersFragment;
import com.bawp.coachme.utils.UserSingleton;

import java.util.ArrayList;
import java.util.List;

public class SelfworkoutSessionTypeFragment extends Fragment {

    private List<SelfWorkoutSessionType> selfWorkoutSessionTypes;
    private SelfWorkoutSession currentSession;
    private Boolean isNewSession;
    private int selfworkoutUserId;
    FragmentManager fm;
    Fragment fragment;
    ProgressBar pbSelfworkoutSessionType;
    LinearLayout llSelfworkoutSessionType;

    public SelfworkoutSessionTypeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selfWorkoutSessionTypes = (List<SelfWorkoutSessionType>) getArguments().getSerializable("sessionTypesList");
            currentSession = (SelfWorkoutSession) getArguments().getSerializable("sessionObj");
            isNewSession = getArguments().getBoolean("isNewSession");
            selfworkoutUserId = getArguments().getInt("workoutUserId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_selfworkout_session_type, container, false);

        SelfworkoutSessionTypeFragment currentFragment = this;

        pbSelfworkoutSessionType = view.findViewById(R.id.pbSelfworkoutSessionType);
        llSelfworkoutSessionType = view.findViewById(R.id.llSelfworkoutSessionType);

        pbSelfworkoutSessionType.setVisibility(View.VISIBLE);
        llSelfworkoutSessionType.setVisibility(View.GONE);

        fm = getActivity().getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.sessionTypeFragmentContainer);
        if (fragment == null){
            fragment = SelfworkoutSessionTypeRecyclerFragment.newInstance(selfWorkoutSessionTypes,currentSession,selfworkoutUserId,currentFragment );

            fm.beginTransaction()
                    .add(R.id.sessionTypeFragmentContainer,fragment)
                    .commit();
        }else{
            fragment = SelfworkoutSessionTypeRecyclerFragment.newInstance(selfWorkoutSessionTypes,currentSession,selfworkoutUserId,currentFragment);

            fm.beginTransaction()
                    .replace(R.id.sessionTypeFragmentContainer,fragment)
                    .commit();
        }

        pbSelfworkoutSessionType.setVisibility(View.GONE);
        llSelfworkoutSessionType.setVisibility(View.VISIBLE);

        // Inflate the layout for this fragment
        return view;
    }

}
