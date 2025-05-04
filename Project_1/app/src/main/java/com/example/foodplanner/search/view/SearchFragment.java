package com.example.foodplanner.search.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodplanner.FragmentCommunication;
import com.example.foodplanner.R;
import com.example.foodplanner.model.local_source.MealLocalDataSource;
import com.example.foodplanner.model.pojo.Category;
import com.example.foodplanner.model.pojo.Country;
import com.example.foodplanner.model.pojo.Ingredients;
import com.example.foodplanner.model.pojo.Meal;
import com.example.foodplanner.model.remote_source.RemoteDataSource;
import com.example.foodplanner.model.repositry.Repository;
import com.example.foodplanner.model.repositry.RepositoryImpl;
import com.example.foodplanner.search.presenter.ISearchPresenter;
import com.example.foodplanner.search.presenter.SearchPresenterImpl;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SearchView {

    private EditText searchEditText;
    private Button categoryButton, countryButton, ingredientButton;
    private RecyclerView filterOptionsRecyclerView, mealsRecyclerView;
    private TextView emptyStateText;
    private ISearchPresenter presenter;
    private FilterOptionsAdapter filterOptionsAdapter;
    private MealAdapter mealAdapter;
    private FragmentCommunication communicator; // Added

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Repository repository = RepositoryImpl.getInstance(
                RemoteDataSource.getInstance(),
                MealLocalDataSource.getInstance(requireContext())
        );
        presenter = new SearchPresenterImpl(this, repository);
        // Get FragmentCommunication from activity
        if (getActivity() instanceof FragmentCommunication) {
            communicator = (FragmentCommunication) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initializeViews(view);
        setupAdapters();
        setupSearch();
        setupButtons();
        return view;
    }

    private void initializeViews(View view) {
        searchEditText = view.findViewById(R.id.search_edit_text);
        categoryButton = view.findViewById(R.id.category_button);
        countryButton = view.findViewById(R.id.country_button);
        ingredientButton = view.findViewById(R.id.ingredient_button);
        filterOptionsRecyclerView = view.findViewById(R.id.filter_options_recycler_view);
        mealsRecyclerView = view.findViewById(R.id.meals_recycler_view);
        emptyStateText = view.findViewById(R.id.empty_state_text);
    }

    private void setupAdapters() {
        filterOptionsAdapter = new FilterOptionsAdapter(new ArrayList<>(), this::onFilterItemClicked);
        mealAdapter = new MealAdapter(new ArrayList<>(), communicator); // Pass communicator

        filterOptionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        filterOptionsRecyclerView.setAdapter(filterOptionsAdapter);

        mealsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mealsRecyclerView.setAdapter(mealAdapter);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (presenter.getCurrentFilter() == ISearchPresenter.FilterType.NONE) {
                    presenter.searchMealsByName(query);
                } else {
                    filterOptions(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupButtons() {
        categoryButton.setOnClickListener(v -> presenter.setCurrentFilter(ISearchPresenter.FilterType.CATEGORY));
        countryButton.setOnClickListener(v -> presenter.setCurrentFilter(ISearchPresenter.FilterType.COUNTRY));
        ingredientButton.setOnClickListener(v -> presenter.setCurrentFilter(ISearchPresenter.FilterType.INGREDIENT));
    }

    private void onFilterItemClicked(String filterItem) {
        switch (presenter.getCurrentFilter()) {
            case CATEGORY:
                presenter.searchMealsByCategory(filterItem);
                break;
            case COUNTRY:
                presenter.searchMealsByCountry(filterItem);
                break;
            case INGREDIENT:
                presenter.searchMealsByIngredient(filterItem);
                break;
        }
    }

    @Override
    public void showMeals(List<Meal> meals) {
        mealAdapter.updateMeals(meals != null ? meals : new ArrayList<>());
        mealsRecyclerView.setVisibility(View.VISIBLE);
        filterOptionsRecyclerView.setVisibility(View.GONE);
        emptyStateText.setVisibility(meals == null || meals.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showFilterOptions(List<?> options, ISearchPresenter.FilterType filterType) {
        filterOptionsAdapter.setFilterType(filterType);
        filterOptionsAdapter.updateOptions(options != null ? options : new ArrayList<>());
        filterOptionsRecyclerView.setVisibility(View.VISIBLE);
        mealsRecyclerView.setVisibility(View.GONE);
        emptyStateText.setVisibility(options == null || options.isEmpty() ? View.VISIBLE : View.GONE);
        if (options == null || options.isEmpty()) {
            emptyStateText.setText("No " + filterType.name().toLowerCase() + " options available");
        }
    }

    @Override
    public void filterOptions(String query) {
        filterOptionsAdapter.getFilter().filter(query);
    }

    @Override
    public void hideFilterOptions() {
        filterOptionsRecyclerView.setVisibility(View.GONE);
        mealsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String errorMessage) {
        emptyStateText.setText(errorMessage);
        emptyStateText.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmptyState() {
        emptyStateText.setText(R.string.no_results_found);
        emptyStateText.setVisibility(View.VISIBLE);
    }
}