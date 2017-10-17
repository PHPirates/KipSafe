package com.b18.kipsafe.Alarms;

import android.content.Context;

import com.b18.kipsafe.SharedPreferenceManager;

import java.util.Calendar;

/**
 * Set alarms, retreive alarm status (set/not set).
 */

public class KipAlarmManager {

    private AlarmScheduler scheduler;
    private Context context;

    public KipAlarmManager(Context context) {
        scheduler = new AlarmScheduler(context);
        this.context = context;
    }

    /**
     * Schedule alarm for given time.
     */
    public void setAlarm(Calendar calendar) {
        setIsAlarmSet(true);
        scheduler.scheduleAlarm(calendar);
    }

    /**
     * Cancel the alarm.
     */
    public void cancelAlarm() {
        setIsAlarmSet(false);
        scheduler.cancelAlarm();
    }

    /**
     * Alarm status.
     * @return whether alarm is set or not.
     */
    public boolean isAlarmSet() {
        SharedPreferenceManager manager = new SharedPreferenceManager(context);
        return manager.getIsAlarmSet();
    }

    /**
     * Allow state to change because it can happen that the alarm was set on another phone, and
     * then the state is updated with Firebase.
     * @param isAlarmSet true if alarm is set
     */
    public void setIsAlarmSet(boolean isAlarmSet) {
        SharedPreferenceManager manager = new SharedPreferenceManager(context);
        manager.saveIsAlarmSet(isAlarmSet);
    }
}
