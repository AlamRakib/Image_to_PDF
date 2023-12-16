package com.example.imagestopdf;

import android.app.Application;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static String formalTimestamp(long timestamp)
    {
        Calendar calender = Calendar.getInstance(Locale.ENGLISH);
        calender.setTimeInMillis(timestamp);

        String date = DateFormat.format("dd/mm/yyyy",calender).toString();

        return date;


    }
}
