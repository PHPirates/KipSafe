package com.b18.kipsafe;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.b18.kipsafe.alarms.KipAlarmManager;
import com.b18.kipsafe.SunsetCommunication.GetSunSetTask;

import java.util.Calendar;

/**
 * Reschedule alarm after reboot.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context pContext, Intent intent) {
        KipAlarmManager alarmManager = new KipAlarmManager(pContext);
        SharedPreferenceManager preferenceManager = new SharedPreferenceManager(pContext);

        if (preferenceManager.getIsAlarmSet()) {

            // If the alarm was scheduled, reschedule it with the last time known. If no last time
            // is known, request new sunset time.
            Calendar time;
            try {
                time = preferenceManager.getAlarmTime();
                // debug
//                time = Calendar.getInstance();
//                time.set(Calendar.SECOND, time.get(Calendar.SECOND) + 10);
                Calendar now = Calendar.getInstance();
                if (now.compareTo(time) > 0) {
                    // Now is after the sunset of today
                    // So schedule alarm for next sunset
                    time.set(Calendar.DAY_OF_MONTH, time.get(Calendar.DAY_OF_MONTH) + 1);
                }
                alarmManager.setAlarm(time);
            } catch (DataNotFoundException e) {
                int minutesBeforeSunset = preferenceManager.getPrefTime();
                new AlarmSetter(pContext).set(minutesBeforeSunset, GetSunSetTask.Delay.NO_DELAY);
            }
        }
    }
}
