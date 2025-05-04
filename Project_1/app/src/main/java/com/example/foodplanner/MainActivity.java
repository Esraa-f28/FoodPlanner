package com.example.foodplanner;

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

public class MainActivity extends AppCompatActivity implements FragmentCommunication, FragmentCommunicator {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        // Load default fragment
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
            } else if (itemId == R.id.nav_calendar) {
                selectedFragment = new CalendarFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
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
}