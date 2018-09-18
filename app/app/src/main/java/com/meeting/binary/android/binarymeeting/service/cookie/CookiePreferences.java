package com.meeting.binary.android.binarymeeting.service.cookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by meldi on 4/2/2018.
 */

public class CookiePreferences {

    private static final String PREF_COOKIES = "PREF_COOKIES";
    private static final String PREF_NAME = "PREF_NAME";


    public static String getStoredCookie(Context context){
        return PreferenceManager.
                getDefaultSharedPreferences(context)
                .getString(PREF_COOKIES, null);
    }


    public static void setStoredCookie(Context context, String cookie){
        PreferenceManager.getDefaultSharedPreferences(context).
                edit().putString(PREF_COOKIES ,cookie)
                .apply();
    }

    public static String getStoredName(Context context){
        return PreferenceManager.
                getDefaultSharedPreferences(context)
                .getString(PREF_NAME, null);
    }

    public static void setStoredName(Context context, String name){
        PreferenceManager.getDefaultSharedPreferences(context).
                edit().putString(PREF_NAME ,name)
                .apply();
    }

    public static String getPrefCookies() {
        return PREF_COOKIES;
    }


    public static void deleteOnLogout(Context context){
        SharedPreferences sharedPreferences = context
                .getSharedPreferences("PreferencesName", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(CookiePreferences.getPrefCookies()).apply();

        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
    }


}
