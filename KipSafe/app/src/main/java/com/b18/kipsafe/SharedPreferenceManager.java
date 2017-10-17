package com.b18.kipsafe;

import android.content.Context;
import android.content.SharedPreferences;

import com.b18.kipsafe.Alarms.KipAlarmManager;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Manage SharedPreferences.
 */

public class SharedPreferenceManager {

    private SharedPreferences prefs;
    private Context context;

    public SharedPreferenceManager(Context context) {
        prefs = android.preference
                .PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    /**
     * Save the number of minutes before sunset when the alarm should go off.
     *
     * @param time integer
     */
    public void writePrefTime(int time) {
        SharedPreferences.Editor edit = prefs.edit();
        String keyString = context.getResources().getString(R.string.pref_time);
        edit.putInt(keyString, time);
        edit.apply();
    }

    /**
     * Get the number of minutes before sunset when the alarm should go off.
     *
     * @return time integer
     */
    public int getPrefTime() {
        String keyString = context.getResources().getString(R.string.pref_time);
        return prefs.getInt(keyString, -1);
    }

    /**
     * Save the last known sunset, in case there is no internet when an alarm wants to reset.
     * @param time ISO string
     */
    public void saveSunsetTime(String time) {
        SharedPreferences.Editor edit = prefs.edit();
        String keyString = context.getResources().getString(R.string.pref_sunset);
        edit.putString(keyString, time);
        edit.apply();
    }

    /**
     * Get the last known sunet.
     * @return Calendar.
     */
    public Calendar getSunsetTime() {
        String keyString = context.getResources().getString(R.string.pref_sunset);
        String time = prefs.getString(keyString, "2017-10-11T17:00:00+00:00");
        try {
            return IsoConverter.convertIsoToCal(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar defaultCalendar = new GregorianCalendar();
        defaultCalendar.set(Calendar.HOUR_OF_DAY, 18);
        return defaultCalendar;
    }

    /**
     * Save boolean whether the alarm is set or not.
     */
    public void saveIsAlarmSet(boolean isAlarmSet) {
        SharedPreferences.Editor edit = prefs.edit();
        String keyString = context.getResources().getString(R.string.pref_isalarmset);
        edit.putBoolean(keyString, isAlarmSet);
        edit.apply();
    }

    /**
     * Get whether the alarm is set or not.
     * @return boolean, true if alarm is set
     */
    public boolean getIsAlarmSet() {
        String keyString = context.getResources().getString(R.string.pref_isalarmset);
        return prefs.getBoolean(keyString, false);
    }

}
