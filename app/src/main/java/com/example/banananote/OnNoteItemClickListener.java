package com.example.banananote;

import android.view.View;

public interface OnNoteItemClickListener {
    //메모를 누를때 그 위치를 알아낸다.
    public void onItemClick(NoteAdapter.ViewHolder holder, View view, int position);
}
