package com.b18.kipsafe

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.b18.kipsafe.alarms.KipAlarmManager
import com.b18.kipsafe.databinding.ActivityMainBinding
import com.b18.kipsafe.firebase.FirebaseManager
import com.b18.kipsafe.sunsetcommunication.GetSunsetTask

class MainActivity : AppCompatActivity() {
    private lateinit var alarmManager: KipAlarmManager
    lateinit var binding: ActivityMainBinding

    private val database = FirebaseManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmManager = KipAlarmManager(this)
        KipTimeSlider(this).setup()

        // Update the egg picture.
        val kipButton = binding.kipButton
        kipButton.isSelected = alarmManager.isAlarmSet()

        // Make a checkbox to set alarm on weekend only.
        WeekendCheckBox(this)

        database.setupListener()
    }

    /**
     * Fires when the kip button is released.
     *
     * @param view Kip button.
     */
    fun hitKip(view: View) {
        var isAlarmSet = alarmManager.isAlarmSet()
        isAlarmSet = !isAlarmSet

        alarmManager.setIsAlarmSet(isAlarmSet)
        database.updateValue(isAlarmSet)

        if(isAlarmSet) {
            val sharedPreferenceManager = SharedPreferenceManager(this)
            val handler = AlarmSetter(this)
            handler.set(sharedPreferenceManager.minutesBeforeSunset,
                    GetSunsetTask.Delay.NO_DELAY)
        } else {
            alarmManager.cancelAlarm()
            Toast.makeText(baseContext, R.string.cancel_alarm, Toast.LENGTH_SHORT).show()
        }
    }
}