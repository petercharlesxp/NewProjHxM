package com.NewApp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.NewApp.data.HxMContract.HxMEntry;

/**
 * Created by Peter on 04/08/2015.
 */
public class HxMDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "hxm.db";


    public HxMDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_HXM_TABLE = "CREATE TABLE " + HxMEntry.TABLE_NAME + " (" +
                HxMEntry.COLUMN_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                HxMEntry.COLUMN_HeartRate + " TEXT NOT NULL, " +
                HxMEntry.COLUMN_InstantSpeed + " TEXT NOT NULL, " +
                HxMEntry.COLUMN_posted + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_HXM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + HxMEntry.TABLE_NAME);
        onCreate(db);
    }
}
