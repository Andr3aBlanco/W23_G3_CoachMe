package com.bawp.coachme.presentation.userAuthantication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.text.font.FontVariation;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bawp.coachme.LoadingDBSplashActivity;
import com.bawp.coachme.MainActivity;
import com.bawp.coachme.R;
import com.bawp.coachme.utils.UserSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    String myDeviceId;
    Button logInBtn;
    CountDownTimer countDownTimer;
    FirebaseAuth mAuth;
    ImageView googleLoginBtn;
    ImageView fbLoginBtn;
    // importing data
    EditText usernametxt;
    EditText passwordtxt;
    TextView goToRegister, txtforgotpass;
    ProgressBar porgBar;
    private String myDeviceToken;
    FirebaseUser user;
    String current_User=null;
    DatabaseReference databaseRef;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Setting the user Id into the UserSingleton Static Object
            UserSingleton.getInstance().setUserId(currentUser.getUid());
            Intent intent = new Intent(getApplicationContext(), LoadingDBSplashActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        googleLoginBtn = findViewById(R.id.imageGoogleAuth);
        usernametxt = findViewById(R.id.txtusername);
        passwordtxt = findViewById(R.id.txtpassword);
        goToRegister = findViewById(R.id.btnGoToRegister);
        porgBar = findViewById(R.id.pBar);
        logInBtn = findViewById(R.id.btnLogin);
        fbLoginBtn = findViewById(R.id.imageFacebookAuth);
        txtforgotpass = findViewById(R.id.txtForgotPassword);
        myDeviceToken= Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
        // sending user to create new account page
        goToRegister.setOnClickListener((View view) -> {
            Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(regIntent);
        });

        // creating on click listener for googlesign In
        googleLoginBtn.setOnClickListener((View view) -> {
            Intent regIntent = new Intent(LoginActivity.this, GoogleSignInActivity.class);
            startActivity(regIntent);

        });
        // creating on click listener for facebook In
        fbLoginBtn.setOnClickListener((View view) -> {
            Intent regIntent = new Intent(LoginActivity.this, FacebookSignInActivity.class);
            startActivity(regIntent);

        });

        // creating on click listener for forgot password
        txtforgotpass.setOnClickListener((View view) -> {
            Intent regIntent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(regIntent);

        });

        // creating Log in button on click listener
        logInBtn.setOnClickListener((View View) -> {
            porgBar.setVisibility(View.GONE);
            String email, password, name;
            email = String.valueOf(usernametxt.getText());
            password = String.valueOf(passwordtxt.getText());

            if (TextUtils.isEmpty(email)) {

                Toast.makeText(LoginActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                Toast.makeText(LoginActivity.this, "Please provide a valid Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }

            // sending request to the firebase to check the existing user
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(LoginActivity.this, "Loged in",
                                        Toast.LENGTH_SHORT).show();
                                // starting new Activity when user id password is correct send them to main
                                // activity
                                Intent intent = new Intent(getApplicationContext(), NewUserForm.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.

                                Toast.makeText(LoginActivity.this, "Please check your Id and password",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        });

    }

}