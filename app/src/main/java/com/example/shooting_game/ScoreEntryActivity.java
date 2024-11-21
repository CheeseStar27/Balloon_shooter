package com.example.shooting_game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;

public class ScoreEntryActivity extends AppCompatActivity {
    private EditText nameInput;
    private TextView congratsText;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_entry);

        score = getIntent().getIntExtra("score", 0);
        nameInput = findViewById(R.id.nameInput);
        TextView congratsText = findViewById(R.id.congratsText);
        Button submitButton = findViewById(R.id.submitButton);

        // Check if the user made it to the top 25
        if (isTop25(score)) {
            // Make the congratulatory text visible
            congratsText.setVisibility(View.VISIBLE);

            // Load and start the pop-out animation
            Animation popOut = AnimationUtils.loadAnimation(this, R.anim.pop_out);
            congratsText.startAnimation(popOut);
        } else {
            // Hide the congratulatory message if not in top 25
            congratsText.setVisibility(View.GONE);
        }

        submitButton.setOnClickListener(v -> {
            saveScore(nameInput.getText().toString(), score);
            startActivity(new Intent(this, HighScoresActivity.class));
            finish();
        });
    }


    private boolean isTop25(int score) {
        SharedPreferences prefs = getSharedPreferences("GameScores", MODE_PRIVATE);
        ArrayList<Integer> scores = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            int savedScore = prefs.getInt("score_" + i, 0);
            scores.add(savedScore);
        }
        Collections.sort(scores, (s1, s2) -> s2 - s1);
        return scores.size() < 25 || score >= scores.get(scores.size() - 1);
    }


    private void saveScore(String name, int score) {
        SharedPreferences prefs = getSharedPreferences("GameScores", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        ArrayList<ScoreEntry> scores = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            String savedName = prefs.getString("name_" + i, "");
            int savedScore = prefs.getInt("score_" + i, 0);
            if (!savedName.isEmpty()) {
                scores.add(new ScoreEntry(savedName, savedScore));
            }
        }
        scores.add(new ScoreEntry(name, score));
        Collections.sort(scores, (s1, s2) -> s2.score - s1.score);

        for (int i = 0; i < Math.min(25, scores.size()); i++) {
            editor.putString("name_" + (i + 1), scores.get(i).name);
            editor.putInt("score_" + (i + 1), scores.get(i).score);
        }

        editor.apply();
    }

    private static class ScoreEntry {
        String name;
        int score;

        ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }
}
