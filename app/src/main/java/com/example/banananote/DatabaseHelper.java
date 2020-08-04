package com.example.banananote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    //DB 생성 후 테이블을 만듦.

    private static final String DATABASE_NAME = "Banananote.db"; //DB명
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_NAME = "Note"; //Note 테이블

    public static final String ID = "id"; //id (기본키, AUTO 증가)
    public static final String TITLE = "title"; //제목
    public static final String CREATE_DATE = "createDate"; //생성 날짜
    public static final String CONTENTS_MEMO = "contentsMemo"; //메모 내용 (이미지, 동영상 관련은 나중에)
    public static final String isFAVORITE = "isFavorite"; //즐겨찾기
    public static final String WHICH_FOLDER = "whichFolder";

    public static final String[] ALL_COLUMNS = {ID, TITLE, CREATE_DATE,CONTENTS_MEMO,isFAVORITE,WHICH_FOLDER};

    private static final String CREATE_TABLE_NOTE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TITLE + " TEXT, " +
                    CREATE_DATE + " TEXT, " +
                    CONTENTS_MEMO + " TEXT, " +
                    isFAVORITE + " INTEGER, " +
                    WHICH_FOLDER + " TEXT) ";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //onCreate(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }
}
