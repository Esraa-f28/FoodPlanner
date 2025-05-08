package com.example.foodplanner;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class splashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT =7000; // Adjusted to match animation duration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LottieAnimationView firstAnim = findViewById(R.id.firstAnimation);
        LottieAnimationView secondAnim = findViewById(R.id.secondAnimation);

        // Start first animation
        firstAnim.playAnimation();

        // Listener to trigger second animation after first ends
        firstAnim.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                secondAnim.setVisibility(View.VISIBLE);
                secondAnim.playAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        // Navigate to next activity after timeout
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(splashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIME_OUT);
    }
}
