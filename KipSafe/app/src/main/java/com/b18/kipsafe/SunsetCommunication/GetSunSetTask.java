package com.b18.kipsafe.SunsetCommunication;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.b18.kipsafe.Alarms.AlarmScheduler;
import com.b18.kipsafe.IsoConverter;

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

/**
 * Get sunset time and pass back result.
 */

public class GetSunSetTask extends AsyncTask<Void, Void, String> {

    private Context context;
    private int prefTime;

    public GetSunSetTask(Context context, int prefTime) {
        this.context = context;
        this.prefTime = prefTime;
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
            Calendar timeCal = new GregorianCalendar();
            try {
                timeCal = IsoConverter.convertIsoToCal(time);
            } catch (ParseException e) {
                Toast.makeText(context, "I cannot understand the sunset time", Toast.LENGTH_SHORT).show();
            }
            //give notification before sunset as much in advance as indicated by user (default 0)
            timeCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE) - prefTime);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
            Toast.makeText(context, "Alarm set for " + sdf.format(timeCal.getTime()), Toast.LENGTH_SHORT).show();

            //dispatch alarm with right time to all phones
//            FirebaseAlarmSender sendAlarmToAllPhones = new FirebaseAlarmSender(time);
//            sendAlarmToAllPhones.execute();
            AlarmScheduler alarmScheduler = new AlarmScheduler(context);
            // todo debug
            timeCal = new GregorianCalendar();
            timeCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE) + 2);

            alarmScheduler.scheduleAlarm(timeCal);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
