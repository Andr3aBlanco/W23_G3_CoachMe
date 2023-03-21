package com.bawp.coachme.presentation.user;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bawp.coachme.R;
import com.bawp.coachme.presentation.user.NewUserForm;

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