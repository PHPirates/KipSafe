package com.b18.kipsafe

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.b18.kipsafe.util.kukeleku

/**
 * Sends a notification.
 *
 * @param pendingIntent The intent to start when clicking the notificaiton.
 */
fun sendNotification(context: Context?, pendingIntent: PendingIntent) {

    val alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
            ";//" + context?.packageName + "/raw/rooster")

    // Deprecated as of API 26. But we don't use that (yet).
    val notification = Notification.Builder(context)
            .setContentTitle(context!!.resources.getString(R.string.notif))
            .setContentText("Kukeleku!")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.egg)
            .setVibrate(kukeleku)
            .setSound(alarmSound)
            .build()

    // Sound only stops after user sees notification.
    notification.flags = Notification.FLAG_INSISTENT

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

    notificationManager.notify(0, notification)
}