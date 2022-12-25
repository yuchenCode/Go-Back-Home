package com.example.game;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private List<Achievement> AchievementList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View achievementView;
        TextView achievementName;
        ImageView achievementImage;

        public ViewHolder(View view) {
            super(view);
            achievementView = view;
            achievementName = view.findViewById(R.id.mission_name);
            achievementImage = view.findViewById(R.id.mission_image);
        }

    }

    public AchievementAdapter(List<Achievement> achievementList) {
        AchievementList = achievementList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mission_item, parent, false);
        final Context mContext = parent.getContext();
        final ViewHolder holder = new ViewHolder(view);
        holder.achievementView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Achievement achievement = AchievementList.get(position);
                Intent intent = new Intent(mContext, AchievementDisplay.class);
                intent.putExtra("name", achievement.getName());
                intent.putExtra("description", achievement.getDes());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Achievement achievement = AchievementList.get(position);
        holder.achievementName.setText(achievement.getName());
        holder.achievementImage.setImageResource(achievement.getImageId());
    }

    @Override
    public int getItemCount() {
        return AchievementList.size();
    }
}