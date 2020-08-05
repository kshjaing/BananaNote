package com.example.banananote;

import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ChoiceFormat;
import java.util.ArrayList;
import java.util.List;

//frag_main_item.xml 틀을 이용한 Setting 과정
//이 부분은 나도 완벽하지 않아 공부 필요
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> implements OnNoteItemClickListener {

    //ArrayList<Note> items = new ArrayList<>();
    private List<Note> items;
    private Fragment fragment;

    OnNoteItemClickListener listener; //뷰 클릭시 여부
    //static int a = 0;
    static Boolean Edit_Activation;


    public void addItem(Note item) {
        items.add(item);
    }

    public void setItems(ArrayList<Note> items) {
        this.items = items;
    }

    public Note getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Note item) {
        items.set(position,item);
    }

    public NoteAdapter(Fragment fragment, List<Note> itemModels) {
        this.items = itemModels;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //처음에 ViewHolder 에 담을 xml
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.frag_main_item,parent,false);

        //ViewHolder Setting
        return new ViewHolder(itemView, this);
    }

    public void setOnItemClickListener(OnNoteItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if(listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //뿌리는 곳
        //노트 모양을 여기서 상황껏 변경할 예정
        Note item = items.get(position);
        initializeViews(item,holder,position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void initializeViews(final Note item, final NoteAdapter.ViewHolder holder, int position) {
        //holder 된 값 설정
        holder.textView_Title.setText(item.getTitle());
        holder.textView_CreateDate.setText(item.getCreateDate());
        holder.textView_Memo.setText(item.getMemo());
        holder.main_checkbox.setChecked(item.isSelected());
        holder.main_checkbox.setTag(position);

        //MainActivity 에서 가져온 tag 값
        //single - > 그냥 클릭
        //multi - > 체크박스 모드

        if(MainActivity.tag == "multi") {

            holder.main_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(holder.main_checkbox.isChecked()) {
                        int ClickedPosition = (Integer) holder.main_checkbox.getTag();
                        items.get(ClickedPosition).setSelected(holder.main_checkbox.isChecked());
                    } else {
                        int ClickedPosition = (Integer) holder.main_checkbox.getTag();
                        items.get(ClickedPosition).setSelected(holder.main_checkbox.isChecked());
                    }
                }
            });

        }
    }

    public List<Note> getSelectedItem() {
        List<Note> itemModelList = new ArrayList<>();
        int i;
        for (i = 0; i< items.size(); i++) {
            Note item = items.get(i);

            if(item.isSelected()) {
                itemModelList.add(item);
            }
        }
        return itemModelList;
    }

    //설정
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_Title;
        TextView textView_CreateDate;
        TextView textView_Memo;

        CheckBox main_checkbox;
        CardView Main_CardView;

        public ViewHolder(View itemView, final OnNoteItemClickListener listener) {
            super(itemView);

            textView_Title = itemView.findViewById(R.id.NoteTitle);
            textView_CreateDate = itemView.findViewById(R.id.CreateDate);
            textView_Memo = itemView.findViewById(R.id.Memo);

            //frag_main_item.xml
            //checkbox checked, card view margins
            main_checkbox = itemView.findViewById(R.id.main_checkbox);

            Main_CardView = itemView.findViewById(R.id.Main_CardView);
            ViewGroup.MarginLayoutParams layoutParams =
                    (ViewGroup.MarginLayoutParams) Main_CardView.getLayoutParams();



            if(MainActivity.tag == "single") {

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();

                        //선택된 itemView position 전달
                        if(listener != null) {
                            listener.onItemClick(ViewHolder.this, view, position);

                            if(main_checkbox.isChecked()) {
                                main_checkbox.setChecked(false);
                            }
                            else {
                                main_checkbox.setChecked(true);
                            }
                        }
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        //체크박스 모드
                        if(main_checkbox.getVisibility() == View.INVISIBLE) {
                            MainActivity.tag = "multi";
                            main_checkbox.setChecked(true);
                            ((MainActivity)MainActivity.context_main).restart();
                            //Edit_Activation = true;
                        }
                        return true;
                    }
                });
            } else { //MainActivity.tag == multi
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();

                        if(listener != null) {

                            if(main_checkbox.isChecked()) {
                                main_checkbox.setChecked(false);
                            }
                            else {
                                main_checkbox.setChecked(true);
                            }
                        }
                    }
                });
            }

            //애니메이션
            ValueAnimator valueAnimator = ValueAnimator.ofInt(25);
            valueAnimator.setDuration(400);
            Edit_Activation = ((MainActivity)MainActivity.context_main).Edit_Activation;
            //프래그먼트 재시작 과정에서 Edit_Activation 값 true -> 체크박스 모드

            if(Edit_Activation) {
                if(!main_checkbox.isChecked()) {
                    main_checkbox.setVisibility(View.VISIBLE);

                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            layoutParams.setMargins(0,
                                    0,
                                    (Integer) valueAnimator.getAnimatedValue(),
                                    (Integer) valueAnimator.getAnimatedValue());
                            Main_CardView.requestLayout();
                        }
                    });
                    valueAnimator.start();
                }
            } else {
                main_checkbox.setVisibility(View.INVISIBLE);
            }

        }

        public void setItem(Note item) {
            textView_Title.setText(item.getTitle());
            textView_CreateDate.setText(item.getCreateDate());
            textView_Memo.setText(item.getMemo());
        }
    }
}
