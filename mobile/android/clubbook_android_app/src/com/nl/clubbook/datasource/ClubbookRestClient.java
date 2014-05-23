package com.nl.clubbook.datasource;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Andrew on 5/19/2014.
 */
public class ClubbookRestClient {
    private static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

    //private static final String BC_BASE_URL = "http://clubbook.herokuapp.com/_s/";
    private static final String BC_BASE_URL = "http://192.168.2.108:3000/_s/";
    //private static final String BC_BASE_URL = "http://mysterious-bastion-9023.herokuapp.com/_s/";

    private static String getBcAbsoluteUrl(String relativeUrl) {
        return BC_BASE_URL + relativeUrl;
    }

    public static void loginByFb(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getBcAbsoluteUrl("signin/fb"), params, responseHandler);
    }

    public static void file(RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getBcAbsoluteUrl("file"), params, responseHandler);
    }

}
