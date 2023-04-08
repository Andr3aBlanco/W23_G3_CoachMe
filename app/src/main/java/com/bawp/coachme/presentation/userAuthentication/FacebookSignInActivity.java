package com.bawp.coachme.presentation.userAuthantication;
/**
 * Clsss: FacebookSignInActivity.java
 * class which extends LoginActivity.class and it will helps user to get register or login easily with the facebook
    and save data to the firebase
 * @author Jaydip mulani
 * @version 1.0
 */
import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.bawp.coachme.LoadingDBSplashActivity;
import com.bawp.coachme.MainActivity;
import com.bawp.coachme.utils.UserSingleton;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FacebookSignInActivity extends LoginActivity {
CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();


LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            changingPath();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(FacebookSignInActivity.this, ""+task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }
    public void changingPath () {
        FirebaseUser user = mAuth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("firstName").exists()) {
                    //user exists
                    //here i have to change to main activity when i create one
                    Intent intent = new Intent(getApplicationContext(), LoadingDBSplashActivity.class);
                    startActivity(intent);
                    Toast.makeText(FacebookSignInActivity.this, "Welcome Back ", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    FirebaseUser user = mAuth.getCurrentUser();
                    Intent intent=new Intent(getApplicationContext(),NewUserForm.class);
                    startActivity(intent);
                    finish();
                }
            };


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}