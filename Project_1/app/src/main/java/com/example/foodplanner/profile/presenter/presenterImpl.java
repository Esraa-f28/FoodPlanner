package com.example.foodplanner.profile.presenter;

import com.example.foodplanner.profile.view.IView;
import com.google.firebase.auth.FirebaseAuth;
import android.content.SharedPreferences;

public class presenterImpl implements presenter {
    private IView view;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;
    private String userId;
    private boolean isGuest;

    public presenterImpl(IView view, SharedPreferences prefs, String userId, boolean isGuest) {
        this.view = view;
        this.prefs = prefs;
        this.userId = userId;
        this.isGuest = isGuest;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void loadUserInfo() {
        String email = prefs.getString("email", null);
        if (isGuest) {
            view.showUserInfo("Guest User", true);
        } else if (email != null) {
            view.showUserInfo(email, false);
        } else {
            view.showUserInfo("Unknown User", false);
        }
    }

    @Override
    public void logout() {
        if (isGuest) {
            clearSharedPreferences();
            view.onLogoutSuccess();
            return;
        }

        mAuth.signOut();
        clearSharedPreferences();
        view.onLogoutSuccess();
    }

    private void clearSharedPreferences() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}