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
        db.execSQL("CREATE TABLE Memo (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "TITLE TEXT, CONTENTS_MEMO TEXT, CREATE_DATE TEXT, is_FAVORITE INT," +
                "WHICH_FOLDER TEXT);");
        db.execSQL("CREATE TABLE Folder (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "FOLDER_NAME TEXT);");
        db.execSQL("CREATE TABLE Todo (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "CONTENTS_TODO TEXT, is_COMPLETE INT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Memo");
        onCreate(db);
    }
}
