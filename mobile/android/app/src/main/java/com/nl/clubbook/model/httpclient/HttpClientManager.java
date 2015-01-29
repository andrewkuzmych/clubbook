package com.nl.clubbook.model.httpclient;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.nl.clubbook.model.ClubbookPreferences;
import com.nl.clubbook.model.data.Chat;
import com.nl.clubbook.model.data.JSONConverter;
import com.nl.clubbook.model.data.Place;
import com.nl.clubbook.model.data.User;
import com.nl.clubbook.model.data.UserPhoto;
import com.nl.clubbook.ui.fragment.PlacesFragment;
import com.nl.clubbook.utils.CalendarUtils;
import com.nl.clubbook.utils.L;

import org.apache.http.Header;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Andrew on 5/19/2014.
 */
public class HttpClientManager {

    private static HttpClientManager sManager;

    private RequestHandle mRetrieveAllPlaceRequestHandle;

    public static HttpClientManager getInstance() {
        if(sManager == null) {
            sManager = new HttpClientManager();
        }

        return sManager;
    }

    private HttpClientManager() {
    }

    public void regByEmail(String name, String email, String pass, String gender, String dob, String country, String city, String bio, JSONObject avatar,
                                  final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("name", name);
        params.put("gender", gender);
        params.put("password", pass);
        params.put("dob", dob);
        params.put("city", city);
        params.put("country", country);
        params.put("bio", bio);
        params.put("avatar", avatar);

        ClubbookRestClient.regByEmail(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equalsIgnoreCase(responseJson.optString("status"))) {
                    User user = new User(responseJson.optJSONObject("result").optJSONObject("user"));
                    onResultReady.onReady(user, false);
                } else {
                    onResultReady.onReady(new User(), true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void updateUserProfile(String accessToken, String name, String gender, String dob, String country, String bio, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("name", name);
        params.put("gender", gender);
        params.put("dob", dob);
        params.put("country", country);
        params.put("bio", bio);
        params.put("access_token", accessToken);

        ClubbookRestClient.updateProfile(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                User user = null;
                if ("ok".equalsIgnoreCase(responseJson.optString("status"))) {
                    user = new User(responseJson.optJSONObject("result").optJSONObject("user"));
                    failed = false;
                } else {
                    failed = true;
                }

                onResultReady.onReady(user, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void updateNotificationEnabling(String accessToken, String enablingState, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("push_not", enablingState);
        params.put("access_token", accessToken);

        ClubbookRestClient.updateProfile(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equalsIgnoreCase(responseJson.optString("status"))) {
                    failed = false;
                } else {
                    failed = true;
                }

                onResultReady.onReady(null, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void updateVisibleNearby(String accessToken, String isVisibleNearby, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("is_visible_nearby", isVisibleNearby);
        params.put("access_token", accessToken);

        ClubbookRestClient.updateProfile(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equalsIgnoreCase(responseJson.optString("status"))) {
                    failed = false;
                } else {
                    failed = true;
                }

                onResultReady.onReady(null, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void deleteProfile(Context context, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.deleteProfile(context, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equals(responseJson.optString("status"))) {
                    onResultReady.onReady(null, false);
                } else {
                    onResultReady.onReady(null, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void profileAddImage(String accessToken, String userId, JSONObject avatar, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);
        params.put("avatar", avatar);

        ClubbookRestClient.profileAddImage(userId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                UserPhoto userPhoto = null;
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        userPhoto = new UserPhoto(responseJson.getJSONObject("result").getJSONObject("image"));
                        failed = false;
                    } else {
                        failed = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                onResultReady.onReady(userPhoto, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void profileDeleteImage(Context context, String accessToken, String userId, String imageId, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.profileDeleteImage(context, userId, imageId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        failed = false;
                    } else {
                        failed = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                onResultReady.onReady(null, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void profileUpdateImage(String accessToken, String userId, String imageId, Boolean isAvatar, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);
        params.put("is_avatar", isAvatar);

        ClubbookRestClient.profileUpdateImage(userId, imageId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                User user = null;
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        user = new User(responseJson.getJSONObject("result").getJSONObject("user"));
                        failed = false;
                    } else {
                        failed = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                onResultReady.onReady(user, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void loginByEmail(String email, String pass, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", pass);

        ClubbookRestClient.loginByEmail(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                User user = null;
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        user = new User(responseJson.getJSONObject("result").getJSONObject("user"));
                        failed = false;
                    } else {
                        failed = true;
                    }
                } catch (Exception e) {
                    L.i("" + e);
                }

                onResultReady.onReady(user, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void loginByFb(String name, String email, String fbId, String fbAccessToken, String gender, String dob, JSONObject avatar,
                                 final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        if(email != null && !email.equalsIgnoreCase("null")) {
            params.put("email", email);
        }
        params.put("name", name);
        params.put("fb_id", fbId);
        params.put("fb_access_token", fbAccessToken);
        params.put("gender", gender);
        if(dob != null && !dob.equalsIgnoreCase("null")) {
            params.put("dob", dob);
        }
        params.put("avatar", avatar);

        ClubbookRestClient.loginByFb(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                User user = null;
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        user = new User(responseJson.getJSONObject("result").getJSONObject("user"));
                        failed = false;
                    } else {
                        failed = true;
                    }
                } catch (Exception e) {
                    L.i("" + e);
                }

                onResultReady.onReady(user, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void retrievePlaces(String type, String search, String skip, String take, String lat, String lon, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.add("skip", skip);
        params.add("take", take);
        params.add("user_lat", lat);
        params.add("user_lon", lon);
        params.add("type", type);
        params.add("search", search);
        params.add("access_token", accessToken);

        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                JSONArray jsonArrClubs = responseJson.optJSONArray("clubs");
                List<Place> places = JSONConverter.newPlaceList(jsonArrClubs);

                onResultReady.onReady(places, false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }
        };

        // we need instance of getAllPlace request for canceling it when we search. In other way, we will have bug with displaying correct finding results, when one request finished faster then other one
        // it's only implemented for GetAllPlaces

        if(mRetrieveAllPlaceRequestHandle != null && !mRetrieveAllPlaceRequestHandle.isFinished() && PlacesFragment.Types.ALL.equalsIgnoreCase(type)) {
            mRetrieveAllPlaceRequestHandle.cancel(true);
        }

        if(PlacesFragment.Types.ALL.equalsIgnoreCase(type)) {
            mRetrieveAllPlaceRequestHandle = ClubbookRestClient.retrievePlaces(params, handler);
        } else {
            ClubbookRestClient.retrievePlaces(params, handler);
        }
    }

    public void retrieveYesterdayCheckedInPlaces(String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.add("access_token", accessToken);

        ClubbookRestClient.retrieveYesterdayCheckedInPlaces(params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if(responseJson == null || !"OK".equalsIgnoreCase(responseJson.optString("status"))) {
                    onResultReady.onReady(null, true);
                    return;
                }

                JSONArray jsonArrClubs = responseJson.optJSONArray("clubs");
                List<Place> places = JSONConverter.newPlaceList(jsonArrClubs);

                onResultReady.onReady(places, false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }
        });
    }

    public void retrieveFastCheckInClub(String lat, String lon, String distance, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.add("user_lat", lat);
        params.add("user_lon", lon);
        params.add("access_token", accessToken);
        params.add("distance", distance);

        ClubbookRestClient.retrievePlaces(params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                JSONArray jsonArrClubs = responseJson.optJSONArray("clubs");
                List<Place> places = JSONConverter.newPlaceList(jsonArrClubs);

                onResultReady.onReady(places, false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }
        });
    }

    public void retrieveClubCheckedInUsers(String clubId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.retrieveClubCheckedInUsers(clubId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                String status = responseJson.optString("status");
                List<User> users = new ArrayList<User>();

                if("ok".equalsIgnoreCase(status)) {
                    failed = false;

                    JSONArray jsonArrUsers = responseJson.optJSONArray("users");
                    if(jsonArrUsers != null && jsonArrUsers.length() != 0) {
                        users = JSONConverter.newUsersList(jsonArrUsers, true);
                    }
                } else {
                    failed = true;
                }

                onResultReady.onReady(users, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }
        });
    }

    public void retrieveClubYesterdayCheckedInUsers(String clubId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.retrieveClubYesterdayCheckedInUsers(clubId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                String status = responseJson.optString("status");

                if("ok".equalsIgnoreCase(status)) {
                    String msg = responseJson.optString("msg");
                    if(TextUtils.isEmpty(msg)) {
                        List<User> users = new ArrayList<User>();
                        JSONArray jsonArrUsers = responseJson.optJSONArray("users");
                        if(jsonArrUsers != null && jsonArrUsers.length() != 0) {
                            users = JSONConverter.newUsersList(jsonArrUsers, true);
                        }

                        onResultReady.onReady(users, false);
                    } else {
                        onResultReady.onReady(msg, true);
                    }

                } else {
                    onResultReady.onReady(null, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }
        });
    }

    public void retrieveNearbyUsers(String requestType, String gender, String accessToken, String userLat, String userLong, String distance, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("gender", gender);
        params.put("user_lat", userLat);
        params.put("user_lon", userLong);
        params.put("distance", distance);
        params.put("access_token", accessToken);

        ClubbookRestClient.retrieveNearbyUsers(requestType, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                String status = responseJson.optString("status");

                if("ok".equalsIgnoreCase(status)) {
                    List<User> users = new ArrayList<User>();
                    JSONArray jsonArrUsers = responseJson.optJSONArray("users");
                    if(jsonArrUsers != null && jsonArrUsers.length() != 0) {
                        users = JSONConverter.newUsersList(jsonArrUsers, true);
                    }

                    onResultReady.onReady(users, false);
                } else {
                    onResultReady.onReady(null, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }
        });
    }

    public void retrieveUser(String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.retrieveUser(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                User user = null;
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        user = new User(responseJson.getJSONObject("result").getJSONObject("user"));
                        failed = false;
                    } else {
                        failed = true;
                    }
                } catch (Exception e) {
                    L.i("" + e);
                }

                //failed = false;
                onResultReady.onReady(user, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
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

    public void retrieveFriends(String userId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.retrieveFriends(userId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                List<User> friends = new ArrayList<User>();

                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        JSONArray friendsJson = responseJson.getJSONObject("result").getJSONArray("friends");
                        friends = JSONConverter.newUsersList(friendsJson, true);

                        failed = false;
                    } else {
                        failed = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                onResultReady.onReady(friends, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
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

    public void getFacebookFriendsOnClubbook(String accessToken, List<String> fbIds, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        StringBuilder fbIdsParamsBuilder = new StringBuilder();
        for(int i = 0; i < fbIds.size(); i++) {
            String id = fbIds.get(i);
            fbIdsParamsBuilder.append(id);

            if(i < (fbIds.size() - 1)) {
                fbIdsParamsBuilder.append(",");
            }
        }
        params.put("fb_ids", fbIdsParamsBuilder.toString());

        ClubbookRestClient.getFacebookFriendsOnClubbook(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                List<User> users = new ArrayList<User>();

                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        JSONArray friendsJson = responseJson.getJSONObject("result").getJSONArray("users");
                        users = JSONConverter.newUsersList(friendsJson, true);

                        failed = false;
                    } else {
                        failed = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                onResultReady.onReady(users, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }


            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void invitedFriendsToClubbookFbIds(String profileId, String accessToken, List<String> fbIds, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        StringBuilder fbIdsParamsBuilder = new StringBuilder();
        for(int i = 0; i < fbIds.size(); i++) {
            String id = fbIds.get(i);
            fbIdsParamsBuilder.append(id);

            if(i < (fbIds.size() - 1)) {
                fbIdsParamsBuilder.append(",");
            }
        }
        params.put("fb_ids", fbIdsParamsBuilder.toString());

        ClubbookRestClient.invitedFriendsToClubbookFbIds(profileId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                List<User> users = new ArrayList<User>();

                onResultReady.onReady(users, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
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

    public void retrievePendingFriends(String userId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.retrievePendingFriends(userId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                List<User> friends = new ArrayList<User>();

                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        JSONArray friendsJson = responseJson.getJSONObject("result").getJSONArray("friends");
                        friends = JSONConverter.newUsersList(friendsJson, false);

                        failed = false;
                    } else {
                        failed = true;
                    }
                } catch (Exception e) {
                    L.i("" + e);
                }

                onResultReady.onReady(friends, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
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

    public void addFriendRequest(String userId, String friendId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.addFriend(userId, friendId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equalsIgnoreCase(responseJson.optString("status"))) {
                    onResultReady.onReady(null, false);
                } else {
                    onResultReady.onReady(null, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }


            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void acceptFriendRequest(String userId, String friendId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.acceptFriend(userId, friendId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equalsIgnoreCase(responseJson.optString("status"))) {
                    onResultReady.onReady(null, false);
                } else {
                    onResultReady.onReady(null, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }


            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void cancelFriendRequest(String userId, String friendId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.cancelFriend(userId, friendId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equalsIgnoreCase(responseJson.optString("status"))) {
                    onResultReady.onReady(null, false);
                } else {
                    onResultReady.onReady(null, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }


            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void blockUserRequest(String userId, String friendId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.blockUserFriend(userId, friendId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equalsIgnoreCase(responseJson.optString("status"))) {
                    onResultReady.onReady(null, false);
                } else {
                    onResultReady.onReady(null, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }


            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void unblockUserRequest(String userId, String friendId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.unblockUserFriend(userId, friendId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equalsIgnoreCase(responseJson.optString("status"))) {
                    onResultReady.onReady(null, false);
                } else {
                    onResultReady.onReady(null, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }


            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void unfriendRequest(String accessToken, String userId, String friendId, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.unfriendFriend(userId, friendId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equalsIgnoreCase(responseJson.optString("status"))) {
                    onResultReady.onReady(null, false);
                } else {
                    onResultReady.onReady(null, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }


            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void declineFriendRequest(String accessToken, String userId, String friendId, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.declineFriendRequest(userId, friendId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equalsIgnoreCase(responseJson.optString("status"))) {
                    onResultReady.onReady(null, false);
                } else {
                    onResultReady.onReady(null, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }


            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void retrieveUserFriend(String accessToken, String friendId, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.retrieveUserFriend(friendId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equalsIgnoreCase(responseJson.optString("status"))) {
                    JSONObject jsonResult = responseJson.optJSONObject("result");
                    if (jsonResult == null) {
                        onResultReady.onReady(null, true);
                        return;
                    }

                    JSONObject jsonFriend = jsonResult.optJSONObject("user");
                    if (jsonFriend == null) {
                        onResultReady.onReady(null, true);
                        return;
                    }

                    onResultReady.onReady(new User(jsonFriend), false);
                } else {
                    onResultReady.onReady(null, true); // failed.. so put true
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }


            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void resetPassword(String currentPassword, String newPassword, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("old_password", currentPassword);
        params.put("new_password", newPassword);
        params.put("access_token", accessToken);

        ClubbookRestClient.resetPassword(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equalsIgnoreCase(responseJson.optString("status"))) {
                    onResultReady.onReady(null, false);
                } else {
                    onResultReady.onReady(null, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }


            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void checkin(String placeId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.checkin(placeId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                User user = null;
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        user = new User(responseJson.getJSONObject("user"));
                        failed = false;
                    } else {
                        failed = true;
                    }
                } catch (Exception e) {
                    L.i("" + e);
                }

                //failed = false;
                onResultReady.onReady(user, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
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

    public void updateCheckin(String placeId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.updateCheckin(placeId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                User user = null;
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        user = new User(responseJson.getJSONObject("user"));
                        failed = false;
                    } else {
                        failed = true;
                    }
                } catch (Exception e) {
                    L.i("" + e);
                }

                //failed = false;
                onResultReady.onReady(user, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
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

    public void checkout(String placeId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.checkout(placeId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                User user = null;
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        user = new User(responseJson.getJSONObject("user"));
                        failed = false;
                    } else {
                        failed = true;
                    }
                } catch (Exception e) {
                    L.i("" + e);
                }

                //failed = false;
                onResultReady.onReady(user, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
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

    /**
     * Get conversation between 2 people
     *
     * @param currentUserId
     * @param receiverUserId
     * @param onResultReady
     */
    public void getConversation(final Activity activity, String currentUserId, String receiverUserId, String accessToken,
                                       final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.getConversation(currentUserId, receiverUserId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject responseJson) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Chat chat;

                        if ("ok".equalsIgnoreCase(responseJson.optString("status"))) {
                            int TimeDifferenceFromUTC = CalendarUtils.getTimeDifferenceFromUTC();

                            JSONObject jsonResult = responseJson.optJSONObject("result");
                            chat = JSONConverter.newChatDto(jsonResult, TimeDifferenceFromUTC);
                            failed = false;

                        } else {
                            failed = true;
                            chat = null;
                        }

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onResultReady.onReady(chat, failed);
                            }
                        });
                    }
                });
                thread.start();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    /**
     * Get all conversation one person have with all people
     *
     * @param userId
     * @param accessToken
     * @param onResultReady
     */
    public void getConversations(final String userId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.getConversations(userId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                List<Chat> chats = new ArrayList<Chat>();
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        JSONArray chatsJson = responseJson.getJSONObject("result").getJSONArray("chats");
                        for (int i = 0; i < chatsJson.length(); i++) {
                            chats.add(JSONConverter.newChatDto(chatsJson.optJSONObject(i), 0));
                        }
                        failed = false;
                    } else {
                        failed = true;
                    }
                } catch (Exception e) {
                    L.i("" + e);
                }

                //failed = false;
                onResultReady.onReady(chats, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void sendMessage(String userFrom, String userTo, String msg, String type, String accessToken, String lat, String lng, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("user_from", userFrom);
        params.put("user_to", userTo);
        params.put("msg", msg);
        params.put("msg_type", type);
        params.put("access_token", accessToken);

        if(lat != null && lng != null) {
            params.put("lat", lat);
            params.put("lon", lng);
        }

        ClubbookRestClient.sendMsg(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        failed = false;
                    } else {
                        failed = true;
                    }
                } catch (Exception e) {
                    L.i("" + e);
                }

                onResultReady.onReady("ok", failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void readMessages(String currentUserId, String receiverId, String accessToken,
                                    final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.readMessages(currentUserId, receiverId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equalsIgnoreCase(responseJson.optString("status"))) {
                    failed = false;
                } else {
                    failed = true;
                }

                onResultReady.onReady(null, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void file(InputStream file, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("image", file);

        ClubbookRestClient.file(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                /*UserDto user = new UserDto();
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
                }                     */

                //failed = false;
                onResultReady.onReady("ok", failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
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

    public void getNotifications(String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.getNotifications(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                String count = "0";
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        failed = false;
                        count = responseJson.getString("unread_chat_count");
                    } else {
                        failed = true;
                    }
                } catch (Exception e) {
                    L.d("" + e);
                }

                onResultReady.onReady(count, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public void getConfig(final Context context, final OnResultReady onResultReady) {
        ClubbookRestClient.getConfig(new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equalsIgnoreCase(responseJson.optString("status"))) {
                    failed = false;

                    JSONObject jsonResult = responseJson.optJSONObject("result");
                    if(jsonResult != null) {
                        ClubbookPreferences clubbookPreferences = ClubbookPreferences.getInstance(context);
                        clubbookPreferences.setUpdateCheckInStatusInterval(jsonResult.optInt("update_checkin_status_interval"));
                        clubbookPreferences.setMaxFailedCheckInCount(jsonResult.optInt("max_failed_checkin_count"));
                        clubbookPreferences.setCheckInMaxDistance(jsonResult.optInt("chekin_max_distance"));
                    }

                } else {
                    failed = true;
                }

                onResultReady.onReady(null, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    public interface OnResultReady {
        public void onReady(@Nullable Object result, boolean failed);
    }

    /*
     *
     *  Deprecated
     *
     */

    @Deprecated
    public void retrievePlace(String placeId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.retrievePlace(placeId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
//                JSONObject jsonClub = responseJson.optJSONObject("club");
//
//                Club club = JSONConverter.newClub(jsonClub);
//                if (jsonClub != null && club != null) {
//                    JSONArray jsonArrUsers = responseJson.optJSONArray("users");
//                    List<CheckInUser> checkInUsers = JSONConverter.newCheckInUsersList(jsonArrUsers);
//                    club.setUsers(checkInUsers);
//                }
//
//                failed = false;
//                onResultReady.onReady(club, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

        });
    }
}
