package com.example.mq661.govproject.MainInterface;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * @创建者 鑫鱻
 * @描述 Android零基础入门到精通系列教程
 * 首发微信公众号分享达人秀（ShareExpert）
 */
public class ViewPagerAdapter extends PagerAdapter {
    private ArrayList<View> mPageList = null;

    public ViewPagerAdapter(ArrayList<View> pageList) {
        this.mPageList = pageList;
    }

    @Override
    public int getCount() {
        return mPageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View pageView = mPageList.get(position);
        ViewGroup parent = (ViewGroup) pageView.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        container.addView(mPageList.get(position));

        return pageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 将当前位置的View移除
        container.removeView(mPageList.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


}
