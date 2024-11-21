package com.example.shooting_game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.media.MediaPlayer;

import java.util.List;
import java.util.ArrayList;
import com.example.shooting_game.Achievement;
import com.example.shooting_game.AchievementAdapter;

public class MainActivity extends AppCompatActivity {
    private GridLayout gameGrid;
    private TextView scoreText, timerText, levelText;
    private Button endGameButton;
    private int currentLevel = 1;
    private int currentScore = 0;
    private int highlightedViewIndex = -1;
    private CountDownTimer timer;
    private Random random = new Random();
    private boolean isGameActive = false;
    private static final int LEVEL_DURATION = 5000; // 5 seconds
    private static final float BUTTON_SIZE_RATIO = 0.8f;
    private int uniqueBalloonIndex = -1;
    private boolean isUniqueBalloonAppeared = false;
    private List<Achievement> achievements;
    private AchievementAdapter achievementAdapter;
    private MediaPlayer bgmPlayer;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bgmPlayer = MediaPlayer.create(this, R.raw.bgm);
        bgmPlayer.setLooping(true);
        bgmPlayer.start();


        gameGrid = findViewById(R.id.gameGrid);
        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);
        levelText = findViewById(R.id.levelText);
        endGameButton = findViewById(R.id.endGameButton);

        endGameButton.setOnClickListener(v -> endGame());

        int startingLevel = getIntent().getIntExtra("startingLevel", 1);

        startLevel(startingLevel);
        initializeAchievements();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop and release the BGM player
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer.release();
            bgmPlayer = null;
        }

        // Cancel the timer if it's running
        if (timer != null) {
            timer.cancel();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (bgmPlayer != null) {
            bgmPlayer.start();
        }
    }

    private void initializeAchievements() {
        achievements = new ArrayList<>();
        achievements.add(new Achievement("Sharpshooter", "Hit 5 targets in a row", 5));
        achievements.add(new Achievement("Precision Master", "Hit 10 targets in a row", 10));
        achievements.add(new Achievement("Perfect Aim", "Hit 15 targets in a row", 15));
        achievements.add(new Achievement("High Scorer", "Reach 50 points in a game", 50));
        achievements.add(new Achievement("Balloon Popper", "Pop 20 balloons in a single game", 20));
    }

    private void startLevel(int level) {
        currentLevel = level;
        levelText.setText("Level: " + level);

        // Reset red balloon appearance flags
        isUniqueBalloonAppeared = false;
        uniqueBalloonIndex = -1;

        // Clear previous views
        gameGrid.removeAllViews();

        // Set grid dimensions based on level
        int gridSize = getGridSize(level);
        gameGrid.setColumnCount((int) Math.sqrt(gridSize));
        gameGrid.setRowCount((int) Math.sqrt(gridSize));

        // Create views for the grid
        for (int i = 0; i < gridSize; i++) {
            ImageButton btn = createGameButton();
            gameGrid.addView(btn);
        }

        // Start the timer
        startTimer();

        // Show first yellow balloon and introduce red balloon
        highlightRandomView();

        isGameActive = true;
    }

    private void highlightRandomView() {
        // Reset previous yellow balloon
        if (highlightedViewIndex != -1) {
            // Only reset if it wasn't the red balloon position
            if (highlightedViewIndex != uniqueBalloonIndex) {
                gameGrid.getChildAt(highlightedViewIndex)
                        .setBackgroundResource(R.drawable.blue_balloon);
            }
        }

        // Select new position for yellow balloon
        do {
            highlightedViewIndex = random.nextInt(gameGrid.getChildCount());
        } while (highlightedViewIndex == uniqueBalloonIndex); // Avoid overlapping with red balloon

        // Show yellow balloon
        gameGrid.getChildAt(highlightedViewIndex)
                .setBackgroundResource(R.drawable.yellow_balloon);

        // Handle red balloon appearance - only once per level
        if (!isUniqueBalloonAppeared) {
            // Select position for red balloon
            do {
                uniqueBalloonIndex = random.nextInt(gameGrid.getChildCount());
            } while (uniqueBalloonIndex == highlightedViewIndex); // Ensure it doesn't overlap with yellow

            // Show red balloon
            gameGrid.getChildAt(uniqueBalloonIndex)
                    .setBackgroundResource(R.drawable.red_balloon);
            isUniqueBalloonAppeared = true;
        }
    }

    private ImageButton createGameButton() {
        ImageButton btn = new ImageButton(this);

        // Calculate the screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        // Calculate button size
        int buttonSize = (int) ((screenWidth - 100) / gameGrid.getColumnCount());

        // Create layout parameters
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = buttonSize;
        params.height = buttonSize;
        params.setMargins(4, 4, 4, 4);

        btn.setLayoutParams(params);
        btn.setPadding(0, 0, 0, 0);

        // Set scale type to fit the image properly
        btn.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // Set adjustment for the background
        btn.setAdjustViewBounds(true);

        // Set the background
        btn.setBackgroundResource(R.drawable.blue_balloon);

        // Set click listener for the button
        btn.setOnClickListener(v -> {
            if (!isGameActive) return;

            boolean yellowBalloonHit = false;

            // Handle red balloon click
            if (uniqueBalloonIndex != -1 && v.equals(gameGrid.getChildAt(uniqueBalloonIndex))) {
                currentScore += 2;
                scoreText.setText("Hits: " + currentScore);

                // Update red balloon achievements
                updateAchievementProgress("Balloon Popper", 1);
                updateAchievementProgress("High Scorer", currentScore);

                gameGrid.getChildAt(uniqueBalloonIndex)
                        .setBackgroundResource(R.drawable.blue_balloon);
                uniqueBalloonIndex = -1;
                highlightRandomView();

                consecutiveHits++; // Count consecutive hits
            }
            // Handle yellow balloon click
            else if (v.equals(gameGrid.getChildAt(highlightedViewIndex))) {
                currentScore++;
                scoreText.setText("Hits: " + currentScore);

                // Update yellow balloon achievements
                updateAchievementProgress("Sharpshooter", 1);
                updateAchievementProgress("Precision Master", 1);
                updateAchievementProgress("Perfect Aim", 1);
                updateAchievementProgress("High Scorer", currentScore);

                yellowBalloonHit = true;
                consecutiveHits++;
                highlightRandomView();
            }

            // Reset consecutive hits if no balloon hit
            if (!yellowBalloonHit) {
                consecutiveHits = 0;
            }

            trackConsecutiveHits();

            // Track total balloon pops
            trackBalloonPops();

            // Check for high score achievements
            checkHighScoreAchievements();
        });

        return btn;
    }

    private int consecutiveHits = 0;
    private int totalBalloonPops = 0;

    private void trackConsecutiveHits() {
        consecutiveHits++;

        // Check achievements for consecutive hits
        for (Achievement achievement : achievements) {
            if (achievement.getTitle().contains("Sharpshooter") ||
                    achievement.getTitle().contains("Precision Master") ||
                    achievement.getTitle().contains("Perfect Aim")) {
                achievement.updateProgress(1);
            }
        }
    }

    private void updateAchievementProgress(String achievementTitle, int progress) {
        for (Achievement achievement : achievements) {
            if (achievement.getTitle().equals(achievementTitle)) {
                achievement.updateProgress(progress);
                break;
            }
        }
    }

    private void checkCompletedAchievements() {
        boolean hasNewAchievement = false;
        for (Achievement achievement : achievements) {
            if (achievement.isUnlocked() && !achievement.isDisplayed()) {
                // TODO: Show achievement unlock notification
                achievement.setDisplayed(true);
                hasNewAchievement = true;
            }
        }

        if (hasNewAchievement) {
            showAchievementsDialog();
        }
    }

    private void showAchievementsDialog() {
        // Implement a dialog or toast to show unlocked achievements
        // Or navigate to AchievementsActivity
        Intent intent = new Intent(this, AchievementsActivity.class);
        startActivity(intent);
    }

    private void trackBalloonPops() {
        totalBalloonPops++;

        // Check Balloon Popper achievement
        for (Achievement achievement : achievements) {
            if (achievement.getTitle().equals("Balloon Popper")) {
                achievement.updateProgress(1);
            }
        }
    }

    private void checkHighScoreAchievements() {
        for (Achievement achievement : achievements) {
            if (achievement.getTitle().equals("High Scorer")) {
                achievement.updateProgress(currentScore);
            }
        }
    }


    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(LEVEL_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Time: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                if (currentLevel < 4) {
                    startLevel(currentLevel + 1);
                } else {
                    endGame();
                }
            }
        }.start();
    }

    private void showAchievements() {
        Intent intent = new Intent(this, AchievementsActivity.class);
        startActivity(intent);
    }



    private int getGridSize(int level) {
        switch (level) {
            case 1: return 4;
            case 2: return 9;
            case 3: return 16;
            case 4: return 25;
            default: return 4;
        }
    }

    private void endGame() {
        isGameActive = false;
        if (timer != null) {
            timer.cancel();
        }

        // Check if score is in top 25
        if (isTopScore(currentScore)) {
            // Show dialog to enter name
            Intent intent = new Intent(this, ScoreEntryActivity.class);
            intent.putExtra("score", currentScore);
            startActivity(intent);
        } else {
            // Show high scores directly
            startActivity(new Intent(this, HighScoresActivity.class));
        }

        finish();
    }

    private boolean isTopScore(int score) {
        SharedPreferences prefs = getSharedPreferences("GameScores", MODE_PRIVATE);
        // Get the 25th highest score
        int lowestTopScore = prefs.getInt("score_25", 0);
        return score > lowestTopScore;
    }

}