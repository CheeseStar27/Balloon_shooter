package com.example.shooting_game;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private List<Achievement> achievements;

    public AchievementAdapter(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_achievement_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Achievement achievement = achievements.get(position);
        holder.titleTextView.setText(achievement.getTitle());
        holder.descriptionTextView.setText(achievement.getDescription());

        // Add progress tracking
        if (achievement.isUnlocked()) {
            // Achievement completed, show the complete symbol
            holder.progressView.setVisibility(View.GONE);
            holder.completeSymbol.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(1.0f); // Full opacity for completed achievements
        } else {
            // Show the progress bar for in-progress achievements
            holder.progressView.setVisibility(View.VISIBLE);
            holder.completeSymbol.setVisibility(View.GONE);
            holder.progressView.setProgress((int)((achievement.getCurrentProgress() / (float)achievement.getTargetValue()) * 100));
            holder.itemView.setAlpha(0.5f); // Dim in-progress achievements
        }
    }

    // Update ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        ProgressBar progressView;
        ImageView completeSymbol;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.achievementTitle);
            descriptionTextView = itemView.findViewById(R.id.achievementDescriptionText);
            progressView = itemView.findViewById(R.id.achievementProgressBar);
            completeSymbol = itemView.findViewById(R.id.achievementCompleteSymbol);
        }
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    // This method will be used to update the RecyclerView when progress changes
    public void updateAchievementProgress(int position, int progressValue) {
        Achievement achievement = achievements.get(position);
        achievement.updateProgress(progressValue);
        notifyItemChanged(position); // Refresh the achievement item at the given position
    }
}


