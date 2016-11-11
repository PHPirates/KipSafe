package com.b18.kipsafe;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * fires on button released
     * @param v kip button
     */
    public void hitKip(View v) {
        GetSunSetTask getSunSetTask = new GetSunSetTask();
        getSunSetTask.execute();
        startHandler(getSunSetTask);
    }

    void scheduleAlarm(Calendar timeCal) {
        //schedule alarm
//        timeCal.set(Calendar.HOUR_OF_DAY,13);
//        timeCal.set(Calendar.MINUTE,9);
        //give notification an hour before sunset
        timeCal.set(Calendar.HOUR_OF_DAY,timeCal.get(Calendar.HOUR_OF_DAY)-1);
        //toast
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        Toast.makeText(getBaseContext(),"Alarm set for "+sdf.format(timeCal.getTime()),Toast.LENGTH_SHORT).show();
        //time received is in UTC
        Intent intentAlarm = new Intent(this,AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeCal.getTimeInMillis(),
                PendingIntent.getBroadcast(this,1,intentAlarm,PendingIntent.FLAG_UPDATE_CURRENT));
    }

    /**
     * Starts handler to handle a dataSender and kill it after 2 seconds
     * @param getSunSetTask getSunSetTask object
     */
    public void startHandler(final GetSunSetTask getSunSetTask) {
        //start a new handler that will cancel the AsyncTask after x seconds
        //in case the Arduino can't be reached
        Handler handler = new Handler(Looper.getMainLooper()); //make sure to start from UI thread
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getSunSetTask.getStatus() == AsyncTask.Status.RUNNING) {
                    getSunSetTask.cancel(true);
                    Toast.makeText(getBaseContext(),"The Arduino could not be reached, request terminated.",Toast.LENGTH_SHORT).show();
                }

            }
        }, 2000);
    }

    /**
     * convert iso 8601 string to calendar object
     *
     * @param isoTime time in UTC
     * @return calendar object
     */
    Calendar convertIsoToCal(String isoTime) {
        Date date = convertIsoToDate(isoTime);
        Calendar c = new GregorianCalendar(); //defaults to good timezone
        try { // in case date == null
            c.setTime(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    /**
     * convert a time string in ns format to a date object
     *
     * @param isoTime time in ns format
     * @return date object
     */
    private Date convertIsoToDate(String isoTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC")); //time is in UTC
            return sdf.parse(isoTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null; //default
    }

    void parseResult(String response) {
        try {
            //parse json
            JSONObject responseObject = new JSONObject(response);
            JSONObject results = responseObject.getJSONObject("results");
            String sunset = results.getString("sunset");
            scheduleAlarm(convertIsoToCal(sunset));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetSunSetTask extends AsyncTask<Void, Void, String> {
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
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return response;
        }

        protected void onPostExecute(String message) {
            parseResult(message);
        }
    }
}
