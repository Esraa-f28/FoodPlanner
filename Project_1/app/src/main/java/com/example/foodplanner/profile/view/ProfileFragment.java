package com.example.foodplanner.profile.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.foodplanner.R;
import com.example.foodplanner.SignInActivity;
import com.example.foodplanner.profile.presenter.presenterImpl;

public class ProfileFragment extends Fragment implements IView {
    private static final String TAG = "ProfileFragment";
    private presenterImpl presenter;
    private TextView userInfoTextView;
    private Button logoutButton;
    private String userId;
    private boolean isGuest;
    private CardView mainContent; // Changed from LinearLayout to CardView
    private LinearLayout noConnectionContainer;
    private LottieAnimationView noInternetAnimation;
    private Button retryButton;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
            isGuest = getArguments().getBoolean("isGuest");
        }
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", requireActivity().MODE_PRIVATE);
        presenter = new presenterImpl(this, prefs, userId, isGuest);
        connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        setupNetworkCallback();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeViews(view);
        setupRetryButton();
        checkNetworkState();
        if (isNetworkAvailable()) {
            presenter.loadUserInfo();
        } else {
            updateNetworkUI(false);
        }
        logoutButton.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                presenter.logout();
            } else {
                updateNetworkUI(false);
            }
        });
        return view;
    }

    private void initializeViews(View view) {
        mainContent = view.findViewById(R.id.main_content); // Now a CardView
        noConnectionContainer = view.findViewById(R.id.no_connection_container);
        noInternetAnimation = view.findViewById(R.id.no_internet_animation);
        retryButton = view.findViewById(R.id.retry_button);
        userInfoTextView = view.findViewById(R.id.user_info);
        logoutButton = view.findViewById(R.id.logout_button);
    }

    private void setupNetworkCallback() {
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                requireActivity().runOnUiThread(() -> {
                    Log.d(TAG, "onAvailable: Network restored");
                    updateNetworkUI(true);
                    presenter.loadUserInfo();
                });
            }

            @Override
            public void onLost(Network network) {
                requireActivity().runOnUiThread(() -> {
                    Log.d(TAG, "onLost: Network lost");
                    updateNetworkUI(false);
                });
            }
        };
        Log.d(TAG, "Network callback initialized");
    }

    private void checkNetworkState() {
        boolean isNetworkAvailable = isNetworkAvailable();
        Log.d(TAG, "checkNetworkState: Network available=" + isNetworkAvailable);
        updateNetworkUI(isNetworkAvailable);
    }

    private void updateNetworkUI(boolean isNetworkAvailable) {
        if (isNetworkAvailable) {
            mainContent.setVisibility(View.VISIBLE);
            noConnectionContainer.setVisibility(View.GONE);
            noInternetAnimation.pauseAnimation();
        } else {
            mainContent.setVisibility(View.GONE);
            noConnectionContainer.setVisibility(View.VISIBLE);
            noInternetAnimation.playAnimation();
        }
    }

    private void setupRetryButton() {
        retryButton.setOnClickListener(v -> {
            Log.d(TAG, "Retry button clicked");
            checkNetworkState();
            if (isNetworkAvailable()) {
                presenter.loadUserInfo();
            }
        });
    }

    private boolean isNetworkAvailable() {
        try {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                Log.d(TAG, "isNetworkAvailable: No active network");
                return false;
            }
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            boolean isAvailable = capabilities != null &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            Log.d(TAG, "isNetworkAvailable: " + isAvailable);
            return isAvailable;
        } catch (Exception e) {
            Log.e(TAG, "Error checking network availability", e);
            return false;
        }
    }

    @Override
    public void showUserInfo(String email, boolean isGuest) {
        if (isGuest) {
            userInfoTextView.setText("Logged in as: Guest");
        } else {
            userInfoTextView.setText("Logged in as: " + email);
        }
    }

    @Override
    public void onLogoutSuccess() {
        Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity(), SignInActivity.class));
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void onLogoutError(String message) {
        Toast.makeText(getContext(), "Logout failed: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Registering network callback");
        try {
            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
            Log.d(TAG, "Network callback registered");
        } catch (Exception e) {
            Log.e(TAG, "Failed to register network callback", e);
        }
        checkNetworkState();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Unregistering network callback");
        try {
            if (networkCallback != null) {
                connectivityManager.unregisterNetworkCallback(networkCallback);
                Log.d(TAG, "Network callback unregistered");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to unregister network callback", e);
        }
        noInternetAnimation.pauseAnimation();
    }
}