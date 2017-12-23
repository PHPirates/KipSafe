package com.b18.kipsafe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * converts Iso formats to other formats, and back.
 */
public class IsoConverter {

    /**
     * convert iso 8601 string to calendar object
     *
     * @param isoTime time in UTC
     * @return calendar object
     */
    public static Calendar convertIsoToCal(String isoTime) throws ParseException {
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
    public static Date convertIsoToDate(String isoTime) throws ParseException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC")); //time is in UTC
            return sdf.parse(isoTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new ParseException("Failed to convert Iso format to Date", 0);
    }

}
