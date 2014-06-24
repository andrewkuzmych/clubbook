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

    private static final String BC_BASE_URL = "http://192.168.2.102:3000/_s/";
    //private static final String BC_BASE_URL = "http://clubbookapp.herokuapp.com/_s/";
    //private static final String BC_BASE_URL = "http://192.168.2.112:3000/_s/";

    private static String getBcAbsoluteUrl(String relativeUrl) {
        return BC_BASE_URL + relativeUrl;
    }

    public static void loginByFb(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getBcAbsoluteUrl("signin/fb"), params, responseHandler);
    }

    public static void regByEmail(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getBcAbsoluteUrl("signup"), params, responseHandler);
    }

    public static void loginByEmail(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getBcAbsoluteUrl("signinmail"), params, responseHandler);
    }

    public static void retrieveUser(String user_id, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("user/by_id/" + user_id), params, responseHandler);
    }
                                          //list_club/:distance/:user_lat/:user_lon
    public static void retrievePlaces(String distance, String lat, String lon, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("list_club/" + distance + "/" + lat + "/" + lon), params, responseHandler);
    }

    public static void retrievePlace(String place_id, String user_id, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("find_club/" + place_id + "/" + user_id), params, responseHandler);
    }

    public static void checkin(String place_id, String user_id, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("checkin/" + place_id + "/" + user_id), params, responseHandler);
    }

    public static void updateCheckin(String place_id, String user_id, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("checkin/update/" + place_id + "/" + user_id), params, responseHandler);
    }

    public static void checkout(String place_id, String user_id, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("checkout/" + place_id + "/" + user_id), params, responseHandler);
    }

    public static void file(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getBcAbsoluteUrl("file"), params, responseHandler);
    }

    public static void get_conversation(String user1, String user2, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("conversation/" + user1 + "/" + user2), params, responseHandler);
    }

    public static void get_conversations(String user_id, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("conversations/" + user_id ), params, responseHandler);
    }

    public static void send_msg(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getBcAbsoluteUrl("chat"), params, responseHandler);
    }

    public static void unread_messages_count(String user_id, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("unread/messages/count/" + user_id), params, responseHandler);
    }

    public static void read_messages(String chat_id, String user_id, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getBcAbsoluteUrl("readchat/" + chat_id + "/" + user_id), params, responseHandler);
    }
}
