package com.example.mq661.govproject.SearchRoom;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mq661.govproject.AlterRoom.deleteroom;
import com.example.mq661.govproject.Login_Register.Login_noToken;
import com.example.mq661.govproject.R;
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

public class searchroom_handler_forchange_only extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    Intent ssdata = new Intent();
    //handler 处理返回的请求结果
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
        }
    };
    private List<roomAdapterInfo> data;
    private OkHttpClient okhttpClient;
    private tokenDBHelper helper;
    private String Token1, bidui = "";
    private String ssBuildingNumber, ssRoomNumber, ssSize, ssFunction, ssIsMeeting, ssDays, IsMeeting2;
    private ListView searchroomlv;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchroom_lv_layout);
        new Thread(runnable).start();  //启动子线程
        helper = new tokenDBHelper(this);
        initView();
    }

    private void initView() {

        searchroomlv = findViewById(R.id.searchroomlv);
        searchroomlv.setOnItemClickListener(this);       //设置短按事件

    }

    private void sendRequest(String Token1) {
        Map map = new HashMap();
        map.put("Token", Token1);

        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()

                .url("http://39.96.68.13:8080/SmartRoom/SearchServlet")

                .post(body)
                .build();
        Call call = okhttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(searchroom_handler_forchange_only.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                try {
                    JSONArray jsonArray = new JSONArray(res);
                    bidui = jsonArray.getJSONObject(0).getString("buildingNumber") + jsonArray.getJSONObject(0).getString("roomNumber") + jsonArray.getJSONObject(0).getString("days");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);


                        String BuildingNumber1 = jsonObj.getString("buildingNumber");
                        String RoomNumber1 = jsonObj.getString("roomNumber");
                        String Size1 = jsonObj.getString("size");
                        String Function1 = jsonObj.getString("functions");
                        String IsMeeting = jsonObj.getString("isMeeting");
                        String Days = jsonObj.getString("days");
                        if (IsMeeting.equals("0")) {
                            IsMeeting2 = "空闲";
                        } else if (IsMeeting.equals("1")) {
                            IsMeeting2 = "占用";
                        } else if (IsMeeting.equals("2")) {
                            IsMeeting2 = "维修";
                        } else {
                            IsMeeting2 = "未知";
                        }

                        String mapx = "map" + i;
                        if (BuildingNumber1.equals("-1") && RoomNumber1.equals("-1")) {
                            showRequestResult(BuildingNumber1, RoomNumber1, Size1, Function1, IsMeeting2, Days, mapx);

                            break;
                        } else if (BuildingNumber1.equals("-3") && RoomNumber1.equals("-3")) {
                            showRequestResult(BuildingNumber1, RoomNumber1, Size1, Function1, IsMeeting2, Days, mapx);

                            break;
                        } else {
                            if (bidui.equals(BuildingNumber1 + RoomNumber1 + Days)) {
                                continue;
                            } else {
                                bidui = BuildingNumber1 + RoomNumber1 + Days;
                                showRequestResult(BuildingNumber1, RoomNumber1, Size1, Function1, IsMeeting2, Days, mapx);
                            }
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void showRequestResult(final String BuildNumber1, final String RoomNumber1, final String Size1, final String Function1, final String IsMeeting1, final String Days1, final String mapx) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {

                if (BuildNumber1.equals("-1") && RoomNumber1.equals("-1")) {
                    Toast.makeText(searchroom_handler_forchange_only.this, "查询不成功！", Toast.LENGTH_SHORT).show();

                    saveDeviceInfo.savelogin(getApplicationContext(), "0");
                } else if (BuildNumber1.equals("-3") && RoomNumber1.equals("-3")) {
                    Toast.makeText(searchroom_handler_forchange_only.this, " 认证信息失效，请重新登录", Toast.LENGTH_SHORT).show();
                    delete(Token1);
                    saveDeviceInfo.savelogin(getApplicationContext(), "0");
                    relog();
                } else {
                    roomAdapterInfo mapx = new roomAdapterInfo();
                    mapx.setBuildingNumber(BuildNumber1);
                    mapx.setRoomNumber(RoomNumber1);
                    mapx.setFunction(Function1);
                    mapx.setSize(Size1);
                    mapx.setIsMeeting(IsMeeting1);
                    mapx.setDays(Days1);
                    data.add(mapx);

                    searchroomlv.setAdapter(new searchroom_handler_forchange_only.MyAdapter());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    public void onBackPressed() {
        ssdata.putExtra("BuildingNumber", "空的");
        ssdata.putExtra("Size", "空的");
        ssdata.putExtra("RoomNumber", "空的");
        ssdata.putExtra("Function", "空的");
        ssdata.putExtra("IsMeeting", "空的");
        ssdata.putExtra("Days", "空的");
        setResult(1, ssdata);
//        finish();
        super.onBackPressed();//注释掉这行,back键不退出activity

        Log.i("aaa", "onBackPressed");
    }

    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        ssBuildingNumber = data.get(position).getBuildingNumber();
        ssSize = data.get(position).getSize();
        ssRoomNumber = data.get(position).getRoomNumber();
        ssFunction = data.get(position).getFunction();
        ssIsMeeting = data.get(position).getIsMeeting();
        ssDays = data.get(position).getDays();


        ssdata.putExtra("BuildingNumber", ssBuildingNumber);
        ssdata.putExtra("Size", ssSize);
        ssdata.putExtra("RoomNumber", ssRoomNumber);
        ssdata.putExtra("Function", ssFunction);
        ssdata.putExtra("IsMeeting", ssIsMeeting);
        ssdata.putExtra("Days", ssDays);
        setResult(1, ssdata);
        finish();
    }


    public void deleteroom() {
        Intent intent;
        intent = new Intent(this, deleteroom.class);
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

        db.close();
    }

    public void update(String token) {


        //自定义更新
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("token", token);
        int i = db.update("token", values, null, null);

        db.close();
    }

    public void delete(String token) {

        SQLiteDatabase db = helper.getWritableDatabase();


        int i = db.delete("token", "token=?", new String[]{token});

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

            View view = View.inflate(searchroom_handler_forchange_only.this, R.layout.wty_searchroom_adp_layout_forchange_only, null);


            TextView BuildingNumber = view.findViewById(R.id.BuildNumber);
            TextView RoomNumber = view.findViewById(R.id.RoomNumber);
            TextView Size = view.findViewById(R.id.Size);
            TextView Function = view.findViewById(R.id.Function);
            TextView IsMeeting = view.findViewById(R.id.IsMeeting);
            TextView Days = view.findViewById(R.id.Days3);

            BuildingNumber.setText(data.get(position).getBuildingNumber());
            Size.setText(data.get(position).getSize());
            RoomNumber.setText(data.get(position).getRoomNumber());
            Function.setText(data.get(position).getFunction());
            IsMeeting.setText(data.get(position).getIsMeeting());
            Days.setText(data.get(position).getDays());
            return view;
        }
    }

}