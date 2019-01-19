package com.devnock.basearchitecture.application;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

public class Preferences {

    @Nullable
    private static String getStringPreferences(String key) {
        return getStringPreferences(key, null);
    }

    @Nullable
    private static String getStringPreferences(String key, @Nullable String defaultValue) {
        synchronized (key) {
            return getPreferences().getString(key, defaultValue);
        }
    }

    private static void setStringPreferences(String key, String value) {
        synchronized (key) {
            getPreferences().edit().putString(key, value).apply();
        }
    }

    private static boolean getBooleanPreferences(String key, boolean defaultValue) {
        synchronized (key) {
            return getPreferences().getBoolean(key, defaultValue);
        }
    }

    private static void setBooleanPreferences(String key, boolean value) {
        synchronized (key) {
            getPreferences().edit().putBoolean(key, value).apply();
        }
    }

    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(App.getContext());
    }

    public static String getAuthToken() {
        // TODO:
        return null;
    }

    public static boolean isUserAuthorize() {
        return false;
        // TODO:
    }

    public static void logOut() {
        // TODO:
    }
}
