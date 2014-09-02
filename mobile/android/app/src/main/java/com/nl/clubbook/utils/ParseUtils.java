package com.nl.clubbook.utils;

/**
 * Created by Volodymyr on 02.09.2014.
 */
public class ParseUtils {

    private ParseUtils() {
    }

    public static int parseInt(String valueToParse) {
        int value = 0;

        try {
            value = Integer.parseInt(valueToParse);
        } catch (NumberFormatException e) {
            L.i("" + e);
        }

        return value;
    }
}
