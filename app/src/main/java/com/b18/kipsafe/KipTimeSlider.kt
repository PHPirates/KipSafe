package com.b18.kipsafe

import android.app.Activity
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

/**
 * TimeSlider to adjust the alarm time before sunset.
 */
class KipTimeSlider(private val activity: Activity) {

    /**
     * Setup the TimeSlider.
     */
    fun setup() {
        val sharedPreferenceManager = SharedPreferenceManager(activity)

        // Default is zero if no time is set.
        val prefTime = sharedPreferenceManager.minutesBeforeSunset
        if (prefTime == -1) {
            sharedPreferenceManager.minutesBeforeSunset = 0
            writeSliderText(0)
        } else {
            sharedPreferenceManager.minutesBeforeSunset = prefTime
            writeSliderText(prefTime)
        }

        activity.timeslider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, minutes: Int, b: Boolean) {
                writeSliderText(minutes)
                sharedPreferenceManager.minutesBeforeSunset = minutes
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
    }

    /**
     * Write text above the slider.
     *
     * @param t The minutes to set the alarm before sunset.
     */
    fun writeSliderText(t: Int) {
        if (t == 0) {
            activity.timeslidertext.text =
                    activity.resources.getString(R.string.slidertext_zero)
        } else {
            activity.timeslidertext.text = String.format(
                    activity.resources.getString(R.string.slidertext_one), t)
        }
    }
}