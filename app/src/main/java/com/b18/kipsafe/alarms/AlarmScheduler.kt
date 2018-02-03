package com.b18.kipsafe.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

/**
 * Schedules alarms.
 */
class AlarmScheduler(private val context: Context?) {
    /**
     * Schedule a daily alarm.
     *
     * @param timeCal Time to schedule the alarm for, as Calendar object.
     */
    fun scheduleAlarm(timeCal: Calendar) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeCal.timeInMillis, getPendingIntent())
    }

    /**
     * Cancel an alarm that has been set.
     */
    fun cancelAlarm() {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(getPendingIntent())
    }

    private fun getPendingIntent() : PendingIntent {
        val intentAlarm = Intent(context, AlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
                context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}