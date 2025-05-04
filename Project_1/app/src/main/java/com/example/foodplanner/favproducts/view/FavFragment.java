package com.example.foodplanner.favproducts.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private RecyclerView favoritesRecyclerView;
    private FavAdapter adapter;
    private IFavPresenter presenter;
    private FragmentCommunication communicator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new FavPresenter(this, RepositoryImpl.getInstance(RemoteDataSource.getInstance(), MealLocalDataSource.getInstance(requireContext())));
        if (getActivity() instanceof FragmentCommunication) {
            communicator = (FragmentCommunication) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fav, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favoritesRecyclerView = view.findViewById(R.id.favorites_recycler_view);
        adapter = new FavAdapter(requireContext(), this, communicator);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        favoritesRecyclerView.setAdapter(adapter);

        presenter.getLocalData().observe(getViewLifecycleOwner(), meals -> {
            if (meals != null && !meals.isEmpty()) {
                showFavorites(meals);
            } else {
                showError("No favorite meals yet");
            }
        });
    }

    @Override
    public void showFavorites(List<Meal> meals) {
        adapter.setMeals(meals);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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