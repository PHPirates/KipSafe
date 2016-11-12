package com.b18.kipsafe;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;

public class AlarmReceiver extends BroadcastReceiver {

    BaseClass baseClass;

    @Override
    public void onReceive(Context context, Intent intent) {

        Firebase.setAndroidContext(context);
        Firebase firebase = new Firebase("https://kipsafe-f5610.firebaseio.com/");
        baseClass = new BaseClass(firebase,false);
        baseClass.changeOpen(false);

        Toast.makeText(context,"Tok tok!",Toast.LENGTH_SHORT).show();
        //create notification
        Notification notif = new NotificationCompat.Builder(context) //build the notification
                .setContentTitle(context.getString(R.string.notif)) //required
                .setContentText("test") //required
                .setSmallIcon(R.drawable.egg) //required
                .setVibrate(new long[] {0,200,100,50,50,50,50,1000,1000})
                .build();

        NotificationManager nm = (NotificationManager) context
                .getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        nm.notify(0, notif); //(int id, Notification notification);

    }
}
