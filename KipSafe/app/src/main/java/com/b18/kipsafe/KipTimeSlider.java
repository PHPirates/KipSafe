package com.b18.kipsafe;

import android.app.Activity;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Timeslider to adjust the alarm time before
 */

public class KipTimeSlider {

    private Activity activity;

    public KipTimeSlider(Activity activity) {
        this.activity = activity;
    }

    public void setup() {
        //setup timeslider

        SeekBar timeslider = (SeekBar) activity.findViewById(R.id.timeslider);

        final DataManager dataManager = new DataManager(activity);

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
    private void writeSliderText(int t) {
        final TextView slidertext = (TextView) activity.findViewById(R.id.timeslidertext);
        if (t == 0) {
            slidertext.setText(R.string.slidertext_zero);
        } else {
            slidertext.setText(String.format(activity.getResources().getString(R.string.slidertext_one), t));
        }
    }

}
