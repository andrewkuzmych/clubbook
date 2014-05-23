package com.nl.clubbook.helper;

/**
 * Created by Andrew on 5/19/2014.
 */
import java.util.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import org.json.JSONArray;
import org.json.JSONException;

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

    // User name (make variable public to access from outside)
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_BIRTHDAY = "birthday";
    public static final String KEY_FBACCESSTOKEN = "access_token";
    public static final String KEY_FBACCESSEXPITES = "access_expires";
    public static final String KEY_PERMISSIONS = "permissions";

    // Constructor
    public SessionManager(Context context){
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

    public void setPermissions(String[] permissions)
    {
        //Set<String> permissionsSet = new HashSet<String>(Arrays.asList(permissions));
        ArrayList<String> permissionList = new ArrayList<String>(Arrays.asList(permissions));
        setStringArrayPref(KEY_PERMISSIONS, permissionList)  ;
        //editor.putStringSet(KEY_PERMISSIONS, permissionsSet);
        // commit changes
        editor.commit();
    }

    public boolean hasPermission(String permission)
    {
        ArrayList<String> permissionsList =getStringArrayPref(KEY_PERMISSIONS);
        if(permissionsList == null)
            return false;
        return permissionsList.contains(permission);
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String id, String name, String email, String gender, String birthday){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_ID, id);
        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        editor.putString(KEY_GENDER, gender);

        editor.putString(KEY_BIRTHDAY, birthday);

        // commit changes
        editor.commit();
    }

    public void putFbData(String access_token, long access_expires)
    {
        editor.putString(KEY_FBACCESSEXPITES, String.valueOf(access_expires));

        editor.putString(KEY_FBACCESSTOKEN, access_token);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
   /* public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }*/



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        //user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        //user.put(KEY_GENDER, pref.getString(KEY_GENDER, null));
        //user.put(KEY_BIRTHDAY, pref.getString(KEY_BIRTHDAY, null));
        //user.put(KEY_FBACCESSTOKEN, pref.getString(KEY_FBACCESSTOKEN, null));
        //user.put(KEY_FBACCESSEXPITES, pref.getString(KEY_FBACCESSEXPITES, "0"));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}


