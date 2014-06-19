package com.nl.clubbook.helper;

/**
 * Created by Andrew on 5/23/2014.
 */
public class ImageHelper {

    public static final String CLOUD_NAME_CLOUDINARY = "ddsoyfjll";
    public static final String UPLOAD_URL_CLOUDINARY =
            "http://res.cloudinary.com/" + CLOUD_NAME_CLOUDINARY + "/image/upload/";

    // http://res.cloudinary.com/ddsoyfjll/image/upload/v1403179538/qskqyrtbnma2r6chhdaa.jpg

    public static String GenarateUrl(String url, String param)
    {
        String last = url.substring(url.lastIndexOf('/')+1, url.length() );

        String first = url.substring( 0, url.lastIndexOf('/')+1 );

        return first + param + "/" +last;
    }

    public static String getUserAvatar(String url)
    {
        String image_part = url.substring(url.lastIndexOf('/')+1, url.length());
        String avatar_style= "w_100,h_100,c_thumb,g_face";

        return UPLOAD_URL_CLOUDINARY + "/" + avatar_style + "/" + image_part;
    }
}
