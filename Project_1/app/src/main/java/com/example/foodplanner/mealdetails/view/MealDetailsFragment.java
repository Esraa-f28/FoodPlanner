package com.example.foodplanner.mealdetails.view;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplanner.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.example.foodplanner.mealdetails.presenter.MealDetailsPresenter;
import com.example.foodplanner.mealdetails.presenter.MealDetailsPresenterImpl;
import com.example.foodplanner.model.local_source.MealLocalDataSource;
import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.pojo.ScheduleMeal;
import com.example.foodplanner.model.remote_source.RemoteDataSource;
import com.example.foodplanner.model.repositry.Repository;
import com.example.foodplanner.model.repositry.RepositoryImpl;
import com.bumptech.glide.Glide;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MealDetailsFragment extends Fragment implements MealDetailsView {
    private static final String TAG = "MealDetailsFragment";
    private MealDetailsPresenter presenter;
    private IngredientsAdapter ingredientsAdapter;
    private Meal currentMeal;
    private View rootView;
    private ImageButton favButton;
    private ImageButton calendarButton;
    private boolean isFavorite = false;
    private boolean isScheduled = false;
    private Repository repository;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "UserPrefs";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("meal")) {
            currentMeal = (Meal) getArguments().getSerializable("meal");
            if (currentMeal != null) {
                Log.d(TAG, "Received meal: " + currentMeal.getStrMeal() + ", idMeal=" + currentMeal.getIdMeal());
            } else {
                Log.e(TAG, "Meal object is null");
            }
        } else {
            Log.e(TAG, "No meal data in arguments");
        }

        repository = RepositoryImpl.getInstance(RemoteDataSource.getInstance(), MealLocalDataSource.getInstance(requireContext()));
        presenter = new MealDetailsPresenterImpl(this, repository, requireContext());

        // Initialize SharedPreferences
        prefs = requireContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_meal_details, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.setLifecycleOwner(getViewLifecycleOwner());

        favButton = rootView.findViewById(R.id.favButton);
        favButton.setOnClickListener(v -> toggleFavorite());

        calendarButton = rootView.findViewById(R.id.calendarButton);
        calendarButton.setOnClickListener(v -> showDateTimePicker());

        YouTubePlayerView youtubePlayerView = rootView.findViewById(R.id.youtubePlayerView);
        getLifecycle().addObserver(youtubePlayerView);

        RecyclerView ingredientsRecyclerView = rootView.findViewById(R.id.ingredientsRecyclerView);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        ingredientsAdapter = new IngredientsAdapter(new ArrayList<>());
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);

        if (currentMeal != null && currentMeal.getIdMeal() != null && !currentMeal.getIdMeal().isEmpty()) {
            Log.d(TAG, "Fetching details for meal id: " + currentMeal.getIdMeal());
            showLoading();
            presenter.getMealDetails(currentMeal.getIdMeal());
        } else {
            Log.e(TAG, "No valid meal data or idMeal");
            showError("Failed to load meal details");
        }
    }

    private void toggleFavorite() {
        // Check if user is a guest
        boolean isGuest = prefs.getBoolean("isGuest", true); // Default to true if not set
        if (isGuest) {
            Toast.makeText(getContext(), "Please sign up to add meals to favorites", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentMeal == null) {
            Log.e(TAG, "Cannot toggle favorite: currentMeal is null");
            showError("Cannot toggle favorite");
            return;
        }
        if (isFavorite) {
            presenter.removeFromFavorites(currentMeal);
            Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
        } else {
            presenter.addToFavorites(currentMeal);
            Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDateTimePicker() {
        // Check if user is a guest
        boolean isGuest = prefs.getBoolean("isGuest", true); // Default to true if not set
        if (isGuest) {
            Toast.makeText(getContext(), "Please sign up to schedule meals", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentMeal == null || currentMeal.getIdMeal() == null || currentMeal.getStrMeal() == null) {
            Log.e(TAG, "Cannot schedule meal: currentMeal or required fields are null");
            showError("Cannot schedule meal");
            return;
        }

        final Calendar calendar = Calendar.getInstance();
        final Calendar minDate = Calendar.getInstance(); // Today
        final Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_MONTH, 7); // 7 days from today

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            requireContext(),
                            (view1, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                String date = dateFormat.format(calendar.getTime());
                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
                                String time = timeFormat.format(calendar.getTime());

                                String thumbnail = currentMeal.getStrMealThumb() != null ?
                                        currentMeal.getStrMealThumb() : "";
                                ScheduleMeal scheduleMeal = new ScheduleMeal(
                                        currentMeal.getIdMeal(),
                                        currentMeal.getStrMeal(),
                                        thumbnail,
                                        date,
                                        time
                                );
                                presenter.addToSchedule(scheduleMeal, currentMeal);

                                isScheduled = true;
                                calendarButton.setImageResource(R.drawable.calender_filled);
                                Toast.makeText(getContext(),
                                        "Meal scheduled for " + date + " at " + time,
                                        Toast.LENGTH_SHORT).show();
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Restrict date picker to current date and next 7 days
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        datePickerDialog.show();
    }

    @Override
    public void showMealDetails(Meal meal) {
        if (rootView == null) {
            Log.e(TAG, "Root view is null");
            return;
        }
        currentMeal = meal;
        Log.d(TAG, "Showing meal details: " + meal.getStrMeal());
        try {
            TextView mealName = rootView.findViewById(R.id.mealName);
            TextView category = rootView.findViewById(R.id.mealCategory);
            TextView area = rootView.findViewById(R.id.mealArea);
            TextView instructions = rootView.findViewById(R.id.mealInstructions);
            ImageView mealImage = rootView.findViewById(R.id.mealImage);

            mealName.setText(meal.getStrMeal() != null ? meal.getStrMeal() : "Unknown");
            category.setText(meal.getStrCategory() != null ? meal.getStrCategory() : "Unknown");
            area.setText(meal.getStrArea() != null ? meal.getStrArea() : "Unknown");
            instructions.setText(meal.getStrInstructions() != null ? meal.getStrInstructions() : "No instructions available");

            Glide.with(this)
                    .load(meal.getStrMealThumb())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(mealImage);

            YouTubePlayerView youtubePlayerView = rootView.findViewById(R.id.youtubePlayerView);
            String videoId = extractVideoId(meal.getStrYoutube());
            if (videoId != null) {
                youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(YouTubePlayer youTubePlayer) {
                        youTubePlayer.cueVideo(videoId, 0);
                    }
                });
            }

            List<MealIngredientExtractor.IngredientItem> ingredients = MealIngredientExtractor.extractIngredients(meal);
            Log.d(TAG, "Ingredients count: " + ingredients.size());
            ingredientsAdapter.setIngredients(ingredients);

            presenter.checkIfFavorite(meal.getIdMeal());
        } catch (Exception e) {
            Log.e(TAG, "Error showing meal details", e);
            showError("Error displaying meal");
        }
    }

    @Override
    public void setFavorite(boolean favoriteStatus) {
        isFavorite = favoriteStatus;
        try {
            favButton.setImageResource(isFavorite ? R.drawable.fav_filled : R.drawable.fav);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Drawable resource not found for favorite button", e);
            favButton.setImageDrawable(null);
        }
        Log.d(TAG, "Favorite status set: " + isFavorite);
    }

    @Override
    public void setScheduled(boolean scheduledStatus) {
        isScheduled = scheduledStatus;
        try {
            calendarButton.setImageResource(isScheduled ? R.drawable.calender_filled : R.drawable.calender);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Drawable resource not found for calendar button", e);
            calendarButton.setImageDrawable(null);
        }
        Log.d(TAG, "Scheduled status set: " + isScheduled);
    }

    @Override
    public void showError(String errorMessage) {
        Log.e(TAG, "Error: " + errorMessage);
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        Log.d(TAG, "Showing loading indicator");
    }

    @Override
    public void hideLoading() {
        Log.d(TAG, "Hiding loading indicator");
    }

    @Override
    public void showIngredientsList() {
        Log.d(TAG, "Ingredients list shown");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        YouTubePlayerView youtubePlayerView = rootView.findViewById(R.id.youtubePlayerView);
        if (youtubePlayerView != null) {
            youtubePlayerView.release();
        }
        presenter.onDestroy();
        rootView = null;
    }

    private String extractVideoId(String youtubeUrl) {
        if (youtubeUrl == null || youtubeUrl.isEmpty()) {
            Log.d(TAG, "No YouTube URL provided");
            return null;
        }
        try {
            String videoId = null;
            if (youtubeUrl.contains("v=")) {
                videoId = youtubeUrl.substring(youtubeUrl.indexOf("v=") + 2);
                int ampersandIndex = videoId.indexOf('&');
                if (ampersandIndex != -1) {
                    videoId = videoId.substring(0, ampersandIndex);
                }
            } else if (youtubeUrl.contains("youtu.be/")) {
                videoId = youtubeUrl.substring(youtubeUrl.indexOf("youtu.be/") + 9);
                int queryIndex = videoId.indexOf('?');
                if (queryIndex != -1) {
                    videoId = videoId.substring(0, queryIndex);
                }
            }
            if (videoId != null && !videoId.isEmpty()) {
                Log.d(TAG, "Extracted video ID: " + videoId);
                return videoId;
            }
            Log.e(TAG, "Invalid YouTube URL format: " + youtubeUrl);
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error extracting YouTube video ID", e);
            return null;
        }
    }
}