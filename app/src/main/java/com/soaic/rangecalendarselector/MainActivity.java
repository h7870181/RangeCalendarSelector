package com.soaic.rangecalendarselector;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.soaic.calendarselector.CalendarSelectorView;

import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private CalendarSelectorView calendar_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar_view = findViewById(R.id.calendar_view);

        Calendar c1 = Calendar.getInstance();
        c1.setTime(new Date());
        Calendar c2 = Calendar.getInstance();
        c2.setTime(c1.getTime());
        c2.add(Calendar.MONTH,5);

        calendar_view.setMinMaxDate(c1.getTime(),c2.getTime());
    }
}
