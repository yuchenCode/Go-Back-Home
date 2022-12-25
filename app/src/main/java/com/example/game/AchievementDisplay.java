package com.example.game;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AchievementDisplay extends AppCompatActivity {

    String name;
    String description;
    String time = "2021-12-18";

    TextView achievement_name;
    TextView achievement_description;
    TextView achievement_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_display);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        achievement_name = findViewById(R.id.achievement_name);
        achievement_description = findViewById(R.id.achievement_des);
        achievement_time = findViewById(R.id.achievement_time);

        // set achievement details
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        description = intent.getStringExtra("description");

        achievement_name.setText(name);
        achievement_description.setText(description);
        if (!name.equals("Game Master")) {
            achievement_time.setText("Completed in " + time);
        }
    }
}