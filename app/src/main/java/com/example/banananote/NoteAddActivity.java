package com.example.banananote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoteAddActivity extends AppCompatActivity {

    Toolbar toolbar;
    ActionBar actionBar;

    EditText Title;
    EditText Memo;

    //current day
    long now;

    //date
    Date mdate;

    //DateFormat
    SimpleDateFormat simpleDateFormat;

    String getTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.button_hamburger_size);

        //toolbar.setNavigationIcon(R.drawable.button_hamburger_size);

        Title = findViewById(R.id.Add_Title);
        Memo = findViewById(R.id.Add_Memo);

        now = System.currentTimeMillis();
        mdate = new Date(now);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        getTime = simpleDateFormat.format(mdate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.note_add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.btnEdit:
                break;
            case R.id.test1:
                break;
            case android.R.id.home:
                //finish();
                INSERT_NOTE();
                Intent intent = new Intent(getApplicationContext(),NoteShowActivity.class);
                intent.putExtra("title",Title.getText().toString());
                intent.putExtra("createDate",getTime);
                intent.putExtra("contentsMemo",Memo.getText().toString());
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //INSERT NOTE
    public void INSERT_NOTE() {
        Log.e("NoteAddActivity.java"," INSERT_NOTE() 메서드 호출");

        String uriString = "content://com.example.banananote/Note";
        Uri uri = new Uri.Builder().build().parse(uriString);

        Cursor cursor = getContentResolver().query(uri, null, null, null,null);
        String[] columns = cursor.getColumnNames();
        Log.e("NoteAddActivity.java","columns count = " + columns.length);

        int i;
        for (i = 0; i< columns.length; i++) {
            Log.e("NoteAddActivity.java", "레코드 " + i + " : " + columns[i]);
        }

        ContentValues values = new ContentValues();

        values.put("title",Title.getText().toString());
        Log.e("NoteAddActivity.java", "메모_제목 : " + Title.getText().toString());

        values.put("createDate",getTime);
        Log.e("NoteAddActivity.java", "메모_생성 날짜 : " + getTime);

        values.put("contentsMemo",Memo.getText().toString());
        Log.e("NoteAddActivity.java", "메모_내용 : " + Memo.getText().toString());

        //즐겨찾기 기본 값 0  - > 즐겨찾기 체크 하면 1
        values.put("isFavorite",0);
        Log.e("NoteAddActivity.java","메모_즐겨찾기 : " + 0);

        //폴더에 속해있는지 판단 (기본값 0)
        //여러 폴더에 들어가면 (folder1, folder2) 이렇게 추가됨.
        values.put("whichFolder",0);
        Log.e("NoteAddActivity.java","속해진 폴더 : " + 0);

        uri = getContentResolver().insert(uri, values);
        Log.e("NoteAddActivity.java","INSERT 결과 : " + uri.toString());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.save = 0;
    }
}