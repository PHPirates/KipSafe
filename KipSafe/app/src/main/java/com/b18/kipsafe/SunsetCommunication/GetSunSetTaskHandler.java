package com.b18.kipsafe.SunsetCommunication;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.b18.kipsafe.Alarms.KipAlarmManager;
import com.b18.kipsafe.SharedPreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Starts handler to handle a dataSender and kill it after 2 seconds.
 */
public class GetSunSetTaskHandler {

    private Context context;

    public GetSunSetTaskHandler(Context context) {
        this.context = context;
    }

    /**
     * Starts handler to handle a dataSender and kill it after 2 seconds
     *
     * @param getSunSetTask getSunSetTask object
     */
    public void start(final GetSunSetTask getSunSetTask) {
        //start a new handler that will cancel the AsyncTask after x seconds
        //in case the sunset can't be reached
        Handler handler = new Handler(Looper.getMainLooper()); //make sure to start from UI thread
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getSunSetTask.getStatus() == AsyncTask.Status.RUNNING) {
                    getSunSetTask.cancel(true);
//                    Toast.makeText(getBaseContext(),"The sun could not be reached, therefore we couldn't ask him for his sleepy time.",Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Kan de zon niet vinden!", Toast.LENGTH_SHORT).show();

                    // Schedule alarm at last known time, default if not exists.
                    Calendar calendar = new SharedPreferenceManager(context).getAlarmTime();

                    KipAlarmManager alarmManager = new KipAlarmManager(context);
                    alarmManager.setAlarm(calendar);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
                    Toast.makeText(context, "Alarm set for " + sdf.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
                }

            }
        }, 2000);
    }
}
