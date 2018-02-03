package com.b18.kipsafe.alarms

import android.app.Activity
import android.content.Context
import android.widget.ImageButton
import com.b18.kipsafe.SharedPreferenceManagerKot
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
        return SharedPreferenceManagerKot(context).getIsAlarmSet()
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

        SharedPreferenceManagerKot(context).savePref(isAlarmSet)
    }
}