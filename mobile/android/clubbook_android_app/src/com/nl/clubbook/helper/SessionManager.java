package com.nl.clubbook.helper;

/**
 * Created by Andrew on 5/19/2014.
 */

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

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final int DEFOULT_DISTANCE = 4;

    // User name (make variable public to access from outside)
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_BIRTHDAY = "birthday";
    public static final String KEY_AGE = "age";
    public static final String KEY_AVATAR = "avatar";
    public static final String KEY_FBACCESSTOKEN = "access_token";
    public static final String KEY_FBACCESSEXPITES = "access_expires";
    public static final String KEY_PERMISSIONS = "permissions";
    public static final String KEY_CHECKIN_CLUB_ID = "checkin_club_id";
    public static final String KEY_CHECKIN_CLUB_LAT = "checkin_club_lan";
    public static final String KEY_CHECKIN_CLUB_LON = "checkin_club_lat";
    public static final String KEY_CURRENT_CONVERSATION = "current_conversation";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setStringArrayPref(String key, ArrayList<String> values) {
        //SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
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
        //Set<String> permissionsSet = new HashSet<String>(Arrays.asList(permissions));
        ArrayList<String> permissionList = new ArrayList<String>(Arrays.asList(permissions));
        setStringArrayPref(KEY_PERMISSIONS, permissionList);
        //editor.putStringSet(KEY_PERMISSIONS, permissionsSet);
        // commit changes
        editor.commit();
    }

    public boolean hasPermission(String permission) {
        ArrayList<String> permissionsList = getStringArrayPref(KEY_PERMISSIONS);
        if (permissionsList == null)
            return false;
        return permissionsList.contains(permission);
    }

    /**
     * Create login session
     */
    public void createLoginSession(UserDto user) {
        PushService.subscribe(_context, "user_" + user.getId(), MainActivity.class);
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_ID, user.getId());
        // Storing name in pref
        editor.putString(KEY_NAME, user.getName());

        // Storing email in pref
        editor.putString(KEY_EMAIL, user.getEmail());

        editor.putString(KEY_GENDER, user.getGender());

        editor.putString(KEY_BIRTHDAY, user.getDob());

        editor.putString(KEY_AGE, user.getAge());

        editor.putString(KEY_AVATAR, user.getAvatar());

        // commit changes
        editor.commit();
    }

    public void setConversationListner(String currentConversation) {
        editor.putString(KEY_CURRENT_CONVERSATION, currentConversation);
        editor.commit();
    }

    public String getConversationListner() {
        return pref.getString(KEY_CURRENT_CONVERSATION, null);
    }

    public void putFbData(String access_token, long access_expires) {
        editor.putString(KEY_FBACCESSEXPITES, String.valueOf(access_expires));

        editor.putString(KEY_FBACCESSTOKEN, access_token);

        // commit changes
        editor.commit();
    }

    public void putClubInfo(String club_id, String lat, String lon) {
        editor.putString(KEY_CHECKIN_CLUB_ID, club_id);
        editor.putString(KEY_CHECKIN_CLUB_LAT, lat);
        editor.putString(KEY_CHECKIN_CLUB_LON, lon);

        // commit changes
        editor.commit();
    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_GENDER, pref.getString(KEY_GENDER, null));
        user.put(KEY_BIRTHDAY, pref.getString(KEY_BIRTHDAY, null));
        user.put(KEY_AGE, pref.getString(KEY_AGE, null));
        user.put(KEY_AVATAR, pref.getString(KEY_AVATAR, null));

        // return user
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
        // Clearing all data from Shared Preferences
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


