package com.nl.clubbook.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.datasource.UserDto;
import com.parse.PushService;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Andrew on 5/19/2014.
 */
public class SessionManager {
    private SharedPreferences pref;

//    // Editor for Shared preferences
//    Editor editor;

    private Context _context;

    // SharedPref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final int DEFAULT_DISTANCE = 4;

    // User name (make variable public to access from outside)
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_COUNTRY = "country";
    public static final String KEY_BIRTHDAY = "birthday";
    public static final String KEY_ABOUT_ME = "about_me";
    public static final String KEY_AGE = "age";
    public static final String KEY_AVATAR = "avatar";
    public static final String KEY_ACCESS_TOCKEN = "access_token";
    public static final String KEY_FBACCESSTOKEN = "fb_access_token";
    public static final String KEY_FBACCESSEXPITES = "fb_access_expires";
    public static final String KEY_PERMISSIONS = "fb_permissions";
    public static final String KEY_CHECKIN_CLUB_ID = "checkin_club_id";
    public static final String KEY_CHECKIN_CLUB_LAT = "checkin_club_lan";
    public static final String KEY_CHECKIN_CLUB_LON = "checkin_club_lat";
    public static final String KEY_CURRENT_CONVERSATION = "current_conversation";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setStringArrayPref(String key, ArrayList<String> values) {
        Editor editor = pref.edit();

        JSONArray jsonArr = new JSONArray();
        for (String value : values) {
            jsonArr.put(value);
        }

        if (!values.isEmpty()) {
            editor.putString(key, jsonArr.toString());
        } else {
            editor.putString(key, null);
        }

        editor.commit();
    }

    public ArrayList<String> getStringArrayPref(String key) {
        String json = pref.getString(key, null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    public void setPermissions(String[] permissions) {
        Editor editor = pref.edit();

        ArrayList<String> permissionList = new ArrayList<String>(Arrays.asList(permissions));
        setStringArrayPref(KEY_PERMISSIONS, permissionList);

        editor.commit();
    }

    public boolean hasPermission(String permission) {
        ArrayList<String> permissionsList = getStringArrayPref(KEY_PERMISSIONS);
        if (permissionsList == null) {
            return false;
        }
        return permissionsList.contains(permission);
    }

    /**
     * Create login session
     */
    public void createLoginSession(UserDto user) {
        PushService.subscribe(_context, "user_" + user.getId(), MainActivity.class);
        // Storing login value as TRUE
        updateLoginSession(user);
    }

    public void updateValue(String key, String value) {
        Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void updateLoginSession(UserDto user) {
        Editor editor = pref.edit();

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, user.getId());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_GENDER, user.getGender());
        editor.putString(KEY_COUNTRY, user.getCountry());
        editor.putString(KEY_BIRTHDAY, user.getDob());
        editor.putString(KEY_ABOUT_ME, user.getBio());
        editor.putString(KEY_AGE, user.getAge());
        editor.putString(KEY_AVATAR, user.getAvatar());
        editor.putString(KEY_ACCESS_TOCKEN, user.getAccessToken());

        editor.commit();
    }

    public void setConversationListner(String currentConversation) {
        Editor editor = pref.edit();
        editor.putString(KEY_CURRENT_CONVERSATION, currentConversation);
        editor.commit();
    }

    public String getConversationListner() {
        return pref.getString(KEY_CURRENT_CONVERSATION, null);
    }

    public void putFbData(String access_token, long access_expires) {
        Editor editor = pref.edit();

        editor.putString(KEY_FBACCESSEXPITES, String.valueOf(access_expires));
        editor.putString(KEY_FBACCESSTOKEN, access_token);

        editor.commit();
    }

    public void putClubInfo(String club_id, String lat, String lon) {
        Editor editor = pref.edit();

        editor.putString(KEY_CHECKIN_CLUB_ID, club_id);
        editor.putString(KEY_CHECKIN_CLUB_LAT, lat);
        editor.putString(KEY_CHECKIN_CLUB_LON, lon);

        editor.commit();
    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_GENDER, pref.getString(KEY_GENDER, null));
        user.put(KEY_BIRTHDAY, pref.getString(KEY_BIRTHDAY, null));
        user.put(KEY_AGE, pref.getString(KEY_AGE, null));
        user.put(KEY_AVATAR, pref.getString(KEY_AVATAR, null));
        user.put(KEY_ACCESS_TOCKEN, pref.getString(KEY_ACCESS_TOCKEN, null));

        return user;
    }

    public HashMap<String, String> getClubInfo() {
        HashMap<String, String> club = new HashMap<String, String>();

        club.put(KEY_CHECKIN_CLUB_ID, pref.getString(KEY_CHECKIN_CLUB_ID, null));
        club.put(KEY_CHECKIN_CLUB_LAT, pref.getString(KEY_CHECKIN_CLUB_LAT, null));
        club.put(KEY_CHECKIN_CLUB_LON, pref.getString(KEY_CHECKIN_CLUB_LON, null));

        return club;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // unsubscribe from parse
    	PushService.unsubscribe(_context, "user_" + getUserDetails().get(KEY_ID));

        Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * Quick check for login
     * *
     */
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}


