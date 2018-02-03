package com.b18.kipsafe.alarms

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.b18.kipsafe.*
import com.b18.kipsafe.sunsetcommunication.GetSunsetTask

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, R.string.kukeleku, Toast.LENGTH_SHORT).show()

        // Define the intent to open when clicking the notifcation.
        val contentIntent = Intent(context, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
                context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Send the notification.
        sendNotification(context, contentPendingIntent)

        // Schedule alarm for the next day.
        val sharedPreferenceManager = SharedPreferenceManager(context)
        val handler = AlarmSetter(context)
        handler.set(sharedPreferenceManager.getMinutes(),
                GetSunsetTask.Delay.ONE_DAY)
    }
}