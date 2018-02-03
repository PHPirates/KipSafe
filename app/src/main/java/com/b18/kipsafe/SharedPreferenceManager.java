package com.b18.kipsafe;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.ParseException;
import java.util.Calendar;

import static com.b18.kipsafe.converters.IsoConverterKt.convertIsoToCalendar;

/**
 * Manage SharedPreferences.
 */
@Deprecated
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
     * Get the last known sunset, default time if could not parse api result.
     * @return Calendar.
     * @throws DataNotFoundException when the last known sunset could not be found
     */
    public Calendar getSunsetTime() throws DataNotFoundException {
        String keyString = context.getResources().getString(R.string.pref_sunset);
        String defaultTime = "2017-10-11T17:00:00+00:00";
        String time = prefs.getString(keyString, defaultTime);
        if (time.equals(defaultTime)) {
            throw new DataNotFoundException("Could not find requested data in SharedPreferences");
        }
        try {
            return convertIsoToCalendar(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Couldn't understand what the sunset api returned.
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        return calendar;
    }

    /**
     * Combines sunset and preferred minutes before sunset to an alarm time.
     * @return preferred alarm time
     * @throws DataNotFoundException when the last known sunset could not be found
     */
    public Calendar getAlarmTime() throws DataNotFoundException {
        Calendar time = getSunsetTime();
        int minutes = getPrefTime();
        time.add(Calendar.MINUTE, -minutes);
        return time;
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
     * @return true if alarm is set
     */
    public boolean getIsAlarmSet() {
        String keyString = context.getResources().getString(R.string.pref_isalarmset);
        return prefs.getBoolean(keyString, false);
    }

}
