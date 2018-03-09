package com.b18.kipsafe

import android.app.Activity
import android.widget.CheckBox
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Represents the checkbox to set alarm on weekend only.
 */
class WeekendCheckBox(activity: Activity) {

    init {

        val sharedPreferenceManager = SharedPreferenceManager(activity)

//        val savedValue = sharedPreferenceManager.

        activity.weekend_checkbox.setOnCheckedChangeListener {
            checkbox: CompoundButton?, isChecked: Boolean ->

        }
    }

}