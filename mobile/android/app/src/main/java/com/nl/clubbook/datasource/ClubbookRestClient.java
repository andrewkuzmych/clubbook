package com.nl.clubbook.datasource;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Andrew on 5/19/2014.
 */
public class ClubbookRestClient {
    private static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    static {
        client.setTimeout(20*1000);
    }

    //private static final String BC_BASE_URL = "http://10.0.0.104:3000/_s/";
    private static final String BC_BASE_URL = "http://clubbookapp.herokuapp.com/_s/";
    //private static final String BC_BASE_URL = "http://192.168.1.16:3000/_s/";

    private static String getBcAbsoluteUrl(String relativeUrl) {
        return BC_BASE_URL + relativeUrl;
    }

    public static void loginByFb(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getBcAbsoluteUrl("signin/fb"), params, responseHandler);
    }

    public static void regByEmail(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getBcAbsoluteUrl("signup"), params, responseHandler);
    }

    public static void updateProfile(String user_id, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.put(getBcAbsoluteUrl("obj/user/" + user_id), params, responseHandler);
    }

    public static void profileAddImage(String user_id, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getBcAbsoluteUrl("obj/user/" + user_id + "/image"), params, responseHandler);
    }

    public static void profileUpdateImage(String user_id, String image_id, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.put(getBcAbsoluteUrl("obj/user/" + user_id + "/image/" + image_id), params, responseHandler);
    }

    public static void profileDeleteImage(String user_id, String image_id, AsyncHttpResponseHandler responseHandler) {
        client.delete(getBcAbsoluteUrl("obj/user/" + user_id + "/image/" + image_id), responseHandler);
    }

    public static void loginByEmail(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getBcAbsoluteUrl("signinmail"), params, responseHandler);
    }

    public static void retrieveUser(String user_id, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("user/by_id/" + user_id), params, responseHandler);
    }

    public static void retrieveUserFriend(String friendId, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("obj/user/" + friendId), params, responseHandler);
    }

    public static void retrieveFriends(String user_id, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("obj/user/" + user_id + "/friends"), params, responseHandler);
    }

    public static void addFriend(String userId, String friendId, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("obj/user/" + userId + "/friends/" + friendId + "/friend"), params, responseHandler);
    }

    public static void removeFriend(String userId, String friendId, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("obj/user/" + userId + "/friends/" + friendId + "/remove"), params, responseHandler);
    }

    //list_club/:distance/:user_lat/:user_lon
    public static void retrievePlaces(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("obj/club"), params, responseHandler);
    }

    public static void retrievePlace(String placeId, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("obj/club/" + placeId), params, responseHandler);
    }

    public static void checkin(String placeId, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("obj/club/" + placeId + "/checkin"), params, responseHandler);
    }

    public static void updateCheckin(String placeId, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("obj/club/" + placeId + "/update"), params, responseHandler);
    }

    public static void checkout(String placeId, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("obj/club/" + placeId + "/checkout"), params, responseHandler);
    }

    public static void file(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getBcAbsoluteUrl("file"), params, responseHandler);
    }

    public static void sendMsg(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getBcAbsoluteUrl("obj/chat"), params, responseHandler);
    }

    public static void getConversation(String currentUserId, String receiverUserId, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("obj/chat/" + currentUserId + "/" + receiverUserId), params, responseHandler);
    }

    public static void readMessages(String currentUser, String receiver, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("obj/chat/" + currentUser + "/" + receiver + "/read"), params, responseHandler);
    }

    public static void getConversations(String userId, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("obj/chat/" + userId ), params, responseHandler);
    }

    public static void getNotifications(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("obj/user/me/notifications"), params, responseHandler);
    }
}
