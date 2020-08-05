package com.example.banananote;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Fragment_Main extends Fragment {

    RecyclerView recyclerView;
    NoteAdapter adapter; //frag_main_item.xml 모양을 어떤 규칙으로 뿌릴지 설정
    Button btnPlus; //메모 추가
    Button scrollTop; //스크롤 맨 위로

    ///
    public static Fragment_Main context_Frag_Main;
    public String Title;
    public String CreateDate;
    public String Memo;

    /// DB에서 받은 값 배열로 저장
    public static String[] Arr_ID = {};
    public static String[] Arr_TITLE = {};
    public static String[] Arr_CREATE_DATE = {};
    public static String[] Arr_CONTENTS_MEMO = {};
    public static String[] Arr_isFAVORITE = {};
    public static String[] Arr_WHICH_FOLDER = {};

    //스크롤 모션 이벤트 관련
    //모션 상황에 따라 true false
    boolean scrollStop;

    @Nullable
    @Override
    @SuppressLint("ClickableViewAccessibility")
    //MainActivity.java 에서 OnCreate 비슷함
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        context_Frag_Main = Fragment_Main.this;

        //프래그먼트 매인의 xml 객체
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        //DB 에서 SELECT 문
        QUERY_NOTE();

        //getList() 메서드에서 Note.java 에 전달
        List<Note> list = getList();

        //2열로 뿌리기
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        //MainActivity.tag = "multi";

        adapter = new NoteAdapter(this,list);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnNoteItemClickListener() {
            //메모 클릭하면 OnNoteItemClickListener 에서 받아옴
            @Override
            public void onItemClick(NoteAdapter.ViewHolder holder, View view, int position) {
                Note item = adapter.getItem(position); //현제 위치 값

                //NoteShowActivity.java 에서 이 값을 뿌려야함.
                Title = item.getTitle();
                CreateDate = item.getCreateDate();
                Memo = item.getMemo();

                //Toast.makeText(getContext(), "아이템 선택됨: " + item.getTitle(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(),NoteShowActivity.class);
                startActivity(intent);
            }
        });

        //메모추가 버튼 (스크롤 내리면 사라짐, 올릴때 보여짐)
        btnPlus = ((MainActivity)MainActivity.context_main).btnPlus;
        scrollTop = ((MainActivity)MainActivity.context_main).scrollTop;
        ScrollView scrollView = v.findViewById(R.id.frag_main_scroll);

        //스크롤 터치로 btnPlus 버튼 visible 변경
        //Up 즉 손 떼면 생겨남
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_SCROLL:
                        Log.e("MotionEvent","SCROLL");
                    case MotionEvent.ACTION_MOVE:
                        Log.e("MotionEvent","MOVE");
                        scrollStop = false;
                        break;

                    case MotionEvent.ACTION_DOWN:
                        Log.e("MotionEvent","DOWN");
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        Log.e("MotionEvent","CANCEL");
                    case MotionEvent.ACTION_UP:
                        Log.e("MotionEvent","UP");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scrollStop = true;

                                btnPlus.setVisibility(View.VISIBLE);
                            }
                        }, 700);

                        break;
                }
                return false;
            }
        });

        scrollView.setOnScrollChangeListener(new ScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                //위 터치이벤트에 따라 변경
                if(scrollStop) {
                    btnPlus.setVisibility(View.VISIBLE);
                } else {
                    btnPlus.setVisibility(View.INVISIBLE);
                    scrollTop.setVisibility(View.VISIBLE);

                }

                if (scrollY == 0) {
                    //Top
                    scrollTop.setVisibility(View.INVISIBLE);
                }
            }
        });

        scrollTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //nestedScrollView.smoothScrollTo(0,0);
                scrollTop.setVisibility(View.GONE);
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });


        return v;
    }

    public void QUERY_NOTE() {
        //Content provider ,Uri 를 이용해 SELECT
        int i = 0;

        try {
            String uriString = "content://com.example.banananote/Note";
            Uri uri = new Uri.Builder().build().parse(uriString);

            String[] columns = new String[] {"id","title","createDate","contentsMemo","isFavorite","whichFolder"};
            Cursor cursor = getContext().getContentResolver().query(uri, columns, null, null, "id ASC");
            Log.e("Fragment_Main.java","QUERY 결과 : " + cursor.getCount());
            int columns_count = cursor.getCount();

            int index = 1;
            Arr_ID = new String[columns_count];
            Arr_TITLE = new String[columns_count];
            Arr_CREATE_DATE = new String[columns_count];
            Arr_CONTENTS_MEMO = new String[columns_count];
            Arr_isFAVORITE = new String[columns_count];
            Arr_WHICH_FOLDER = new String[columns_count];

            while (cursor.moveToNext()) { //출력
                String id = cursor.getString(cursor.getColumnIndex(columns[0]));
                String title = cursor.getString(cursor.getColumnIndex(columns[1]));
                String createDate = cursor.getString(cursor.getColumnIndex(columns[2]));
                String contentsMemo = cursor.getString(cursor.getColumnIndex(columns[3]));
                String isFavorite = cursor.getString(cursor.getColumnIndex(columns[4]));
                String which_folder = cursor.getString(cursor.getColumnIndex(columns[5]));

                //배열에 담음
                Arr_ID[i] = id;
                Arr_TITLE[i] = title;

                createDate = createDate.substring(0,11);
                Arr_CREATE_DATE[i] = createDate;

                Arr_CONTENTS_MEMO[i] = contentsMemo;
                Arr_isFAVORITE[i] = isFavorite;
                Arr_WHICH_FOLDER[i] = which_folder;

                Log.e("Fragment_Main.java", "레코드 " + index + " : " + id + ", " + title + ", " + createDate + ", " +
                        contentsMemo + ", " + isFavorite + ", " + which_folder );
                index += 1;
                i+=1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Note.java 에 DB 값 저장
    public List<Note> getList() {
        List<Note> list = new ArrayList<>();

        for(int i = 0; i< Arr_TITLE.length; i++) {
            Note model = new Note();
            model.setTitle(Arr_TITLE[i]);
            model.setCreateDate(Arr_CREATE_DATE[i]);
            model.setMemo(Arr_CONTENTS_MEMO[i]);
            list.add(model);
        }

        return list;
    }

    //체크박스 관련
    //체크하고 btnPlus 누르면 값 Toast 로 뿌림
    public void selectedClick() {

        List list = adapter.getSelectedItem();

        if(list.size() > 0 ) {
            StringBuilder sb = new StringBuilder();
            for(int index = 0; index < list.size(); index++) {
                Note model = (Note) list.get(index);
                sb.append(model.getTitle()).append("\n");
            }
            showToast(sb.toString());

        } else {
            showToast("체크 안됨");
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
