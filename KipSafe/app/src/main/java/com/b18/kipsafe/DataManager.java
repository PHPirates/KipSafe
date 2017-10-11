package com.b18.kipsafe;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manage SharedPreferences.
 */

public class DataManager {

    private Context context;

    public DataManager(Context context) {
        this.context = context;
    }

    /**
     * write time to shared preferences
     *
     * @param time integer (mins before sunet)
     */
    public void writePrefTime(int time) {
        SharedPreferences prefs = android.preference
                .PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(context.getResources().getString(R.string.pref_time), time);
        edit.apply();
    }

    /**
     * get time from shared preferences
     *
     * @return time integer (mins before sunset)
     */
    public int getPrefTime() {
        SharedPreferences prefs = android.preference.PreferenceManager
                .getDefaultSharedPreferences(context);
        return prefs.getInt(context.getResources().getString(R.string.pref_time), -1);
    }

}
