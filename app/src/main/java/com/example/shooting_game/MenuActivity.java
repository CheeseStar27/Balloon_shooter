package com.example.shooting_game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;

public class MenuActivity extends AppCompatActivity {
    private Spinner levelSpinner;
    private MediaPlayer bgmPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize views
        levelSpinner = findViewById(R.id.levelSpinner);
        Button startGameButton = findViewById(R.id.startGameButton);
        Button viewScoresButton = findViewById(R.id.viewScoresButton);
        Button guidelineButton = findViewById(R.id.guidelineButton);

        // Set up level spinner
        String[] levels = {"Level 1", "Level 2", "Level 3", "Level 4"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                levels
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinner.setAdapter(adapter);

        // Initialize high scores if first time
        initializeHighScores();

        // Set up button click listeners
        startGameButton.setOnClickListener(v -> startGame());
        viewScoresButton.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, HighScoresActivity.class));
        });
        guidelineButton.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, Guideline.class));
        });
        Button achievementsButton = findViewById(R.id.achievementsButton);
        achievementsButton.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, AchievementsActivity.class));
        });

        // Add the animation
        animateTitle();
    }

    private void initBGM() {
        bgmPlayer = MediaPlayer.create(this, R.raw.menu_bgm);
        bgmPlayer.setLooping(true);
        bgmPlayer.start();
    }

    private void animateTitle() {
        TextView titleText = findViewById(R.id.gameTitleText);

        // Create a combination of animations
        AnimationSet animationSet = new AnimationSet(true);

        // 1. Scale animation (bounce effect)
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 1.2f, // X-axis: start and end scale
                1.0f, 1.2f, // Y-axis: start and end scale
                Animation.RELATIVE_TO_SELF, 0.5f, // pivot X
                Animation.RELATIVE_TO_SELF, 0.5f  // pivot Y
        );
        scaleAnimation.setDuration(1000);
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        scaleAnimation.setRepeatMode(Animation.REVERSE);

        // 2. Floating animation (slight up and down movement)
        TranslateAnimation floatAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -0.1f
        );
        floatAnimation.setDuration(1500);
        floatAnimation.setRepeatCount(Animation.INFINITE);
        floatAnimation.setRepeatMode(Animation.REVERSE);

        // 3. Alpha animation (subtle fade effect)
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0.8f);
        alphaAnimation.setDuration(1500);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        // Add all animations to the set
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(floatAnimation);
        animationSet.addAnimation(alphaAnimation);

        // Apply interpolator for smoother animation
        animationSet.setInterpolator(new LinearInterpolator());

        // Start the animation
        titleText.startAnimation(animationSet);
    }

    private void startGame() {
        // Get selected level (add 1 because array index starts at 0)
        int selectedLevel = levelSpinner.getSelectedItemPosition() + 1;

        // Start game activity with selected level
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        intent.putExtra("startingLevel", selectedLevel);
        startActivity(intent);
    }

    private void initializeHighScores() {
        SharedPreferences prefs = getSharedPreferences("GameScores", MODE_PRIVATE);
        if (!prefs.contains("initialized")) {
            SharedPreferences.Editor editor = prefs.edit();

            // Add 25 default scores
            for (int i = 1; i <= 25; i++) {
                editor.putString("name_" + i, "Player " + i);
                editor.putInt("score_" + i, 26 - i); // Descending scores
            }

            editor.putBoolean("initialized", true);
            editor.apply();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause background music when the activity is paused
        if (bgmPlayer != null) {
            bgmPlayer.pause();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Resume background music when the activity is resumed
        if (bgmPlayer != null) {
            bgmPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release MediaPlayer resources when activity is destroyed
        if (bgmPlayer != null) {
            bgmPlayer.release();
        }
    }
}