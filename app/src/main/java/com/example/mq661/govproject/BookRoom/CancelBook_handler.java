package com.example.mq661.govproject.BookRoom;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mq661.govproject.Login_Register.Login_noToken;
import com.example.mq661.govproject.Login_Register.bookroomInfoAdapter;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.tools.dateToString;
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

public class CancelBook_handler extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    Button commit;
    Intent ssdata = new Intent();
    //handler 处理返回的请求结果
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private List<bookroomInfoAdapter> data;
    private OkHttpClient okhttpClient;
    private tokenDBHelper helper;
    private String Token1;
    private String ssBuildingNumber, ssRoomNumber, ssTime, ssDays, ssNowTime;
    private ListView searchroomlv;
    //新线程进行网络请求
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            data = new ArrayList<bookroomInfoAdapter>();
            Token1 = select();
            sendRequest(Token1);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", "请求结果");
            msg.setData(data);
            handler.sendMessage(msg);
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchroom_lv_layout_forcancel);
        new Thread(runnable).start();  //启动子线程
        helper = new tokenDBHelper(this);
        initView();
    }

    private void initView() {

        searchroomlv = findViewById(R.id.searchroomlv);
        searchroomlv.setOnItemClickListener(this);       //设置短按事件
        searchroomlv.setOnItemLongClickListener(this);   //设置长按事件


    }

    private void sendRequest(String Token1) {
        Map map = new HashMap();
        map.put("Token", Token1);

        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://39.96.68.13:8080/SmartRoom/BookMessageServlet")
                .post(body)
                .build();
        Call call = okhttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CancelBook_handler.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
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
                        String nowTime = jsonObj.getString("nowTime");
                        String Days = jsonObj.getString("days");
                        String mapx = "map" + i;

                        int hh = Integer.parseInt(dateToString.nowdateToString3());
                        int thishh = Integer.parseInt(Time1.substring(0, 2));
                        int day = Integer.parseInt(dateToString.nowdateToString4());
                        int thisday = Integer.parseInt(Days.substring(8, 10));
                        Log.d("abb", day + "    " + thisday);
                        Log.d("abb", hh + "    " + thishh);
                        if (day > thisday) {
                            continue;
                        } else if (hh >= thishh && day == thisday) {
                            continue;
                        } else {
                            showRequestResult(BuildingNumber1, RoomNumber1, Time1, nowTime, Days, mapx);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void showRequestResult(final String BuildNumber1, final String RoomNumber1, final String Time1, final String nowTime, final String Days, final String mapx) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {

                if (BuildNumber1.equals("-1") && RoomNumber1.equals("-1") && Time1.equals("-1")) {
                    Toast.makeText(CancelBook_handler.this, "查询不成功！", Toast.LENGTH_SHORT).show();
                    //delete(Token1);
                    saveDeviceInfo.savelogin(getApplicationContext(), "0");
                    // relog();
                } else if (BuildNumber1.equals("-3") && RoomNumber1.equals("-3") && Time1.equals("-3")) {
                    Toast.makeText(CancelBook_handler.this, " 认证信息失效，请重新登录", Toast.LENGTH_SHORT).show();
                    delete(Token1);
                    saveDeviceInfo.savelogin(getApplicationContext(), "0");
                    relog();
                } else {
                    bookroomInfoAdapter mapx = new bookroomInfoAdapter();
                    mapx.setBuildingNumber(BuildNumber1);
                    mapx.setRoomNumber(RoomNumber1);
                    mapx.setTime(Time1);
                    mapx.setDays(Days);
                    mapx.setNowTime(nowTime);
                    data.add(mapx);

                    searchroomlv.setAdapter(new CancelBook_handler.MyAdapter());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        ssBuildingNumber = data.get(position).getBuildingNumber();
        ssRoomNumber = data.get(position).getRoomNumber();
        ssTime = data.get(position).getTime();
        ssNowTime = data.get(position).getNowTime();
        ssDays = data.get(position).getDays();
        showMultiBtnDialog(ssBuildingNumber, ssRoomNumber, ssTime, ssNowTime, ssDays);
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ssBuildingNumber = data.get(position).getBuildingNumber();
        ssRoomNumber = data.get(position).getRoomNumber();
        ssTime = data.get(position).getTime();
        ssNowTime = data.get(position).getNowTime();
        ssDays = data.get(position).getDays();
        Toast.makeText(this, "长按显示"
                , Toast.LENGTH_LONG).show();
        showMultiBtnDialog(ssBuildingNumber, ssRoomNumber, ssTime, ssNowTime, ssDays);
        return true;      //返回true时可以解除长按与短按的冲突。


    }

    /* @setNeutralButton 设置中间的按钮
     * 若只需一个按钮，仅设置 setPositiveButton 即可
     */
    public void showMultiBtnDialog(final String BuildingNumber, final String RoomNumber,
                                   final String Time, final String ssNowTime, final String Days) {


        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(CancelBook_handler.this);
        normalDialog.setIcon(R.drawable.cancel_book);
        normalDialog.setTitle("GoV").setMessage("房间信息：\n" + "楼    号：" + BuildingNumber + "      房间号：" + RoomNumber + "\n日    期： " + Days + "\n时间段：  " + Time + "\n预定时间：" + ssNowTime

        );

        normalDialog.setPositiveButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        normalDialog.setNegativeButton("取消预约", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelbook(BuildingNumber, RoomNumber, Time, Token1, Days);
            }
        });

        // 创建实例并显示
        normalDialog.show();
    }

    public void cancelbook(String BuildingNumber, String RoomNumber,
                           String Time, String token2, String Days) {
        CancelBookServer cancelbook = new CancelBookServer();
        cancelbook.setContent(this);

        cancelbook.startCancelBook(BuildingNumber, RoomNumber, Time, token2, Days);

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
//        if (i == 0) {
//            Toast.makeText(this, "更新不成功", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "更新成功" + i, Toast.LENGTH_SHORT).show();
//        }
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
//            mytoken token= new mytoken();
//            token.setMytoken(cursor.getString(0));
            token1 = cursor.getString(0);
        }
        db.close();
        return token1;
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

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data.size();

        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = View.inflate(CancelBook_handler.this, R.layout.wty_bookinfo_adp_layout, null);


            TextView BuildingNumber = view.findViewById(R.id.BuildNumber);
            TextView RoomNumber = view.findViewById(R.id.RoomNumber);
            TextView Time = view.findViewById(R.id.Time);
            TextView NowTime = view.findViewById(R.id.nowtime);
            TextView Days = view.findViewById(R.id.Days4);

            BuildingNumber.setText(data.get(position).getBuildingNumber());
            RoomNumber.setText(data.get(position).getRoomNumber());
            Time.setText(data.get(position).getTime());
            NowTime.setText(data.get(position).getNowTime());
            Days.setText(data.get(position).getDays());
            return view;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }

}