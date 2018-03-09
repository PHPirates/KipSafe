package com.b18.kipsafe

import android.app.Activity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Represents the checkbox to set alarm on weekend only.
 */
class WeekendCheckBox(activity: Activity) {

    init {

        val sharedPreferenceManager = SharedPreferenceManager(activity)

        // Update on initialisation with old value.
        activity.weekend_checkbox.isChecked = sharedPreferenceManager.isWeekendOnly

        activity.weekend_checkbox.setOnCheckedChangeListener {
            _, isChecked: Boolean ->
            sharedPreferenceManager.isWeekendOnly = isChecked
        }
    }

}