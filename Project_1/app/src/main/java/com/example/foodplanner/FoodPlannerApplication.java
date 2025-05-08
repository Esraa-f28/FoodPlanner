package com.example.foodplanner;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import android.util.Log;

public class FoodPlannerApplication extends Application {
    private static final String TAG = "FoodPlannerApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            FirebaseApp.initializeApp(this);
            Log.d(TAG, "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Firebase", e);
        }
    }
}