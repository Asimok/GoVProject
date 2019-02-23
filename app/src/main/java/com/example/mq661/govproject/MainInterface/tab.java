package com.example.mq661.govproject.MainInterface;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.mq661.govproject.AlterRoom.gov_Admin;
import com.example.mq661.govproject.AlterRoom.gov_Founction;
import com.example.mq661.govproject.Login_Register.Logout;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.tools.roomSortDBHelper;

public class tab extends TabActivity implements NavigationView.OnNavigationItemSelectedListener, TabHost.OnTabChangeListener {
    int image[] = {R.drawable.functions, R.drawable.click_functions1, R.drawable.smartgroup, R.drawable.smartgroup_click, R.drawable.main, R.drawable.click_main1, R.drawable.admin, R.drawable.admin_click, R.drawable.mine, R.drawable.click_mine1};
    private TabHost tabHost;
    private TextView textView;
    private roomSortDBHelper helper4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabhost_layout);
        helper4 = new roomSortDBHelper(this);
        tabHost = getTabHost();
        tabHost.getTabWidget().setDividerDrawable(null); // 去掉分割线
        //TODO
        //修改
        //deleteAllRoom();

        TabHost.TabSpec tab_function = tabHost.newTabSpec("0");
        tab_function.setIndicator(getImageView(2));
        tab_function.setContent(new Intent(this, gov_Founction.class));
        TabHost.TabSpec tab_smartGroup = tabHost.newTabSpec("1");
        tab_smartGroup.setIndicator(getImageView(6));
        tab_smartGroup.setContent(new Intent(this, smartGroupViewPager.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        TabHost.TabSpec tab_main = tabHost.newTabSpec("2");
        tab_main.setIndicator(getImageView(1));
        tab_main.setContent(new Intent(this, mainViewPager.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        TabHost.TabSpec tab_manage = tabHost.newTabSpec("3");
        tab_manage.setIndicator(getImageView(8));
        tab_manage.setContent(new Intent(this, gov_Admin.class));
        TabHost.TabSpec tab_mine = tabHost.newTabSpec("4");
        tab_mine.setIndicator(getImageView(4));
        tab_mine.setContent(new Intent(this, Logout.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        tabHost.addTab(tab_function);
        tabHost.addTab(tab_smartGroup);
        tabHost.addTab(tab_main);
        tabHost.addTab(tab_manage);
        tabHost.addTab(tab_mine);
        tabHost.setOnTabChangedListener(this);

//            tabHost.addTab(tabHost.newTabSpec(0 + "").setIndicator(getImageView(0)).setContent(new Intent(this, mainViewPager.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
//            tabHost.addTab(tabHost.newTabSpec(1 + "").setIndicator(getImageView(2)).setContent(new Intent(this, gov_Founction.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
//        tabHost.addTab(tabHost.newTabSpec(2 + "").setIndicator(getImageView(4)).setContent(new Intent(this, Logout.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        tabHost.setCurrentTab(2);
        setContentView(tabHost);

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
    }

    private View getImageView(int index) {
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.view_tab_indicator, null);
        ImageView imageView = view.findViewById(R.id.tab_iv_image);
        imageView.setImageResource(image[index]);
        return view;
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onTabChanged(String tabId) {
        if (tabId.equals("0")) {
            ImageView iv = tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.click_functions1));
            iv = tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.smartgroup));
            iv = tabHost.getTabWidget().getChildAt(2).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.main));
            iv = tabHost.getTabWidget().getChildAt(3).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.admin));
            iv = tabHost.getTabWidget().getChildAt(4).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.mine));

        } else if (tabId.equals("1")) {

            ImageView iv = tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.functions));
            iv = tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.smartgroup_click));
            iv = tabHost.getTabWidget().getChildAt(2).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.main));
            iv = tabHost.getTabWidget().getChildAt(3).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.admin));
            iv = tabHost.getTabWidget().getChildAt(4).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.mine));

        } else if (tabId.equals("2")) {
            ImageView iv = tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.functions));
            iv = tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.smartgroup));
            iv = tabHost.getTabWidget().getChildAt(2).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.click_main1));
            iv = tabHost.getTabWidget().getChildAt(3).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.admin));
            iv = tabHost.getTabWidget().getChildAt(4).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.mine));
        } else if (tabId.equals("3")) {
            ImageView iv = tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.functions));
            iv = tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.smartgroup));
            iv = tabHost.getTabWidget().getChildAt(2).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.main));
            iv = tabHost.getTabWidget().getChildAt(3).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.admin_click));
            iv = tabHost.getTabWidget().getChildAt(4).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.mine));
        } else if (tabId.equals("4")) {
            ImageView iv = tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.functions));
            iv = tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.smartgroup));
            iv = tabHost.getTabWidget().getChildAt(2).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.main));
            iv = tabHost.getTabWidget().getChildAt(3).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.admin));
            iv = tabHost.getTabWidget().getChildAt(4).findViewById(R.id.tab_iv_image);
            iv.setImageDrawable(getResources().getDrawable(R.drawable.click_mine1));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //非默认值
        if (newConfig.fontScale != 1) {
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {//还原字体大小
        Resources res = super.getResources();
        //非默认值
        if (res.getConfiguration().fontScale != 1) {
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    public void deleteAllRoom() {

        SQLiteDatabase db = helper4.getWritableDatabase();
        int i = db.delete("allroom", null, null);
        if (i == 0) {
            Log.d("aaa", "deleteAllRoom  删除不成功");
        } else {
            Log.d("aaa", "deleteAllRoom  删除成功");
        }
        db.close();
    }
}
