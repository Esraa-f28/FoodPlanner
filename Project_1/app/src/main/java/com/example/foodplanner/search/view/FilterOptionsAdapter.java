package com.example.foodplanner.search.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.model.pojo.Category;
import com.example.foodplanner.model.pojo.Country;
import com.example.foodplanner.model.pojo.Ingredients;
import com.example.foodplanner.search.presenter.ISearchPresenter;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterOptionsAdapter extends RecyclerView.Adapter<FilterOptionsAdapter.ViewHolder> implements Filterable {

    private List<Object> options; // Store Category, Country, Ingredients objects
    private List<Object> filteredOptions;
    private ISearchPresenter.FilterType filterType;
    private final OnFilterItemClickListener listener;

    // Country name to ISO country code mapping
    private static final Map<String, String> COUNTRY_TO_CODE = Map.ofEntries(
            Map.entry("American", "US"),   // United States
            Map.entry("British", "GB"),    // United Kingdom
            Map.entry("Canadian", "CA"),   // Canada
            Map.entry("Chinese", "CN"),    // China
            Map.entry("Croatian", "HR"),   // Croatia
            Map.entry("Dutch", "NL"),      // Netherlands
            Map.entry("Egyptian", "EG"),   // Egypt
            Map.entry("Filipino", "PH"),   // Philippines
            Map.entry("French", "FR"),     // France
            Map.entry("Greek", "GR"),      // Greece
            Map.entry("Indian", "IN"),     // India
            Map.entry("Irish", "IE"),      // Ireland
            Map.entry("Italian", "IT"),    // Italy
            Map.entry("Jamaican", "JM"),   // Jamaica
            Map.entry("Japanese", "JP"),   // Japan
            Map.entry("Kenyan", "KE"),     // Kenya
            Map.entry("Malaysian", "MY"),  // Malaysia
            Map.entry("Mexican", "MX"),    // Mexico
            Map.entry("Moroccan", "MA"),   // Morocco
            Map.entry("Polish", "PL"),     // Poland
            Map.entry("Portuguese", "PT"), // Portugal
            Map.entry("Russian", "RU"),    // Russian Federation
            Map.entry("Spanish", "ES"),    // Spain
            Map.entry("Thai", "TH"),       // Thailand
            Map.entry("Tunisian", "TN"),   // Tunisia
            Map.entry("Turkish", "TR"),    // Turkey
            Map.entry("Ukrainian", "UA"),  // Ukraine
            Map.entry("Uruguayan", "UY"),  // Uruguay
            Map.entry("Vietnamese", "VN")  // Vietnam
    );

    public interface OnFilterItemClickListener {
        void onFilterItemClicked(String filterItem);
    }

    public FilterOptionsAdapter(List<Object> options, OnFilterItemClickListener listener) {
        this.options = options != null ? options : new ArrayList<>();
        this.filteredOptions = new ArrayList<>(this.options);
        this.listener = listener;
    }

    public void setFilterType(ISearchPresenter.FilterType filterType) {
        this.filterType = filterType;
    }

    public void updateOptions(List<?> newOptions) {
        this.options = new ArrayList<>();
        if (newOptions != null) {
            this.options.addAll(newOptions);
        }
        this.filteredOptions = new ArrayList<>(this.options);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object option = filteredOptions.get(position);
        String name = "";
        String imageUrl = null;

        if (option instanceof Category) {
            Category category = (Category) option;
            name = category.getStrCategory();
            imageUrl = category.getStrCategoryThumb();
        } else if (option instanceof Country) {
            Country country = (Country) option;
            name = country.getStrArea();
            String countryCode = COUNTRY_TO_CODE.getOrDefault(country.getStrArea(), "US");
            imageUrl = "https://flagsapi.com/" + countryCode + "/shiny/64.png";
        } else if (option instanceof Ingredients) {
            Ingredients ingredient = (Ingredients) option;
            name = ingredient.getStrIngredient();
            imageUrl = ingredient.getStrIngredientThumb();
        }

        holder.bind(name, imageUrl, listener);
    }

    @Override
    public int getItemCount() {
        return filteredOptions.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Object> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(options);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Object item : options) {
                        String name = "";
                        if (item instanceof Category) {
                            name = ((Category) item).getStrCategory();
                        } else if (item instanceof Country) {
                            name = ((Country) item).getStrArea();
                        } else if (item instanceof Ingredients) {
                            name = ((Ingredients) item).getStrIngredient();
                        }
                        if (name != null && !name.isEmpty()) {
                            if (filterPattern.length() == 1) {
                                // Filter by first letter for single-character queries
                                if (name.toLowerCase().startsWith(filterPattern)) {
                                    filteredList.add(item);
                                }
                            } else {
                                // Filter by contains for longer queries
                                if (name.toLowerCase().contains(filterPattern)) {
                                    filteredList.add(item);
                                }
                            }
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredOptions = (List<Object>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView optionName;
        private final ImageView optionImage;
        private final MaterialCardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.filter_option_card);
            optionName = itemView.findViewById(R.id.option_name);
            optionImage = itemView.findViewById(R.id.option_image);
        }

        void bind(String option, String imageUrl, OnFilterItemClickListener listener) {
            optionName.setText(option != null ? option : "Unknown");
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(optionImage);
            cardView.setOnClickListener(v -> {
                if (option != null) {
                    listener.onFilterItemClicked(option);
                }
            });
        }
    }
}