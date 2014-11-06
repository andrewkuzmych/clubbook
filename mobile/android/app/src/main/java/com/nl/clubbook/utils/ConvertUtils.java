package com.nl.clubbook.utils;

/**
 * Created by Volodymyr on 06.11.2014.
 */
public class ConvertUtils {

    private ConvertUtils() {
    }

    public static boolean intToBoolean(int value) {
        return value == 1;
    }

    public static int booleanToInt(boolean value) {
        return value ? 1 : 0;
    }
}
