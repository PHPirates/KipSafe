package com.b18.kipsafe.firebase;

import android.app.Activity;
import android.widget.ImageButton;

import com.b18.kipsafe.alarms.KipAlarmManager;
import com.b18.kipsafe.R;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Setting up Firebase.
 */

public class FirebaseManager {

    private Firebase firebase;
    private KipAlarmManager alarmManager;
    private String KEY_OPEN = "open";
    private Activity activity;

    public FirebaseManager(KipAlarmManager alarmManager, Activity activity) {
        this.alarmManager = alarmManager;
        this.activity = activity;
    }

    /**
     * Firebase setup.
     */
    public void setup() {
        //setup firebase
        FirebaseMessaging.getInstance().subscribeToTopic("Kip");
        firebase = new Firebase("https://kipsafe-f5610.firebaseio.com/");
        changeOpen(alarmManager.isAlarmSet());

        // onDataChange reacts to a difference between local and remote database (open/closed)
        firebase.child(KEY_OPEN).addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                //if there is a difference, use data from database
                boolean eggOpen = (boolean) dataSnapshot.getValue();
                // Update global state.
                alarmManager.setIsAlarmSet(eggOpen);

                //update egg picture
                ImageButton kipButton;
                kipButton = (ImageButton) activity.findViewById(R.id.kipButton);
                kipButton.setSelected(eggOpen);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    /**
     * update egg on all phones via firebase
     *
     * @param open boolean egg-state
     */
    public void changeOpen(boolean open) {
        // Update local.
        alarmManager.setIsAlarmSet(open);
        // Update remote.
        firebase.child("open").setValue(open);
    }

}
