package com.example.mq661.govproject.SmartGroup;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.example.mq661.govproject.Participants.addPerson_handler;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.SearchRoom.searchroom_server_forSmartGroup;
import com.example.mq661.govproject.tools.RoomMessage;
import com.example.mq661.govproject.tools.dateToString;
import com.example.mq661.govproject.tools.roomSortDBHelper;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class groupBySize extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    ArrayList<RoomMessage> Sizes, RoomMessages;
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private ExpandableListView mExpandableListView = null;
    private ZLoadingDialog dialog;
    // 列表数据
    private List<String> mGroupNameList = null;
    private List<List<RoomMessage>> mItemNameList = null;
    // 适配器
    private MyExpandableListViewAdapter_size mAdapter = null;
    private roomSortDBHelper helper4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandlist_layout_swiperefreshlayout);
        mSwipeRefreshLayout = findViewById(R.id.container_swipe);
        // 设置刷新时动画的颜色，可以设置4个
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        // 设置下拉监听事件
        mSwipeRefreshLayout.setOnRefreshListener(this);
        helper4 = new roomSortDBHelper(this);
        Sizes = new ArrayList<RoomMessage>();
        RoomMessages = new ArrayList<RoomMessage>();

        // 获取组件
        mExpandableListView = findViewById(R.id.expendlist);
        mExpandableListView.setGroupIndicator(null);

        // 初始化数据
        initData();

        // 为ExpandableListView设置Adapter
        mAdapter = new MyExpandableListViewAdapter_size(this, mGroupNameList, mItemNameList);
        mExpandableListView.setAdapter(mAdapter);

        // 监听组点击
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return mGroupNameList.get(groupPosition).isEmpty();
            }
        });

        // 监听每个分组里子控件的点击事件
        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                return false;
            }
        });
        mExpandableListView.setOnItemLongClickListener(new ExpandableListView.OnItemLongClickListener() {


            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final long packedPosition = mExpandableListView.getExpandableListPosition(position);
                final int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
                final int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);
                //长按的是group的时候，childPosition = -1
                if (childPosition != -1) {

                    final String BuildingNumber = mItemNameList.get(groupPosition).get(childPosition).getBuildingNumber();
                    final String RoomNumber = mItemNameList.get(groupPosition).get(childPosition).getRoomNumber();
                    final String Days = mItemNameList.get(groupPosition).get(childPosition).getDays();
                    final String Time = mItemNameList.get(groupPosition).get(childPosition).getTime();
                    String Function = mItemNameList.get(groupPosition).get(childPosition).getFunction();
                    final String IsMeeting = mItemNameList.get(groupPosition).get(childPosition).getIsMeeting();
                    String Size = mItemNameList.get(groupPosition).get(childPosition).getSize();

                    android.support.v7.app.AlertDialog.Builder normalDialog =
                            new android.support.v7.app.AlertDialog.Builder(getParent());
                    normalDialog.setIcon(R.drawable.size);
                    normalDialog.setTitle("房间信息").setMessage("房间信息：\n" + "楼号：" + BuildingNumber + " 房间号：" + RoomNumber + " 容量：" + Size + "\n时间段：" + Time + "    功能：" + Function + "\n是否开会：" + IsMeeting
                            + "       日期： " + Days
                    );

                    normalDialog.setPositiveButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    normalDialog.setNegativeButton("预约", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (IsMeeting.equals("占用")) {
                                ZLoadingDialog dialog2 = new ZLoadingDialog(groupBySize.this);
                                dialog2.setLoadingBuilder(Z_TYPE.PAC_MAN)//设置类型
                                        .setLoadingColor(Color.RED)//颜色
                                        .setHintTextSize(16)
                                        .setHintText("此会议室该时段不可用，无法预约")
                                        .show();
                            } else if (IsMeeting.equals("维修")) {
                                ZLoadingDialog dialog2 = new ZLoadingDialog(groupBySize.this);
                                dialog2.setLoadingBuilder(Z_TYPE.ELASTIC_BALL)//设置类型
                                        .setLoadingColor(Color.RED)//颜色
                                        .setHintTextSize(16)
                                        .setHintText("非常抱歉，该会议室正在维修！")
                                        .show();
                            } else if (Integer.parseInt(Time.substring(0, 2)) <= Integer.parseInt(dateToString.nowdateToString3()) && Integer.parseInt(dateToString.nowdateToString4()) == Integer.parseInt(Days.substring(8, 10))) {

                                ZLoadingDialog dialog2 = new ZLoadingDialog(getParent());
                                dialog2.setLoadingBuilder(Z_TYPE.SNAKE_CIRCLE)//设置类型
                                        .setLoadingColor(Color.RED)//颜色
                                        .setHintTextSize(16)
                                        .setHintText("请预约 " + dateToString.nowdateToString4() + "日 " + dateToString.nowdateToString3() + "点 后的房间")
                                        .show();

                            } else {
                                Intent intent = new Intent(groupBySize.this, addPerson_handler.class);
                                intent.putExtra("BuildingNumber", BuildingNumber);
                                intent.putExtra("RoomNumber", RoomNumber);
                                intent.putExtra("Days", Days);
                                intent.putExtra("Time", Time);
                                startActivity(intent);
                            }
                        }
                    });

                    // 创建实例并显示
                    normalDialog.show();

                }
                return true;
            }
        });
    }

    // 初始化数据
    private void initData() {
        // 组名
        mGroupNameList = new ArrayList<String>();
        List<RoomMessage> data1 = selectSize();
        mItemNameList = new ArrayList<List<RoomMessage>>();
        List<RoomMessage> itemList = new ArrayList<RoomMessage>();
        for (int i = 0; i < data1.size(); i++) {
            mGroupNameList.add(data1.get(i).getSize());
            Log.d("ee1", "selectSize   查出来的SIZE   " + data1.get(i).getSize());
        }
        for (int i = 0; i < data1.size(); i++) {
            Log.d("ddd", "第几次   " + i);
            String Sizes1 = data1.get(i).getSize();
            Log.d("ddd", "第几次   " + Sizes1);
            itemList = selectAllRoomBySizes(Sizes1);
            Log.d("ddd", "执行  selectAllRoomBySizes  " + Sizes1);
            mItemNameList.add(itemList);
            // itemList.clear();
        }
        // deleteAllRoom();
//        deleteSizes();
    }

    public ArrayList<RoomMessage> selectSize() {
        SQLiteDatabase db = helper4.getReadableDatabase();
        Cursor cursor = db.rawQuery("select distinct * from Size ", null);
        String Sizes1 = null;
        while (cursor.moveToNext()) {

            RoomMessage data1 = new RoomMessage();
            Sizes1 = cursor.getString(cursor.getColumnIndex("Size"));
            data1.setSize(Sizes1);
            Log.d("ccc", Sizes1);
            Sizes.add(data1);

        }
        db.close();
        Collections.sort(Sizes, new Comparator<RoomMessage>() {
            public int compare(RoomMessage o1, RoomMessage o2) {
                return o1.getSize().compareTo(o2.getSize());
            }
        });
        return Sizes;
    }

    public ArrayList<RoomMessage> selectAllRoomBySizes(String Sizes) {
        Log.d("ddd", "selectAllRoomBySizes jinru");
        SQLiteDatabase db = helper4.getReadableDatabase();
        ArrayList<RoomMessage> RoomMessages2 = new ArrayList<RoomMessage>();
        Cursor cursor = db.rawQuery("select * from allroom where Size =?", new String[]{Sizes});
        String BuildNumber = null;
        String RoomNumber = null;
        String Time = null;
        String days = null;
        String Functions = null;
        String Size = null;
        String isMeeting;
        while (cursor.moveToNext()) {
            RoomMessage data = new RoomMessage();
            BuildNumber = cursor.getString(cursor.getColumnIndex("BuildNumber"));
            RoomNumber = cursor.getString(cursor.getColumnIndex("RoomNumber"));
            Time = cursor.getString(cursor.getColumnIndex("Time"));
            days = cursor.getString(cursor.getColumnIndex("Days"));
            Functions = cursor.getString(cursor.getColumnIndex("Functions"));
            Size = cursor.getString(cursor.getColumnIndex("Size"));
            isMeeting = cursor.getString(cursor.getColumnIndex("isMeeting"));
            data.setDays(days);
            data.setTime(Time);
            data.setRoomNumber(RoomNumber);
            data.setBuildingNumber(BuildNumber);
            data.setSize(Size);
            data.setFunction(Functions);
            data.setIsMeeting(isMeeting);
            RoomMessages2.add(data);
        }
        db.close();
        return RoomMessages2;
    }

    public void deleteSizes() {

        SQLiteDatabase db = helper4.getWritableDatabase();
        int i = db.delete("Size", null, null);
        if (i == 0) {
            Log.d("ccc", "deleteSizes  删除不成功");
        } else {
            Log.d("ccc", "deleteSizes  删除成功");
        }
        db.close();
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

    public void deleteBuildAndRoom() {

        SQLiteDatabase db = helper4.getWritableDatabase();
        int i = db.delete("BuildAndRoomNumber", null, null);
        if (i == 0) {
            Log.d("fff", "deleteBuildAndRoom  删除不成功");
        } else {
            Log.d("fff", "deleteBuildAndRoom  删除成功");
        }
        db.close();
    }

    public void deleteDays() {

        SQLiteDatabase db = helper4.getWritableDatabase();
        int i = db.delete("Days", null, null);
        if (i == 0) {
            Log.d("fff", "deleteDays  删除不成功");
        } else {
            Log.d("fff", "deleteDays  删除成功");
        }
        db.close();
    }

    public void deleteFunctions() {

        SQLiteDatabase db = helper4.getWritableDatabase();
        int i = db.delete("Functions1", null, null);
        if (i == 0) {
            Log.d("aaa", "deleteFunctions  删除不成功");
        } else {
            Log.d("aaa", "deleteFunctions  删除成功");
        }
        db.close();
    }

    public void deleteTimes() {

        SQLiteDatabase db = helper4.getWritableDatabase();
        int i = db.delete("Time", null, null);
        if (i == 0) {
            Log.d("ggg", "deleteTimes  删除不成功");
        } else {
            Log.d("ggg", "deleteTimes  删除成功");
        }
        db.close();
    }

    public void refresh(View view) {
        new MyThread().start();

        Timer timer = new Timer();//初始化一个时间
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialog.cancel();
                //这里填入时间结束后要进行的逻辑
            }

        }, 2200);

        deleteAllRoom();
        deleteSizes();
        deleteDays();
        deleteFunctions();
        deleteTimes();
        deleteBuildAndRoom();
        searchroom_server_forSmartGroup getAllRoom = new searchroom_server_forSmartGroup();
        getAllRoom.setContent(this);
        getAllRoom.startGetAllroom();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        new MyThread().start();

        Timer timer = new Timer();//初始化一个时间
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialog.cancel();
                //这里填入时间结束后要进行的逻辑
            }

        }, 2200);

        deleteAllRoom();
        deleteSizes();
        deleteDays();
        deleteFunctions();
        deleteTimes();
        deleteBuildAndRoom();
        searchroom_server_forSmartGroup getAllRoom = new searchroom_server_forSmartGroup();
        getAllRoom.setContent(this);
        getAllRoom.startGetAllroom();
    }

    public void onEnterAnimationComplete() {
        dialog = new ZLoadingDialog(getParent());
        dialog.setLoadingBuilder(Z_TYPE.ELASTIC_BALL)//设置类型
                .setLoadingColor(Color.RED)//颜色
                .setHintText("加载中...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                .setDurationTime(1) // 设置动画时间百分比 - 0.5倍
                .setDialogBackgroundColor(Color.parseColor("#ffffffff")) // 设置背景色，默认白色
                .setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public class MyThread extends Thread {

        //继承Thread类，并改写其run方法
        @Override
        public void run() {
            Log.d("vvv", "进入线程");
            Looper.prepare();
            onEnterAnimationComplete();
            Looper.loop();
        }
    }

}
