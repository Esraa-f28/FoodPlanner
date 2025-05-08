package com.example.foodplanner.profile.view;

public interface IView {
    void showUserInfo(String email, boolean isGuest);
    void onLogoutSuccess();
    void onLogoutError(String message);
}