package com.b18.kipsafe

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.b18.kipsafe.SunsetCommunication.GetSunSetTask
import com.b18.kipsafe.alarms.KipAlarmManager
import java.util.*

/**
 * Reschedule alarm after reboot.
 */
class BootBroadcastReceiver : BroadcastReceiver(){
    override fun onReceive(pContext: Context?, intent: Intent?) {
        val alarmManager = KipAlarmManager(pContext)
        val preferenceManager = SharedPreferenceManager(pContext)

        if (preferenceManager.isAlarmSet) {
            // If the alarm was scheduled, reschedule it with the last time known.
            // If no time is known, request new sunset time.
            val time: Calendar
            try {
                time = preferenceManager.alarmTime

                val now = Calendar.getInstance()
                if(now > time) {
                    // Now is past today's sunset, so schedule alarm for next sunset.
                    time.set(Calendar.DAY_OF_MONTH, time.get(Calendar.DAY_OF_MONTH) + 1)
                }
                alarmManager.setAlarm(time)
            } catch (e: DataNotFoundException){
                AlarmSetterKot(pContext).set(preferenceManager.prefTime,
                        GetSunSetTask.Delay.NO_DELAY)
            }
        }
    }
}