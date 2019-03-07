package com.example.mq661.govproject.HomePage;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mq661.govproject.AlterRoom.deleteroom;
import com.example.mq661.govproject.Login_Register.Login_noToken;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.SearchRoom.roomAdapterInfo;
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

public class MainInterfaceNow extends AppCompatActivity implements View.OnClickListener {
    Intent ssdata = new Intent();
    Button commit;
    private String ssBuildingNumber, ssRoomNumber, ssTime, ssSize, ssFunction, ssIsMeeting, ssDays, ssssTime;
    private List<roomAdapterInfo> data;
    private OkHttpClient okhttpClient;
    private tokenDBHelper helper;
    private String Token1;
    private LinearLayout linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout_now);
        helper = new tokenDBHelper(this);
        initView();
    }

    private void initView() {

        // searchroomlv.setAdapter(new searchroom.MyAdapter());
        // 提交修改
        linear = findViewById(R.id.rooms);

        commit = findViewById(R.id.commit);
        commit.setOnClickListener(this);
        ssTime = dateToString.nowdateToString2();//获取当前时间
    }

    //    protected void onRestart() {
//        super.onRestart();
//        ssTime=dateToString.nowdateToString2();//获取当前时间
//        Toast.makeText(this, "onRestart 里的时间"+ssTime, Toast.LENGTH_LONG).show();
//
//
//    }
    @Override
    public void onClick(View v) {
        data = new ArrayList<roomAdapterInfo>();

        Token1 = select();
        Toast.makeText(this, "读本地" + Token1, Toast.LENGTH_SHORT).show();
        //   Toast.makeText(this, "sendRequest 里的时间"+ssTime, Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                sendRequest(Token1, ssTime);
            }
        }).start();
    }

    private void sendRequest(String Token1, String Time1) {
        //
        Map map = new HashMap();
        map.put("Token", Token1);
        map.put("Time", Time1);


        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()

                .url("http://39.96.68.13:8080/SmartRoom/MainInterfaceServlet")

                .post(body)
                .build();
        Call call = okhttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainInterfaceNow.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                try {

                    // JSONObject jsonObj = new JSONObject(res);
                    //  JSONObject json = new JSONObject(res);
                    JSONArray jsonArray = new JSONArray(res);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);


                        String BuildingNumber1 = jsonObj.getString("buildingNumber");
                        String RoomNumber1 = jsonObj.getString("roomNumber");
                        String Time1 = jsonObj.getString("time");
                        String Size1 = jsonObj.getString("size");
                        String Function1 = jsonObj.getString("functions");
                        String IsMeeting = jsonObj.getString("isMeeting");
                        String Days = jsonObj.getString("days");
                        String mapx = "map" + i;

                        if (BuildingNumber1.equals("-1") && RoomNumber1.equals("-1") && Time1.equals("-1")) {

                            showRequestResult(BuildingNumber1, RoomNumber1, Time1, Size1, Function1, IsMeeting, Days, mapx);

                            break;
                        } else if (BuildingNumber1.equals("-3") && RoomNumber1.equals("-3") && Time1.equals("-3")) {

                            showRequestResult(BuildingNumber1, RoomNumber1, Time1, Size1, Function1, IsMeeting, Days, mapx);

                            break;
                        } else
                            showRequestResult(BuildingNumber1, RoomNumber1, Time1, Size1, Function1, IsMeeting, Days, mapx);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void showRequestResult(final String BuildNumber1, final String RoomNumber1, final String Time1, final String Size1, final String Function1, final String IsMeeting1, final String Days1, final String mapx) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {

                if (BuildNumber1.equals("-1") && RoomNumber1.equals("-1") && Time1.equals("-1")) {
                    Toast.makeText(MainInterfaceNow.this, "当前没有空闲的会议室", Toast.LENGTH_SHORT).show();

                } else if (BuildNumber1.equals("-3") && RoomNumber1.equals("-3") && Time1.equals("-3")) {
                    Toast.makeText(MainInterfaceNow.this, "Token失效，请重新登录", Toast.LENGTH_SHORT).show();
                    delete(Token1);
                    saveDeviceInfo.savelogin(getApplicationContext(), "0");
                    relog();

                } else {
                    linear.removeAllViews();
                    roomAdapterInfo mapx = new roomAdapterInfo();
                    mapx.setBuildingNumber(BuildNumber1);
                    mapx.setRoomNumber(RoomNumber1);
                    mapx.setFunction(Function1);
                    mapx.setSize(Size1);
                    mapx.setTime(Time1);
                    mapx.setIsMeeting(IsMeeting1);
                    mapx.setDays(Days1);
                    data.add(mapx);

//                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    layoutParams.setMargins(0,10,0,10);//4个参数按顺序分别是左上右下
                    TextView tv = new TextView(MainInterfaceNow.this);
//                    tv.setLayoutParams(layoutParams);
                    tv.setPadding(0, 100, 0, 10);
                    // tv.setText(mapx.toString());
                    tv.setText(data.toString());
                    linear.addView(tv);
                }
            }
        });
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
}


