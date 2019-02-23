package com.example.mq661.govproject.SearchRoom;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mq661.govproject.AlterRoom.deleteroom;
import com.example.mq661.govproject.Login_Register.Login_noToken;
import com.example.mq661.govproject.tools.RoomMessage;
import com.example.mq661.govproject.tools.roomSortDBHelper;
import com.example.mq661.govproject.tools.saveDeviceInfo;
import com.example.mq661.govproject.tools.tokenDBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class searchroom_server_forSmartGroup extends AppCompatActivity {
    Button commit;
    Intent ssdata = new Intent();
    ArrayList<RoomMessage> RoomMessages, function, Size;
    //handler 处理返回的请求结果
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.i("mylog", "请求结果-->" + val);
        }
    };
    private List<roomAdapterInfo> data;
    private OkHttpClient okhttpClient;
    private tokenDBHelper helper;
    private roomSortDBHelper helper4;
    private String Token1;
    private String ssBuildingNumber, ssRoomNumber, ssTime, ssSize, ssFunction = "function", ssFunction1 = "function", ssFunction2 = "function", ssIsMeeting, ssDays, IsMeeting2 = "";
    private int j = 0;
    private ListView searchroomlv;
    private Context content;
    //新线程进行网络请求
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            data = new ArrayList<roomAdapterInfo>();
            Token1 = select();
            sendRequest(Token1);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", "请求结果");
            msg.setData(data);
            handler.sendMessage(msg);
        }


    };

    public Context getContent() {
        return content;
    }

    public void setContent(Context content) {
        this.content = content;
    }

    public void startGetAllroom() {
        //  deleteAllRoom();
        new Thread(runnable).start();  //启动子线程
        helper = new tokenDBHelper(content);
        helper4 = new roomSortDBHelper(content);
        RoomMessages = new ArrayList<RoomMessage>();
        function = new ArrayList<RoomMessage>();
        Size = new ArrayList<RoomMessage>();
        Toast.makeText(content, "刷新了", Toast.LENGTH_SHORT);

    }

    private void sendRequest(String Token1) {
        Map map = new HashMap();
        map.put("Token", Token1);

        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                //dafeng 192.168.2.176
                //  .url("http://192.168.2.176:8080/LoginProject/login")
                // .url("http://192.168.43.174:8080/LoginProject/login")
                // .url("http://39.96.68.13:8080/SmartRoom/RegistServlet") //服务器
                //  .url("http://192.168.43.174:8080/SmartRoom4/SelectServlet") //马琦IP
                .url("http://39.96.68.13:8080/SmartRoom/SearchServlet")
                // .url("http://192.168.2.176:8080/SmartRoom/login")
                .post(body)
                .build();
        Call call = okhttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(content, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                try {
                    JSONArray jsonArray = new JSONArray(res);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);

                        String BuildingNumber1 = jsonObj.getString("buildingNumber");
                        String RoomNumber1 = jsonObj.getString("roomNumber");
                        String Time1 = jsonObj.getString("time");
                        String Size1 = jsonObj.getString("size");
                        String Function1 = jsonObj.getString("functions");
                        String IsMeeting = jsonObj.getString("isMeeting");
                        Log.d("bb", "IsMeeting   " + IsMeeting);
                        if (IsMeeting.equals("0")) {
                            IsMeeting2 = "空闲";
                        } else if (IsMeeting.equals("1")) {
                            IsMeeting2 = "占用";
                        } else if (IsMeeting.equals("2")) {
                            IsMeeting2 = "维修";
                        } else {
                            IsMeeting2 = "未知";
                        }

                        String Days = jsonObj.getString("days");
                        String mapx = "map" + i;
                        if (BuildingNumber1.equals("-1") && RoomNumber1.equals("-1") && Time1.equals("-1")) {
                            showRequestResult(BuildingNumber1, RoomNumber1, Time1, Size1, Function1, IsMeeting2, Days, mapx);

                            break;
                        } else if (BuildingNumber1.equals("-3") && RoomNumber1.equals("-3") && Time1.equals("-3")) {
                            showRequestResult(BuildingNumber1, RoomNumber1, Time1, Size1, Function1, IsMeeting2, Days, mapx);

                            break;
                        } else
                            showRequestResult(BuildingNumber1, RoomNumber1, Time1, Size1, Function1, IsMeeting2, Days, mapx);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void showRequestResult(final String BuildNumber1, final String RoomNumber1, final String Time1, final String Size1, final String Function1, final String IsMeeting1, final String Days1, final String mapx) {
        // runOnUiThread(new Runnable() {
        //     @Override
        /**
         * 实时更新，数据库信息改变时，客户端内容发生改变
         */
        //    public void run() {

        if (BuildNumber1.equals("-1") && RoomNumber1.equals("-1") && Time1.equals("-1")) {
            Toast.makeText(content, "查询不成功！", Toast.LENGTH_SHORT).show();

            saveDeviceInfo.savelogin(getApplicationContext(), "0");
            // relog();
        } else if (BuildNumber1.equals("-3") && RoomNumber1.equals("-3") && Time1.equals("-3")) {
            Toast.makeText(content, "token失效！请重新登录", Toast.LENGTH_SHORT).show();
            delete(Token1);
            saveDeviceInfo.savelogin(getApplicationContext(), "0");
            relog();
        } else {
            //TODO 数据库操作
            Log.d("bb", "插入   " + IsMeeting1);
            insertAllRoom(BuildNumber1, RoomNumber1, Time1, Size1, Function1, IsMeeting1, Days1);
            insertFounctions(Function1);
            insertSize(Size1);
            insertDays(Days1);
            insertTime(Time1);
            insertBuildAndRoom(BuildNumber1, RoomNumber1);
        }
        //    }
        //  });
    }


    public void deleteroom() {
        Intent intent;
        intent = new Intent(content, deleteroom.class);
        startActivityForResult(intent, 0);

        // finish();
    }


    public void insert(String token) {


        //自定义增加数据
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //String token =mytoken.getMytoken();

        values.put("token", token);
        long l = db.insert("token", null, values);

        if (l == -1) {
            Toast.makeText(content, "插入不成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(content, "插入成功" + l, Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    public void update(String token) {


        //自定义更新
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("token", token);
        int i = db.update("token", values, null, null);
        if (i == 0) {
            Toast.makeText(content, "更新不成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(content, "更新成功" + i, Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    public void delete(String token) {

        SQLiteDatabase db = helper.getWritableDatabase();
        int i = db.delete("token", "token=?", new String[]{token});
        if (i == 0) {
            Toast.makeText(content, "删除不成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(content, "删除成功" + i, Toast.LENGTH_SHORT).show();
        }
        db.close();

    }

    //查找
    public String select() {

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from token", null);
        String token1 = null;
        while (cursor.moveToNext()) {
            token1 = cursor.getString(0);
        }
        db.close();
        return token1;
    }

    public void relog() {
        Intent intent;
        intent = new Intent(content, Login_noToken.class);
        startActivityForResult(intent, 0);
        finish();
    }

    public void insertAllRoom(String BuildingNumber, String RoomNumber, String Time, String Size, String Functions, String isMeeting, String Days) {


        //自定义增加数据
        SQLiteDatabase db = helper4.getWritableDatabase();
        ContentValues values = new ContentValues();
        // values.put("UUID", getUUID.getUUID32());
        values.put("BuildNumber", BuildingNumber);
        values.put("RoomNumber", RoomNumber);
        values.put("Time", Time);
        values.put("Days", Days);
        values.put("Functions", Functions);
        values.put("Size", Size);
        values.put("isMeeting", isMeeting);

        long l = db.replace("allroom", null, values);

        if (l == -1) {
            Log.d("aaa", "allroom 插入不成功");
        } else {
            Log.d("aaa", "allroom 插入成功" + l);
        }
        db.close();
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
            Log.d("ccc", "select 里的" + BuildNumber);
            RoomMessages.add(data);
        }
//        Log.d("ddd", "查出来的  楼号"+bookinfos.get(0).getBuildNumber()+"  房间号   "+bookinfos.get(0).getRoomNumber());
        db.close();
        return RoomMessages;
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

    /*
     *
     * 功能
     * */
    public void insertFounctions(String functions) {

        SQLiteDatabase db = helper4.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put("UUID",getUUID.getUUID32());
        values.put("Functions1", functions);
        long l = db.replace("Functions1", null, values);

        if (l == -1) {
            Log.d("aaa", "insertFounctions 插入不成功");
        } else {
            Log.d("aaa", "insertFounctions 插入成功");
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
    /*
     *
     * 容量
     * */

    public void insertSize(String Size) {

        SQLiteDatabase db = helper4.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Size", Size);
        long l = db.replace("Size", null, values);

        if (l == -1) {
            Log.d("ccc", "insertSize 插入不成功");
        } else {
            Log.d("ccc", "insertSize 插入成功");
        }
        db.close();
    }
    /*
     *
     * 日期
     * */

    public void insertDays(String Days) {

        SQLiteDatabase db = helper4.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Days", Days);
        long l = db.replace("Days", null, values);

        if (l == -1) {
            Log.d("fff", "insertDays 插入不成功");
        } else {
            Log.d("fff", "insertDays 插入成功");
        }
        db.close();
    }
    /*
     *
     * 日期
     * */

    public void insertTime(String Time1) {

        SQLiteDatabase db = helper4.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Time", Time1);
        long l = db.replace("Time", null, values);

        if (l == -1) {
            Log.d("ggg", "insertTime 插入不成功");
        } else {
            Log.d("ggg", "insertTime 插入成功");
        }
        db.close();
    }
    /*
     *
     * 房间号,楼号
     * */

    public void insertBuildAndRoom(String BuildNumber1, String RoomNumber1) {

        SQLiteDatabase db = helper4.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("BuildNumber", BuildNumber1);
        values.put("RoomNumber", RoomNumber1);

        long l = db.replace("BuildAndRoomNumber", null, values);

        if (l == -1) {
            Log.d("eee", "insertBuildAndRoom 插入不成功");
        } else {
            Log.d("eee", "insertBuildAndRoom 插入成功");
        }
        db.close();
    }

}