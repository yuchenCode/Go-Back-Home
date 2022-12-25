package com.example.game;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private Button playMusicButton;
    private Button stopMusicButton;
    private Button chineseButton;
    private Button englishButton;
    private Button tipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        playMusicButton = findViewById(R.id.playmusic);
        stopMusicButton = findViewById(R.id.stopmusic);
        chineseButton = findViewById(R.id.chinese);
        englishButton = findViewById(R.id.english);
        tipButton = findViewById(R.id.tip);

        // play music
        playMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MusicServer.class);
                startService(intent);
            }
        });

        // stop music
        stopMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MusicServer.class);
                stopService(intent);
            }
        });

        // set Chinese
        chineseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources resources = getResources();
                Configuration config = resources.getConfiguration();
                DisplayMetrics dm = resources.getDisplayMetrics();
                config.locale = Locale.SIMPLIFIED_CHINESE;
                resources.updateConfiguration(config, dm);
                finish();
                Intent it = new Intent(SettingsActivity.this, MainActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(it);
            }
        });

        // set English
        englishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources resources = getResources();
                Configuration config = resources.getConfiguration();
                DisplayMetrics dm = resources.getDisplayMetrics();
                config.locale = Locale.ENGLISH;
                resources.updateConfiguration(config, dm);
                finish();
                Intent it = new Intent(SettingsActivity.this, MainActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(it);
            }
        });

        // tips
        tipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, TipsActivity.class);
                startActivity(intent);
            }
        });
    }
}
