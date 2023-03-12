package com.bawp.coachme;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bawp.coachme.databinding.ActivityMainBinding;
import com.bawp.coachme.presentation.order.OrdersFragment;
import com.bawp.coachme.presentation.trainermap.TrainerListFragment;
import com.bawp.coachme.presentation.trainermap.TrainerSearchFragment;
import com.bawp.coachme.utils.DBHelper;
import com.bawp.coachme.utils.UserSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FloatingActionButton plusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Button +
        plusButton = findViewById(R.id.floatingAdd);

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Andrea", "click on +");
                replaceFragment(new TrainerSearchFragment());
            }
        });

        //After loging, we have set the User Id
        UserSingleton.getInstance().setUserId("-NOjpL1jiGcc80qBrFIl");

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        Log.d("FIREBASE MESSAGING","Failed token");
                        return;
                    }
                    String token = task.getResult();
                    // Store the FCM registration token in your database
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference usersRef = database.child("users");
                    UserSingleton.getInstance().setUserDeviceToken(token);
                    usersRef.child(UserSingleton.getInstance().getUserId()).child("deviceToken").setValue(token);

                    DBHelper dbHelper = new DBHelper(this);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    if (dbHelper.isDatabaseJustCreated()) {

                        //Get the information from each file
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference csvFileWp = storage.getReferenceFromUrl(dbHelper.URL_FIRESTORE_SELF_WORKOUT_PLANS_TABLE);
                        StorageReference csvFileWST = storage.getReferenceFromUrl(dbHelper.URL_FIRESTORE_SELF_WORKOUT_SESSION_TYPES_TABLE);
                        StorageReference csvFileEx = storage.getReferenceFromUrl(dbHelper.URL_FIRESTORE_SELF_PLAN_EXERCISES_TABLE);
                        StorageReference csvFileTrainer = storage.getReferenceFromUrl(dbHelper.URL_FIRESTORE_TRAINER_TABLE);
                        StorageReference csvFileRatings = storage.getReferenceFromUrl(dbHelper.URL_FIRESTORE_RATINGS_TABLE);
                        StorageReference csvFileTrainerService = storage.getReferenceFromUrl(dbHelper.URL_FIRESTORE_TRAINERSERVICE_TABLE);
                        StorageReference csvFileSchedule = storage.getReferenceFromUrl(dbHelper.URL_FIRESTORE_TRAINER_OPEN_SCHEDULE_TABLE);

                        List<Task<byte[]>> downloadTasks = new ArrayList<>();
                        downloadTasks.add(csvFileWp.getBytes(Long.MAX_VALUE));
                        downloadTasks.add(csvFileWST.getBytes(Long.MAX_VALUE));
                        downloadTasks.add(csvFileEx.getBytes(Long.MAX_VALUE));
                        downloadTasks.add(csvFileTrainer.getBytes(Long.MAX_VALUE));
                        downloadTasks.add(csvFileRatings.getBytes(Long.MAX_VALUE));
                        downloadTasks.add(csvFileTrainerService.getBytes(Long.MAX_VALUE));
                        downloadTasks.add(csvFileSchedule.getBytes(Long.MAX_VALUE));

                        // Wait for all Tasks to complete
                        Task<List<byte[]>> allTasks = Tasks.whenAllSuccess(downloadTasks);

                        allTasks.addOnCompleteListener(new OnCompleteListener<List<byte[]>>() {
                            @Override
                            public void onComplete(@NonNull Task<List<byte[]>> task) {
                                byte[] csvFileWpByte = task.getResult().get(0);
                                byte[] csvFileWSTByte = task.getResult().get(1);
                                byte[] csvFileExByte = task.getResult().get(2);
                                byte[] csvFileTrainerByte = task.getResult().get(3);
                                byte[] csvFileRatingsByte = task.getResult().get(4);
                                byte[] csvFileTrainerServiceByte = task.getResult().get(5);
                                byte[] csvScheduleByte = task.getResult().get(6);

                                dbHelper.uploadSelfWorkoutPlans(csvFileWpByte);
                                dbHelper.uploadSelfWorkoutSessionTypes(csvFileWSTByte);
                                dbHelper.uploadSelfWorkoutPlanExercises(csvFileExByte);
                                dbHelper.uploadTrainers(csvFileTrainerByte);

                                // do there the trainers
                                dbHelper.uploadRatings(csvFileRatingsByte);
                                dbHelper.uploadTrainerService(csvFileTrainerServiceByte);
                                dbHelper.uploadTrainerSchedule(csvScheduleByte);
                                //some dump data
                                dbHelper.uploadSampleAppointment();
                                dbHelper.uploadSampleWorkoutPlanByUser();
                                fragmentBinding();
                            }
                        });

                    } else {
                        // The database was already created, so onCreate has already finished
                        fragmentBinding();
                    }
                });
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
                    replaceFragment(new TrainerSearchFragment());

                    break;

                case R.id.menu_stats:
                    replaceFragment(new TrainerListFragment());

                    break;

                case R.id.menu_add:
                    replaceFragment(new HomeFragment());

                    break;

            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.barFrame, fragment);
        fragmentTransaction.commit();

    }



}