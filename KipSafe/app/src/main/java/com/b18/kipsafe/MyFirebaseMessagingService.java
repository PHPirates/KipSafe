package com.b18.kipsafe;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("Msg", "Message received ["+remoteMessage+"]");

        String time = remoteMessage.getData().get("time");
        Log.e("time", time);
        scheduleAlarm(convertIsoToCal(time));

    }

    /**
     * convert iso 8601 string to calendar object
     *
     * @param isoTime time in UTC
     * @return calendar object
     */
    Calendar convertIsoToCal(String isoTime) {
        Date date = convertIsoToDate(isoTime);
        Calendar c = new GregorianCalendar(); //defaults to good timezone
        try { // in case date == null
            c.setTime(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    /**
     * convert a time string in ns format to a date object
     *
     * @param isoTime time in ns format
     * @return date object
     */
    private Date convertIsoToDate(String isoTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC")); //time is in UTC
            return sdf.parse(isoTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null; //default
    }

    void scheduleAlarm(Calendar timeCal) {
        //schedule alarm
//        timeCal.set(Calendar.HOUR_OF_DAY,13);
//        timeCal.set(Calendar.MINUTE,9);
        //give notification an hour before sunset
        timeCal.set(Calendar.HOUR_OF_DAY,timeCal.get(Calendar.HOUR_OF_DAY)-1);
        //toast
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
//        Toast.makeText(getBaseContext(),"Alarm set for "+sdf.format(timeCal.getTime()),Toast.LENGTH_SHORT).show();
        //time received is in UTC
        Intent intentAlarm = new Intent(getBaseContext(),AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeCal.getTimeInMillis(),
                PendingIntent.getBroadcast(getBaseContext(),1,intentAlarm,PendingIntent.FLAG_UPDATE_CURRENT));
    }
}

