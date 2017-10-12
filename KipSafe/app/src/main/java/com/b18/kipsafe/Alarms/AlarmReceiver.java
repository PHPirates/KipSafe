package com.b18.kipsafe.Alarms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.b18.kipsafe.DataManager;
import com.b18.kipsafe.MainActivity;
import com.b18.kipsafe.SunsetCommunication.GetSunSetTask;
import com.b18.kipsafe.SunsetCommunication.GetSunSetTaskHandler;
import com.b18.kipsafe.R;

public class AlarmReceiver extends BroadcastReceiver {

//    BaseClass baseClass;

    @Override
    public void onReceive(Context context, Intent intent) {

        //shut egg immediately after notification
//        Firebase.setAndroidContext(context);
//        Firebase firebase = new Firebase("https://kipsafe-f5610.firebaseio.com/");
//        baseClass = new BaseClass(firebase,false);
//        baseClass.changeOpen(false);

        Toast.makeText(context, "Tok tok!", Toast.LENGTH_SHORT).show();

        // Find the sound file.
        Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getPackageName() + "/raw/rooster");

        // Define on-click action
        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //create notification
        Notification notif = new NotificationCompat.Builder(context) //build the notification
                .setContentTitle(context.getString(R.string.notif)) //required
                .setContentText("tok tok!") //required
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.egg) //required
                .setVibrate(new long[]{0, 200, 100, 50, 50, 50, 50, 1000, 1000})
                .setSound(alarmSound)
                .build();

        // Make sound only stop after user sees notification.
        notif.flags = Notification.FLAG_INSISTENT;

        NotificationManager nm = (NotificationManager) context
                .getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        nm.notify(0, notif); //(int id, Notification notification);

        // Schedule alarm for next day
        DataManager dataManager = new DataManager(context);
        // Next day, not this day, so delay one day.
        GetSunSetTask getSunSetTask = new GetSunSetTask(context, dataManager.getPrefTime(),
                GetSunSetTask.Delay.ONE_DAY);
        getSunSetTask.execute();
        GetSunSetTaskHandler handler = new GetSunSetTaskHandler(context);
        handler.start(getSunSetTask);
    }
}
