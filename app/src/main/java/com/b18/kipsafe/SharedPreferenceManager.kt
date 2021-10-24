package com.b18.kipsafe

import android.content.Context
import android.content.SharedPreferences
import com.b18.kipsafe.converters.convertIsoToCalendar
import com.b18.kipsafe.util.prefIsAlarmSet
import com.b18.kipsafe.util.prefSunset
import com.b18.kipsafe.util.prefTime
import java.text.ParseException
import java.util.*

class SharedPreferenceManager(val context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("kip", Context.MODE_PRIVATE)

    // Actually property getters and setters should not be used when accessing slow things like shared preferences, but not a big performance issue here.

    /** The number of minutes before sunset to set the alarm. */
    var minutesBeforeSunset: Int
        get() = preferences.getInt(UserPreference.TIME.keyString, -1)
        set(value) {
            preferences.edit().putInt(UserPreference.TIME.keyString, value).apply()
        }

    /** True if the alarm is set, false otherwise. */
    var isAlarmSet: Boolean
        get() = preferences.getBoolean(UserPreference.IS_ALARM_SET.keyString, false)
        set(value) {
            preferences.edit().putBoolean(UserPreference.IS_ALARM_SET.keyString, value).apply()
        }

    /** Last known sunset. */
    var sunset: String
        get() = preferences.getString(UserPreference.SUNSET.keyString, "")!!
        set(value) {
            preferences.edit().putString(UserPreference.SUNSET.keyString, value).apply()
        }

    /** Whether the alarm should go of during weekends only. */
    var isWeekendOnly: Boolean
        get() = preferences.getBoolean(UserPreference.WEEKEND_ONLY.keyString, false)
        set(value) {
            preferences.edit().putBoolean(UserPreference.WEEKEND_ONLY.keyString, value).apply()
        }

    /**
     * Get the last known sunset. This will be 18:00 hrs if we cannot parse api results.
     *
     * @throws DataNotFoundException when the last known sunset could not be found.
     */
    fun getSunset(): Calendar {
        // Copy value so we only access shared prefs once.
        val time = sunset
        if(time == "") throw DataNotFoundException(
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
     * Combines sunset and preferred minutes before sunset to return an alarm time.
     *
     * @return Calendar object with preferred allar
     */
    fun getAlarmTime(): Calendar {
        val time = getSunset()
        time.add(Calendar.MINUTE, -minutesBeforeSunset)
        return time
    }

}