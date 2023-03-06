package com.bawp.coachme.model;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CustomCalendarView extends CalendarView {
    private HashMap<String, TrainerSchedule> trainerScheduleHashMap;

    public CustomCalendarView(Context context) {
        super(context);
    }

    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTrainerScheduleHashMap(HashMap<String, TrainerSchedule> trainerScheduleHashMap) {
        this.trainerScheduleHashMap = trainerScheduleHashMap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("Andrea", "Inside onDraw");
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(getDate());

        int currentYear = currentCalendar.get(Calendar.YEAR);
        int currentMonth = currentCalendar.get(Calendar.MONTH);

        // Iterate through each day of the current month
        for (int i = 1; i <= currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            Calendar dayCalendar = Calendar.getInstance();
            dayCalendar.set(currentYear, currentMonth, i);

            // Check if the day is contained in the HashMap
            TrainerSchedule trainerSchedule = trainerScheduleHashMap.get(getDayId(dayCalendar));
            Log.d("Andrea", "day not found " + dayCalendar);
            if (trainerSchedule == null) {
                // If the day is not contained in the HashMap, shade it and make it unclickable
                int dayOfMonth = dayCalendar.get(Calendar.DAY_OF_MONTH);
                View dayView = findViewWithTag(dayOfMonth);
                if (dayView != null) {
                    dayView.setAlpha(0.5f);
                    dayView.setClickable(false);
                }
            }
        }
    }

    private String getDayId(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(calendar.getTime());
    }
}
