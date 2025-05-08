package com.example.foodplanner.weeklyplan.view;

import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.example.foodplanner.utils.UserSessionManager;
import com.example.foodplanner.weeklyplan.presenter.Presenter;
import com.example.foodplanner.weeklyplan.presenter.PresenterImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment implements IView {

    private static final String TAG = "CalendarFragment";
    private static final int CALENDAR_PERMISSION_REQUEST_CODE = 100;
    private CalendarView calendarView;
    private TextView textView;
    private RecyclerView recyclerView;
    private CalenderAdapter adapter;
    private Presenter presenter;
    private String selectedDate;
    private FragmentCommunication communicator;
    private UserSessionManager sessionManager;
    private String userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Repository repository = RepositoryImpl.getInstance(
                RemoteDataSource.getInstance(),
                MealLocalDataSource.getInstance(requireContext())
        );
        presenter = new PresenterImpl(this, repository);
        sessionManager = new UserSessionManager(requireContext());
        userId = sessionManager.getUserId();
        if (requireActivity() instanceof FragmentCommunication) {
            communicator = (FragmentCommunication) requireActivity();
        }
        Log.d(TAG, "User ID initialized: " + (userId != null ? userId : "null"));
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
                },
                this::addMealToPhoneCalendar
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

    private void addMealToPhoneCalendar(ScheduleMeal meal) {
        // Check for WRITE_CALENDAR permission
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            requestCalendarPermission();
            return;
        }

        try {
            // Parse the selected date (yyyy-MM-dd)
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date eventDate = dateFormat.parse(selectedDate);
            if (eventDate == null) {
                Log.e(TAG, "Failed to parse selected date: " + selectedDate);
                Toast.makeText(requireContext(), "Failed to add meal to calendar", Toast.LENGTH_SHORT).show();
                return;
            }

            // Set start and end times (all-day event)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(eventDate);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long startMillis = calendar.getTimeInMillis();
            long endMillis = startMillis + 24 * 60 * 60 * 1000; // Next day for all-day event

            // Insert event into calendar
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.TITLE, meal.getStrMeal());
            values.put(CalendarContract.Events.DESCRIPTION, "Meal plan from FoodPlanner");
            values.put(CalendarContract.Events.ALL_DAY, 1);
            values.put(CalendarContract.Events.CALENDAR_ID, 1); // Default calendar
            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

            requireContext().getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);

            Log.d(TAG, "Added meal to calendar: " + meal.getStrMeal());
            Toast.makeText(requireContext(), "Added " + meal.getStrMeal() + " to calendar", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date for calendar event", e);
            Toast.makeText(requireContext(), "Error adding meal to calendar", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Log.e(TAG, "Permission error adding calendar event", e);
            Toast.makeText(requireContext(), "Calendar permission required", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestCalendarPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                android.Manifest.permission.WRITE_CALENDAR)) {
            Toast.makeText(requireContext(),
                    "Calendar permission is needed to add events",
                    Toast.LENGTH_LONG).show();
        }
        requestPermissions(new String[]{android.Manifest.permission.WRITE_CALENDAR},
                CALENDAR_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALENDAR_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Calendar permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(),
                        "Calendar permission denied. Cannot add events.",
                        Toast.LENGTH_LONG).show();
            }
        }
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
        if (userId == null || userId.isEmpty()) {
            Log.w(TAG, "No userId available, cannot fetch meals");
            showMeals(new ArrayList<>());
            return;
        }
        presenter.getMealsForUserAndDate(userId, date);
        Log.d(TAG, "updateMealsForDate: " + date + " for userId: " + userId);
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