package com.nl.clubbook.datasource;

import android.content.Context;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Andrew on 5/19/2014.
 */
public class DataStore {
    private static Context context;

    public static void setContext(Context mcontext) {
        context = mcontext;
    }

    public static void loginByFb(String name, String email, String fb_id, String fb_access_token, String gender, String dob, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("name", name);
        params.put("fb_id", fb_id);
        params.put("fb_access_token", fb_access_token);
        params.put("fb_token_expires", 123456);
        params.put("gender", gender);
        params.put("dob", dob);

        ClubbookRestClient.loginByFb(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers,JSONObject response_json) {
                UserDto user = new UserDto();
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        JSONObject user_dto = response_json.getJSONObject("result").getJSONObject("user");
                        user.setEmail(user_dto.getString("email"));
                        user.setGender(user_dto.getString("gender"));
                        user.setName(user_dto.getString("name"));
                        user.setId(user_dto.getString("_id"));
                        failed = false;
                    } else
                        failed = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //failed = false;
                onResultReady.onReady(user, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,java.lang.Throwable throwable, final JSONObject errorResponse)
            {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,java.lang.Throwable throwable, final JSONArray errorResponse)
            {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                //if (failed)
                //    onResultReady.onReady(null, true);
            }
        });
    }

    public interface OnResultReady {
        public void onReady(Object result, boolean failed);
    }
}
