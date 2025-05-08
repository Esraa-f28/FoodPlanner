package com.example.foodplanner.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class UserSessionManager {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_IS_GUEST = "isGuest";
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public UserSessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public String getUserId() {
        String userId = preferences.getString(KEY_USER_ID, null);
        if (userId == null) {
            userId = "guest_" + UUID.randomUUID().toString();
            editor.putString(KEY_USER_ID, userId);
            editor.putBoolean(KEY_IS_GUEST, true);
            editor.apply();
        }
        return userId;
    }

    public boolean isGuest() {
        return preferences.getBoolean(KEY_IS_GUEST, true);
    }

    public void setUserId(String userId, boolean isGuest) {
        editor.putString(KEY_USER_ID, userId);
        editor.putBoolean(KEY_IS_GUEST, isGuest);
        editor.apply();
    }

    public void clearSession() {
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_IS_GUEST);
        editor.apply();
    }
}