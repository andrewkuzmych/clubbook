package com.nl.clubbook.datasource;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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
    private static Context mContext;

    public static void setContext(Context context) {
        mContext = context;
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

    public static void updateUserProfile(String accessToken, String name, String gender, String dob, String country, String bio, final OnResultReady onResultReady) {
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

    public static void profileAddImage(String accessToken, String userId, JSONObject avatar, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);
        params.put("avatar", avatar);

        ClubbookRestClient.profileAddImage(userId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                UserPhotoDto userPhotoDto = null;
                try {
                    if ("ok".equalsIgnoreCase(response_json.getString("status"))) {
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

    public static void profileDeleteImage(Context context, String accessToken, String userId, String imageId, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.profileDeleteImage(context, userId, imageId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
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

    public static void profileUpdateImage(String accessToken, String userId, String imageId, Boolean isAvatar, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);
        params.put("is_avatar", isAvatar);

        ClubbookRestClient.profileUpdateImage(userId, imageId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                UserDto user = null;
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        user = new UserDto(responseJson.getJSONObject("result").getJSONObject("user"));
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
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                UserDto user = null;
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        user = new UserDto(responseJson.getJSONObject("result").getJSONObject("user"));
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
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                UserDto user = null;
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        user = new UserDto(responseJson.getJSONObject("result").getJSONObject("user"));
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

    public static void retrievePlaces(String distance, String lat, String lon, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.add("distance", distance);
        params.add("user_lat", lat);
        params.add("user_lon", lon);
        params.add("access_token", accessToken);

        ClubbookRestClient.retrievePlaces(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                JSONArray jsonArrClubs = responseJson.optJSONArray("clubs");
                List<ClubDto> clubs = JSONConverter.newClubList(jsonArrClubs);

                failed = false;
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

    public static void retrievePlace(String placeId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.retrievePlace(placeId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                JSONObject jsonClub = responseJson.optJSONObject("club");

                ClubDto club = JSONConverter.newClub(jsonClub);
                if(jsonClub != null && club != null) {
                    JSONArray jsonArrUsers = responseJson.optJSONArray("users");
                    List<CheckInUserDto> checkInUsers = JSONConverter.newCheckInUsersList(jsonArrUsers);
                    club.setUsers(checkInUsers);
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

    public static void retrieveUser(String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.retrieveUser(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                UserDto user = null;
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        user = new UserDto(responseJson.getJSONObject("result").getJSONObject("user"));
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

    public static void retrieveFriends(String userId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.retrieveFriends(userId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                List<UserDto> friends = new ArrayList<UserDto>();

                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        JSONArray friendsJson = responseJson.getJSONObject("result").getJSONArray("friends");
                        friends = JSONConverter.newFriendList(friendsJson);

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

    public static void addFriendRequest(String userId, String friendId, String accessToken, final OnResultReady onResultReady) {
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

    public static void removeFriendRequest(String userId, String friendId, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();

        ClubbookRestClient.removeFriend(userId, friendId, params, new JsonHttpResponseHandler() {
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

    public static void retrieveUserFriend(String accessToken, String friendId, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.retrieveUserFriend(friendId, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                if ("ok".equalsIgnoreCase(responseJson.optString("status"))) {
                    JSONObject jsonResult = responseJson.optJSONObject("result");
                    if(jsonResult == null) {
                        onResultReady.onReady(null, true);
                        return;
                    }

                    JSONObject jsonFriend = jsonResult.optJSONObject("user");
                    if(jsonFriend == null) {
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

    //TODO
    public static void checkin(String placeId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.checkin(placeId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                UserDto user = null;
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        user = new UserDto(responseJson.getJSONObject("user"));
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

    public static void updateCheckin(String placeId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.updateCheckin(placeId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                UserDto user = null;
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        user = new UserDto(responseJson.getJSONObject("user"));
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

    public static void checkout(String placeId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.checkout(placeId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                UserDto user = null;
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        user = new UserDto(responseJson.getJSONObject("user"));
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
     * @param currentUserId
     * @param receiverUserId
     * @param onResultReady
     */
    public static void getConversation(String currentUserId, String receiverUserId, String accessToken,
                                       final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.getConversation(currentUserId, receiverUserId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                ChatDto chat = null;

                if ("ok".equalsIgnoreCase(responseJson.optString("status"))) {
                    JSONObject jsonResult = responseJson.optJSONObject("result");
                    chat = JSONConverter.newChatDto(jsonResult);
                    failed = false;
                } else {
                    failed = true;
                }

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
     * @param userId
     * @param accessToken
     * @param onResultReady
     */
    public static void getConversations(final String userId, String accessToken, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("access_token", accessToken);

        ClubbookRestClient.getConversations(userId, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                List<ChatDto> chats = new ArrayList<ChatDto>();
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
                        JSONArray chatsJson = responseJson.getJSONObject("result").getJSONArray("chats");
                        for (int i = 0; i < chatsJson.length(); i++) {
                            chats.add(JSONConverter.newChatDto(chatsJson.optJSONObject(i)));
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

    public static void chat(String userFrom, String userTo, String msg, String type, String accessToken,
                            final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("user_from", userFrom);
        params.put("user_to", userTo);
        params.put("msg", msg);
        params.put("msg_type", type);
        params.put("access_token", accessToken);

        ClubbookRestClient.sendMsg(params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseJson) {
                try {
                    if ("ok".equalsIgnoreCase(responseJson.getString("status"))) {
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

    public static void readMessages(String currentUserId, String receiverId, String accessToken,
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

    public static void getNotifications(String accessToken, final OnResultReady onResultReady) {
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
