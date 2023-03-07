package com.bawp.coachme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Main_page_Activity extends AppCompatActivity {
TextView user_info;
Button logOutBtn;
     FirebaseAuth auth;

    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        auth=FirebaseAuth.getInstance();
        user_info=findViewById(R.id.txtUserInfo);
        logOutBtn=findViewById(R.id.btnLogOut);
        user= auth.getCurrentUser();

        //if user is null we will open login activity
        if (user == null) {
            Intent intent = new Intent (getApplicationContext(), Login_in_Activity.class);
            startActivity (intent);
            finish();
        }
        else {
            user_info.setText(user.getEmail());
        }
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login_in_Activity.class);
                startActivity (intent);
                finish();
            }
        });
    }
}