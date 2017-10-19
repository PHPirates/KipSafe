package com.b18.kipsafe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.b18.kipsafe.Alarms.KipAlarmManager;
import com.b18.kipsafe.Firebase.FirebaseManager;
import com.b18.kipsafe.SunsetCommunication.GetSunSetTask;
import com.b18.kipsafe.SunsetCommunication.GetSunSetTaskHandler;
import com.firebase.client.Firebase;

public class MainActivity extends AppCompatActivity {

    // The managers used for the main activity.
    private FirebaseManager firebaseManager;
    private KipAlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);

        new UpdateChecker(this).check();

        alarmManager = new KipAlarmManager(this);
//        firebaseManager = new FirebaseManager(alarmManager, this);
//        firebaseManager.setup();

        KipTimeSlider slider = new KipTimeSlider(this);
        slider.setup();
    }

    /**
     * fires on button released
     *
     * @param v kip button
     */
    public void hitKip(View v) {
        boolean isAlarmSet = alarmManager.isAlarmSet();
        isAlarmSet = !isAlarmSet;

        // This will also update alarmManager.
//        firebaseManager.changeOpen(isAlarmSet);
        alarmManager.setIsAlarmSet(isAlarmSet);

        if (isAlarmSet) {
            SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(this);
            // Schedule alarm for the next sunset.
            GetSunSetTask getSunSetTask = new GetSunSetTask(
                    this, sharedPreferenceManager.getPrefTime(), GetSunSetTask.Delay.NO_DELAY);
            getSunSetTask.execute();
            GetSunSetTaskHandler handler = new GetSunSetTaskHandler(this);
            handler.start(getSunSetTask);
        } else {
            alarmManager.cancelAlarm();
            Toast.makeText(getBaseContext(), "Alarm canceled", Toast.LENGTH_SHORT).show();
        }
    }

}
