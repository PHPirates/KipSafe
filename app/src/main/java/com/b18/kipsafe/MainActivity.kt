package com.b18.kipsafe

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import com.b18.kipsafe.alarms.KipAlarmManager
import com.b18.kipsafe.sunsetcommunication.GetSunsetTask
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var alarmManager: KipAlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        alarmManager = KipAlarmManager(this)
        KipTimeSlider(this).setup()

        // Update the egg picture.
        val kipButton = kipButton as ImageButton
        kipButton.isSelected = alarmManager.isAlarmSet()
    }

    /**
     * Fires when the kip button is released.
     *
     * @param view Kip button.
     */
    fun hitKip(view: View) {
        var isAlarmSet = alarmManager.isAlarmSet()
        isAlarmSet = !isAlarmSet

        alarmManager.setIsAlarmSet(isAlarmSet)  // User feedback.

        if(isAlarmSet) {
            val sharedPreferenceManager = SharedPreferenceManager(this)
            val handler = AlarmSetter(this)
            handler.set(sharedPreferenceManager.getMinutes(),
                    GetSunsetTask.Delay.NO_DELAY)
        } else {
            alarmManager.cancelAlarm()
            Toast.makeText(baseContext, R.string.cancel_alarm, Toast.LENGTH_SHORT).show()
        }
    }
}