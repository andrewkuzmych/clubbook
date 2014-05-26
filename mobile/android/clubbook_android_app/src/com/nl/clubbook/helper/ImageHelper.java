package com.nl.clubbook.helper;

/**
 * Created by Andrew on 5/23/2014.
 */
public class ImageHelper {

    public static String GenarateUrl(String url, String param)
    {
        String last = url.substring(url.lastIndexOf('/')+1, url.length() );

        String first = url.substring( 0, url.lastIndexOf('/')+1 );

        return first + param + "/" +last;
    }
}
