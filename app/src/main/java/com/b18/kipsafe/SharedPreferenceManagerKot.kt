package com.b18.kipsafe

import android.content.Context
import com.b18.kipsafe.converters.convertIsoToCalendar
import com.b18.kipsafe.util.prefIsAlarmSet
import com.b18.kipsafe.util.prefSunset
import com.b18.kipsafe.util.prefTime
import java.text.ParseException
import java.util.*

class SharedPreferenceManagerKot(val context: Context?) {
    private val prefs = android.preference
            .PreferenceManager.getDefaultSharedPreferences(context)

    /**
     * Saves the given preference.
     * If the preference is a
     *      Int: save number of minutes before sunset when the alarm should go off.
     *      String: save the last known sunset, which we can use when there is no internet.
     *      Boolean: save whether the alarm is set or not.
     *
     * @param preference in practice one of the following three:
     *      Int (minutes)
     *      String (ISO time)
     *      Boolean (isAlarmSet)
     */
    fun savePref(preference: Any) {
        val edit = prefs.edit()
        val success = when(preference) {
            is Int -> {
                edit.putInt(prefTime, preference)
                true
            }
            is String -> {
                edit.putString(prefSunset, preference)
                true
            }
            is Boolean -> {
                edit.putBoolean(prefIsAlarmSet, preference)
                true
            }
            else -> false
        }
        if(success) edit.apply()
    }

    /**
     * Get the number of minutes before sunset.
     */
    fun getTime(): Int {
        return prefs.getInt(prefTime, -1)
    }

    /**
     * Get the last known sunset. This is a default time if we cannot parse api results.
     *
     * @throws DataNotFoundException when the last known sunset could not be found.
     */
    fun getSunset(): Calendar {
        val defaultTime = "2017-10-11T17:00:00+00:00"
        val time = prefs.getString(prefSunset, defaultTime)

        if(time == defaultTime) throw DataNotFoundException(
                "Could not find requested time in SharedPreferences.")

        return try {
            convertIsoToCalendar(time)
        } catch (e: ParseException) {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 18)
            e.printStackTrace()
            calendar
        }
    }

    /**
     * Get whether the alarm is set or not. Returns truw if alarm is set.
     */
    fun getIsAlarmSet(): Boolean {
        return prefs.getBoolean(prefIsAlarmSet, false)
    }
}