package com.nl.clubbook.helper;

/**
 * Created by Andrew on 5/23/2014.
 */
public class ImageHelper {

    public static final String CLOUD_NAME_CLOUDINARY = "ddsoyfjll";
    public static final String UPLOAD_URL_CLOUDINARY =
            "http://res.cloudinary.com/" + CLOUD_NAME_CLOUDINARY + "/image/upload/";

    // http://res.cloudinary.com/ddsoyfjll/image/upload/v1403179538/qskqyrtbnma2r6chhdaa.jpg

    public static String generateUrl(String url, String avatar_style) {
        String image_part = url.substring(url.lastIndexOf('/') + 1, url.length());

        return UPLOAD_URL_CLOUDINARY + avatar_style + "/" + image_part;
    }

    public static String getUserAvatar(String url) {
        String avatar_style = "w_100,h_100,c_thumb,g_face";
        return generateUrl(url, avatar_style);
    }

    public static String getUserAvatarProfileUi(String url) {
        String avatar_style = "w_100,h_100,c_thumb,g_face";
        return generateUrl(url, avatar_style);
    }

    public static String getSquareUserAvatar(String url) {
        String avatar_style = "w_100,h_100,c_fit";
        return generateUrl(url, avatar_style);
    }
}
