package com.b18.kipsafe

import android.content.Context
import android.os.AsyncTask
import android.os.Looper
import android.os.Handler
import android.widget.Toast
import com.b18.kipsafe.alarms.KipAlarmManager
import com.b18.kipsafe.sunsetcommunication.GetSunsetTask
import java.text.SimpleDateFormat
import java.util.*

/**
 * Starts handler to handle a dataSender and kill it after 2 seconds.
 */
class AlarmSetter(private val context: Context?) {
    /**
     * Sets alarm by first requesting sunset time, if it is not reachable it will
     * kill it after 2 seconds.
     *
     * @param minutesBeforeSunset Number of minutes before sunset that the alarm should go off.
     * @param delay Whether the alarm should be delayed a day or not.
     */
    fun set(minutesBeforeSunset: Int, delay: GetSunsetTask.Delay) {
        // Make a new task which will request the sunset time. When done, it will set an alarm.
        val getSunSetTask = GetSunsetTask(context, minutesBeforeSunset, delay)
        getSunSetTask.execute()

        // Make sure to set from UI thread.
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            if(getSunSetTask.status == AsyncTask.Status.RUNNING) {
                getSunSetTask.cancel(true)
                Toast.makeText(context, "Kan de zon niet vinden!", Toast.LENGTH_SHORT).show()

                val calendar: Calendar = try {
                    SharedPreferenceManager(context).getAlarmTime()
                } catch (e: DataNotFoundException) {
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.HOUR_OF_DAY, 18)
                    cal
                }

                val alarmManager = KipAlarmManager(context)
                alarmManager.setAlarm(calendar)
                val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                Toast.makeText(context,
                        "Alarm set for " + simpleDateFormat.format(calendar.time),
                        Toast.LENGTH_SHORT).show()
            }
        }, 2000)
    }
}