package com.artanst.checkczk.utility;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db_rates.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "rates";

    private static DatabaseHelper instance;

    private DatabaseHelper(Context context) {
        super(context, "db_rates.db", null, 1);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS rates (Date DATE PRIMARY KEY, AUD REAL, CAD REAL, EUR REAL, GBP REAL, NZD REAL, TRY REAL, USD REAL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS mytable;");
        onCreate(db);
    }

}
