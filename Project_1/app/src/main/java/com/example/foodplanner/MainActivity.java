package com.example.foodplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.foodplanner.favproducts.view.FavFragment;
import com.example.foodplanner.homepage.view.HomeFragment;
import com.example.foodplanner.mealdetails.view.MealDetailsFragment;
import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.profile.view.ProfileFragment;
import com.example.foodplanner.search.view.SearchFragment;
import com.example.foodplanner.weeklyplan.view.CalendarFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements FragmentCommunication, FragmentCommunicator {
    private static final String TAG = "MainActivity";
    private static final String PREFS_NAME = "UserPrefs";
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;
    private String userId;
    private boolean isGuest;
    private Fragment currentFragment;
    private Fragment previousFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Check authentication state
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userId = prefs.getString("userId", null);
        isGuest = prefs.getBoolean("isGuest", true);

        if (currentUser == null && userId == null) {
            Log.d(TAG, "No user or guest, redirecting to SignInActivity");
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }

        if (currentUser != null) {
            userId = currentUser.getUid();
            isGuest = false;
            saveLoginState(userId, currentUser.getEmail(), false);
            Log.d(TAG, "User signed in: " + userId);
        } else if (userId != null && isGuest) {
            Log.d(TAG, "Continuing as guest: " + userId);
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        // Initialize with HomeFragment
        loadFragment(new HomeFragment());
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            } else if (itemId == R.id.nav_fav) {
                selectedFragment = new FavFragment();
                Bundle bundle = new Bundle();
                bundle.putString("userId", userId);
                selectedFragment.setArguments(bundle);
            } else if (itemId == R.id.nav_calendar) {
                selectedFragment = new CalendarFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("userId", userId);
                bundle.putBoolean("isGuest", isGuest);
                selectedFragment.setArguments(bundle);
            }

            return loadFragment(selectedFragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            try {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                Log.d(TAG, "Fragment loaded: " + fragment.getClass().getSimpleName());
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Failed to load fragment: " + fragment.getClass().getSimpleName(), e);
                return false;
            }
        }
        return false;
    }

    @Override
    public void navigateToSearch(String filterType, String filterValue) {
        Log.d(TAG, "Navigating to SearchFragment: type=" + filterType + ", value=" + filterValue);
        SearchFragment searchFragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("filterType", filterType);
        bundle.putString("filterValue", filterValue);
        searchFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, searchFragment);
        transaction.addToBackStack(null);
        try {
            transaction.commit();
            Log.d(TAG, "SearchFragment navigation committed");
        } catch (Exception e) {
            Log.e(TAG, "SearchFragment navigation failed", e);
        }
    }

    @Override
    public void navigateToMealDetails(Meal meal) {
        Log.d(TAG, "Navigating to MealDetailsFragment: " + (meal != null ? meal.getStrMeal() : "null") + ", idMeal=" + (meal != null ? meal.getIdMeal() : "null"));
        if (meal == null || meal.getIdMeal() == null) {
            Log.e(TAG, "Cannot navigate: meal or idMeal is null");
            return;
        }
        MealDetailsFragment mealDetailsFragment = new MealDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("meal", meal);
        mealDetailsFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, mealDetailsFragment);
        transaction.addToBackStack(null);
        try {
            transaction.commit();
            Log.d(TAG, "MealDetailsFragment navigation committed");
        } catch (Exception e) {
            Log.e(TAG, "MealDetailsFragment navigation failed", e);
        }
    }

    private void saveLoginState(String uid, String email, boolean isGuest) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userId", uid);
        editor.putString("email", email);
        editor.putBoolean("isGuest", isGuest);
        editor.apply();
    }
}