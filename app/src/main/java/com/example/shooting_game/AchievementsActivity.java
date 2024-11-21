package com.example.shooting_game;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity {

    private List<Achievement> achievements;
    private AchievementAdapter achievementAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        // Initialize achievements
        initializeAchievements();

        // Set up RecyclerView
        RecyclerView achievementsRecycler = findViewById(R.id.achievementsRecycler);
        achievementsRecycler.setLayoutManager(new LinearLayoutManager(this));
        achievementAdapter = new AchievementAdapter(achievements);
        achievementsRecycler.setAdapter(achievementAdapter);

        // Set up back button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Example: Update progress for a specific achievement (e.g., index 0)
        updateAchievementProgress(0, 5); // Update progress for the first achievement
    }

    private void initializeAchievements() {
        achievements = new ArrayList<>();
        achievements.add(new Achievement("Sharpshooter", "Hit 5 targets in a row", 5));
        achievements.add(new Achievement("Precision Master", "Hit 10 targets in a row", 10));
        achievements.add(new Achievement("Perfect Aim", "Hit 15 targets in a row", 15));
        achievements.add(new Achievement("High Scorer", "Reach 50 points in a game", 50));
        achievements.add(new Achievement("Balloon Popper", "Pop 20 balloons in a single game", 20));
    }

    // This method will update the achievement progress and notify the adapter
    private void updateAchievementProgress(int position, int progressValue) {
        achievementAdapter.updateAchievementProgress(position, progressValue);
    }
}
