package com.b18.kipsafe.Firebase;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Sends via firebase a message to all phones with the time at which the alarm should fire
 */
public class FirebaseAlarmSender extends AsyncTask<Void, Void, String> {

    private String time;

    public FirebaseAlarmSender(String time) {
        this.time = time;
    }

    protected void onPreExecute() {

    }

    protected String doInBackground(Void... urls) {

        String postMessage = "{\n" +
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    protected void onPostExecute(String response) {
    }
}
