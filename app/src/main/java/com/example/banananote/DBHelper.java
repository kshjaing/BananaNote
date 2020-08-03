package com.example.banananote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "BananaNote.db";

    public DBHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Memo (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, contentsMemo TEXT, createDate TEXT, isFavorite INT," +
                "whichFolder TEXT);");
        db.execSQL("CREATE TABLE Folder (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "folderName TEXT);");
        db.execSQL("CREATE TABLE Todo (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "contentsTodo TEXT, isComplete INT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Memo");
        onCreate(db);
    }
}
