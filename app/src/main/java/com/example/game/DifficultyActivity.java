package com.example.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class DifficultyActivity extends AppCompatActivity {

    private Button easyButton;
    private Button middleButton;
    private Button hardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        easyButton = findViewById(R.id.easy);
        middleButton = findViewById(R.id.middle);
        hardButton = findViewById(R.id.hard);

        // easy mode
        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DifficultyActivity.this, MissionActivity.class);
                intent.putExtra("difficulty", 1);
                startActivity(intent);
            }
        });

        // middle mode
        middleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DifficultyActivity.this, MissionActivity.class);
                intent.putExtra("difficulty", 2);
                startActivity(intent);
            }
        });

        // hard mode
        hardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DifficultyActivity.this, MissionActivity.class);
                intent.putExtra("difficulty", 3);
                startActivity(intent);
            }
        });
    }
}
