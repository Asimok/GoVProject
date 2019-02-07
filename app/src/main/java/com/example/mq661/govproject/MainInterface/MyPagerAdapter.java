package com.example.mq661.govproject.MainInterface;


import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.example.mq661.govproject.R;

import java.util.ArrayList;

public class MyPagerAdapter extends PagerAdapter {

    private ArrayList<View> viewLists;
int image[]={R.drawable.guide1,R.drawable.guide2,R.drawable.guide3,R.drawable.guide4};
    public MyPagerAdapter() {
        super();
    }

    public MyPagerAdapter(ArrayList<View> viewLists) {
        super();
        this.viewLists = viewLists;
    }

    @Override
    public int getCount() {
        return viewLists.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //在此设置背景图片，提高加载速度，解决OOM问题
        View view=viewLists.get(position);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        view.setBackgroundResource(image[position]);
        view.setLayoutParams(params);
        container.addView(view,0);
        return viewLists.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewLists.get(position));

    }

}
