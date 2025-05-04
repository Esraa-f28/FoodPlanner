package com.example.foodplanner.mealdetails.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplanner.R;
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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

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
    private Repository repository; // Class-level field

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentMeal = (Meal) getArguments().getSerializable("meal");
            Log.d(TAG, "Received meal: " + (currentMeal != null ? currentMeal.getStrMeal() : "null") + ", idMeal=" + (currentMeal != null ? currentMeal.getIdMeal() : "null"));
        }

        // Initialize repository field
        repository = RepositoryImpl.getInstance(RemoteDataSource.getInstance(), MealLocalDataSource.getInstance(requireContext()));
        presenter = new MealDetailsPresenterImpl(this, repository, requireContext());
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
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientsAdapter = new IngredientsAdapter(new ArrayList<>());
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);

        if (currentMeal != null && currentMeal.getIdMeal() != null) {
            Log.d(TAG, "Fetching details for meal id: " + currentMeal.getIdMeal());
            showLoading();
            presenter.getMealDetails(currentMeal.getIdMeal());
        } else {
            Log.e(TAG, "No valid meal data or idMeal");
            showError("Failed to load meal details");
        }
    }

    private void toggleFavorite() {
        if (currentMeal == null) {
            Log.e(TAG, "Cannot toggle favorite: currentMeal is null");
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
        if (currentMeal == null || currentMeal.getIdMeal() == null || currentMeal.getStrMeal() == null) {
            Log.e(TAG, "Cannot schedule meal: currentMeal or required fields are null");
            showError("Cannot schedule meal");
            return;
        }

        final Calendar calendar = Calendar.getInstance();
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

                                // Use fallback for strMealThumb if null
                                String thumbnail = currentMeal.getStrMealThumb() != null ? currentMeal.getStrMealThumb() : "";
                                ScheduleMeal scheduleMeal = new ScheduleMeal(
                                        currentMeal.getIdMeal(),
                                        currentMeal.getStrMeal(),
                                        thumbnail,
                                        date,
                                        time
                                );
                                repository.insertScheduledMeal(scheduleMeal);
                                // Cache full Meal for offline support
                                repository.insertMeal(currentMeal);
                                Log.d(TAG, "Scheduled meal: " + currentMeal.getStrMeal() + " on " + date + " at " + time);
                                Toast.makeText(getContext(), "Meal scheduled for " + date + " at " + time, Toast.LENGTH_SHORT).show();
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
        // Uncomment when resources are added
        // favButton.setImageResource(isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);
        Log.d(TAG, "Favorite status set: " + isFavorite);
    }

    @Override
    public void showError(String errorMessage) {
        Log.e(TAG, "Error: " + errorMessage);
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        Log.d(TAG, "Showing loading indicator");
        // Implement loading indicator if needed
    }

    @Override
    public void hideLoading() {
        Log.d(TAG, "Hiding loading indicator");
        // Hide loading indicator if implemented
    }

    @Override
    public void showIngredientsList() {
        Log.d(TAG, "Ingredients list shown");
        // RecyclerView is already visible
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
    }

    private String extractVideoId(String youtubeUrl) {
        if (youtubeUrl == null || youtubeUrl.isEmpty()) {
            Log.d(TAG, "No YouTube URL provided");
            return null;
        }
        try {
            return youtubeUrl.substring(youtubeUrl.indexOf("=") + 1);
        } catch (Exception e) {
            Log.e(TAG, "Error extracting YouTube video ID", e);
            return null;
        }
    }
}