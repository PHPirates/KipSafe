package com.b18.kipsafe;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.messaging.FirebaseMessaging;
import com.winsontan520.wversionmanager.library.WVersionManager;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
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

    boolean open;
    String KEY_OPEN = "open";
    ImageButton kipButton;
    String time;
    BaseClass baseClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);

        WVersionManager versionManager = new WVersionManager(this);
        versionManager.setVersionContentUrl("https://github.com/PHPirates/KipSafe/raw/master/version.json");
        versionManager.setUpdateUrl("https://github.com/PHPirates/KipSafe/raw/master/Kipsafe/app/app-release.apk");
        versionManager.checkVersion();

        kipButton = (ImageButton)findViewById(R.id.kipButton);

        FirebaseMessaging.getInstance().subscribeToTopic("Kip");

        Firebase firebase = new Firebase("https://kipsafe-f5610.firebaseio.com/");
        baseClass = new BaseClass(firebase,open);

        firebase.child(KEY_OPEN).addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                open = (boolean) dataSnapshot.getValue();
                kipButton.setSelected(open);
                baseClass.changeOpen(open);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    /**
     * fires on button released
     * @param v kip button
     */
    public void hitKip(View v) {
        open = !open;
        baseClass.changeOpen(open);
        v.setSelected(open);
        if(open) {
            GetSunSetTask getSunSetTask = new GetSunSetTask();
            getSunSetTask.execute();
            startHandler(getSunSetTask);
        }

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
                    Toast.makeText(getBaseContext(),"The sun could not be reached, therefore we couldn't ask him for his sleepy time.",Toast.LENGTH_SHORT).show();
                }

            }
        }, 2000);
    }

    void parseResult(String response) {
        try {
            //parse json
            JSONObject responseObject = new JSONObject(response);
            JSONObject results = responseObject.getJSONObject("results");
            time = results.getString("sunset");
            Calendar timeCal = convertIsoToCal(time);
            //give notification an hour before sunset
            timeCal.set(Calendar.HOUR_OF_DAY,timeCal.get(Calendar.HOUR_OF_DAY)-1);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
            Toast.makeText(getBaseContext(),"Alarm set for "+sdf.format(timeCal.getTime()),Toast.LENGTH_SHORT).show();
            SendJson sendJson = new SendJson();
            sendJson.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC")); //time is in UTC
            return sdf.parse(isoTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null; //default
    }

    private class SendJson extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {

        }

        protected String doInBackground(Void... urls) {

            String postMessage="{\n" +
                    "\t\"to\": \"/topics/Kip\",\n" +
                    "\"data\": {\n" +
                            "\"time\":\"" + time +
                        "\"}" +
                    "}";

            String response = "";

            try {
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", "key=AIzaSyBmJGCute8urbujBRypnZb1WhE2gqFEyEQ");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoInput(true);
                urlConnection.connect();

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(postMessage);
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
                wr.flush();
                wr.close();
            } catch(Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String response) {
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
