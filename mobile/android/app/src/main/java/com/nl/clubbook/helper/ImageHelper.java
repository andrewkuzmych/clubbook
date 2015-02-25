package com.nl.clubbook.helper;

import android.text.TextUtils;

/**
 * Created by Andrew on 5/23/2014.
 */
public class ImageHelper {

    public static final String CLOUD_NAME_CLOUDINARY = "ddsoyfjll";
    public static final String UPLOAD_URL_CLOUDINARY =
            "http://res.cloudinary.com/" + CLOUD_NAME_CLOUDINARY + "/image/upload/";

    // http://res.cloudinary.com/ddsoyfjll/image/upload/v1403179538/qskqyrtbnma2r6chhdaa.jpg

    public static String generateUrl(String url, String avatarStyle) {
        String imagePart = url.substring(url.lastIndexOf('/') + 1, url.length());
        return UPLOAD_URL_CLOUDINARY + avatarStyle + "/" + imagePart;
    }

    public static String getClubListAvatar(String url) {
        String avatarStyle = "w_300,h_300,c_fit";
        return generateUrl(url, avatarStyle);
    }

    public static String getUserListAvatar(String url) {
        if(TextUtils.isEmpty(url)) {
            return "";
        }

        String avatarStyle = "w_300,h_300,c_thumb";
        return generateUrl(url, avatarStyle);
    }

    public static String getChatMessagePhotoUrl(String url, int size) {
        String avatarStyle = "h_" + size + ",w_" + size + ",c_fit";
        return generateUrl(url, avatarStyle);
    }

    public static String getClubImage(String url) {
        String avatarStyle = "c_fit,w_700";
        return generateUrl(url, avatarStyle);
    }

    public static String getProfileImage(String url) {
        String avatarStyle = "w_300,h_300,c_thumb";
        return generateUrl(url, avatarStyle);
    }

    public static String getProfileBigImage(String url, int size) {
        String avatarStyle = "w_" + size + ",h_" + size + ",c_thumb";
        return generateUrl(url, avatarStyle);
    }

    /**
     * Left navigation menu
     *
     * @param url
     * @return
     */
    public static String getUserAvatar(String url) {
        String avatar_style = "w_100,h_100,c_thumb";
        return generateUrl(url, avatar_style);
    }

    /**
     * Edit profile small preview
     *
     * @param url
     * @return
     */
    public static String getUserPhotoSmallPreview(String url) {
        String avatar_style = "w_500,h_500";
        return generateUrl(url, avatar_style);
    }

    /**
     * Edit profile big preview
     *
     * @param url
     * @return
     */
    public static String getUserPhotoBigPreview(String url) {
        String avatar_style = "c_fit,w_700";
        return generateUrl(url, avatar_style);
    }
}
