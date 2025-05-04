package com.example.foodplanner.homepage.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.FragmentCommunicator;
import com.example.foodplanner.R;
import com.example.foodplanner.model.pojo.Category;
import com.example.foodplanner.model.pojo.Country;
import com.example.foodplanner.model.pojo.Ingredients;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private List<Object> options;
    private final String type; // "category", "country", or "ingredient"
    private final FragmentCommunicator communicator;
    private final BiConsumer<String, String> onFilterSelected;
    private static final String TAG = "HomeAdapter";

    // Country name to ISO country code mapping
    private static final Map<String, String> COUNTRY_TO_CODE = Map.ofEntries(
            Map.entry("American", "US"),
            Map.entry("British", "GB"),
            Map.entry("Canadian", "CA"),
            Map.entry("Chinese", "CN"),
            Map.entry("Croatian", "HR"),
            Map.entry("Dutch", "NL"),
            Map.entry("Egyptian", "EG"),
            Map.entry("Filipino", "PH"),
            Map.entry("French", "FR"),
            Map.entry("Greek", "GR"),
            Map.entry("Indian", "IN"),
            Map.entry("Irish", "IE"),
            Map.entry("Italian", "IT"),
            Map.entry("Jamaican", "JM"),
            Map.entry("Japanese", "JP"),
            Map.entry("Kenyan", "KE"),
            Map.entry("Malaysian", "MY"),
            Map.entry("Mexican", "MX"),
            Map.entry("Moroccan", "MA"),
            Map.entry("Polish", "PL"),
            Map.entry("Portuguese", "PT"),
            Map.entry("Russian", "RU"),
            Map.entry("Spanish", "ES"),
            Map.entry("Thai", "TH"),
            Map.entry("Tunisian", "TN"),
            Map.entry("Turkish", "TR"),
            Map.entry("Ukrainian", "UA"),
            Map.entry("Uruguayan", "UY"),
            Map.entry("Vietnamese", "VN")
    );

    public HomeAdapter(List<Object> options, String type, FragmentCommunicator communicator, BiConsumer<String, String> onFilterSelected) {
        this.options = options != null ? options : new ArrayList<>();
        this.type = type;
        this.communicator = communicator;
        this.onFilterSelected = onFilterSelected;
    }

    public void updateOptions(List<?> newOptions) {
        this.options = new ArrayList<>();
        if (newOptions != null) {
            this.options.addAll(newOptions);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object option = options.get(position);
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

        holder.bind(name, imageUrl, type, communicator, onFilterSelected);
    }

    @Override
    public int getItemCount() {
        return options.size();
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

            if (cardView == null) {
                Log.e(TAG, "MaterialCardView (filter_option_card) is null");
            }
            if (optionName == null) {
                Log.e(TAG, "TextView (option_name) is null");
            }
            if (optionImage == null) {
                Log.e(TAG, "ImageView (option_image) is null");
            }
        }

        void bind(String option, String imageUrl, String type, FragmentCommunicator communicator, BiConsumer<String, String> onFilterSelected) {
            if (optionName != null) {
                optionName.setText(option != null ? option : "Unknown");
            } else {
                Log.e(TAG, "Cannot set text: optionName is null");
            }

            if (optionImage != null) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(optionImage);
            } else {
                Log.e(TAG, "Cannot load image: optionImage is null");
            }

            if (cardView != null && option != null) {
                cardView.setOnClickListener(v -> {
                    Log.d(TAG, "Filter clicked: type=" + type + ", value=" + option);
                    onFilterSelected.accept(type, option); // Show meals in HomeFragment
                });
            } else {
                Log.e(TAG, "Cannot set click listener: cardView=" + cardView + ", option=" + option);
            }
        }
    }
}