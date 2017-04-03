package com.k.caldemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.k.cal.CalendarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CalendarView calendarView = (CalendarView) findViewById(R.id.calendar);
        List<String> list = new ArrayList();
        list.add("2");
        list.add("3");
        list.add("1");
        calendarView.setData(13, list);
    }
}
