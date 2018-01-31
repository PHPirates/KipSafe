package com.b18.kipsafe.Firebase;

import android.util.Log;

import com.b18.kipsafe.Alarms.KipAlarmManager;
import com.google.firebase.messaging.RemoteMessage;

import java.text.ParseException;

import static com.b18.kipsafe.converters.IsoConverterKt.convertIsoToCalendar;


public class FirebaseMessageReceiver extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("Msg", "Message received [" + remoteMessage + "]");

        String time = remoteMessage.getData().get("time");
        Log.e("time", time);
        try {
            KipAlarmManager alarmManager = new KipAlarmManager(getBaseContext());
            alarmManager.setAlarm(convertIsoToCalendar(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}

