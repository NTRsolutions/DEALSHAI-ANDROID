package com.ogma.dealshaiapp.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class Session {

    public static String KEY_ID = "KEY_ID";
    public static String KEY_USER_TYPE = "KEY_USER_TYPE";
    public static String KEY_EMAIL = "KEY_EMAIL";
    public static String KEY_CONTACT_NUMBER = "KEY_CONTACT_NUMBER";
    public static String KEY_NAME = "KEY_NAME";
    public static String KEY_GENDER = "KEY_GENDER";
    public static String KEY_DOB = "KEY_DOB";
    public static String KEY_IMAGE = "KEY_IMAGE";
    public static String KEY_IMAGE_PATH = "KEY_IMAGE_PATH";
    public static String KEY_CITY = "Select City ";
    public static String KEY_AREA = "Select Area ";
    public static String KEY_LATITUDE = "KEY_LATITUDE";
    public static String KEY_LONGITUDE = "KEY_LONGITUDE";
    private static String PREF_NAME = "SessionVariable";

    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    public Session(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveUserLoginSession(String id, String name, String mobile, String email, String dob, String gender, String pfImg) {
        editor.putString(KEY_ID, id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_CONTACT_NUMBER, mobile);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_DOB, dob);
        editor.putString(KEY_GENDER, gender);
        editor.putString(KEY_IMAGE, pfImg);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_CONTACT_NUMBER, pref.getString(KEY_CONTACT_NUMBER, null));
        user.put(KEY_DOB, pref.getString(KEY_DOB, null));
        user.put(KEY_GENDER, pref.getString(KEY_GENDER, ""));
        user.put(KEY_IMAGE, pref.getString(KEY_IMAGE, null));
        return user;
    }

    public static String getKeyUserType() {
        return KEY_USER_TYPE;
    }

    public static void setKeyUserType(String keyUserType) {
        KEY_USER_TYPE = keyUserType;
    }

    public void setKeyImagePath(String path) {
        editor.putString(KEY_IMAGE_PATH, path);
        editor.commit();
    }

    public HashMap<String, String> getKeyImagePath() {
        HashMap<String, String> path = new HashMap<String, String>();
        path.put(KEY_IMAGE_PATH, pref.getString(KEY_IMAGE_PATH, null));
        return path;
    }

    public void setLocationDetails(String cityName, String areaName, String lat, String lng) {
        editor.putString(KEY_CITY, cityName);
        editor.putString(KEY_AREA, areaName);
        editor.putString(KEY_LATITUDE, lat);
        editor.putString(KEY_LONGITUDE, lng);
        editor.commit();
    }

    public HashMap<String, String> getLocationDetails() {
        HashMap<String, String> locationDetails = new HashMap<>();
        locationDetails.put(KEY_CITY, pref.getString(KEY_CITY, ""));
        locationDetails.put(KEY_AREA, pref.getString(KEY_AREA, ""));
        locationDetails.put(KEY_LATITUDE, pref.getString(KEY_LATITUDE, ""));
        locationDetails.put(KEY_LONGITUDE, pref.getString(KEY_LONGITUDE, ""));
        return locationDetails;
    }

    public void revokeSession() {
        KEY_ID = "";
        KEY_EMAIL = "";
        KEY_CONTACT_NUMBER = "";
        KEY_NAME = "";
        KEY_GENDER = "";
        KEY_DOB = "";
        KEY_IMAGE = "";
        KEY_IMAGE_PATH = "";
        KEY_CITY = "";
        KEY_AREA = "";
        KEY_LATITUDE = "";
        KEY_LONGITUDE = "";

        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_ID, KEY_ID);
        editor.putString(KEY_NAME, KEY_NAME);
        editor.putString(KEY_CONTACT_NUMBER, KEY_CONTACT_NUMBER);
        editor.putString(KEY_EMAIL, KEY_EMAIL);
        editor.putString(KEY_DOB, KEY_DOB);
        editor.putString(KEY_GENDER, KEY_GENDER);
        editor.putString(KEY_IMAGE, KEY_IMAGE);
        editor.putString(KEY_IMAGE_PATH, KEY_IMAGE_PATH);
        editor.putString(KEY_CITY, KEY_CITY);
        editor.putString(KEY_AREA, KEY_AREA);
        editor.putString(KEY_LATITUDE, KEY_LATITUDE);
        editor.putString(KEY_LONGITUDE, KEY_LONGITUDE);
        editor.apply();
    }

    public void clearPref() {
        SharedPreferences.Editor editor = pref.edit();
        editor.clear().apply();
    }
}
