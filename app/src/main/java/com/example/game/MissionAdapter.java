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

public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.ViewHolder> {

    private List<Mission> MissionList;
    private int diff;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View missionView;
        TextView missionName;
        ImageView missionImage;

        public ViewHolder(View view) {
            super(view);
            missionView = view;
            missionName = view.findViewById(R.id.mission_name);
            missionImage = view.findViewById(R.id.mission_image);
        }

    }

    public MissionAdapter(List<Mission> missionList, int difficulty) {
        MissionList = missionList;
        diff = difficulty;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mission_item, parent, false);
        final Context mContext = parent.getContext();
        final ViewHolder holder = new ViewHolder(view);
        holder.missionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Mission mission = MissionList.get(position);
                Intent intent = new Intent(mContext, SingleGame.class);
                intent.putExtra("difficulty", diff);
                intent.putExtra("mission", mission.getName());
                intent.putExtra("mode", 1);
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Mission mission = MissionList.get(position);
        holder.missionName.setText("Mission " + mission.getName());
        holder.missionImage.setImageResource(mission.getImageId());
    }

    @Override
    public int getItemCount() {
        return MissionList.size();
    }
}
