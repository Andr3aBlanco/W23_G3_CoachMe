package com.bawp.coachme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class Current_Location_Activity extends NewUserForm implements Filterable {
TextView btn_location;
EditText txtAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        btn_location=findViewById(R.id.txtGetCurrentLocation);
        txtAddress=findViewById(R.id.txtAddress);
        //runtime permissions to get access location


  }

    @Override
    public Filter getFilter() {
        return null;
    }
}