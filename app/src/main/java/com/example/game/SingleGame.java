package com.example.game;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SingleGame extends AppCompatActivity implements GameCallBack{

    private BoardView boardView;
    private Button undoButton;
    private Button restartButton;
    private PopupWindow popupWindowNext;
    private View rootViewNext;
    private PopupWindow popupWindowWin;
    private View rootViewWin;
    private TextView textView1;
    private TextView textView2;
    private ImageView imageView;
    private Button button;

    private DBHelper dbHelper;
    private AchieveDBHelper achieveDBHelper;

    private int difficulty;
    private int mission;
    private int mode;
//    private int win = 0;

    private int seconds = 0;
    private boolean running = true;
    private boolean wasRunning = false;

    private int steps;
    private int stars;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_game);
        boardView = findViewById(R.id.boardView);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        undoButton = findViewById(R.id.undo);
        restartButton = findViewById(R.id.restart);

        dbHelper = new DBHelper(this, "MissionRecord.db", null, 1);
        achieveDBHelper = new AchieveDBHelper(this, "AchievementRecord.db", null, 1);

        // set difficulty
        Intent intent = getIntent();
        difficulty = intent.getIntExtra("difficulty", 0);
        mission = intent.getIntExtra("mission", 0);
        mode = intent.getIntExtra("mode", 0);

        initGame();

        if(savedInstanceState!=null){
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }
        runTime();

        rootViewNext = this.getLayoutInflater().inflate(R.layout.layout_popupwindownext,null);
        popupWindowNext = new PopupWindow(rootViewNext, 255, 275);
//        rootViewWin = this.getLayoutInflater().inflate(R.layout.layout_popupwindowwin,null);
//        popupWindowWin = new PopupWindow(rootViewWin, 255, 275);

        if (mode == 1) {
            // undo the step
            undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boardView.undo();
                }
            });

            // restart the game
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boardView.restart();
                    seconds = 0;
                    running = true;
                }
            });
        } else if (mode == 2) {
            undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SingleGame.this, "This button can't be clicked in this mode", Toast.LENGTH_SHORT).show();
                }
            });

            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SingleGame.this, "This button can't be clicked in this mode", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putInt("seconds",seconds);
        saveInstanceState.putBoolean("running",running);
        saveInstanceState.putBoolean("wasRunning",wasRunning);
    }

    @Override
    protected void onStop() {
        super.onStop();
        wasRunning = running;
        running = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (wasRunning) running = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        wasRunning = running;
        running = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wasRunning) running = true;
    }

    private void initGame() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "select car from mission where difficulty = " + difficulty + " and mission = " + mission + ";";
        Cursor cursor = db.rawQuery(sql, null);
        String cars = null;
        while (cursor.moveToNext()) {
            cars = cursor.getString(0);

        }
        cursor.close();
        int carNum = cars.length() / 3;
        int[][] car = new int[carNum][3];
        for (int i = 0; i < carNum; i++) {
            for (int j = 0; j < 3; j++) {
                car[i][j] = Integer.parseInt(cars.substring(i*3+j, i*3+j+1));
            }
        }
        boardView.setDifficulty(car);

        boardView.setCallBack(this);
    }

    @Override
    public void GameOver() {
        wasRunning = running;
        running = false;
        if (seconds < 30 && steps < 30) {
            stars = 3;
        } else if (seconds > 30 && steps > 30) {
            stars = 1;
        } else {
            stars = 2;
        }
        if (mode == 1) {
            if (seconds <= 10) {
                SQLiteDatabase db = achieveDBHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("complete", 1);
                db.update("achievement", values, "id = 1", null);
            }
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String sql = "select time, step, star from mission where difficulty = " + difficulty + " and mission = " + mission + ";";
            Cursor cursor = db.rawQuery(sql, null);
            int time = 999;
            int step = 999;
            int star = 0;
            while (cursor.moveToNext()) {
                time = cursor.getInt(0);
                step = cursor.getInt(1);
                star = cursor.getInt(2);

            }
            cursor.close();
            if (seconds < time) {
                time = seconds;
            }
            if (steps < step) {
                step = steps;
            }
            if (stars > star) {
                star = stars;
            }
            ContentValues values = new ContentValues();
            values.put("time", time);
            values.put("step", step);
            values.put("star", star);
            int id = (difficulty - 1) * 5 + mission;
            db.update("mission", values, "id = " + id, null);

            imageView = popupWindowNext.getContentView().findViewById(R.id.star);
            if (stars == 1) {
                imageView.setImageResource(R.drawable.one_star);
            } else if (stars == 2) {
                imageView.setImageResource(R.drawable.two_star);
            } else {
                imageView.setImageResource(R.drawable.three_star);
            }
            textView1 = popupWindowNext.getContentView().findViewById(R.id.text);
            textView1.setText(String.format("Time: %03d Step: %03d", seconds, steps));
            button = popupWindowNext.getContentView().findViewById(R.id.next);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindowNext.dismiss();
                    if (mission < 5 && difficulty < 5) {
                        mission += 1;
                    } else if (mission == 5 && difficulty < 5) {
                        difficulty += 1;
                        mission = 1;
                    }
                    initGame();
                    seconds = 0;
                    running = true;
                    final TextView textView = findViewById(R.id.step);
                    textView.setText("Step: 000");
                }
            });
            popupWindowNext.showAtLocation(findViewById(R.id.title), Gravity.CENTER, 0, 0);
            popupWindowNext.showAsDropDown(rootViewNext);
            popupWindowNext.setOutsideTouchable(false);
        } else if (mode == 2) {
            Intent intent = new Intent();
//            imageView = popupWindowWin.getContentView().findViewById(R.id.star);
            if (stars == 1) {
//                imageView.setImageResource(R.drawable.one_star);
                intent.putExtra("star", 1);
            } else if (stars == 2) {
//                imageView.setImageResource(R.drawable.two_star);
                intent.putExtra("star", 2);
            } else {
//                imageView.setImageResource(R.drawable.three_star);
                intent.putExtra("star", 3);
            }
            intent.putExtra("time", seconds);
            intent.putExtra("step", steps);
            setResult(RESULT_OK, intent);
            finish();
//            if (win == 0) {
//                textView1 = popupWindowWin.getContentView().findViewById(R.id.text1);
//                textView1.setText("You Win!");
//                textView2 = popupWindowWin.getContentView().findViewById(R.id.text2);
//                textView2.setText("Another player hasn't finished!");
//            } else if (win == 1) {
//                textView1 = popupWindowWin.getContentView().findViewById(R.id.text1);
//                textView1.setText("You Lose...");
//                textView2 = popupWindowWin.getContentView().findViewById(R.id.text2);
//                textView2.setText("Another player finished before you...");
//            }
//            popupWindowWin.showAtLocation(findViewById(R.id.title), Gravity.CENTER, 0, 0);
//            popupWindowWin.showAsDropDown(rootViewNext);
//            popupWindowWin.setOutsideTouchable(false);
        }
    }

    @Override
    public void UpdateStep(int step) {
        steps = step;
        final TextView textView = findViewById(R.id.step);
        String steps = String.format("Step: %03d", step);
        textView.setText(steps);
    }

    private void runTime() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                final TextView textView = findViewById(R.id.time);
                String time = String.format("Time: %03d", seconds);
                textView.setText(time);
                if (running) seconds++;
                handler.postDelayed(this, 1000);
            }
        });
    }

}
