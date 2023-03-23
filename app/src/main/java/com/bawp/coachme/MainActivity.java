package com.bawp.coachme;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bawp.coachme.databinding.ActivityMainBinding;
import com.bawp.coachme.model.Appointment;
import com.bawp.coachme.presentation.home.HomeFragment;
import com.bawp.coachme.presentation.order.OrdersFragment;
import com.bawp.coachme.presentation.trainermap.TrainerListFragment;
import com.bawp.coachme.presentation.trainermap.TrainerSearchFragment;
import com.bawp.coachme.utils.DBHelper;
import com.bawp.coachme.utils.UserSingleton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserSingleton.getInstance().setUserId("-NOjpL1jiGcc80qBrFIl");
        replaceFragment(new HomeFragment());
        fragmentBinding();
    }

    private void fragmentBinding(){

        //bind
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){

                case R.id.menu_home:
                    replaceFragment(new HomeFragment());
                    break;

                case R.id.menu_orders:
                    replaceFragment(new OrdersFragment());

                    break;

                case R.id.menu_profile:
                    replaceFragment(new ProfileFragment());

                    break;

                case R.id.menu_stats:
                    replaceFragment(new StatsFragment());

                    break;

                case R.id.menu_add:
                    replaceFragment(new HomeFragment());

                    break;

            }

            return true;
        });

        binding.floatingAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new TrainerSearchFragment());
            }
        });
    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.barFrame, fragment);
        fragmentTransaction.commit();

    }



}