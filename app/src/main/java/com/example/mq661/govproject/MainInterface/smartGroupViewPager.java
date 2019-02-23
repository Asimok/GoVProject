package com.example.mq661.govproject.MainInterface;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mq661.govproject.R;
import com.example.mq661.govproject.SmartGroup.groupByBulidAndRoom;
import com.example.mq661.govproject.SmartGroup.groupByDays;
import com.example.mq661.govproject.SmartGroup.groupByFounction;
import com.example.mq661.govproject.SmartGroup.groupBySize;
import com.example.mq661.govproject.SmartGroup.groupByTime;
import com.zyao89.view.zloading.ZLoadingDialog;

import java.util.ArrayList;

public class smartGroupViewPager extends AppCompatActivity
        implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager mViewPager = null;
    private ImageView mCursorImg = null;
    private TextView mOneTv = null;
    private TextView mTwoTv = null;
    private TextView mThreeTv = null;
    private TextView mFourTv = null;
    private TextView mFiveTv = null;
    private ViewPagerAdapter mAdapter = null;
    private ArrayList<View> mPageList = null;
    private LocalActivityManager manager;
    private Intent intent4, intent5, intent1, intent2, intent3;
    private int mOffset = 0;// 移动条图片的偏移量
    private int mCurrIndex = 0; // 当前页面的编号
    private int mOneDis = 0; // 移动条滑动一页的距离
    private int mTwoDis = 0; // 滑动条移动两页的距离
    private int mThreeDis = 0; // 滑动条移动3页的距离
    private int mFourDis = 0; // 滑动条移动4页的距离
    private ZLoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_smartgroup_layout);
        Toast.makeText(this, "下拉刷新！", Toast.LENGTH_SHORT).show();
        //     new MyThread().start();
//
//        Timer timer = new Timer();//初始化一个时间
//        timer.schedule(new TimerTask() {
//             @Override
//            public void run() {
//                dialog.cancel();
//                //这里填入时间结束后要进行的逻辑
//            }
//
//        } , 200);
        // 获取界面组件
        mViewPager = findViewById(R.id.view_pager);
        mCursorImg = findViewById(R.id.cursor_img);
        mOneTv = findViewById(R.id.viewpager_tv_one1);
        mTwoTv = findViewById(R.id.viewpager_tv_two1);
        mThreeTv = findViewById(R.id.viewpager_tv_three1);
        mFourTv = findViewById(R.id.viewpager_tv_four1);
        mFiveTv = findViewById(R.id.viewpager_tv_five1);

        // 初始化指示器位置
        initCursorPosition();

        manager = new LocalActivityManager(this, true);
        manager.dispatchCreate(savedInstanceState);

        intent1 = new Intent(this, groupByBulidAndRoom.class);
        View tab01 = manager.startActivity("viewID1", intent1).getDecorView();
        intent2 = new Intent(this, groupByFounction.class);
        View tab02 = manager.startActivity("viewID1", intent2).getDecorView();
        intent3 = new Intent(this, groupBySize.class);
        View tab03 = manager.startActivity("viewID1", intent3).getDecorView();
        intent4 = new Intent(this, groupByDays.class);
        View tab04 = manager.startActivity("viewID1", intent4).getDecorView();
        intent5 = new Intent(this, groupByTime.class);
        View tab05 = manager.startActivity("viewID1", intent5).getDecorView();

        mPageList = new ArrayList<>();
        mPageList.add(tab01);
        mPageList.add(tab02);
        mPageList.add(tab03);
        mPageList.add(tab04);
        mPageList.add(tab05);
        // 设置适配器
        mAdapter = new ViewPagerAdapter(mPageList);
        mViewPager.setAdapter(mAdapter);

        // 文本框点击监听器
        mOneTv.setOnClickListener(this);
        mTwoTv.setOnClickListener(this);
        mThreeTv.setOnClickListener(this);
        mFourTv.setOnClickListener(this);
        mFiveTv.setOnClickListener(this);

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
        mOffset = (screenWidth / 5 - cursorWidth) / 2;

        // 设置动画初始位置
        Matrix matrix = new Matrix();
        matrix.postTranslate(mOffset, 0);
        mCursorImg.setImageMatrix(matrix);

        // 计算指示器图片的移动距离
        mOneDis = mOffset * 2 + cursorWidth;// 页卡1 -> 页卡2 偏移量
        mTwoDis = mOneDis * 2;// 页卡1 -> 页卡3 偏移量
        mThreeDis = mOneDis * 3;
        mFourDis = mOneDis * 4;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewpager_tv_one1:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.viewpager_tv_two1:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.viewpager_tv_three1:
                mViewPager.setCurrentItem(2);
                break;
            case R.id.viewpager_tv_four1:
                mViewPager.setCurrentItem(3);
                break;
            case R.id.viewpager_tv_five1:
                mViewPager.setCurrentItem(4);
                break;
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
                } else if (3 == mCurrIndex) {
                    animation = new TranslateAnimation(mThreeDis, 0, 0, 0);
                } else if (4 == mCurrIndex) {
                    animation = new TranslateAnimation(mFourDis, 0, 0, 0);
                }
                break;
            case 1:
                if (0 == mCurrIndex) {
                    animation = new TranslateAnimation(0, mOneDis, 0, 0);
                } else if (2 == mCurrIndex) {
                    animation = new TranslateAnimation(mTwoDis, mOneDis, 0, 0);
                } else if (3 == mCurrIndex) {
                    animation = new TranslateAnimation(mThreeDis, mOneDis, 0, 0);
                } else if (4 == mCurrIndex) {
                    animation = new TranslateAnimation(mFourDis, mOneDis, 0, 0);
                }
                break;
            case 2:
                if (0 == mCurrIndex) {
                    animation = new TranslateAnimation(0, mTwoDis, 0, 0);
                } else if (1 == mCurrIndex) {
                    animation = new TranslateAnimation(mOneDis, mTwoDis, 0, 0);
                } else if (3 == mCurrIndex) {
                    animation = new TranslateAnimation(mThreeDis, mTwoDis, 0, 0);
                } else if (4 == mCurrIndex) {
                    animation = new TranslateAnimation(mFourDis, mTwoDis, 0, 0);
                }
                break;
            case 3:
                if (0 == mCurrIndex) {
                    animation = new TranslateAnimation(0, mThreeDis, 0, 0);
                } else if (1 == mCurrIndex) {
                    animation = new TranslateAnimation(mOneDis, mThreeDis, 0, 0);
                } else if (2 == mCurrIndex) {
                    animation = new TranslateAnimation(mTwoDis, mThreeDis, 0, 0);
                } else if (4 == mCurrIndex) {
                    animation = new TranslateAnimation(mFourDis, mThreeDis, 0, 0);
                }
                break;
            case 4:
                if (0 == mCurrIndex) {
                    animation = new TranslateAnimation(0, mFourDis, 0, 0);
                } else if (1 == mCurrIndex) {
                    animation = new TranslateAnimation(mOneDis, mFourDis, 0, 0);
                } else if (2 == mCurrIndex) {
                    animation = new TranslateAnimation(mTwoDis, mFourDis, 0, 0);
                } else if (3 == mCurrIndex) {
                    animation = new TranslateAnimation(mThreeDis, mFourDis, 0, 0);
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
//    public class MyThread extends Thread {
//
//        //继承Thread类，并改写其run方法
//        @Override
//        public void run() {
//            Looper.prepare();
//            Toast.makeText(smartGroupViewPager.this,"点击见面右小角按钮刷新！" , Toast.LENGTH_SHORT);
//            Looper.loop();
//        }
//    }
//    public void onEnterAnimationComplete() {
//        dialog = new ZLoadingDialog(smartGroupViewPager.this);
//        dialog.setLoadingBuilder(Z_TYPE.CIRCLE_CLOCK)//设置类型
//                .setLoadingColor(Color.RED)//颜色
//                .setHintText("加载中...")
//                .setHintTextSize(16) // 设置字体大小 dp
//                .setHintTextColor(Color.GRAY)  // 设置字体颜色
//                .setDurationTime(0.2) // 设置动画时间百分比 - 0.5倍
//                .setDialogBackgroundColor(Color.parseColor("#ffffffff")) // 设置背景色，默认白色
//                .setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//                        dialog.show();
//    }
}
