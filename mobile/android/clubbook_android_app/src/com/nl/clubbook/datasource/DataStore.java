package com.nl.clubbook.datasource;

import android.content.Context;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nl.clubbook.adapter.CheckinAdapter;
import com.nl.clubbook.adapter.ClubsAdapter;
import com.nl.clubbook.adapter.MessagesAdapter;
import com.nl.clubbook.helper.LocationCheckinHelper;
import android.util.Log;
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

    private static ClubsAdapter clubsAdapter;

    private static CheckinAdapter checkinAdapter;

    private static MessagesAdapter messagesAdapter;

    public static void setContext(Context mcontext) {
        context = mcontext;
    }

    public static MessagesAdapter getMessagesAdapter() {
        return messagesAdapter;
    }

    public static void setMessagesAdapter(MessagesAdapter messagesAdapter) {
        DataStore.messagesAdapter = messagesAdapter;
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

           /* @Override
            public void onFailure(java.lang.Throwable e, org.json.JSONArray errorResponse)
            {
                onResultReady.onReady(null, true);
                Log.e("error", errorResponse.toString());
            }*/

        /*    @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                onResultReady.onReady(null, true);
                Log.e("error", error.toString());
            }*/

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
                        club.setDistance(LocationCheckinHelper.calculateDistance(context, Double.parseDouble(club.getLat()), Double.parseDouble(club.getLon())));
                        clubs.add(club);
                    }

                    //Collections.sort(places, new PlaceDistanceComparator());
                    failed = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                onResultReady.onReady(clubs, failed);
            }

            /*@Override
            public void onFailure(java.lang.Throwable e, org.json.JSONArray errorResponse)
            {
                onResultReady.onReady(null, true);
                Log.e("error", errorResponse.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                onResultReady.onReady(null, true);
                Log.e("error", error.toString());
            }*/

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONObject errorResponse)
            {
                onResultReady.onReady(null, true);
                //Log.e("error", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, java.lang.Throwable throwable, final JSONArray errorResponse)
            {
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
                    club.setLon(club_dto.getJSONObject("club_loc").getString("lon"));
                    club.setLat(club_dto.getJSONObject("club_loc").getString("lat"));
                    club.setDistance(LocationCheckinHelper.calculateDistance(context, Double.parseDouble(club.getLat()), Double.parseDouble(club.getLon())));

                    List<String> photos = new ArrayList<String>();
                    JSONArray photo_list =  club_dto.getJSONArray("club_photos");
                    for (int i = 0; i < photo_list.length(); i++) {
                       photos.add(photo_list.getString(i));
                    }

                    JSONArray users_dto = response_json.getJSONArray("users");
                    List<UserDto> users = new ArrayList<UserDto>();
                    for(int i = 0; i < users_dto.length(); i++)
                    {
                        JSONObject user_dto = users_dto.getJSONObject(i);
                        UserDto user = new UserDto();
                        user.setEmail(user_dto.getString("email"));
                        user.setGender(user_dto.getString("gender"));
                        user.setName(user_dto.getString("name"));
                        user.setId(user_dto.getString("_id"));

                        JSONArray photos_json =  user_dto.getJSONArray("photos");
                        List<String> user_photos = new ArrayList<String>();
                        for (int j = 0; j < photos_json.length(); j++) {
                            String photo_url = photos_json.getJSONObject(j).getString("url");
                            boolean is_profile =  photos_json.getJSONObject(j).getBoolean("profile");
                            if (is_profile)
                                user.setAvatar(photo_url);
                            else
                                user_photos.add(photo_url);

                            user.setPhotos(user_photos);
                        }

                        users.add(user);
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

    public static void checkin(String place_id, String user_id, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();

        ClubbookRestClient.checkin(place_id, user_id, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                UserDto user = new UserDto();
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        //Log.i("user", response_json.getJSONObject("result").getJSONObject("user").toString());

                        JSONObject user_dto = response_json.getJSONObject("user");
                        user.setEmail(user_dto.getString("email"));
                        //user.setAvatar(user_dto.getString("avatar"));
                        user.setGender(user_dto.getString("gender"));
                        user.setName(user_dto.getString("name"));
                        user.setId(user_dto.getString("_id"));

                        JSONArray photos_json = user_dto.getJSONArray("photos");
                        List<String> photos = new ArrayList<String>();
                        for (int i = 0; i < photos_json.length(); i++) {
                            String photo_url = photos_json.getJSONObject(i).getString("url");
                            boolean is_profile = photos_json.getJSONObject(i).getBoolean("profile");
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

    public static void updateCheckin(String place_id, String user_id, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();

        ClubbookRestClient.updateCheckin(place_id, user_id, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                UserDto user = new UserDto();
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        //Log.i("user", response_json.getJSONObject("result").getJSONObject("user").toString());

                        JSONObject user_dto = response_json.getJSONObject("user");
                        user.setEmail(user_dto.getString("email"));
                        //user.setAvatar(user_dto.getString("avatar"));
                        user.setGender(user_dto.getString("gender"));
                        user.setName(user_dto.getString("name"));
                        user.setId(user_dto.getString("_id"));

                        JSONArray photos_json = user_dto.getJSONArray("photos");
                        List<String> photos = new ArrayList<String>();
                        for (int i = 0; i < photos_json.length(); i++) {
                            String photo_url = photos_json.getJSONObject(i).getString("url");
                            boolean is_profile = photos_json.getJSONObject(i).getBoolean("profile");
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

    public static void checkout(String place_id, String user_id, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();

        ClubbookRestClient.checkout(place_id, user_id, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                UserDto user = new UserDto();
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        //Log.i("user", response_json.getJSONObject("result").getJSONObject("user").toString());

                        JSONObject user_dto = response_json.getJSONObject("user");
                        user.setEmail(user_dto.getString("email"));
                        //user.setAvatar(user_dto.getString("avatar"));
                        user.setGender(user_dto.getString("gender"));
                        user.setName(user_dto.getString("name"));
                        user.setId(user_dto.getString("_id"));

                        JSONArray photos_json = user_dto.getJSONArray("photos");
                        List<String> photos = new ArrayList<String>();
                        for (int i = 0; i < photos_json.length(); i++) {
                            String photo_url = photos_json.getJSONObject(i).getString("url");
                            boolean is_profile = photos_json.getJSONObject(i).getBoolean("profile");
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

    public static void get_conversation(String user1, String user2, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        ClubbookRestClient.get_conversation(user1, user2, params, new JsonHttpResponseHandler() {
            private boolean failed = true;
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                Chat chat = new Chat();
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        JSONArray conversation_dto = response_json.getJSONArray("conversation");

                        List<Conversation> conversation = new ArrayList<Conversation>();
                        for (int i = 0; i < conversation_dto.length(); i++) {
                            Conversation con = new Conversation();
                            con.setUser_from(conversation_dto.getJSONObject(i).getString("from_who"));
                            con.setMsg(conversation_dto.getJSONObject(i).getString("msg"));

                            conversation.add(con);
                        }

                        chat.setChatId(response_json.getString("chat_id"));
                        chat.setConversation(conversation);

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

    public static void get_conversations(final String user_id, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        ClubbookRestClient.get_conversations(user_id, params, new JsonHttpResponseHandler() {
            private boolean failed = true;

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response_json) {
                List<ConversationShort> conversations = new ArrayList<ConversationShort>();
                try {
                    if (response_json.getString("status").equalsIgnoreCase("ok")) {
                        JSONArray conversations_dto = response_json.getJSONArray("conversations");
                        for (int i = 0; i < conversations_dto.length(); i++) {
                            ConversationShort con = new ConversationShort();

                            con.setId(conversations_dto.getJSONObject(i).getString("id"));

                            JSONObject user_dto;
                            if (conversations_dto.getJSONObject(i).getJSONObject("user1").getString("_id").equalsIgnoreCase(user_id)) {
                                user_dto = conversations_dto.getJSONObject(i).getJSONObject("user2");
                            } else {
                                user_dto = conversations_dto.getJSONObject(i).getJSONObject("user1");
                            }

                            con.setUser_id(user_dto.getString("_id"));
                            con.setUser_name(user_dto.getString("name"));

                            JSONArray photo_list = user_dto.getJSONArray("photos");
                            String user_from_photo = null;                            for (int j = 0; j < photo_list.length(); j++) {
                                if (photo_list.getJSONObject(j).getBoolean("profile")) {
                                    user_from_photo = photo_list.getJSONObject(j).getString("url");
                                    break;
                                }
                            }

                            con.setUser_photo(user_from_photo);

                            if (conversations_dto.getJSONObject(i).has("unread") &&
                                conversations_dto.getJSONObject(i).getJSONObject("unread").has("user") &&
                                conversations_dto.getJSONObject(i).getJSONObject("unread").getString("user").equalsIgnoreCase(user_id)) {

                                con.setUnread_messages(conversations_dto.getJSONObject(i).getJSONObject("unread").getInt("count"));
                            }
                            else {
                                con.setUnread_messages(0);
                            }

                            if (conversations_dto.getJSONObject(i).has("conversation") ) {
                                JSONArray cons_dto = conversations_dto.getJSONObject(i).getJSONArray("conversation");
                                if (cons_dto.length() > 0)
                                    con.setLast_message(cons_dto.getJSONObject(0).getString("msg"));
                            }

                            conversations.add(con);
                        }

                        failed = false;
                    } else
                        failed = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //failed = false;
                onResultReady.onReady(conversations, failed);
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

    public static void chat(String user_from, String user_to , String msg, final OnResultReady onResultReady) {
        RequestParams params = new RequestParams();
        params.put("user_from", user_from);
        params.put("user_to", user_to);
        params.put("msg", msg);

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
                        count = response_json.getString("count");
                    } else
                        failed = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //failed = false;
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
