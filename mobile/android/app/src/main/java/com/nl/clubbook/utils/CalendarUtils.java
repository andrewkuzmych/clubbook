package com.nl.clubbook.utils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by User on 23.08.2014.
 */
public class CalendarUtils {

    private CalendarUtils() {
    }

    public static long getCurrentTimeWithoutHours() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.HOUR);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);

        return calendar.getTimeInMillis();
    }

    public static int getDayTimeInMilliseconds() {
        return 24 * 60 * 60 * 1000;
    }

    public static int getTimeDifferenceFromUTC() {
        return TimeZone.getDefault().getRawOffset();
    }
}
