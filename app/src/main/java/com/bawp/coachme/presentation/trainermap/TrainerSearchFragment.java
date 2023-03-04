package com.bawp.coachme.presentation.trainermap;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.bawp.coachme.HomeFragment;
import com.bawp.coachme.R;
import com.bawp.coachme.presentation.order.OrdersFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrainerSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrainerSearchFragment extends Fragment {

    //Get Elements from the Layout
    RadioGroup rgMapList;
    FrameLayout searchFilter;
    FrameLayout outletMapList;

//    Fragment fragmentMapList = new TrainerMapFragment();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TrainerSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrainerSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrainerSearchFragment newInstance(String param1, String param2) {
        TrainerSearchFragment fragment = new TrainerSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.trainer_search_fragment, container, false);

        rgMapList = view.findViewById(R.id.rgMapListSelector);
        searchFilter = view.findViewById(R.id.outletFilterFragment);
        outletMapList = view.findViewById(R.id.searchOptContainer);


        //Logic to replace for either the Map or the List
        //check if nothing selected
      if(rgMapList.getCheckedRadioButtonId() == - 1){

          replaceFragment( new TrainerMapFragment());

      }

        rgMapList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                switch (checkedId) {
                    case R.id.rbMapView:
                        replaceFragment(new TrainerMapFragment());
                        break;
                    case R.id.rbListView:
                        replaceFragment( new HomeFragment()); //Replace this for the list later
                        break;
                    default:
                        replaceFragment( new TrainerMapFragment());
                        break;
                }
            }
        });



        return view;
    }


    //Replace Fragment method
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.searchOptContainer, fragment);
        transaction.commit();
    }

}