package com.b18.kipsafe.SunsetCommunication;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.b18.kipsafe.SharedPreferenceManager;
import com.b18.kipsafe.alarms.KipAlarmManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.b18.kipsafe.converters.IsoConverterKt.convertIsoToCalendar;

/**
 * Get sunset time and pass back result.
 */

public class GetSunSetTask extends AsyncTask<Void, Void, String> {

    public enum Delay {
        NO_DELAY,
        ONE_DAY
    }

    private Context context;
    private int minutesBeforeSunset;
    private Delay delay;

    /**
     * Constructor. The delay is meant to be no delay when setting the alarm on a certain day
     * before sunset, and delay should be one day when alarm wants to repeat itself or alarm
     * is set after sunset.
     * @param context Android context.
     * @param timeBeforeSunset Number of minutes before sunset that the alarm should go off.
     * @param delay Whether to delay the alarm one day or not.
     */
    public GetSunSetTask(Context context, int timeBeforeSunset, Delay delay) {
        this.context = context;
        this.minutesBeforeSunset = timeBeforeSunset;
        this.delay = delay;
    }

    protected String doInBackground(Void... urls) {
        String response = "";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL("http://api.sunrise-sunset.org/json?lat=51.546545&lng=4.411744&formatted=0");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
            InputStream in = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
            response = stringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    protected void onPostExecute(String message) {
        prepareAlarm(message);
    }

    void prepareAlarm(String response) {
        try {
            //parse json
            JSONObject responseObject = new JSONObject(response);
            JSONObject results = responseObject.getJSONObject("results");
            String time = results.getString("sunset");

            // Save the time for possible future use in case there will be no internet.
            new SharedPreferenceManager(context).savePref(time);

            Calendar timeCal = new GregorianCalendar();
            try {
                timeCal = convertIsoToCalendar(time);
            } catch (ParseException e) {
                Toast.makeText(context, "I cannot understand the sunset time", Toast.LENGTH_SHORT).show();
            }

            //give notification before sunset as much in advance as indicated by user (default 0)
            timeCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE) - minutesBeforeSunset);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
            Toast.makeText(context, "Alarm set for " + sdf.format(timeCal.getTime()), Toast.LENGTH_SHORT).show();

            // Schedule alarm for next day if wanted.
            if (delay == Delay.ONE_DAY) {
                timeCal.set(Calendar.DAY_OF_MONTH, timeCal.get(Calendar.DAY_OF_MONTH) + 1);
            } else if (delay == Delay.NO_DELAY) {
                // Schedule alarm for next sunset
                Calendar now = Calendar.getInstance();
                if (now.compareTo(timeCal) > 0) {
                    // Now is after the sunset of today
                    // So schedule alarm for next sunset
                    timeCal.set(Calendar.DAY_OF_MONTH, timeCal.get(Calendar.DAY_OF_MONTH) + 1);
                }
            }

            //dispatch alarm with right time to all phones
//            FirebaseAlarmSender sendAlarmToAllPhones = new FirebaseAlarmSender(time);
//            sendAlarmToAllPhones.execute();

            // Set alarm only on this phone
            KipAlarmManager alarmManager = new KipAlarmManager(context);

            alarmManager.setAlarm(timeCal);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
