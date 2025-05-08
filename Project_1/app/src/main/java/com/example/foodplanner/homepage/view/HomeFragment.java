package com.example.foodplanner.homepage.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.example.foodplanner.FragmentCommunication;
import com.example.foodplanner.FragmentCommunicator;
import com.example.foodplanner.R;
import com.example.foodplanner.model.local_source.MealLocalDataSource;
import com.example.foodplanner.model.pojo.Category;
import com.example.foodplanner.model.pojo.Country;
import com.example.foodplanner.model.pojo.Ingredients;
import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.remote_source.RemoteDataSource;
import com.example.foodplanner.model.repositry.Repository;
import com.example.foodplanner.model.repositry.RepositoryImpl;
import com.example.foodplanner.homepage.presenter.HomePresenterImpl;
import com.example.foodplanner.homepage.presenter.IHomePresenter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements HomeView {
    private static final String TAG = "HomeFragment";
    private TextView welcomeText;
    private RecyclerView categoriesRecyclerView, countriesRecyclerView, ingredientsRecyclerView;
    private RecyclerView categoriesMealsRecyclerView, countriesMealsRecyclerView, ingredientsMealsRecyclerView;
    private IHomePresenter presenter;
    private HomeAdapter categoriesAdapter, countriesAdapter, ingredientsAdapter;
    private MealAdapter mealOfTheDayAdapter, categoriesMealsAdapter, countriesMealsAdapter, ingredientsMealsAdapter;
    private Meal currentMealOfTheDay;
    private SharedPreferences preferences;
    private FragmentCommunicator searchCommunicator;
    private FragmentCommunication mealCommunicator;
    private String currentFilterSection; // Tracks which section (category/country/ingredient) is showing meals
    private static final String PREFS_NAME = "MealOfTheDayPrefs";
    private static final String MEAL_KEY = "meal";
    private static final String TIMESTAMP_KEY = "timestamp";
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private LinearLayout mainContent, noConnectionContainer;
    private LottieAnimationView noInternetAnimation;
    private Button retryButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Repository repository = RepositoryImpl.getInstance(
                RemoteDataSource.getInstance(),
                MealLocalDataSource.getInstance(this.getContext())
        );
        presenter = new HomePresenterImpl(this, repository);
        preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (getActivity() instanceof FragmentCommunicator) {
            searchCommunicator = (FragmentCommunicator) getActivity();
        }
        if (getActivity() instanceof FragmentCommunication) {
            mealCommunicator = (FragmentCommunication) getActivity();
        }
        connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        setupNetworkCallback();
        loadCachedMeal();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews(view);
        setupAdapters();
        setupRetryButton();
        checkNetworkState();
        if (isNetworkAvailable() && (currentMealOfTheDay == null || isMealExpired())) {
            presenter.getRandomMeal();
        } else if (currentMealOfTheDay != null) {
            showRandomMeal(currentMealOfTheDay);
        }
        if (isNetworkAvailable()) {
            presenter.getCategories();
            presenter.getCountries();
            presenter.getIngredients();
        }
        return view;
    }

    private void initializeViews(View view) {
        mainContent = view.findViewById(R.id.main_content);
        noConnectionContainer = view.findViewById(R.id.no_connection_container);
        noInternetAnimation = view.findViewById(R.id.no_internet_animation);
        retryButton = view.findViewById(R.id.retry_button);

        welcomeText = view.findViewById(R.id.welcome_text);
        welcomeText.setText("Welcome back! Discover delicious meals");

        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);
        countriesRecyclerView = view.findViewById(R.id.countries_recycler_view);
        ingredientsRecyclerView = view.findViewById(R.id.ingredients_recycler_view);
        categoriesMealsRecyclerView = view.findViewById(R.id.categories_meals_recycler_view);
        countriesMealsRecyclerView = view.findViewById(R.id.countries_meals_recycler_view);
        ingredientsMealsRecyclerView = view.findViewById(R.id.ingredients_meals_recycler_view);

        RecyclerView mealOfTheDayRecyclerView = view.findViewById(R.id.meal_of_the_day_recycler_view);
        mealOfTheDayRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mealOfTheDayAdapter = new MealAdapter(new ArrayList<>(), mealCommunicator);
        mealOfTheDayRecyclerView.setAdapter(mealOfTheDayAdapter);
    }

    private void setupAdapters() {
        // Categories adapter
        categoriesAdapter = new HomeAdapter(new ArrayList<>(), "category", searchCommunicator, this::onFilterSelected);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoriesRecyclerView.setAdapter(categoriesAdapter);

        // Countries adapter
        countriesAdapter = new HomeAdapter(new ArrayList<>(), "country", searchCommunicator, this::onFilterSelected);
        countriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        countriesRecyclerView.setAdapter(countriesAdapter);

        // Ingredients adapter
        ingredientsAdapter = new HomeAdapter(new ArrayList<>(), "ingredient", searchCommunicator, this::onFilterSelected);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);

        // Meals adapters for filter results
        categoriesMealsAdapter = new MealAdapter(new ArrayList<>(), mealCommunicator);
        categoriesMealsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoriesMealsRecyclerView.setAdapter(categoriesMealsAdapter);

        countriesMealsAdapter = new MealAdapter(new ArrayList<>(), mealCommunicator);
        countriesMealsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        countriesMealsRecyclerView.setAdapter(countriesMealsAdapter);

        ingredientsMealsAdapter = new MealAdapter(new ArrayList<>(), mealCommunicator);
        ingredientsMealsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        ingredientsMealsRecyclerView.setAdapter(ingredientsMealsAdapter);
    }

//    private void onFilterSelected(String filterType, String filterValue) {
//        Log.d(TAG, "Filter selected: type=" + filterType + ", value=" + filterValue);
//        // Hide all meals RecyclerViews
//        categoriesMealsRecyclerView.setVisibility(View.GONE);
//        countriesMealsRecyclerView.setVisibility(View.GONE);
//        ingredientsMealsRecyclerView.setVisibility(View.GONE);
//
//        // Clear previous meals
//        categoriesMealsAdapter.updateMeals(new ArrayList<>());
//        countriesMealsAdapter.updateMeals(new ArrayList<>());
//        ingredientsMealsAdapter.updateMeals(new ArrayList<>());
//
//        // Show the appropriate meals RecyclerView and fetch meals
//        currentFilterSection = filterType;
//        if (isNetworkAvailable()) {
//            switch (filterType) {
//                case "category":
//                    categoriesMealsRecyclerView.setVisibility(View.VISIBLE);
//                    presenter.getMealsByCategory(filterValue);
//                    break;
//                case "country":
//                    countriesMealsRecyclerView.setVisibility(View.VISIBLE);
//                    presenter.getMealsByCountry(filterValue);
//                    break;
//                case "ingredient":
//                    ingredientsMealsRecyclerView.setVisibility(View.VISIBLE);
//                    presenter.getMealsByIngredient(filterValue);
//                    break;
//            }
//        } else {
//            updateNetworkUI(false);
//        }
//    }
private void onFilterSelected(String filterType, String filterValue) {
    Log.d(TAG, "Filter selected: type=" + filterType + ", value=" + filterValue);
    if (filterType == null || (!filterType.equals("category") && !filterType.equals("country") && !filterType.equals("ingredient"))) {
        Log.e(TAG, "Invalid filter type: " + filterType);
        showError("Invalid filter selected");
        return;
    }
    // Hide all meals RecyclerViews
    categoriesMealsRecyclerView.setVisibility(View.GONE);
    countriesMealsRecyclerView.setVisibility(View.GONE);
    ingredientsMealsRecyclerView.setVisibility(View.GONE);

    // Clear previous meals
    categoriesMealsAdapter.updateMeals(new ArrayList<>());
    countriesMealsAdapter.updateMeals(new ArrayList<>());
    ingredientsMealsAdapter.updateMeals(new ArrayList<>());

    // Show the appropriate meals RecyclerView and fetch meals
    currentFilterSection = filterType;
    if (isNetworkAvailable()) {
        switch (filterType) {
            case "category":
                categoriesMealsRecyclerView.setVisibility(View.VISIBLE);
                presenter.getMealsByCategory(filterValue);
                break;
            case "country":
                countriesMealsRecyclerView.setVisibility(View.VISIBLE);
                presenter.getMealsByCountry(filterValue);
                break;
            case "ingredient":
                ingredientsMealsRecyclerView.setVisibility(View.VISIBLE);
                presenter.getMealsByIngredient(filterValue);
                break;
        }
    } else {
        updateNetworkUI(false);
    }
}
    private void loadCachedMeal() {
        String mealJson = preferences.getString(MEAL_KEY, null);
        if (mealJson != null) {
            Gson gson = new Gson();
            currentMealOfTheDay = gson.fromJson(mealJson, Meal.class);
        }
    }

    private boolean isMealExpired() {
        long lastUpdate = preferences.getLong(TIMESTAMP_KEY, 0);
        long currentTime = System.currentTimeMillis();
        long oneDayInMillis = 24 * 60 * 60 * 1000; // 24 hours
        return (currentTime - lastUpdate) > oneDayInMillis;
    }

    private void cacheMeal(Meal meal) {
        Gson gson = new Gson();
        String mealJson = gson.toJson(meal);
        preferences.edit()
                .putString(MEAL_KEY, mealJson)
                .putLong(TIMESTAMP_KEY, System.currentTimeMillis())
                .apply();
    }

    private void setupNetworkCallback() {
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                requireActivity().runOnUiThread(() -> {
                    Log.d(TAG, "onAvailable: Network restored");
                    updateNetworkUI(true);
                });
            }

            @Override
            public void onLost(Network network) {
                requireActivity().runOnUiThread(() -> {
                    Log.d(TAG, "onLost: Network lost");
                    updateNetworkUI(false);
                });
            }
        };
        Log.d(TAG, "Network callback initialized");
    }

    private void checkNetworkState() {
        boolean isNetworkAvailable = isNetworkAvailable();
        Log.d(TAG, "checkNetworkState: Network available=" + isNetworkAvailable);
        updateNetworkUI(isNetworkAvailable);
    }

    private void updateNetworkUI(boolean isNetworkAvailable) {
        if (isNetworkAvailable) {
            mainContent.setVisibility(View.VISIBLE);
            noConnectionContainer.setVisibility(View.GONE);
            noInternetAnimation.pauseAnimation();
            // Refresh data if needed
            if (currentMealOfTheDay == null || isMealExpired()) {
                presenter.getRandomMeal();
            }
            presenter.getCategories();
            presenter.getCountries();
            presenter.getIngredients();
        } else {
            mainContent.setVisibility(View.GONE);
            noConnectionContainer.setVisibility(View.VISIBLE);
            noInternetAnimation.playAnimation();
        }
    }

    private void setupRetryButton() {
        retryButton.setOnClickListener(v -> {
            Log.d(TAG, "Retry button clicked");
            checkNetworkState();
        });
    }

    private boolean isNetworkAvailable() {
        try {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                Log.d(TAG, "isNetworkAvailable: No active network");
                return false;
            }
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            boolean isAvailable = capabilities != null &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            Log.d(TAG, "isNetworkAvailable: " + isAvailable);
            return isAvailable;
        } catch (Exception e) {
            Log.e(TAG, "Error checking network availability", e);
            return false;
        }
    }

    @Override
    public void showRandomMeal(Meal meal) {
        Log.d(TAG, "Showing random meal: " + (meal != null ? meal.getStrMeal() : "null"));
        currentMealOfTheDay = meal;
        cacheMeal(meal);
        List<Meal> mealList = new ArrayList<>();
        mealList.add(meal);
        mealOfTheDayAdapter.updateMeals(mealList);
    }

    @Override
    public void showCategories(List<Category> categories) {
        Log.d(TAG, "Showing categories: " + (categories != null ? categories.size() : "null"));
        categoriesAdapter.updateOptions(categories);
    }

    @Override
    public void showCountries(List<Country> countries) {
        Log.d(TAG, "Showing countries: " + (countries != null ? countries.size() : "null"));
        countriesAdapter.updateOptions(countries);
    }

    @Override
    public void showIngredients(List<Ingredients> ingredients) {
        Log.d(TAG, "Showing ingredients: " + (ingredients != null ? ingredients.size() : "null"));
        ingredientsAdapter.updateOptions(ingredients);
    }

//    @Override
//    public void showMealsByFilter(List<Meal> meals) {
//        Log.d(TAG, "Showing meals for filter: " + currentFilterSection + ", count=" + (meals != null ? meals.size() : "null"));
//        if (meals == null || meals.isEmpty()) {
//            showError("No meals found for " + currentFilterSection);
//            return;
//        }
//        switch (currentFilterSection) {
//            case "category":
//                categoriesMealsAdapter.updateMeals(meals);
//                break;
//            case "country":
//                countriesMealsAdapter.updateMeals(meals);
//                break;
//            case "ingredient":
//                ingredientsMealsAdapter.updateMeals(meals);
//                break;
//        }
//    }
    @Override
    public void showMealsByFilter(List<Meal> meals) {
        Log.d(TAG, "Showing meals for filter: " + currentFilterSection + ", count=" + (meals != null ? meals.size() : "null"));
        if (meals == null || meals.isEmpty()) {
            showError("No meals found for " + (currentFilterSection != null ? currentFilterSection : "unknown"));
            return;
        }
        String filterSection = currentFilterSection != null ? currentFilterSection : "category";
        switch (filterSection) {
            case "category":
                categoriesMealsAdapter.updateMeals(meals);
                break;
            case "country":
                countriesMealsAdapter.updateMeals(meals);
                break;
            case "ingredient":
                ingredientsMealsAdapter.updateMeals(meals);
                break;
        }
    }
    @Override
    public void showError(String message) {
        Log.e(TAG, "Error: " + message);
        android.widget.Toast.makeText(getContext(), message, android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Registering network callback");
        try {
            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
            Log.d(TAG, "Network callback registered");
        } catch (Exception e) {
            Log.e(TAG, "Failed to register network callback", e);
        }
        checkNetworkState();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Unregistering network callback");
        try {
            if (networkCallback != null) {
                connectivityManager.unregisterNetworkCallback(networkCallback);
                Log.d(TAG, "Network callback unregistered");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to unregister network callback", e);
        }
        noInternetAnimation.pauseAnimation();
    }
}