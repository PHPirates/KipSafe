package com.b18.kipsafe;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.b18.kipsafe.Alarms.KipAlarmManager;

import java.util.Calendar;

/**
 * Reschedule alarm after reboot.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context pContext, Intent intent) {
        KipAlarmManager alarmManager = new KipAlarmManager(pContext);
        SharedPreferenceManager preferenceManager = new SharedPreferenceManager(pContext);
        Calendar time = preferenceManager.getAlarmTime();
        alarmManager.setAlarm(time);
    }
}
