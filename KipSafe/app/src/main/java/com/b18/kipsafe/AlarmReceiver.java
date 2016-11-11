package com.b18.kipsafe;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Toast.makeText(context,"Tok tok!",Toast.LENGTH_SHORT).show();
        //create notification
        Notification notif = new NotificationCompat.Builder(context) //build the notification
                .setContentTitle(context.getString(R.string.notif)) //required
                .setContentText("Tok tok!") //required
                .setSmallIcon(R.drawable.egg) //required
                .setVibrate(new long[] {0,200,100,50,50,50,50,1000,1000})
                .build();

        NotificationManager nm = (NotificationManager) context
                .getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        nm.notify(0, notif); //(int id, Notification notification);

    }
}
