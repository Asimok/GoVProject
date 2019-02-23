package com.example.mq661.govproject.SmartGroup;


import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mq661.govproject.R;
import com.example.mq661.govproject.tools.RoomMessage;

import java.util.List;

public class MyExpandableListViewAdapter_Function extends BaseExpandableListAdapter {
    private Context mContext = null;
    private List<String> mGroupList = null;
    private List<List<RoomMessage>> mItemList = null;
    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            notifyDataSetChanged();//更新数据
            super.handleMessage(msg);
        }
    };

    public MyExpandableListViewAdapter_Function(Context context, List<String> groupList,
                                                List<List<RoomMessage>> itemList) {

        this.mContext = context;
        this.mGroupList = groupList;
        this.mItemList = itemList;

    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void refresh(ExpandableListView expandableListView, int groupPosition) {
        handler.sendMessage(new Message());
        //必须重新伸缩之后才能更新数据
        expandableListView.collapseGroup(groupPosition);
        expandableListView.expandGroup(groupPosition);
    }

    /**
     * 获取组的个数
     *
     * @return
     * @see android.widget.ExpandableListAdapter#getGroupCount()
     */
    @Override
    public int getGroupCount() {
        return mGroupList.size();
    }

    /**
     * 获取指定组中的子元素个数
     *
     * @param groupPosition
     * @return
     * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return mItemList.get(groupPosition).size();
    }

    /**
     * 获取指定组中的数据
     *
     * @param groupPosition
     * @return
     * @see android.widget.ExpandableListAdapter#getGroup(int)
     */
    @Override
    public String getGroup(int groupPosition) {
        return mGroupList.get(groupPosition);
    }

    /**
     * 获取指定组中的指定子元素数据。
     *
     * @param groupPosition
     * @param childPosition
     * @return
     * @see android.widget.ExpandableListAdapter#getChild(int, int)
     */
    @Override
    public String getChild(int groupPosition, int childPosition) {
        return mItemList.get(groupPosition).get(childPosition).toString();
    }

    /**
     * 获取指定组的ID，这个组ID必须是唯一的
     *
     * @param groupPosition
     * @return
     * @see android.widget.ExpandableListAdapter#getGroupId(int)
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * 获取指定组中的指定子元素ID
     *
     * @param groupPosition
     * @param childPosition
     * @return
     * @see android.widget.ExpandableListAdapter#getChildId(int, int)
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * 获取显示指定组的视图对象
     *
     * @param groupPosition 组位置
     * @param isExpanded    该组是展开状态还是伸缩状态
     * @param convertView   重用已有的视图对象
     * @param parent        返回的视图对象始终依附于的视图组
     * @return
     * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, View,
     * ViewGroup)
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        GroupHolder groupHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.expendlist_group, null);
            groupHolder = new GroupHolder();
            groupHolder.groupNameTv = convertView.findViewById(R.id.groupname_tv);
            groupHolder.groupImg = convertView.findViewById(R.id.group_img);
            groupHolder.sanjiao = convertView.findViewById(R.id.sanjiao);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }

        if (isExpanded) {
            groupHolder.sanjiao.setImageResource(R.drawable.sanjiao_click);
            groupHolder.groupImg.setImageResource(R.drawable.function3_click);
        } else {
            groupHolder.sanjiao.setImageResource(R.drawable.sanjiao);
            groupHolder.groupImg.setImageResource(R.drawable.function3);
        }
        groupHolder.groupNameTv.setText(mGroupList.get(groupPosition));

        return convertView;
    }

    /**
     * 获取一个视图对象，显示指定组中的指定子元素数据。
     *
     * @param groupPosition 组位置
     * @param childPosition 子元素位置
     * @param isLastChild   子元素是否处于组中的最后一个
     * @param convertView   重用已有的视图(View)对象
     * @param parent        返回的视图(View)对象始终依附于的视图组
     * @return
     * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, View,
     * ViewGroup)
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        ItemHolder itemHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.expendlist_item, null);
            itemHolder = new ItemHolder();
            itemHolder.bulidnumber = convertView.findViewById(R.id.bulidnumber);
            itemHolder.RoomNumber = convertView.findViewById(R.id.RoomNumber);
            itemHolder.Size = convertView.findViewById(R.id.Size);
            itemHolder.Function = convertView.findViewById(R.id.Function);
            itemHolder.Days = convertView.findViewById(R.id.Days3);
            itemHolder.IsMeeting = convertView.findViewById(R.id.IsMeeting);
            itemHolder.Time = convertView.findViewById(R.id.Time);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ItemHolder) convertView.getTag();
        }
        itemHolder.bulidnumber.setText(mItemList.get(groupPosition).get(childPosition).getBuildingNumber());
        itemHolder.RoomNumber.setText(mItemList.get(groupPosition).get(childPosition).getRoomNumber());
        itemHolder.Size.setText(mItemList.get(groupPosition).get(childPosition).getSize());
        itemHolder.Function.setText(mItemList.get(groupPosition).get(childPosition).getFunction());
        itemHolder.Days.setText(mItemList.get(groupPosition).get(childPosition).getDays());
        itemHolder.IsMeeting.setText(mItemList.get(groupPosition).get(childPosition).getIsMeeting());
        itemHolder.Time.setText(mItemList.get(groupPosition).get(childPosition).getTime());

        return convertView;
    }

    /**
     * 组和子元素是否持有稳定的ID,也就是底层数据的改变不会影响到它们。
     *
     * @return
     * @see android.widget.ExpandableListAdapter#hasStableIds()
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * 是否选中指定位置上的子元素。
     *
     * @param groupPosition
     * @param childPosition
     * @return
     * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    class GroupHolder {
        public TextView groupNameTv;
        public ImageView groupImg;
        public ImageView sanjiao;
    }

    class ItemHolder {
        public ImageView iconImg;
        public TextView bulidnumber, RoomNumber, Size, Function, IsMeeting, Time, Days;
    }
}
