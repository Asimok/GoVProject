package com.example.mq661.govproject.Login_Register;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mq661.govproject.R;
import com.example.mq661.govproject.tools.MyNotification;
import com.example.mq661.govproject.tools.bookinfo;
import com.example.mq661.govproject.tools.bookinfoDBHelper;
import com.example.mq661.govproject.tools.meetingInfoDBHelper;
import com.example.mq661.govproject.tools.registInfoDBHelper;
import com.example.mq661.govproject.tools.saveDeviceInfo;
import com.example.mq661.govproject.tools.tokenDBHelper;
import com.example.mq661.govproject.tools.userDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class mine extends AppCompatActivity implements View.OnClickListener {
    Button logout;
    bookinfo mybook = new bookinfo();
    TextView tvzhanghu, tvname, bookinfo, chnum, ydnum;
    ArrayList<bookinfo> bookinfos, meetingInfos;
    Map<String, String> countlogin;
    LinearLayout linear, linear2;
    String room[];
    String room1;
    private OkHttpClient okhttpClient;
    private userDBHelper helper1;
    private registInfoDBHelper helper5;
    private tokenDBHelper helper;
    private bookinfoDBHelper helper3;
    private String Token, zhanghu, name;
    private ListView bookinfo2;
    private meetingInfoDBHelper helper4;
    private ArrayList<bookroomInfoAdapter> data, data2;
    private List<bookinfo> data11, data41;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wty_logout_layout);
        helper = new tokenDBHelper(this);
        helper1 = new userDBHelper(this);
        helper3 = new bookinfoDBHelper(this);
        helper4 = new meetingInfoDBHelper(this);
        helper5 = new registInfoDBHelper(this);
        bookinfos = new ArrayList<bookinfo>();
        meetingInfos = new ArrayList<bookinfo>();
        linear = findViewById(R.id.linear3);
        linear2 = findViewById(R.id.linear4);
        data = new ArrayList<bookroomInfoAdapter>();
        data2 = new ArrayList<bookroomInfoAdapter>();

        initView();
        initdata();
    }

    private void initView() {

        logout = findViewById(R.id.logout);
        tvname = findViewById(R.id.name);
        bookinfo = findViewById(R.id.bookinfo);
        tvzhanghu = findViewById(R.id.zhanghu);
        chnum = findViewById(R.id.chnum);
        ydnum = findViewById(R.id.ydnum);
        logout.setOnClickListener(this);
        Toast.makeText(this, "已刷新", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void initdata() {
        searchRegistServer serg = new searchRegistServer();
        serg.setContent(mine.this);
        serg.startsearchRegist(select());

        zhanghu = userselect()[0];
        name = userselect()[1];
        tvzhanghu.setText(zhanghu);
        tvname.setText(name);

        //填充预定信息
        bookinfoServer bookinfo1 = new bookinfoServer();
        bookinfo1.setContent(mine.this);
        Log.d("ddd", "获取预定信息");
        data = bookinfo1.startGetInfo();


        List<bookinfo> data6 = bookselect();
        data11 = data6;
        Log.d("ccc", "data11 size     " + data11.size());
        ydnum.setText(data11.size() + "  条");
        for (int i = 0; i < data11.size(); i++) {
            Log.d("ccc", i + data11.get(i).getBuildNumber());
            if (data11.get(i).getBuildNumber().equals("-3")) {
                Toast.makeText(mine.this, "token失效！返回重新登陆", Toast.LENGTH_SHORT).show();
                delete(Token);
                saveDeviceInfo.savelogin(getApplicationContext(), "0");
                relog();
                break;
            }
            TextView tv = new TextView(this);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            tv.setTextColor(Color.rgb(12, 35, 80));
            tv.setText(data11.get(i).getBuildNumber() + "   " + data11.get(i).getRoomNumber() + "\n预约日期:    " + data11.get(i).getDays() + "  " + data11.get(i).getTime() + "\n预定时间:    " + data11.get(i).getBooktime() + "\n");
            linear.addView(tv);

        }

        bookdelete();

        //填充参会信息
        meetingInfoServer bookinfo2 = new meetingInfoServer();
        bookinfo2.setContent(mine.this);

        data2 = bookinfo2.startGetMeetingInfo();


        List<bookinfo> data4 = meetingselect();
        data41 = data4;
        chnum.setText(data41.size() + "  条");
        for (int i = 0; i < data4.size(); i++) {

            if (data4.get(i).getBuildNumber().equals("-3")) {
                Toast.makeText(mine.this, "token失效！返回重新登陆", Toast.LENGTH_SHORT).show();
                delete(Token);
                saveDeviceInfo.savelogin(getApplicationContext(), "0");
                relog();
                break;
            }
            TextView tv = new TextView(this);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            tv.setTextColor(Color.rgb(12, 35, 80));
            tv.setText(data4.get(i).getBuildNumber() + "   " + data4.get(i).getRoomNumber() + "\n参会日期:    " + data4.get(i).getDays() + "\n时间段:        " + data4.get(i).getTime() + "\n");
            linear2.addView(tv);

        }

        bookdeleteMeeting();


    }

    @Override
    public void onClick(View v) {
        Token = select();
        //  Toast.makeText(mine.this, "查出来的" + Token, Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRequest(Token);
            }
        }).start();
    }

    private void sendRequest(String Token) {
        Map map = new HashMap();
        map.put("Token", Token);

        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
//        Log.d("这将JSON对象转换为json字符串", jsonString);
        RequestBody body = RequestBody.create(null, jsonString);  //以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()

                .url("http://39.96.68.13:8080/SmartRoom/LogoutServlet")//MQ

                .post(body)
                .build();
        okhttp3.Call call = okhttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mine.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                try {
                    JSONObject jsonObj = new JSONObject(res);
                    String status = jsonObj.getString("status");

                    showRequestResult(status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void showRequestResult(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {
                if (status.equals("-3")) {
                    Toast.makeText(mine.this, "token失效！返回重新登陆", Toast.LENGTH_SHORT).show();
                    delete(Token);
                    saveDeviceInfo.savelogin(getApplicationContext(), "0");
                    relog();
                } else if (status.equals("quit")) {
                    MyNotification notify = new MyNotification(getApplicationContext());
                    notify.MyNotification("智能会议室", "注销成功", R.drawable.logout, "mine", "注销", 9, "注销");
                    delete(Token);
                    deletepersoninfo();
                    String zhanghao = userselect()[0];
                    Log.d("ddd", "要删除的账户   " + zhanghao);
                    userdelete(zhanghao);
                    saveDeviceInfo.savelogin(getApplicationContext(), "0");
                    relog();
                    Toast.makeText(mine.this, "注销成功！", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void meetingInfo(View view) {

        showMultiBtnDialogMeeting(data41);
    }

    public void bookInfo(View view) {
        showMultiBtnDialogBook(data11);
    }


    public void insert(String token) {


        //自定义增加数据
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //String token =mytoken.getMytoken();

        values.put("token", token);
        long l = db.insert("token", null, values);


        db.close();
    }

    public void update(String token) {


        //自定义更新
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //     String oldtoken=mytoken.getMytoken();
        values.put("token", token);
//        int i = db.update("token", values, "token=?",new String[]{oldtoken});
        int i = db.update("token", values, null, null);
        if (i == 0) {
            Toast.makeText(this, "更新不成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "更新成功" + i, Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    public void delete(String token) {

        SQLiteDatabase db = helper.getWritableDatabase();


        int i = db.delete("token", "token=?", new String[]{token});
//        if (i == 0) {
//            Toast.makeText(this, "删除不成功", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "删除成功" + i, Toast.LENGTH_SHORT).show();
//        }
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

    public String[] userselect() {

        SQLiteDatabase db = helper1.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from user", null);
        String zhanghao = null;
        String name = null;
        while (cursor.moveToNext()) {
            zhanghao = cursor.getString(cursor.getColumnIndex("zhanghao"));
            name = cursor.getString(cursor.getColumnIndex("name"));
        }
        String[] user = {zhanghao, name};
        Log.d("ddd", "查出来的  账户" + user[0] + "  姓名   " + user[1]);
        db.close();
        return user;

    }

    public void userdelete(String zhanghu3) {

        SQLiteDatabase db = helper1.getWritableDatabase();
        int i = db.delete("user", "zhanghao=?", new String[]{zhanghu3});
        if (i == 0) {
//            Toast.makeText(this, "删除用户信息不成功",Toast.LENGTH_SHORT).show();
//        }else{  Toast.makeText(this, "删除用户信息成功",Toast.LENGTH_SHORT).show();
        }
        db.close();

    }

    public void bookdelete() {

        SQLiteDatabase db = helper3.getWritableDatabase();
        int i = db.delete("bookinfo", "zhanghu=?", new String[]{userselect()[0]});
        if (i == 0) {
        }
        db.close();

    }

    public void bookdeleteMeeting() {

        SQLiteDatabase db = helper4.getWritableDatabase();
        int i = db.delete("meetinginfo", "zhanghu=?", new String[]{userselect()[0]});
        if (i == 0) {
        }
        db.close();

    }

    public List<bookinfo> bookselect() {
        SQLiteDatabase db = helper3.getReadableDatabase();
        String zhanghu = userselect()[0];
        Cursor cursor = db.rawQuery("select * from bookinfo where zhanghu=?", new String[]{zhanghu});
        String BuildNumber = null;
        String RoomNumber = null;
        String Time = null;
        String days = null;
        String booktime = null;
        while (cursor.moveToNext()) {
            bookinfo data = new bookinfo();
            BuildNumber = cursor.getString(cursor.getColumnIndex("BuildNumber"));
            RoomNumber = cursor.getString(cursor.getColumnIndex("RoomNumber"));
            Time = cursor.getString(cursor.getColumnIndex("Time"));
            days = cursor.getString(cursor.getColumnIndex("days"));
            booktime = cursor.getString(cursor.getColumnIndex("booktime"));
            data.setDays(days);
            data.setTime(Time);
            data.setRoomNumber(RoomNumber);
            data.setBuildNumber(BuildNumber);
            data.setBooktime(booktime);
            Log.d("ccc", "select 里的" + BuildNumber);
            bookinfos.add(data);
        }

        db.close();
        return bookinfos;
    }

    public List<bookinfo> meetingselect() {
        SQLiteDatabase db = helper4.getReadableDatabase();
        String zhanghu = userselect()[0];
        Cursor cursor = db.rawQuery("select * from meetinginfo where zhanghu=?", new String[]{zhanghu});
        String BuildNumber = null;
        String RoomNumber = null;
        String Time = null;
        String days = null;
        while (cursor.moveToNext()) {
            bookinfo data = new bookinfo();
            BuildNumber = cursor.getString(cursor.getColumnIndex("BuildNumber"));
            RoomNumber = cursor.getString(cursor.getColumnIndex("RoomNumber"));
            Time = cursor.getString(cursor.getColumnIndex("Time"));
            days = cursor.getString(cursor.getColumnIndex("days"));

            data.setDays(days);
            data.setTime(Time);
            data.setRoomNumber(RoomNumber);
            data.setBuildNumber(BuildNumber);
            meetingInfos.add(data);
        }

        db.close();
        return meetingInfos;
    }

    public void relog() {
        Intent intent;
        intent = new Intent(this, Login_noToken.class);
        startActivityForResult(intent, 0);
        finish();
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

    public void showMultiBtnDialogBook(List<bookinfo> data) {
        String books2 = "";
        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mine.this);
        normalDialog.setIcon(R.drawable.app);
        normalDialog.setTitle("预定信息");
        for (int i = 0; i < data.size(); i++) {
            books2 = books2 + data.get(i).getBuildNumber() + "   " + data.get(i).getRoomNumber() + "\n预约日期:    " + data.get(i).getDays() + "  " + data.get(i).getTime() + "\n预定时间:    " + data.get(i).getBooktime() + "\n" + "\n";
        }
        normalDialog.setMessage(books2);
        normalDialog.setPositiveButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        normalDialog.show();
    }

    public void showMultiBtnDialogMeeting(List<bookinfo> data4) {
        String books2 = "";
        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mine.this);
        normalDialog.setIcon(R.drawable.app);
        normalDialog.setTitle("参会信息");
        for (int i = 0; i < data4.size(); i++) {
            books2 = books2 + data4.get(i).getBuildNumber() + "   " + data4.get(i).getRoomNumber() + "\n参会日期:    " + data4.get(i).getDays() + "  " + data4.get(i).getTime() + "\n" + "\n";
        }
        normalDialog.setMessage(books2);
        normalDialog.setPositiveButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        normalDialog.show();
    }

    public void reRegist(View view) {
        Intent intent = new Intent(this, ReRegist.class);
        startActivity(intent);
    }

    public void deletepersoninfo() {

        SQLiteDatabase db = helper5.getWritableDatabase();
        int i = db.delete("registinfo", null, null);
        if (i == 0) {
            Log.d("aaaa", "deletepersoninfo  删除不成功");
        } else {
            Log.d("aaaa", "deletepersoninfo  删除成功");
        }
        db.close();
    }

    public void about(View view) {
        Intent intent = new Intent(this, about.class);
        startActivity(intent);
    }
}

