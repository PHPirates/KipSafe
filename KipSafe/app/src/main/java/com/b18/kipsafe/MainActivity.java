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
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
            SendJson sendJson = new SendJson();
            sendJson.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
