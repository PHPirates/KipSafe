package com.b18.kipsafe.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Schedule alarms.
 */
class AlarmScheduler {

    private Context context;

    AlarmScheduler(Context context) {
        this.context = context;
    }

    /**
     * Schedule Android alarm, now by default daily.
     *
     * @param timeCal Time to schedule alarm for.
     */
    void scheduleAlarm(Calendar timeCal) {
        //schedule alarm
        //time received is in UTC
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeCal.getTimeInMillis(), getPendingIntent());
    }

    /**
     * Cancel the alarm that was set.
     */
    void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getPendingIntent());

    }

    private PendingIntent getPendingIntent() {
        Intent intentAlarm = new Intent(context, AlarmReceiver.class);
        return PendingIntent.getBroadcast(
                context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
