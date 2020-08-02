package com.example.banananote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.view.View.OnClickListener; //클릭 이벤트
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_OVERLAY_PERMISSION = 1;
    Intent foregroundServiceIntent;
    private static final int MESSAGE_PERMISSION_GRANTED = 1111;
    private static final int MESSAGE_PERMISSION_DENIED = 1112;

    //menu 부분
    private DisplayMetrics metrics;
    private LinearLayout MenuPanel;
    private LinearLayout MainPanel;
    private FrameLayout.LayoutParams MainPanelParameters;
    private FrameLayout.LayoutParams MenuPanelParameters;
    private int PanelWidth;
    private boolean is_Panel_Expanded;

    //private Button btn_menu; //메뉴가 보여지게 할 햄버거 버튼

    //페이저
    ViewPager pager;

    private View Frag_Main;

    Switch Btn_Permission;

    static int Stop = 0;

    //하단 탭
    //private TabLayout tabLayout;

    //activity_bottom_menu.xml

    Button btnPlus;                           //새 메모 버튼

    //pager position value
    int Position;

    ///
    public static Context context_main;
    public static Boolean Edit_Activation;
    public static Boolean isDarkmode;         //다크모드 인수

    String a;
    public static String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //tag = "multi";
        tag = "single";
        ///
        context_main = MainActivity.this;
        Edit_Activation = false; //기본값 비활성화


        Btn_Permission = findViewById(R.id.switch_Permission);

        if (FloatingViewService.isService) Btn_Permission.setChecked(true);
        else Btn_Permission.setChecked(false);

        Btn_Permission.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //새로운 시도
                    if (Settings.canDrawOverlays(MainActivity.this)) {
                        foregroundServiceIntent = new Intent(MainActivity.this, FloatingViewService.class);
                        startService(foregroundServiceIntent);
                    } else {
                        new AwesomeSuccessDialog(MainActivity.this)
                                .setTitle("빠른 메모")
                                .setMessage("빠른 메모를 위해 권한이 필요합니다")
                                .setCancelable(true)
                                .setPositiveButtonText("허용")
                                .setNegativeButtonText("취소")
                                .setPositiveButtonClick(new Closure() {
                                    @Override
                                    public void exec() {
                                        //새로운 시도
                                        if (Settings.canDrawOverlays(MainActivity.this)) {
                                            foregroundServiceIntent = new Intent(MainActivity.this, FloatingViewService.class);
                                            startService(foregroundServiceIntent);
                                        } else {
                                            onObtainingPermissionOverlayWindow();
                                        }
                                    }
                                })
                                .setNegativeButtonClick(new Closure() {
                                    @Override
                                    public void exec() {
                                        Btn_Permission.setChecked(false);
                                    }
                                }).show();
                    }


                } else {
                    foregroundServiceIntent = new Intent(MainActivity.this, FloatingViewService.class);
                    stopService(foregroundServiceIntent);
                    Stop = 1;
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle("ALL");
        getWindow().setStatusBarColor(Color.parseColor("#fff9eb"));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_dehaze_black_24dp);

        btnPlus = findViewById(R.id.btnPlus);
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "메모추가 인텐트로 이동", Toast.LENGTH_SHORT).show();
            }
        });



        //------------------페이저 기능--------------------
        pager = findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2); //페이지 크기 2개

        Tab_PagerAdapter adapter = new Tab_PagerAdapter(getSupportFragmentManager());

        //메인페이지
        Fragment_Main fragment_main = new Fragment_Main();
        adapter.addItem(fragment_main);

        //폴더페이지
        Fragment_Folder fragment_folder = new Fragment_Folder();
        adapter.addItem(fragment_folder);

        pager.setPageTransformer(true, new DepthPageTransformer());
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        Position = position;
                        toolBarLayout.setTitle("ALL");
                        break;
                    case 1:
                        Position = position;
                        toolBarLayout.setTitle("Folder");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

/* 하단바 사라졌으니 이 주석부분은 없애야할듯

        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()) {
                    case R.id.Linear_ALL:
                        pager.setCurrentItem(0, false);
                        break;
                    case R.id.Linear_Favorites:
                        pager.setCurrentItem(1,false);
                        break;
                    case R.id.Linear_Tag:
                        pager.setCurrentItem(2, false);
                        break;
                    case R.id.Linear_Lock:
                        pager.setCurrentItem(3, false);
                        break;
                }
            }
        };*/

        Frag_Main = getLayoutInflater().inflate(R.layout.fragment_main, null, false);


        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        PanelWidth = (int) ((metrics.widthPixels) * 0.85);

        MainPanel = (LinearLayout) findViewById(R.id.MainPanel);
        MainPanelParameters = (FrameLayout.LayoutParams) MainPanel.getLayoutParams();
        MainPanelParameters.width = metrics.widthPixels;
        MainPanel.setLayoutParams(MainPanelParameters);

        MenuPanel = (LinearLayout) findViewById(R.id.MenuPanel);
        MenuPanelParameters = (FrameLayout.LayoutParams) MenuPanel.getLayoutParams();
        MenuPanelParameters.width = PanelWidth;

        MenuPanel.setLayoutParams(MenuPanelParameters);


        View main_cardview;
        main_cardview = getLayoutInflater().inflate(R.layout.frag_main_item, null,false);

        CheckBox main_checkBox;
        main_checkBox = main_cardview.findViewById(R.id.main_checkbox);

        main_checkBox.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        if (!Settings.canDrawOverlays(MainActivity.this)) {
            if (FloatingViewService.isService) Btn_Permission.setChecked(true);
            else Btn_Permission.setChecked(false);
        }
        super.onStop();
    }


    public static void ViewGroup_Enable_Toggle(ViewGroup viewGroup, boolean Enable) {
        int ChildActivity_Count = viewGroup.getChildCount();
        for (int i = 0; i < ChildActivity_Count; i++) {
            View view = viewGroup.getChildAt(i);

            if (view.getId() != android.R.id.home) { //R.id.btn_Menu
                view.setEnabled(Enable);
                if (view instanceof ViewGroup) {
                    ViewGroup_Enable_Toggle((ViewGroup) view, Enable);
                }
            }
        }
    }


    public void onObtainingPermissionOverlayWindow() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQ_CODE_OVERLAY_PERMISSION);
    }

    //페이저 기능
    class Tab_PagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> items = new ArrayList<Fragment>();

        public Tab_PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addItem(Fragment item) {
            items.add(item);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }

    //ActionBar menu inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("검색");

        searchView.setOnClickListener(new SearchView.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                return true;
            }
        });
        return true;
    }

    //ActionBar menu Selected


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //햄버거 버튼
                if (!is_Panel_Expanded) {
                    is_Panel_Expanded = true;
                    MainPanel.animate()
                            .x(PanelWidth)
                            .setDuration(300)
                            .start();

                    androidx.coordinatorlayout.widget.CoordinatorLayout ViewGroup =
                            (androidx.coordinatorlayout.widget.CoordinatorLayout) findViewById(R.id.pager).getParent();
                    ViewGroup_Enable_Toggle(ViewGroup, false);

                    ((LinearLayout) findViewById(R.id.Frame_Empty_LinearLayout)).setVisibility(View.VISIBLE);
                    findViewById(R.id.Frame_Empty_LinearLayout).setEnabled(true);
                    findViewById(R.id.Frame_Empty_LinearLayout).setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            MainPanel.animate()
                                    .x(0)
                                    .setDuration(300)
                                    .start();
                            is_Panel_Expanded = false;

                            androidx.coordinatorlayout.widget.CoordinatorLayout ViewGroup =
                                    (androidx.coordinatorlayout.widget.CoordinatorLayout) findViewById(R.id.pager).getParent();
                            ViewGroup_Enable_Toggle(ViewGroup, true);

                            ((LinearLayout) findViewById(R.id.Frame_Empty_LinearLayout)).setVisibility(View.GONE);
                            findViewById(R.id.Frame_Empty_LinearLayout).setEnabled(false);

                            return true;
                        }
                    });
                } else {
                    MainPanel.animate()
                            .x(0)
                            .setDuration(300)
                            .start();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void restart() {


        Edit_Activation = true;

        //페이저 기능

        Tab_PagerAdapter adapter = new Tab_PagerAdapter(getSupportFragmentManager());

        Fragment_Main fragment_main = new Fragment_Main();
        adapter.addItem(fragment_main);

        Fragment_Folder fragment_folder = new Fragment_Folder();
        adapter.addItem(fragment_folder);

        pager.setPageTransformer(true, new DepthPageTransformer());
        pager.setAdapter(adapter);

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment_main.selectedClick();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Edit_Activation = false;
        //페이저 기능

        Tab_PagerAdapter adapter = new Tab_PagerAdapter(getSupportFragmentManager());

        Fragment_Main fragment_main = new Fragment_Main();
        adapter.addItem(fragment_main);

        Fragment_Folder fragment_folder = new Fragment_Folder();
        adapter.addItem(fragment_folder);

        pager.setPageTransformer(true, new DepthPageTransformer());
        pager.setAdapter(adapter);
    }

    @Override
    public void onResume() {

        super.onResume();
    }
}