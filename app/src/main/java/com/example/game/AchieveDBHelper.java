package com.example.game;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class AchieveDBHelper extends SQLiteOpenHelper {

    public static final String CREATE_ACHIEVEMENT = "create table achievement ("
            + "id integer primary key autoincrement, "
            + "complete integer, "
            + "name String, "
            + "des String)";

    private Context mContext;

    public AchieveDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACHIEVEMENT);
        initDB(db);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    private void initDB(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("complete", 0); values.put("name", "Quick Hand"); values.put("des", "Complete a single game mission in 10s");
        db.insert("achievement", null, values);
        values.clear();
        values.put("complete", 0); values.put("name", "First Blood"); values.put("des", "Win a multi game for the first time");
        db.insert("achievement", null, values);
        values.clear();
        values.put("complete", 0); values.put("name", "Game Master"); values.put("des", "Finish all mission with three star");
        db.insert("achievement", null, values);
        values.clear();
    }

}
