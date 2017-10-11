package com.b18.kipsafe;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Manage SharedPreferences.
 */

public class DataManager {

    private SharedPreferences prefs;
    private String minsKeyString;
    private String sunsetKeyString;

    public DataManager(Context context) {
        prefs = android.preference
                .PreferenceManager.getDefaultSharedPreferences(context);
        minsKeyString = context.getResources().getString(R.string.pref_time);
        sunsetKeyString = context.getResources().getString(R.string.pref_sunset);
    }

    /**
     * Save the number of minutes before sunset when the alarm should go off.
     *
     * @param time integer
     */
    public void writePrefTime(int time) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(minsKeyString, time);
        edit.apply();
    }

    /**
     * Get the number of minutes before sunset when the alarm should go off.
     *
     * @return time integer
     */
    public int getPrefTime() {
        return prefs.getInt(minsKeyString, -1);
    }

    /**
     * Save the last known sunset, in case there is no internet when an alarm wants to reset.
     * @param time ISO string
     */
    public void saveSunsetTime(String time) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(sunsetKeyString, time);
        edit.apply();
    }

    /**
     * Get the last known sunet.
     * @return Calendar.
     */
    public Calendar getSunsetTime() {
        String time = prefs.getString(sunsetKeyString, "2017-10-11T17:00:00+00:00");
        try {
            return IsoConverter.convertIsoToCal(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar defaultCalendar = new GregorianCalendar();
        defaultCalendar.set(Calendar.HOUR_OF_DAY, 18);
        return defaultCalendar;
    }

}
