package com.example.game;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private Button singleButton;
    private Button multiButton;
    private Button achieveButton;
    private ImageButton settingsButton;

    private DBHelper dbHelper;
    private AchieveDBHelper achieveDBHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        singleButton = findViewById(R.id.single);
        multiButton = findViewById(R.id.multi);
        achieveButton = findViewById(R.id.achieve);
        settingsButton = findViewById(R.id.settings);

        dbHelper = new DBHelper(this, "MissionRecord.db", null, 1);
        dbHelper.getWritableDatabase();

        achieveDBHelper = new AchieveDBHelper(this, "AchievementRecord.db", null, 1);
        achieveDBHelper.getWritableDatabase();

        // start single game
        singleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DifficultyActivity.class);
                startActivity(intent);
                dbHelper.getWritableDatabase();
            }
        });

        // start multi game
        multiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RoleActivity.class);
                startActivity(intent);
            }
        });

        // go to achievements
        achieveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AchievementActivity.class);
                startActivity(intent);
            }
        });

        // go to settings
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}