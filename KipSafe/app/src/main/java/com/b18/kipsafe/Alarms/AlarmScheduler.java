package com.b18.kipsafe.Alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.b18.kipsafe.Alarms.AlarmReceiver;

import java.util.Calendar;

/**
 * Schedule alarms.
 */
public class AlarmScheduler {

    private Context context;

    public AlarmScheduler(Context context) {
        this.context = context;
    }

    /**
     * Schedule Android alarm, now by default daily.
     *
     * @param timeCal Time to schedule alarm for.
     */
    public void scheduleAlarm(Calendar timeCal) {
        //schedule alarm
        //time received is in UTC
        Intent intentAlarm = new Intent(context, AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeCal.getTimeInMillis(),
                PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
    }
}
