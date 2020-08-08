package com.example.banananote;

import android.view.View;

public interface OnNoteItem_Multi_ClickListener {
    //이 클래스는 잠시 사용하지 않는다.
    //Note_MultiSelectionAdapter.java 에서 사용했다.
    public void onItem_MultiClick(Note_MultiSelectionAdapter.ViewHolder holder, View view, int position);
}
