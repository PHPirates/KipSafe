package com.b18.kipsafe

import android.app.Activity

/**
 * Represents the checkbox to set alarm on weekend only.
 */
class WeekendCheckBox(activity: MainActivity) {

    init {

        val sharedPreferenceManager = SharedPreferenceManager(activity)

        // Update on initialisation with old value.
        activity.binding.weekendCheckbox.isChecked = sharedPreferenceManager.isWeekendOnly

        activity.binding.weekendCheckbox.setOnCheckedChangeListener {
            _, isChecked: Boolean ->
            sharedPreferenceManager.isWeekendOnly = isChecked
        }
    }

}