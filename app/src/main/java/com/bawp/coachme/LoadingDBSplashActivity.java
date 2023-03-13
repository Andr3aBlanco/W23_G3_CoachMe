package com.bawp.coachme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.animation.Animator;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bawp.coachme.presentation.order.OrdersFragment;
import com.bawp.coachme.utils.DBHelper;
import com.bawp.coachme.utils.UserSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LoadingDBSplashActivity extends AppCompatActivity {

    private boolean isExecutionFinished;
    TextView txtViewLoadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_dbsplash);

        isExecutionFinished = false;
        LottieAnimationView animationView = findViewById(R.id.animation_view_loading);
        txtViewLoadingText = findViewById(R.id.txtViewLoadingText);
        animationView.setAnimation("loading_db_animated.json");


        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {


            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                while (!isExecutionFinished) {
                    animationView.playAnimation();
                }
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });

        animationView.playAnimation();

        loadDB();


    }

    private void loadDB(){
        //After loging, we have set the User Id
        UserSingleton.getInstance().setUserId("-NOjpL1jiGcc80qBrFIl");

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        return;
                    }
                    String token = task.getResult();
                    // Store the FCM registration token in your database
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference usersRef = database.child("users");
                    txtViewLoadingText.setText("Setting up user account...");
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

                        txtViewLoadingText.setText("Downloading Datasets...");
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

                                moveToMainActivity();
                            }
                        });

                    } else {
                        // The database was already created, so onCreate has already finished
                        Log.d("HELLO","MOVE");

                        moveToMainActivity();
                    }
                });
    }

    private void moveToMainActivity(){
        txtViewLoadingText.setText("Welcome!");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                isExecutionFinished = true;
                Intent intent = new Intent(LoadingDBSplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask,3000);
    }
}