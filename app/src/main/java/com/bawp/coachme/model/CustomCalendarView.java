package com.bawp.coachme.model;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CustomCalendarView extends CalendarView {

    private List<Date> disabledDates;

    public CustomCalendarView(Context context) {
        super(context);
        disabledDates = new ArrayList<>();
    }

    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        disabledDates = new ArrayList<>();
    }

    public CustomCalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        disabledDates = new ArrayList<>();
    }

    public void setDisabledDates(List<Date> dates) {
        disabledDates.clear();
        disabledDates.addAll(dates);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Iterate over all visible cells and disable the ones with a disabled date
        for (int i = 0; i < getChildCount(); i++) {
            ViewGroup weekRow = (ViewGroup) getChildAt(i);
            for (int j = 0; j < weekRow.getChildCount(); j++) {
                View dayView = weekRow.getChildAt(j);
                if (dayView instanceof TextView) {
                    TextView dayTextView = (TextView) dayView;
                    Date day = new Date((Long) dayTextView.getTag());
                    if (disabledDates.contains(day)) {
                        dayTextView.setEnabled(false);
                        dayTextView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    } else {
                        dayTextView.setEnabled(true);
                        dayTextView.setTextColor(getResources().getColor(android.R.color.black));
                    }
                }
            }
        }
    }
}
