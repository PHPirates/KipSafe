package com.b18.kipsafe.alarms

import android.app.Activity
import android.content.Context
import android.widget.ImageButton
import com.b18.kipsafe.SharedPreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
 * Set alarms, retrieve alarm status (set/not set).
 */
class KipAlarmManager(private val context: Context?) {
    private var scheduler = AlarmScheduler(context)

    /**
     * Schedule alarm for given time.
     */
    fun setAlarm(calendar: Calendar) {
        setIsAlarmSet(true)

        // Override time if alarm should be set on weekend only.
        val sharedprefs = SharedPreferenceManager(context)
        if (sharedprefs.isWeekendOnly) {
            // If the alarm would be set on any other day than weekend, set it on first day of weekend.
            when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY,
                Calendar.TUESDAY,
                Calendar.WEDNESDAY,
                Calendar.THURSDAY,
                Calendar.FRIDAY -> calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
            }
        }
        scheduler.scheduleAlarm(calendar)

        // Debug
//        val debugCalendar = GregorianCalendar()
//        debugCalendar.set(Calendar.SECOND, debugCalendar.get(Calendar.SECOND) + 10)
//        scheduler.scheduleAlarm(debugCalendar)
    }

    /**
     * Cancel the alarm.
     */
    fun cancelAlarm() {
        setIsAlarmSet(false)
        scheduler.cancelAlarm()
    }

    /**
     * Alarm status.
     * @return whether alarm is set or not.
     */
    fun isAlarmSet(): Boolean {
        return SharedPreferenceManager(context).isAlarmSet
    }

    /**
     * Allow state to change because it can happen that the alarm was set on another phone, and
     * then the state is updated with Firebase.
     * @param isAlarmSet true if alarm is set
     */
    fun setIsAlarmSet(isAlarmSet: Boolean) {
        try {
            val activity = context as Activity
            val kipButton = activity.kipButton as ImageButton
            kipButton.isSelected = isAlarmSet
        } catch (e: ClassCastException) {
            // We cannot cast the context to an activity. So we were called from a context
            // that does not correspond to the main activity, like a notification receiver.
            // Skipping update (will happen when Main is launched next time).
        }

        SharedPreferenceManager(context).isAlarmSet = isAlarmSet
    }
}