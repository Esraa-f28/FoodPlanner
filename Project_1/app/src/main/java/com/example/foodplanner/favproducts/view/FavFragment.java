package com.example.foodplanner.favproducts.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.foodplanner.FragmentCommunication;
import com.example.foodplanner.R;
import com.example.foodplanner.favproducts.presenter.FavPresenter;
import com.example.foodplanner.favproducts.presenter.IFavPresenter;
import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.remote_source.RemoteDataSource;
import com.example.foodplanner.model.repositry.Repository;
import com.example.foodplanner.model.repositry.RepositoryImpl;
import com.example.foodplanner.model.local_source.MealLocalDataSource;

import java.util.List;

public class FavFragment extends Fragment implements IFavView, OnFavClickListener {

    private static final String TAG = "FavFragment";
    private RecyclerView favoritesRecyclerView;
    private FavAdapter adapter;
    private IFavPresenter presenter;
    private FragmentCommunication communicator;
    private String userId;

    private LottieAnimationView lottieAnimationView;
    private TextView emptyView;
    private TextView favoritesTitle;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
            Log.d(TAG, "Received userId: " + userId);
        }
        presenter = new FavPresenter(this, RepositoryImpl.getInstance(RemoteDataSource.getInstance(), MealLocalDataSource.getInstance(requireContext())));
        if (getActivity() instanceof FragmentCommunication) {
            communicator = (FragmentCommunication) getActivity();
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_fav, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favoritesRecyclerView = view.findViewById(R.id.favorites_recycler_view);
        adapter = new FavAdapter(requireContext(), this, communicator);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        favoritesRecyclerView.setAdapter(adapter);
        lottieAnimationView = view.findViewById(R.id.lottie_fav_animation);
        emptyView = view.findViewById(R.id.empty_view);
        favoritesTitle = view.findViewById(R.id.favourites_text);


        if (userId != null) {
            Log.d(TAG, "Fetching favorites for userId: " + userId);
            presenter.getFavorites(userId).observe(getViewLifecycleOwner(), meals -> {
                Log.d(TAG, "Received meals: " + (meals != null ? meals.size() : "null"));
                if (meals != null && !meals.isEmpty()) {
                    showFavorites(meals);
                    emptyView.setVisibility(View.GONE);
                    favoritesRecyclerView.setVisibility(View.VISIBLE);
                    favoritesTitle.setVisibility(View.VISIBLE);
                    lottieAnimationView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                    favoritesRecyclerView.setVisibility(View.GONE);
                    favoritesTitle.setVisibility(View.VISIBLE);
                    lottieAnimationView.setVisibility(View.VISIBLE);
                }

            });
        } else {
            showError("User ID not provided");
        }
    }

    @Override
    public void showFavorites(List<Meal> meals) {
        adapter.setMeals(meals);
        Log.d(TAG, "Displayed " + meals.size() + " favorite meals");


    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Error: " + message);
    }

    @Override
    public void onMealRemoved(Meal meal) {
        Toast.makeText(getContext(), "Meal removed from favorites", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemoveClick(Meal meal) {
        presenter.removeFromFavorites(meal);
    }
}