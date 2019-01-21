package com.example.mq661.govproject.MainInterface;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mq661.govproject.Main.MainInterfaceNow_handler;
import com.example.mq661.govproject.Main.MainInterfaceToday_handler;
import com.example.mq661.govproject.R;
import java.util.ArrayList;

/**
 * @创建者 鑫鱻
 * @描述 Android零基础入门到精通系列教程，欢迎关注微信公众号ShareExpert
 */
public class ViewPagerCustomActivity extends AppCompatActivity
        implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager mViewPager = null;
    private ImageView mCursorImg = null;
    private TextView mOneTv = null;
    private TextView mTwoTv = null;
    private TextView mThreeTv = null;

    private ViewPagerAdapter mAdapter = null;
    private ArrayList<View> mPageList = null;
    private LocalActivityManager manager;
    private Intent intentMain,intentCircle,intentMy;
    private int mOffset = 0;// 移动条图片的偏移量
    private int mCurrIndex = 0; // 当前页面的编号
    private int mOneDis = 0; // 移动条滑动一页的距离
    private int mTwoDis = 0; // 滑动条移动两页的距离

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_custom_layout);

        // 获取界面组件
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mCursorImg = (ImageView) findViewById(R.id.cursor_img);
        mOneTv = (TextView) findViewById(R.id.viewpager_tv_one);
        mTwoTv = (TextView) findViewById(R.id.viewpager_tv_two);
      //  mThreeTv = (TextView) findViewById(R.id.viewpager_tv_three);

        // 初始化指示器位置
        initCursorPosition();

        manager=new LocalActivityManager(this,true);
        manager.dispatchCreate(savedInstanceState);


        intentCircle=new Intent(this,MainInterfaceNow_handler.class);
        View tab01=manager.startActivity("viewID", intentCircle).getDecorView();
        intentMain=new Intent(this,MainInterfaceToday_handler.class);
        View tab02=manager.startActivity("viewID", intentMain).getDecorView();
//        View tab01 = manager.startActivity("viewID", intentMain).getDecorView();          
//        intentCircle = new Intent(StartActivity.this, CircleActivity.class);  
//        View tab02 = manager.startActivity("viewID", intentCircle).getDecorView();        
//        intentMy = new Intent(StartActivity.this, MyActivity.class);  
//        View tab03 = manager.startActivity("viewID", intentMy).getDecorView();  


        mPageList = new ArrayList<>();
 //       LayoutInflater inflater = getLayoutInflater();
//        mPageList.add(inflater.inflate(R.layout.searchroom_lv_layout, null, false));
//        mPageList.add(inflater.inflate(R.layout.viewpager_pager2, null, false));
//        mPageList.add(inflater.inflate(R.layout.viewpager_pager3, null, false));
        mPageList.add(tab01);
        mPageList.add(tab02);
        // 设置适配器
        mAdapter = new ViewPagerAdapter(mPageList);
        mViewPager.setAdapter(mAdapter);

        // 文本框点击监听器
        mOneTv.setOnClickListener(this);
        mTwoTv.setOnClickListener(this);
//        mThreeTv.setOnClickListener(this);

        // 页面改变监听器
        mViewPager.addOnPageChangeListener(this);
        // 初始默认第一页
        mViewPager.setCurrentItem(0);
    }

    private void initCursorPosition() {
        // 获取指示器图片宽度
        int cursorWidth = BitmapFactory.decodeResource(getResources(), R.drawable.a1).getWidth();

        // 获取分辨率宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;

        // 计算偏移量
        mOffset = (screenWidth / 2 - cursorWidth) / 2;

        // 设置动画初始位置
        Matrix matrix = new Matrix();
        matrix.postTranslate(mOffset, 0);
        mCursorImg.setImageMatrix(matrix);

        // 计算指示器图片的移动距离
        mOneDis = mOffset * 2 + cursorWidth;// 页卡1 -> 页卡2 偏移量
      //  mTwoDis = mOneDis * 2;// 页卡1 -> 页卡3 偏移量
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewpager_tv_one:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.viewpager_tv_two:
                mViewPager.setCurrentItem(1);
                break;
//            case R.id.viewpager_tv_three:
//                mViewPager.setCurrentItem(2);
//                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        // 指示器图片动画设置
        Animation animation = null;
        switch (position) {
            case 0:
                if (1 == mCurrIndex) {
                    animation = new TranslateAnimation(mOneDis, 0, 0, 0);
                } else if (2 == mCurrIndex) {
                    animation = new TranslateAnimation(mTwoDis, 0, 0, 0);
                }
                break;
            case 1:
                if (0 == mCurrIndex) {
                    animation = new TranslateAnimation(mOffset, mOneDis, 0, 0);
                } else if (2 == mCurrIndex) {
                    animation = new TranslateAnimation(mTwoDis, mOneDis, 0, 0);
                }
                break;
            case 2:
                if (0 == mCurrIndex) {
                    animation = new TranslateAnimation(mOffset, mTwoDis, 0, 0);
                } else if (1 == mCurrIndex) {
                    animation = new TranslateAnimation(mOneDis, mTwoDis, 0, 0);
                }
                break;
            default:
                break;
        }
        mCurrIndex = position;
        animation.setFillAfter(true); // True:图片停在动画结束位置
        animation.setDuration(300);
        mCursorImg.startAnimation(animation);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
