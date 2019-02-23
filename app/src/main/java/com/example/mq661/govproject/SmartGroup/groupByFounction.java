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
import com.example.mq661.govproject.tools.roomSortDBHelper;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class groupByFounction extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    ArrayList<RoomMessage> function, RoomMessages;
    private ExpandableListView mExpandableListView = null;
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private ZLoadingDialog dialog;
    // 列表数据
    private List<String> mGroupNameList = null;
    private List<List<RoomMessage>> mItemNameList = null;
    // 适配器
    private MyExpandableListViewAdapter_Function mAdapter = null;
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
        function = new ArrayList<RoomMessage>();
        RoomMessages = new ArrayList<RoomMessage>();

        // 获取组件
        mExpandableListView = findViewById(R.id.expendlist);
        mExpandableListView.setGroupIndicator(null);


        // 初始化数据
        initData();

        // 为ExpandableListView设置Adapter
        mAdapter = new MyExpandableListViewAdapter_Function(this, mGroupNameList, mItemNameList);
        mExpandableListView.setAdapter(mAdapter);

        // 监听组点击
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // mAdapter.refresh(mExpandableListView, groupPosition);
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
                    normalDialog.setIcon(R.drawable.app);
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
                                ZLoadingDialog dialog2 = new ZLoadingDialog(groupByFounction.this);
                                dialog2.setLoadingBuilder(Z_TYPE.PAC_MAN)//设置类型
                                        .setLoadingColor(Color.RED)//颜色
                                        .setHintTextSize(16)
                                        .setHintText("此会议室该时段不可用，无法预约")
                                        .show();
                            } else if (IsMeeting.equals("维修")) {
                                ZLoadingDialog dialog2 = new ZLoadingDialog(groupByFounction.this);
                                dialog2.setLoadingBuilder(Z_TYPE.ELASTIC_BALL)//设置类型
                                        .setLoadingColor(Color.RED)//颜色
                                        .setHintTextSize(16)
                                        .setHintText("非常抱歉，该会议室正在维修！")
                                        .show();
                            } else {
                                Intent intent = new Intent(groupByFounction.this, addPerson_handler.class);
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
        //TODO
        //修改
//        searchroom_server_forSmartGroup getAllRoom = new searchroom_server_forSmartGroup();
//        getAllRoom.setContent(this);
//        getAllRoom.startGetAllroom();
        // 组名
        mGroupNameList = new ArrayList<String>();
        List<RoomMessage> data1 = selectFunctions();
        mItemNameList = new ArrayList<List<RoomMessage>>();
        List<RoomMessage> itemList = new ArrayList<RoomMessage>();
        for (int i = 0; i < data1.size(); i++) {
            mGroupNameList.add(data1.get(i).getFunction());
            //Log.d("aaa", "1111111111selectFunctions   查出来的功能   "+i + data1.get(i).getFunction());
            Log.d("ccc", String.valueOf(data1.size()));
        }
        for (int i = 0; i < data1.size(); i++) {
            Log.d("ccc", "第几次   " + i);
            String function = data1.get(i).getFunction();
            Log.d("ccc", "第几次   " + function);
            itemList = selectAllRoomByFounction(function);
            mItemNameList.add(itemList);
            // itemList.clear();
        }
//        deleteFunctions();
        //  deleteAllRoom();
    }

    public ArrayList<RoomMessage> selectFunctions() {
        SQLiteDatabase db = helper4.getReadableDatabase();
        Cursor cursor = db.rawQuery("select distinct * from Functions1 ", null);
        String Functions = null;
        while (cursor.moveToNext()) {
            RoomMessage data1 = new RoomMessage();
            Functions = cursor.getString(cursor.getColumnIndex("Functions1"));
            data1.setFunction(Functions);
            Log.d("aaa", Functions);
            function.add(data1);
        }
        db.close();
        return function;
    }

    public ArrayList<RoomMessage> selectAllRoom() {
        SQLiteDatabase db = helper4.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from allroom ", null);
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
            //  Log.d("ccc", "select 里的"+BuildNumber);
            RoomMessages.add(data);
        }
        db.close();
        return RoomMessages;
    }

    public ArrayList<RoomMessage> selectAllRoomByFounction(String founction) {
        SQLiteDatabase db = helper4.getReadableDatabase();
        ArrayList<RoomMessage> RoomMessages1 = new ArrayList<RoomMessage>();
        Cursor cursor = db.rawQuery("select * from allroom where Functions=?", new String[]{founction});
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
            Log.d("ccc", "select1 里的" + Functions);
            RoomMessages1.add(data);
        }
        db.close();
        return RoomMessages1;
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
        dialog.setLoadingBuilder(Z_TYPE.LEAF_ROTATE)//设置类型
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
