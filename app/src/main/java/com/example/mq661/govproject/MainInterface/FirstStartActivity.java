package com.example.mq661.govproject.MainInterface;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.mq661.govproject.Login_Register.Login;

import com.example.mq661.govproject.Login_Register.Login_noToken;
import com.example.mq661.govproject.R;

import java.util.ArrayList;


public class FirstStartActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPager vpGuiding;
    private MyPagerAdapter myPagerAdapter;

    private ArrayList<View> viewArrayList;
    private  Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_first_start);
        vpGuiding = findViewById(R.id.main_vpGuiding);
        btn=findViewById(R.id.button);
        viewPagerNormalLookLike();
        vpGuiding.addOnPageChangeListener(this);
        btn.setOnClickListener(this);
        btn.setVisibility(View.INVISIBLE);
    }

    //默认效果的
    public void viewPagerNormalLookLike() {
        //List集合赋值，用于给适配器传参数
        viewArrayList = new ArrayList<View>();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        viewArrayList.add(layoutInflater.inflate(R.layout.first_start_layout1, null, false));
        viewArrayList.add(layoutInflater.inflate(R.layout.first_start_layout2, null, false));
        viewArrayList.add(layoutInflater.inflate(R.layout.first_start_layout3, null, false));
        viewArrayList.add(layoutInflater.inflate(R.layout.first_start_layout4, null, false));
        //适配器赋值
        myPagerAdapter = new MyPagerAdapter(viewArrayList);
        //绑定数据适配器
        vpGuiding.setAdapter(myPagerAdapter);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
if(i==3)
{
        btn.setVisibility(View.VISIBLE);
}
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getApplicationContext(), "00", Toast.LENGTH_SHORT);
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), Login_noToken.class);
            startActivity(intent);
            this.finish();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //非默认值
        if (newConfig.fontScale != 1){
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
}
