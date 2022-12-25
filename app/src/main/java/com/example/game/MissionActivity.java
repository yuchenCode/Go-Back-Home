package com.example.game;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MissionActivity extends AppCompatActivity {

    private List<Mission> missionList = new ArrayList<>();
    private int difficulty;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        dbHelper = new DBHelper(this, "MissionRecord.db", null, 1);

        Intent intent = getIntent();
        difficulty = intent.getIntExtra("difficulty", 0);
        initMission(difficulty);


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MissionAdapter adapter = new MissionAdapter(missionList, difficulty);
        recyclerView.setAdapter(adapter);
    }

    private void initMission(int difficulty) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "select mission, star from mission where difficulty = " + difficulty + ";";
        Cursor cursor = db.rawQuery(sql, null);
        Mission mission;
        while (cursor.moveToNext()) {
            switch (cursor.getInt(1)) {
                case 0:
                    mission = new Mission(cursor.getInt(0), R.drawable.zero_star);
                    missionList.add(mission);
                    break;
                case 1:
                    mission = new Mission(cursor.getInt(0), R.drawable.one_star);
                    missionList.add(mission);
                    break;
                case 2:
                    mission = new Mission(cursor.getInt(0), R.drawable.two_star);
                    missionList.add(mission);
                    break;
                case 3:
                    mission = new Mission(cursor.getInt(0), R.drawable.three_star);
                    missionList.add(mission);
                    break;
            }
        }
        cursor.close();
    }
}