package com.bawp.coachme.presentation.trainermap;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bawp.coachme.R;

import java.util.ArrayList;
import java.util.List;

public class TrainerCustomList extends BaseAdapter {

    List<Integer> timesInLong = new ArrayList<>();
    int selectedIndex = -1;


    public TrainerCustomList(List<Integer> timesInLong) {
        this.timesInLong = timesInLong;
        this.selectedIndex = -1;
    }

    public List<Integer> getTimesInLong() {
        return timesInLong;
    }

    public void setTimesInLong(List<Integer> timesInLong) {
        this.timesInLong = timesInLong;
        notifyDataSetChanged();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return timesInLong.size();
    }

    @Override
    public Object getItem(int position) {
        return timesInLong.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        List<String> timesString = new ArrayList<>();
        TextView textView;

        if (convertView == null) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hour_list_custom_item, parent, false);

        }

        textView = convertView.findViewById(R.id.tcHourPlusTime);
        TextView hours = convertView.findViewById(R.id.tcHourPlusTime);

        // Loop through the list using a traditional for loop
        if (timesInLong.get(position) < 12) {
            // Get the element at index i and do something with it
            textView.setText(timesInLong.get(position) + " a.m.");

        } else {
            textView.setText(timesInLong.get(position) + " p.m.");
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Andrea", "Clicked on the hour inside the adapter ");

                    textView.setBackgroundColor(Color.GRAY);

            }
        });


        return convertView;
    }
}
