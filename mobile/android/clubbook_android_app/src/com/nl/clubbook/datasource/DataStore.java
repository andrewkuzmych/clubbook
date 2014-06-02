package com.nl.clubbook.datasource;

import android.content.Context;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nl.clubbook.adapter.CheckinAdapter;
import com.nl.clubbook.adapter.ClubsAdapter;
import com.nl.clubbook.helper.LocationHelper;
import org.apache.commons.logging.Log;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Andrew on 5/19/2014.
 */
public class DataStore {
    private static Context context;

    private static ClubsAdapter clubsAdapter;

    private static CheckinAdapter checkinAdapter;

    public static void setContext(Context mcontext) {
        context = mcontext;
    }

    public static CheckinAdapter getCheckinAdapter() {
        return checkinAdapter;
    }

    public static void setCheckinAdapter(CheckinAdapter checkinAdapter) {
        DataStore.checkinAdapter = checkinAdapter;
    }


    public static ClubsAdapter getPlaceAdapter() {
        return clubsAdapter;
    }

    public static void setPlaceAdapter(ClubsAdapter placeAdapter) {
        DataStore.clubsAdapter = placeAdapter;
    }

    public static void regByEmail(String name, String email, String pass, String gender, String dob, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("name", name);
        params.put("gender", gender);
        params.put("password", pass);
        params.put("dob", dob);

        ClubbookRestClient.regByEmail(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                UserDto user = null;
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        JSONObject user_dto = response_json.getJSONObject("result").getJSONObject("user");
                        user = new UserDto();
                        user.setEmail(user_dto.getString("email"));
                        user.setGender(user_dto.getString("gender"));
                        user.setName(user_dto.getString("name"));
                        user.setId(user_dto.getString("_id"));
                        JSONArray photos_json =  user_dto.getJSONArray("photos");
                        List<String> photos = new ArrayList<String>();
                        for (int i = 0; i < photos_json.length(); i++) {
                            String photo_url = photos_json.getJSONObject(i).getString("url");
                            boolean is_profile =  photos_json.getJSONObject(i).getBoolean("profile");
                            if (is_profile)
                                user.setAvatar(photo_url);
                            else
                                photos.add(photo_url);

                            user.setPhotos(photos);
                        }

                    }
                    failed = false;
                } catch (Exception e) {
                    e.printStackTrace();
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

    public static void loginByEmail(String email, String pass, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", pass);

        ClubbookRestClient.loginByEmail(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                UserDto user = null;
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        JSONObject user_dto = response_json.getJSONObject("result").getJSONObject("user");
                        user = new UserDto();
                        user.setEmail(user_dto.getString("email"));
                        user.setGender(user_dto.getString("gender"));
                        user.setName(user_dto.getString("name"));
                        user.setId(user_dto.getString("_id"));

                        JSONArray photos_json =  user_dto.getJSONArray("photos");
                        List<String> photos = new ArrayList<String>();
                        for (int i = 0; i < photos_json.length(); i++) {
                            String photo_url = photos_json.getJSONObject(i).getString("url");
                            boolean is_profile =  photos_json.getJSONObject(i).getBoolean("profile");
                            if (is_profile)
                                user.setAvatar(photo_url);
                            else
                                photos.add(photo_url);

                            user.setPhotos(photos);
                        }
                    }
                    failed = false;
                } catch (Exception e) {
                    e.printStackTrace();
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

    public static void loginByFb(String name, String email, String fb_id, String fb_access_token, String gender, String dob, String avatar, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("name", name);
        params.put("fb_id", fb_id);
        params.put("fb_access_token", fb_access_token);
        params.put("fb_token_expires", 123456);
        params.put("gender", gender);
        params.put("dob", dob);
        params.put("avatar", avatar);

        ClubbookRestClient.loginByFb(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                UserDto user = new UserDto();
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        JSONObject user_dto = response_json.getJSONObject("result").getJSONObject("user");
                        user.setEmail(user_dto.getString("email"));
                        user.setGender(user_dto.getString("gender"));
                        user.setName(user_dto.getString("name"));
                        user.setId(user_dto.getString("_id"));

                        JSONArray photos_json =  user_dto.getJSONArray("photos");
                        List<String> photos = new ArrayList<String>();
                        for (int i = 0; i < photos_json.length(); i++) {
                            String photo_url = photos_json.getJSONObject(i).getString("url");
                            boolean is_profile =  photos_json.getJSONObject(i).getBoolean("profile");
                            if (is_profile)
                                user.setAvatar(photo_url);
                            else
                                photos.add(photo_url);

                            user.setPhotos(photos);
                        }
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

    public static void retrievePlaces(String distance, String lat, String lon, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();

        ClubbookRestClient.retrievePlaces(distance, lat, lon, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                List<ClubDto> clubs = new ArrayList<ClubDto>();
                try {
                    JSONArray clubs_dto = response_json.getJSONArray("clubs");
                    for (int i = 0; i < clubs_dto.length(); i++) {
                        ClubDto club = new ClubDto();

                        club.setId(clubs_dto.getJSONObject(i).getString("id"));
                        club.setTitle(clubs_dto.getJSONObject(i).getString("club_name"));
                        club.setPhone(clubs_dto.getJSONObject(i).getString("club_phone"));
                        club.setAddress(clubs_dto.getJSONObject(i).getString("club_address"));
                        club.setAvatar(clubs_dto.getJSONObject(i).getString("club_logo"));
                        club.setLon(clubs_dto.getJSONObject(i).getJSONObject("club_loc").getString("lon"));
                        club.setLat(clubs_dto.getJSONObject(i).getJSONObject("club_loc").getString("lat"));
                        club.setDistance(LocationHelper.calculateDistance(context, Double.parseDouble(club.getLat()), Double.parseDouble(club.getLon())));
                        clubs.add(club);
                    }

                    //Collections.sort(places, new PlaceDistanceComparator());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                failed = false;
                onResultReady.onReady(clubs, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse)
            {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse)
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

    public static void retrievePlace(String place_id, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();

        ClubbookRestClient.retrievePlace(place_id, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                ClubDto club = new ClubDto();

                try {
                    JSONObject club_dto = response_json.getJSONObject("club");

                    club.setId(club_dto.getString("id"));
                    club.setTitle(club_dto.getString("club_name"));
                    club.setPhone(club_dto.getString("club_phone"));
                    club.setAddress(club_dto.getString("club_address"));
                    club.setAvatar(club_dto.getString("club_logo"));
                    club.setLon(club_dto.getJSONObject("club_loc").getString("lon"));
                    club.setLat(club_dto.getJSONObject("club_loc").getString("lat"));
                    club.setDistance(LocationHelper.calculateDistance(context, Double.parseDouble(club.getLat()), Double.parseDouble(club.getLon())));

                    List<String> photos = new ArrayList<String>();
                    JSONArray photo_list =  club_dto.getJSONArray("club_photos");
                    for (int i = 0; i < photo_list.length(); i++) {
                       photos.add(photo_list.getString(i));
                    }
                    //place.setDistance(i);
                    club.setPhotos(photos);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                failed = false;
                onResultReady.onReady(club, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse)
            {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse)
            {
                onResultReady.onReady(null, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

        });
    }

    public static void retrieveUser(String user_id, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();

        ClubbookRestClient.retrieveUser(user_id, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                UserDto user = new UserDto();
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        //Log.i("user", response_json.getJSONObject("result").getJSONObject("user").toString());

                        JSONObject user_dto = response_json.getJSONObject("result").getJSONObject("user");
                        user.setEmail(user_dto.getString("email"));
                        //user.setAvatar(user_dto.getString("avatar"));
                        user.setGender(user_dto.getString("gender"));
                        user.setName(user_dto.getString("name"));
                        user.setId(user_dto.getString("_id"));

                        JSONArray photos_json =  user_dto.getJSONArray("photos");
                        List<String> photos = new ArrayList<String>();
                        for (int i = 0; i < photos_json.length(); i++) {
                            String photo_url = photos_json.getJSONObject(i).getString("url");
                            boolean is_profile =  photos_json.getJSONObject(i).getBoolean("profile");
                            if (is_profile)
                                user.setAvatar(photo_url);
                            else
                                photos.add(photo_url);

                            user.setPhotos(photos);
                        }

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

    public static void file(InputStream file, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("image", file);

        ClubbookRestClient.file(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers,JSONObject response_json) {
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
