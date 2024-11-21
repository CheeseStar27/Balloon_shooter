package com.example.shooting_game;

import android.graphics.Color;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;  // Add this import
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;


public class HighScoresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        ListView scoresList = findViewById(R.id.scoresList);
        Button newGameButton = findViewById(R.id.newGameButton);
        Button backToMenuButton = findViewById(R.id.backToMenuButton);

        // Load and display scores
        ArrayList<HashMap<String, String>> scoreData = loadScores();
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                scoreData,
                android.R.layout.simple_list_item_2,
                new String[]{"name", "score"},
                new int[]{android.R.id.text1, android.R.id.text2}

        ) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                // Set default transparent background
                view.setBackgroundColor(Color.argb(160, 255, 255, 255));

                // Customize colors for top records
                if (position == 0) {
                    view.setBackgroundColor(getResources().getColor(R.color.topScore)); // Highlight top 1
                } else if (position == 1) {
                    view.setBackgroundColor(getResources().getColor(R.color.secondTopScore)); // Highlight top 2
                } else if (position == 2) {
                    view.setBackgroundColor(getResources().getColor(R.color.thirdTopScore)); // Highlight top 3
                }
                return view;
            }

        };
        scoresList.setAdapter(adapter);


        // Set up new game button
        newGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("startingLevel", 1); // Start from level 1
            startActivity(intent);
            finish();
        });

        // Set up back to menu button
        backToMenuButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuActivity.class);
            // Clear activity stack and start fresh at menu
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private ArrayList<HashMap<String, String>> loadScores() {
        ArrayList<HashMap<String, String>> scoreData = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences("GameScores", MODE_PRIVATE);

        for (int i = 1; i <= 25; i++) {
            String name = prefs.getString("name_" + i, "");
            int score = prefs.getInt("score_" + i, 0);

            if (!name.isEmpty()) {
                HashMap<String, String> entry = new HashMap<>();
                entry.put("name", i + ". " + name);
                entry.put("score", "Score: " + score);
                scoreData.add(entry);
            }
        }

        return scoreData;
    }
}
