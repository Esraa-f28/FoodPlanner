package com.example.foodplanner.weeklyplan.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplanner.FragmentCommunication;
import com.example.foodplanner.R;
import com.example.foodplanner.model.local_source.MealLocalDataSource;
import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.pojo.ScheduleMeal;
import com.example.foodplanner.model.repositry.Repository;
import com.example.foodplanner.model.repositry.RepositoryImpl;
import com.example.foodplanner.model.remote_source.RemoteDataSource;
import com.example.foodplanner.weeklyplan.presenter.Presenter;
import com.example.foodplanner.weeklyplan.presenter.PresenterImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment implements IView {

    private static final String TAG = "CalendarFragment";
    private CalendarView calendarView;
    private TextView textView;
    private RecyclerView recyclerView;
    private CalenderAdapter adapter;
    private Presenter presenter;
    private String selectedDate;
    private FragmentCommunication communicator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Repository repository = RepositoryImpl.getInstance(
                RemoteDataSource.getInstance(),
                MealLocalDataSource.getInstance(requireContext())
        );
        presenter = new PresenterImpl(this, repository);
        if (requireActivity() instanceof FragmentCommunication) {
            communicator = (FragmentCommunication) requireActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarView = view.findViewById(R.id.calendarView);
        textView = view.findViewById(R.id.textView);
        recyclerView = view.findViewById(R.id.recyclerView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CalenderAdapter(
                requireContext().getApplicationContext(),
                new ArrayList<>(),
                meal -> presenter.deleteScheduledMeal(meal),
                meal -> {
                    if (communicator != null && meal.getIdMeal() != null) {
                        Meal partialMeal = convertToMeal(meal);
                        communicator.navigateToMealDetails(partialMeal);
                    }
                }
        );
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "Adapter initialized with context: " + (requireContext() != null ? "not null" : "null"));

        // Set up CalendarView
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = dateFormat.format(calendar.getTime());
        updateMealsForDate(selectedDate);

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
            updateMealsForDate(selectedDate);
        });
    }

    private Meal convertToMeal(ScheduleMeal scheduleMeal) {
        Meal meal = new Meal();
        meal.setIdMeal(scheduleMeal.getIdMeal());
        meal.setStrMeal(scheduleMeal.getStrMeal());
        meal.setStrMealThumb(scheduleMeal.getStrMealThumb());
        return meal;
    }

    private void updateMealsForDate(String date) {
        textView.setText("Meals for " + date);
        presenter.getMealsForDate(date);
        Log.d(TAG, "updateMealsForDate: " + date);
    }

    @Override
    public void showMeals(List<ScheduleMeal> meals) {
        adapter.setMeals(meals != null ? meals : new ArrayList<>());
        Log.d(TAG, "showMeals, meals size: " + (meals != null ? meals.size() : 0));
    }

    @Override
    public LifecycleOwner getLifecycleOwner() {
        return getViewLifecycleOwner();
    }
}