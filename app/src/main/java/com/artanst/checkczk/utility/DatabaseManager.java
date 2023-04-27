package com.artanst.checkczk.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static DatabaseManager instance;
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    private DatabaseManager(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
        database = dbHelper.getWritableDatabase();
        dbHelper.onCreate(database);
    }

    public static synchronized DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context.getApplicationContext());
        }
        return instance;
    }

    public void addData(List<String> dates) {

        Cursor cursor = database.query("rates", null, "Date=?", new String[] {dates.get(0)}, null, null, null);
        if (cursor.getCount() == 0) {

            ContentValues values = new ContentValues();
            values.put("Date", dates.get(0));
            values.put("AUD", dates.get(1));
            values.put("CAD", dates.get(2));
            values.put("EUR", dates.get(3));
            values.put("GBP", dates.get(4));
            values.put("NZD", dates.get(5));
            values.put("TRY", dates.get(6));
            values.put("USD", dates.get(7));
            database.insert("rates", null, values);
        }
    }

   /* public void deleteData(int id) {
        //String selection = DatabaseHelper.COLUMN_ID + " = ?";
        //String[] selectionArgs = {String.valueOf(id)};
        //database.delete(DatabaseHelper.TABLE_NAME, selection, selectionArgs);
    }

    public void updateData(int id, String newData) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DATA, newData);
        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        database.update(DatabaseHelper.TABLE_NAME, values, selection, selectionArgs);
    }*/

    public void getAllData() {
        List<String> dataList = new ArrayList<>();
        String[] KEYS_CURRENCIES = {"Date", "AUD", "CAD", "EUR", "GBP", "NZD", "TRY", "USD"};
        Cursor cursor = database.query("rates", KEYS_CURRENCIES, null, null, null, null, null);

        int indexDate = cursor.getColumnIndexOrThrow("Date");
        int indexAUD = cursor.getColumnIndexOrThrow("AUD");
        int indexCAD = cursor.getColumnIndexOrThrow("CAD");
        int indexEUR = cursor.getColumnIndexOrThrow("EUR");
        int indexGBP = cursor.getColumnIndexOrThrow("GBP");
        int indexNZD = cursor.getColumnIndexOrThrow("NZD");
        int indexLIRA = cursor.getColumnIndexOrThrow("TRY");
        int indexUSD = cursor.getColumnIndexOrThrow("USD");

        while (cursor.moveToNext()) {
            String date = cursor.getString(indexDate);
            float AUD = cursor.getFloat(indexAUD);
            float CAD = cursor.getFloat(indexCAD);
            float EUR = cursor.getFloat(indexEUR);
            float GBP = cursor.getFloat(indexGBP);
            float NZD = cursor.getFloat(indexNZD);
            float TRY = cursor.getFloat(indexLIRA);
            float USD = cursor.getFloat(indexUSD);

            System.out.println("Date: " + date + ", AUD: " + AUD + ", CAD: " + CAD + ", EUR: " + EUR + ", GBP: " + GBP + ", NZD: " + NZD + ", TRY: " + TRY + "USD: " + USD + "\n");
        }
        cursor.close();
    }

    public String[] getAverage(String leftDate, String rightDate, String currency) {
        String[] minMaxAvg = new String[3];
        String queryMinMaxAvg = new String();
        queryMinMaxAvg = "SELECT AVG(" + currency + ") FROM rates WHERE date >= '" + leftDate + "' AND date <= '" + rightDate + "'";
        Cursor cursor = database.rawQuery(queryMinMaxAvg, null);
        if (cursor.moveToFirst()) {
            minMaxAvg[0] = cursor.getString(0);
        }
        cursor.close();

        queryMinMaxAvg = "SELECT MIN(" + currency + ") FROM rates WHERE date >= '" + leftDate + "' AND date <= '" + rightDate + "'";
        cursor = database.rawQuery(queryMinMaxAvg, null);
        if (cursor.moveToFirst()) {
            minMaxAvg[1] = cursor.getString(0);
        }
        cursor.close();

        queryMinMaxAvg = "SELECT MAX(" + currency + ") FROM rates WHERE date >= '" + leftDate + "' AND date <= '" + rightDate + "'";
        cursor = database.rawQuery(queryMinMaxAvg, null);
        if (cursor.moveToFirst()) {
            minMaxAvg[2] = cursor.getString(0);
        }
        cursor.close();

        return minMaxAvg;
    }
}
