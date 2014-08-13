package com.nl.clubbook.datasource;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nl.clubbook.adapter.ClubsAdapter;
import com.nl.clubbook.adapter.MessagesAdapter;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.utils.L;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 5/19/2014.
 */
public class DataStore {
    private static Context context;

    public static void setContext(Context mcontext) {
        context = mcontext;
    }

    public static void regByEmail(String name, String email, String pass, String gender, String dob, String country, String city, String bio, JSONObject avatar,
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
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                UserDto user = null;
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        user = new UserDto(response_json.getJSONObject("result").getJSONObject("user"));
                        failed = false;
                    } else
                        failed = true;
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

    public static void updateUserProfile(String userId, String name, String gender, String dob, String country, String bio, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("name", name);
        params.put("gender", gender);
        params.put("dob", dob);
        params.put("country", country);
        params.put("bio", bio);

        ClubbookRestClient.updateProfile(userId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                UserDto user = null;
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        user = new UserDto(response_json.getJSONObject("result").getJSONObject("user"));
                        failed = false;
                    } else
                        failed = true;
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

    public static void profileAddImage(String userId, JSONObject avatar, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("avatar", avatar);

        ClubbookRestClient.profileAddImage(userId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                UserPhotoDto userPhotoDto = null;
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        userPhotoDto = new UserPhotoDto(response_json.getJSONObject("result").getJSONObject("image"));
                        failed = false;
                    } else
                        failed = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                onResultReady.onReady(userPhotoDto, failed);
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

    public static void profileDeleteImage(String userId, String imageId, final OnResultReady onResultReady) {

        ClubbookRestClient.profileDeleteImage(userId, imageId, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        failed = false;
                    } else
                        failed = true;
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

    public static void profileUpdateImage(String userId, String imageId, Boolean isAvatar, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("is_avatar", isAvatar);

        ClubbookRestClient.profileUpdateImage(userId, imageId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                UserDto user = null;
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        user = new UserDto(response_json.getJSONObject("result").getJSONObject("user"));
                        failed = false;
                    } else
                        failed = true;
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
                        user = new UserDto(response_json.getJSONObject("result").getJSONObject("user"));
                        failed = false;
                    } else
                        failed = true;
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

    public static void loginByFb(String name, String email, String fb_id, String fb_access_token, String gender, String dob, JSONObject avatar,
                                 final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("name", name);
        params.put("fb_id", fb_id);
        params.put("fb_access_token", fb_access_token);
        params.put("gender", gender);
        params.put("dob", dob);
        params.put("avatar", avatar);

        ClubbookRestClient.loginByFb(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                UserDto user = null;
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        user = new UserDto(response_json.getJSONObject("result").getJSONObject("user"));
                        failed = false;
                    } else
                        failed = true;
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
                        club.setLon(clubs_dto.getJSONObject(i).getJSONObject("club_loc").getDouble("lon"));
                        club.setLat(clubs_dto.getJSONObject(i).getJSONObject("club_loc").getDouble("lat"));
                        club.setDistance(LocationCheckinHelper.calculateDistance(club.getLat(), club.getLon()));
                        club.setActiveCheckins(clubs_dto.getJSONObject(i).getInt("active_checkins"));
                        clubs.add(club);
                    }

                    //Collections.sort(places, new PlaceDistanceComparator());
                    failed = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                onResultReady.onReady(clubs, failed);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse) {
                onResultReady.onReady(null, true);
                //Log.e("error", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse) {
                onResultReady.onReady(null, true);
                //Log.e("error", errorResponse.toString());
            }

            @Override
            public void onFinish() {
                super.onFinish();
                //if (failed)
                //    onResultReady.onReady(null, true);
            }
        });
    }

    public static void retrievePlace(String place_id, String user_id, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();

        ClubbookRestClient.retrievePlace(place_id, user_id, params, new JsonHttpResponseHandler() {
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
                    club.setLon(club_dto.getJSONObject("club_loc").getDouble("lon"));
                    club.setLat(club_dto.getJSONObject("club_loc").getDouble("lat"));
                    club.setDistance(LocationCheckinHelper.calculateDistance(club.getLat(), club.getLon()));

                    List<String> photos = new ArrayList<String>();
                    JSONArray photo_list = club_dto.getJSONArray("club_photos");
                    for (int i = 0; i < photo_list.length(); i++) {
                        photos.add(photo_list.getString(i));
                    }

                    JSONArray users_dto = response_json.getJSONArray("users");
                    List<UserDto> users = new ArrayList<UserDto>();
                    for (int i = 0; i < users_dto.length(); i++) {
                        users.add(new UserDto(users_dto.getJSONObject(i)));
                    }

                    club.setUsers(users);
                    club.setPhotos(photos);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                failed = false;
                onResultReady.onReady(club, failed);
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

    public static void retrieveUser(String user_id, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();

        ClubbookRestClient.retrieveUser(user_id, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                L.v("response_json - " + response_json);

                UserDto user = null;
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        user = new UserDto(response_json.getJSONObject("result").getJSONObject("user"));
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

    public static void retrieveFriends(String user_id, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();

        ClubbookRestClient.retrieveFriends(user_id, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                List<UserDto> friends = new ArrayList<UserDto>();;
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        JSONArray friendsJson = response_json.getJSONObject("result").getJSONArray("friends");
                        for (int i = 0; i < friendsJson.length(); i++) {
                            friends.add(new UserDto(friendsJson.getJSONObject(i)));
                        }

                        failed = false;
                    } else
                        failed = true;
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

    public static void addFriendRequest(String userId, String friendId, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();

        ClubbookRestClient.addFriend(userId, friendId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equalsIgnoreCase(responseJson.optString("status", ""))) {
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

    public static void removeFriendRequest(String userId, String friendId, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();

        ClubbookRestClient.removeFriend(userId, friendId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equalsIgnoreCase(responseJson.optString("status", ""))) {
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

    public static void retrieveUserFriend(String userId, String friendId, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();

        ClubbookRestClient.retrieveUserFriend(userId, friendId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equalsIgnoreCase(responseJson.optString("status", ""))) {
                    JSONObject jsonResult = responseJson.optJSONObject("result");
                    if(jsonResult == null) {
                        L.v("jsonResult = null");
                        onResultReady.onReady(null, true);
                        return;
                    }

                    JSONObject jsonFriend = jsonResult.optJSONObject("user");
                    if(jsonFriend == null) {
                        L.v("jsonResult = null");
                        onResultReady.onReady(null, true);
                        return;
                    }

                    onResultReady.onReady(new FriendDto(jsonFriend), false);
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

    public static void checkin(String place_id, String user_id, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();

        ClubbookRestClient.checkin(place_id, user_id, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                UserDto user = null;
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        user = new UserDto(response_json.getJSONObject("user"));
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

    public static void updateCheckin(String place_id, String user_id, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();

        ClubbookRestClient.updateCheckin(place_id, user_id, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                UserDto user = null;
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        user = new UserDto(response_json.getJSONObject("user"));
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

    public static void checkout(String place_id, String user_id, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();

        ClubbookRestClient.checkout(place_id, user_id, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                UserDto user = null;
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        user = new UserDto(response_json.getJSONObject("user"));
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

    /**
     * Get conversation between 2 people
     *
     * @param user1
     * @param user2
     * @param onResultReady
     */
    public static void get_conversation(String user1, String user2, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        ClubbookRestClient.get_conversation(user1, user2, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                ChatDto chat = null;
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        chat = new ChatDto(response_json.getJSONObject("result"));
                        failed = false;
                    } else
                        failed = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //failed = false;
                onResultReady.onReady(chat, failed);
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
     * @param user_id
     * @param onResultReady
     */
    public static void get_conversations(final String user_id, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        ClubbookRestClient.get_conversations(user_id, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                List<ChatDto> chats = new ArrayList<ChatDto>();
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        JSONArray chatsJson = response_json.getJSONObject("result").getJSONArray("chats");
                        for (int i = 0; i < chatsJson.length(); i++) {
                            chats.add(new ChatDto(chatsJson.getJSONObject(i)));
                        }
                        failed = false;
                    } else
                        failed = true;
                } catch (Exception e) {
                    e.printStackTrace();
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

    public static void chat(String user_from, String user_to, String msg, String type, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("user_from", user_from);
        params.put("user_to", user_to);
        params.put("msg", msg);
        params.put("msg_type", type);

        ClubbookRestClient.send_msg(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        failed = false;
                    } else
                        failed = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            }
        });
    }

    public static void read_messages(String chat_id, String user_id, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        ClubbookRestClient.read_messages(chat_id, user_id, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        failed = false;
                    } else
                        failed = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            }
        });
    }

    public static void file(InputStream file, final OnResultReady onResultReady) {
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

    public static void unread_messages_count(String user_id, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();

        ClubbookRestClient.unread_messages_count(user_id, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                String count = "0";
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        failed = false;
                        count = response_json.getString("unread_chat_count");
                    } else
                        failed = true;
                } catch (Exception e) {
                    e.printStackTrace();
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

    public interface OnResultReady {
        public void onReady(Object result, boolean failed);
    }
}
