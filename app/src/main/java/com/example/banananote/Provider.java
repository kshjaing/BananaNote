package com.example.banananote;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//Content Provider 공부 필요
public class Provider extends ContentProvider {
    private static final String AUTHORITY = "com.example.banananote";
    private static final String BASE_PATH = "Note";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY  + "/" + BASE_PATH);

    private static final int NOTE = 1;
    private static final int NOTE_ID = 1;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, NOTE);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTE_ID);
    }


    private SQLiteDatabase sqLiteDatabase;

    @Override
    public boolean onCreate() {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case NOTE:
                cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_NAME,
                        DatabaseHelper.ALL_COLUMNS,
                        s, null, null, null, DatabaseHelper.ID + " ASC");
                break;

            default:
                throw new IllegalArgumentException("알 수 없는 URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        switch (uriMatcher.match(uri)) {
            case NOTE:
                return "vnd.android.cursor.dir/Note";

            default:
                throw new IllegalArgumentException("알 수 없는 URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        switch (uriMatcher.match(uri)) {
            case NOTE:
                long id = sqLiteDatabase.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
                if(id > 0) {
                    Uri _uri = ContentUris.withAppendedId(CONTENT_URI, id);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
        }
        throw new SQLException("추가 실패 -> URI :" + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        int count = 0;
        switch (uriMatcher.match(uri)) {
            case NOTE:
                count = sqLiteDatabase.delete(DatabaseHelper.TABLE_NAME, s, strings);
                break;

            default:
                throw new IllegalArgumentException("알 수 없는 URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {


        int count = 0;
        switch (uriMatcher.match(uri)) {
            case NOTE:
                count = sqLiteDatabase.update(DatabaseHelper.TABLE_NAME,contentValues,s,strings);
                break;

            default:
                throw new IllegalArgumentException("알 수 없는 URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }
}
