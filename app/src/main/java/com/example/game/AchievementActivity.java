package com.example.game;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class AchievementActivity extends AppCompatActivity {

    private List<Achievement> achievementList = new ArrayList<>();
    private AchieveDBHelper achieveDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        achieveDBHelper = new AchieveDBHelper(this, "AchievementRecord.db", null, 1);
        initAchievement();

        RecyclerView recyclerView = findViewById(R.id.recycler_view_a);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        AchievementAdapter adapter = new AchievementAdapter(achievementList);
        recyclerView.setAdapter(adapter);
    }

    private void initAchievement() {
        SQLiteDatabase db = achieveDBHelper.getWritableDatabase();
        String sql = "select id, complete, name, des from achievement;";
        Cursor cursor = db.rawQuery(sql, null);
        Achievement achievement;
        while (cursor.moveToNext()) {
            switch (cursor.getInt(0)) {
                case 1:
                    if (cursor.getInt(1) == 0) {
                        achievement = new Achievement(cursor.getInt(0), R.drawable.one_no, cursor.getString(2), cursor.getString(3));
                    } else {
                        achievement = new Achievement(cursor.getInt(0), R.drawable.one_yes, cursor.getString(2), cursor.getString(3));
                    }
                    achievementList.add(achievement);
                    break;
                case 2:
                    if (cursor.getInt(1) == 0) {
                        achievement = new Achievement(cursor.getInt(0), R.drawable.two_no, cursor.getString(2), cursor.getString(3));
                    } else {
                        achievement = new Achievement(cursor.getInt(0), R.drawable.two_yes, cursor.getString(2), cursor.getString(3));
                    }
                    achievementList.add(achievement);
                    break;
                case 3:
                    if (cursor.getInt(1) == 0) {
                        achievement = new Achievement(cursor.getInt(0), R.drawable.three_no, cursor.getString(2), cursor.getString(3));
                    } else {
                        achievement = new Achievement(cursor.getInt(0), R.drawable.three_yes, cursor.getString(2), cursor.getString(3));
                    }
                    achievementList.add(achievement);
                    break;
            }
        }
        cursor.close();
    }
}