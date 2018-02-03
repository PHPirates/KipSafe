package com.b18.kipsafe.converters

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Methods to convert ISO formatted time to other formats and back.
 */


/**
 * Convert ISO 8601 string to a calendar object.
 *
 * @param isoTime String: time in ISO format.
 * @return Calendar object.
 */
@Throws(ParseException::class)
fun convertIsoToCalendar(isoTime: String): Calendar {
    val date = convertIsoToDate(isoTime)
    val c = GregorianCalendar()  // The default gets the good time zone.
    try {  // Date might be null.
        c.time = date
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return c
}


/**
 * Convert a time string in ISO 8601 format to a Date object.
 *
 * @param isoTime String: time in ISO format.
 * @return Date object.
 */
@Throws(ParseException::class)
fun convertIsoToDate(isoTime: String): Date {
    try {
        isoDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return isoDateFormat.parse(isoTime)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    throw ParseException("Failed to convert ISO format to Date.", 0)
}
