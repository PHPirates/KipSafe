package com.b18.kipsafe.Alarms;

import android.content.Context;

import java.util.Calendar;

/**
 * Set alarms, retreive alarm status (set/not set).
 */

public class AlarmManager {

    private boolean alarmSet;
    private AlarmScheduler scheduler;

    public AlarmManager(Context context) {
        scheduler = new AlarmScheduler(context);
    }

    /**
     * Schedule alarm for given time.
     */
    public void setAlarm(Calendar calendar) {
        alarmSet = true;
        scheduler.scheduleAlarm(calendar);
    }

    public void cancelAlarm() {
        // void
    }

    /**
     * Alarm status.
     * @return whether alarm is set or not.
     */
    public boolean isAlarmSet() {
        return alarmSet;
    }

    /**
     * Allow state to change because it can happen that the alarm was set on another phone, and
     * then the state is updated with Firebase.
     */
    public void setIsAlarmSet(boolean open) {
        this.alarmSet = open;
    }
}
