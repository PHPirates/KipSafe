package com.b18.kipsafe.firebase

import com.b18.kipsafe.MainActivity
import com.b18.kipsafe.alarms.KipAlarmManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * Manage the communication with the Firebase Realtime Database.
 *
 * @property mainActivity Required to give the user feedback when the value in the database has changed,
 * and to update the value in local storage (shared preferences need a context).
 */
class FirebaseManager(val mainActivity: MainActivity) {
    private val alarmManager: KipAlarmManager = KipAlarmManager(mainActivity)
    private val database = Firebase.database.getReference("open")

    /**
     * Attach a listener to the database that listens to changes of the `open` property.
     *
     * When receiving a value from the database, set or cancel the alarm and update the picture of the egg.
     */
    fun setupListener() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val eggOpen = snapshot.getValue(Boolean::class.java) ?: return

                // Update global state.
                alarmManager.setIsAlarmSet(eggOpen)

                //update egg picture
                mainActivity.binding.kipButton.isSelected = eggOpen
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    fun updateValue(open: Boolean) {
        database.setValue(open)
    }
}