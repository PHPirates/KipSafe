package com.b18.kipsafe;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.b18.kipsafe.alarms.KipAlarmManager;
import com.b18.kipsafe.SunsetCommunication.GetSunSetTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Starts handler to handle a dataSender and kill it after 2 seconds.
 */
public class AlarmSetter {

    private Context context;

    public AlarmSetter(Context context) {
        this.context = context;
    }

    /**
     * Sets alarm by first requesting sunset time, if it is not reachable it will
     * kill it after 2 seconds.
     *
     * @param minutesBeforeSunset Number of minutes before sunset that the alarm should go off.
     * @param delay Whether the alarm should be delayed a day or not.
     */
    public void set(int minutesBeforeSunset, GetSunSetTask.Delay delay) {
        // Make a new task which will request the sunset time. When done, it will set an alarm.
        final GetSunSetTask getSunSetTask = new GetSunSetTask(context, minutesBeforeSunset, delay);
        getSunSetTask.execute();

        //set a new handler that will cancel the AsyncTask after x seconds
        //in case the sunset can't be reached
        Handler handler = new Handler(Looper.getMainLooper()); //make sure to set from UI thread
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getSunSetTask.getStatus() == AsyncTask.Status.RUNNING) {
                    getSunSetTask.cancel(true);
//                    Toast.makeText(getBaseContext(),"The sun could not be reached, therefore we couldn't ask him for his sleepy time.",Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Kan de zon niet vinden!", Toast.LENGTH_SHORT).show();

                    // Schedule alarm at last known time, default if not exists.
                    Calendar calendar;
                    try {
                        calendar = new SharedPreferenceManager(context).getAlarmTime();
                    } catch (DataNotFoundException e) {
                        calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, 18);
                    }

                    KipAlarmManager alarmManager = new KipAlarmManager(context);
                    alarmManager.setAlarm(calendar);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
                    Toast.makeText(context, "Alarm set for " + sdf.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
                }

            }
        }, 2000);
    }
}
