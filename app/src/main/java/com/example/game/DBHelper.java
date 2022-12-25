package com.example.game;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBHelper extends SQLiteOpenHelper {

    public int[][] car = {{1, 2, 0, 4, 1, 3, 3, 3, 3, 2, 3, 1, 2, 4, 4, 5, 0, 2},
            {1, 2, 1, 4, 3, 0, 3, 4, 2, 2, 4, 3, 2, 5, 0, 2, 5, 3, 5, 0, 0, 5, 1, 3},
            {1, 2, 1, 3, 0, 2, 2, 1, 4, 2, 3, 2, 2, 5, 3, 5, 0, 3, 5, 3, 1},
            {1, 2, 0, 4, 0, 3, 4, 4, 0, 3, 0, 1, 3, 1, 5, 3, 3, 4, 3, 3, 5, 5, 1, 3},
            {1, 2, 0, 4, 4, 0, 3, 1, 5, 3, 2, 2, 3, 4, 5, 2, 1, 3, 2, 3, 4, 5, 2, 3},
            {1, 2, 2, 4, 0, 0, 4, 5, 3, 3, 0, 3, 3, 1, 0, 3, 4, 2, 2, 3, 2, 2, 4, 0, 5, 1, 4},
            {1, 2, 0, 4, 3, 3, 4, 5, 0, 3, 0, 0, 3, 2, 2, 3, 3, 0, 3, 4, 4, 2, 0, 1, 5, 0, 5},
            {1, 2, 1, 4, 3, 1, 3, 1, 3, 3, 1, 4, 3, 4, 2, 2, 0, 4, 2, 1, 1, 2, 4, 3, 5, 1, 5},
            {1, 2, 1, 4, 0, 3, 4, 4, 2, 3, 0, 2, 3, 1, 3, 3, 2, 4, 2, 1, 4, 5, 2, 0, 5, 2, 5},
            {1, 2, 0, 4, 1, 3, 3, 0, 0, 3, 0, 2, 3, 2, 3, 3, 4, 1, 2, 4, 2, 2, 5, 4, 5, 2, 5},
            {1, 2, 3, 4, 0, 0, 4, 3, 0, 3, 1, 2, 3, 4, 2, 2, 5, 4, 5, 0, 5, 5, 3, 3},
            {1, 2, 0, 3, 1, 2, 3, 1, 4, 3, 4, 1, 2, 4, 2, 2, 5, 2, 5, 0, 3, 5, 2, 5},
            {1, 2, 2, 3, 3, 2, 2, 3, 0, 2, 4, 4, 2, 5 ,0, 5, 0, 0, 5, 1, 4, 5, 3, 3},
            {1, 2, 2, 4, 3, 2, 3, 0, 2, 2, 0, 0, 2, 5, 0, 5, 0, 4, 5, 1, 0, 5, 1, 1},
            {1, 2, 0, 4, 4, 1, 3, 0, 0, 3, 1, 2, 2, 0, 1, 2, 0, 3, 2, 1, 4, 2, 3, 1, 2, 5, 0, 5, 1, 3, 5, 2, 4}};

    public static final String CREATE_MISSION = "create table mission ("
            + "id integer primary key autoincrement, "
            + "difficulty integer, "
            + "mission integer, "
            + "car text, "
            + "time integer, "
            + "step integer, "
            + "star integer)";

    private Context mContext;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MISSION);
        initDB(db);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    private void initDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        for (int d = 1; d < 4; d++) {
            for (int m = 1; m < 6; m++) {
                String carString = "";
                int index = (d - 1) * 5 + m - 1;
                for (int i = 0; i < car[index].length; i++) {
                    carString += car[index][i] + "";
                }
                values.put("difficulty", d); values.put("mission", m); values.put("car", carString); values.put("time", 999); values.put("step", 999); values.put("star", 0);
                db.insert("mission", null, values);
                values.clear();
            }
        }
    }

}
