package com.nl.clubbook.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.UserDto;
import com.parse.PushService;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Andrew on 5/19/2014.
 */
public class SessionManager {

    private static final String PREF_NAME = "com.nl.clubbook.preferences";

    private static SessionManager mSessionManaged;
    private final SharedPreferences mPreferences;
    private final Context mContext;

    public static void init(Context context) {
        if(mSessionManaged == null) {
            mSessionManaged = new SessionManager(context);
        }
    }

    public static SessionManager getInstance() {
        if(mSessionManaged == null) {
            throw new IllegalArgumentException(SessionManager.class.getSimpleName() + " is not initialized, call init() method in your application class!");
        }

        return mSessionManaged;
    }

    private SessionManager(Context context) {
        mContext = context;
        mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getUserId() {
        return mPreferences.getString(KEY_ID, "");
    }

    public String getAccessToken() {
        return mPreferences.getString(KEY_ACCESS_TOCKEN, "");
    }

    public void setStringArrayPref(String key, ArrayList<String> values) {
        Editor editor = mPreferences.edit();

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
        String json = mPreferences.getString(key, null);
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
        Editor editor = mPreferences.edit();

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
        PushService.subscribe(mContext, "user_" + user.getId(), MainActivity.class);
        // Storing login value as TRUE
        updateLoginSession(user);
    }

    public void updateValue(String key, String value) {
        Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void updateLoginSession(UserDto user) {
        Editor editor = mPreferences.edit();

        editor.putBoolean(KEY_IS_LOGIN, true);
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
        editor.putBoolean(KEY_IS_NOTIFICATION_ENABLE, user.isNotificationEnabled());

        editor.commit();
    }

    public void setConversationListener(String currentConversation) {
        Editor editor = mPreferences.edit();
        editor.putString(KEY_CURRENT_CONVERSATION, currentConversation);
        editor.commit();
    }

    public String getConversationListener() {
        return mPreferences.getString(KEY_CURRENT_CONVERSATION, null);
    }

    public void putFbData(String access_token, long access_expires) {
        Editor editor = mPreferences.edit();

        editor.putString(KEY_FBACCESSEXPITES, String.valueOf(access_expires));
        editor.putString(KEY_FBACCESSTOKEN, access_token);

        editor.commit();
    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_NAME, mPreferences.getString(KEY_NAME, null));
        user.put(KEY_ID, mPreferences.getString(KEY_ID, null));
        user.put(KEY_EMAIL, mPreferences.getString(KEY_EMAIL, null));
        user.put(KEY_GENDER, mPreferences.getString(KEY_GENDER, null));
        user.put(KEY_BIRTHDAY, mPreferences.getString(KEY_BIRTHDAY, null));
        user.put(KEY_AGE, mPreferences.getString(KEY_AGE, null));
        user.put(KEY_AVATAR, mPreferences.getString(KEY_AVATAR, null));
        user.put(KEY_ACCESS_TOCKEN, mPreferences.getString(KEY_ACCESS_TOCKEN, null));

        return user;
    }

    public void putCheckedInClubInfo(@NotNull ClubDto clubDto) {
        Editor editor = mPreferences.edit();

        editor.putString(KEY_CHECKIN_CLUB_ID, clubDto.getId());
        editor.putFloat(KEY_CHECKIN_CLUB_LAT, (float) clubDto.getLat());
        editor.putFloat(KEY_CHECKIN_CLUB_LON, (float) clubDto.getLon());
        editor.putString(KEY_CHECKIN_CLUB_NAME, clubDto.getTitle());

        editor.commit();
    }

    public ClubDto getCheckedInClubInfo() {
        ClubDto checkedInClub = new ClubDto();

        checkedInClub.setId(mPreferences.getString(KEY_CHECKIN_CLUB_ID, ""));
        checkedInClub.setLat(mPreferences.getFloat(KEY_CHECKIN_CLUB_LAT, 0f));
        checkedInClub.setLon(mPreferences.getFloat(KEY_CHECKIN_CLUB_LON, 0f));
        checkedInClub.setTitle(mPreferences.getString(KEY_CHECKIN_CLUB_NAME, ""));

        return checkedInClub;
    }

    public void clearCheckInClubInfo() {
        Editor editor = mPreferences.edit();

        editor.remove(KEY_CHECKIN_CLUB_ID);
        editor.remove(KEY_CHECKIN_CLUB_LAT);
        editor.remove(KEY_CHECKIN_CLUB_LON);
        editor.remove(KEY_CHECKIN_CLUB_NAME);

        editor.commit();
    }

    public void logoutUser() {
        // unsubscribe from parse
    	PushService.unsubscribe(mContext, "user_" + getUserDetails().get(KEY_ID));

        Editor editor = mPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public boolean isNotificationEnabled() {
        return mPreferences.getBoolean(KEY_IS_NOTIFICATION_ENABLE, true);
    }

    public void setNotificationEnabled(boolean isNotificationEnabled) {
        Editor editor = mPreferences.edit();
        editor.putBoolean(KEY_IS_NOTIFICATION_ENABLE, isNotificationEnabled);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return mPreferences.getBoolean(KEY_IS_LOGIN, false);
    }

    public String getValueByKey(String key) {
        return mPreferences.getString(key, "");
    }

    /*
     * Constants
     */
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
    public static final String KEY_CHECKIN_CLUB_NAME= "checkin_club_name";
    public static final String KEY_CURRENT_CONVERSATION = "current_conversation";

    private static final String KEY_IS_LOGIN = "IsLoggedIn";
    private static final String KEY_IS_NOTIFICATION_ENABLE = "KEY_IS_NOTIFICATION_ENABLE";
}


