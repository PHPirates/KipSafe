package com.b18.kipsafe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.b18.kipsafe.Alarms.AlarmManager;
import com.b18.kipsafe.Firebase.FirebaseManager;
import com.b18.kipsafe.SunsetCommunication.GetSunSetTask;
import com.b18.kipsafe.SunsetCommunication.GetSunSetTaskHandler;
import com.firebase.client.Firebase;

public class MainActivity extends AppCompatActivity {

    // The managers used for the main activity.
    private FirebaseManager firebaseManager;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);

        new UpdateChecker(this).check();

        alarmManager = new AlarmManager(getBaseContext());
        firebaseManager = new FirebaseManager(alarmManager, this);
        firebaseManager.setup();

        KipTimeSlider slider = new KipTimeSlider(this);
        slider.setup();
    }

    /**
     * fires on button released
     *
     * @param v kip button
     */
    public void hitKip(View v) {
        boolean open = alarmManager.isAlarmSet();
        open = !open;
        //update egg on all phones
        firebaseManager.changeOpen(open);
        if (open) {
            DataManager dataManager = new DataManager(getBaseContext());
            GetSunSetTask getSunSetTask = new GetSunSetTask(getBaseContext(), dataManager.getPrefTime());
            getSunSetTask.execute();
            GetSunSetTaskHandler handler = new GetSunSetTaskHandler(getBaseContext());
            handler.start(getSunSetTask);
        }
    }

}
