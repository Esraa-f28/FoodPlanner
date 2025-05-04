package com.example.foodplanner.favproducts.presenter;

import androidx.lifecycle.LiveData;

import com.example.foodplanner.favproducts.view.IFavView;
import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.repositry.Repository;

import java.util.List;

public class FavPresenter implements IFavPresenter {
    private IFavView view;
    private Repository repository;

    public FavPresenter(IFavView view, Repository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public LiveData<List<Meal>> getLocalData() {
        return repository.getAllSavedMeals();
    }

    @Override
    public void removeFromFavorites(Meal meal) {
        repository.deleteMeal(meal);
        if (view != null) {
            view.onMealRemoved(meal);
        }
    }
}