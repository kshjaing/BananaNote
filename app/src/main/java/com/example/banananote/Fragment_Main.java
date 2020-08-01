package com.example.banananote;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Main extends Fragment {

    RecyclerView recyclerView;
    NoteAdapter adapter;
    Note_MultiSelectionAdapter multi_adapter;



    static boolean checked= false;

    //public  String[] title;

    public  int i = 0;
    //어댑터 사이즈

    int adapter_size = 0;

    String Arr_settitle[] = {
            "test1","test2","test3",
            "test4","test5","test6",
            "test7","test8","test9",
            "test10","test11","test12",
            "test13","test14","test15",
            "test16","test17","test18"
    };
    String Arr_setCreateDate[] = {
            "2020-07-21","2020-07-22","2020-07-23",
            "2020-07-24","2020-07-25","2020-07-26",
            "2020-07-27","2020-07-28","2020-07-29",
            "2020-07-30","2020-07-31","2020-07-32",
            "2020-07-33","2020-07-34","2020-07-35",
            "2020-07-36","2020-07-37","2020-07-38"
    };
    String Arr_setMemo[] = {
            "테스트 입니다1","테스트입니다2","테스트입니다3",
            "테스트 입니다4","테스트입니다5","테스트입니다6",
            "테스트 입니다7","테스트입니다8","테스트입니다9",
            "테스트 입니다10","테스트입니다11","테스트입니다12",
            "테스트 입니다13","테스트입니다14","테스트입니다15",
            "테스트 입니다16","테스트입니다17","테스트입니다18"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        View v = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        List<Note> list = getList();

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,1);
        recyclerView.setLayoutManager(layoutManager);

        //MainActivity.tag = "multi";

        adapter = new NoteAdapter(this,list);

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnNoteItemClickListener() {
            @Override
            public void onItemClick(NoteAdapter.ViewHolder holder, View view, int position) {
                Note item = adapter.getItem(position);
                Toast.makeText(getContext(), "아이템 선택됨: " + item.getTitle(), Toast.LENGTH_LONG).show();

            }
        });


        
        return v;
    }

    private List<Note> getList() {
        List<Note> list = new ArrayList<>();

        for(int i = 0; i< Arr_settitle.length; i++) {
            Note model = new Note();
            model.setTitle(Arr_settitle[i]);
            model.setCreateDate(Arr_setCreateDate[i]);
            model.setMemo(Arr_setMemo[i]);
            list.add(model);
        }

        return list;
    }

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
