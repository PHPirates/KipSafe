package com.b18.kipsafe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.b18.kipsafe.SunsetCommunication.GetSunSetTask;
import com.b18.kipsafe.SunsetCommunication.GetSunSetTaskHandler;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.messaging.FirebaseMessaging;
import com.winsontan520.wversionmanager.library.WVersionManager;

public class MainActivity extends AppCompatActivity {

    boolean open;
    String KEY_OPEN = "open";
    ImageButton kipButton;
    BaseClass baseClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);

        //check for updates (using library)
        WVersionManager versionManager = new WVersionManager(this);
        versionManager.setVersionContentUrl("https://github.com/PHPirates/KipSafe/raw/master/version.json");
        versionManager.setUpdateUrl("https://github.com/PHPirates/KipSafe/raw/master/Kipsafe/app/app-release.apk");
        versionManager.checkVersion();

        kipButton = (ImageButton) findViewById(R.id.kipButton);

        //setup firebase
        FirebaseMessaging.getInstance().subscribeToTopic("Kip");
        Firebase firebase = new Firebase("https://kipsafe-f5610.firebaseio.com/");
        baseClass = new BaseClass(firebase, open);

        // onDataChange reacts to a difference between local and remote database (open/closed)
        firebase.child(KEY_OPEN).addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                //if there is a difference, use data from database
                open = (boolean) dataSnapshot.getValue();
                kipButton.setSelected(open); //update egg picture
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        //setup timeslider

        SeekBar timeslider = (SeekBar) findViewById(R.id.timeslider);

        final DataManager dataManager = new DataManager(getBaseContext());

        //if no value set, default is zero
        int prefTime = dataManager.getPrefTime();
        if (prefTime == -1) {
            dataManager.writePrefTime(0);
            writeSliderText(0);
        } else {
            timeslider.setProgress(prefTime);
            writeSliderText(prefTime);
        }

        timeslider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                writeSliderText(i);
                dataManager.writePrefTime(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    /**
     * write text above slider
     *
     * @param t minutes
     */
    public void writeSliderText(int t) {
        final TextView slidertext = (TextView) findViewById(R.id.timeslidertext);
        if (t == 0) {
            slidertext.setText(R.string.slidertext_zero);
        } else {
            slidertext.setText(String.format(getResources().getString(R.string.slidertext_one), t));
        }
    }

    /**
     * fires on button released
     *
     * @param v kip button
     */
    public void hitKip(View v) {
        //update global state
        open = !open;
        //update egg on all phones
        baseClass.changeOpen(open);
        if (open) {
            DataManager dataManager = new DataManager(getBaseContext());
            GetSunSetTask getSunSetTask = new GetSunSetTask(getBaseContext(), dataManager.getPrefTime());
            getSunSetTask.execute();
            GetSunSetTaskHandler handler = new GetSunSetTaskHandler(getBaseContext());
            handler.start(getSunSetTask);
        }
    }

}
