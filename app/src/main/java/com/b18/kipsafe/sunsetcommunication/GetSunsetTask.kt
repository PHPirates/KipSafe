package com.b18.kipsafe.sunsetcommunication

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import com.b18.kipsafe.R
import com.b18.kipsafe.SharedPreferenceManager
import com.b18.kipsafe.alarms.KipAlarmManager
import com.b18.kipsafe.converters.convertIsoToCalendar
import com.b18.kipsafe.converters.hourMinuteDateFormat
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.util.*

/**
 * Get sunset time and pass back results.
 */
class GetSunsetTask(private val context: Context, private val minutesBeforeSunset: Int,
                    private val delay: Delay)
    : AsyncTask<Void, Void, String>() {

    enum class Delay {
        NO_DELAY,
        ONE_DAY
    }

    override fun doInBackground(vararg p0: Void?): String {
        var urlConnection: HttpURLConnection? = null
        return try {
            val url = URL("http://api.sunrise-sunset.org/json?lat=51.546545&lng=4.411744&formatted=0")
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.doInput = true
            urlConnection.connect()

            val inputStream = urlConnection.inputStream
            inputStream.bufferedReader().use(BufferedReader::readText)
        } finally {
            if (urlConnection != null) urlConnection.disconnect()
        }
    }

    override fun onPostExecute(message: String) {
        prepareAlarm(message)
    }

    private fun prepareAlarm(response: String) {
        try {
            // Parse JSON
            val responseObject = JSONObject(response)
            val results = responseObject.getJSONObject("results")
            val time = results.getString("sunset")

            // Save the time of the sunset for possible future use in case of no internet.
            SharedPreferenceManager(context).sunset = time

            val timeCalendar = try {
                convertIsoToCalendar(time)
            } catch (e: ParseException) {
                e.printStackTrace()
                GregorianCalendar()
            }

            timeCalendar.add(Calendar.MINUTE, -minutesBeforeSunset)
            Toast.makeText(context,
                    String.format(context!!.resources.getString(R.string.alarm_set),
                            hourMinuteDateFormat.format(timeCalendar.time)),
                    Toast.LENGTH_SHORT)
                    .show()

            if (delay === Delay.ONE_DAY) timeCalendar.add(Calendar.DAY_OF_MONTH, 1)
            else { // delay must be Delay.NO_DELAY.
                val now = Calendar.getInstance()
                if (now > timeCalendar) {
                    // Now is after today's sunset, so schedule alarm for next sunset.
                    timeCalendar.add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            // Set alarm on this phone.
            val alarmManager = KipAlarmManager(context)
            alarmManager.setAlarm(timeCalendar)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}