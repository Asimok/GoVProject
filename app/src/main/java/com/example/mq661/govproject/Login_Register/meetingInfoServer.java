package com.example.mq661.govproject.Login_Register;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mq661.govproject.R;
import com.example.mq661.govproject.tools.bookinfo;
import com.example.mq661.govproject.tools.dateToString;
import com.example.mq661.govproject.tools.getUUID;
import com.example.mq661.govproject.tools.meetingInfoDBHelper;
import com.example.mq661.govproject.tools.tokenDBHelper;
import com.example.mq661.govproject.tools.userDBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class meetingInfoServer extends AppCompatActivity {
    bookinfo myinfo = new bookinfo();
    LinearLayout linear;
    private meetingInfoDBHelper helper4;
    private userDBHelper helper1;
    private ArrayList<bookroomInfoAdapter> data, data1, data2, data3;
    private tokenDBHelper helper;
    private String aa = "22222", bb;
    private String zhanghu;
    private Context content;

    public Context getContent() {
        return content;
    }

    public void setContent(Context content) {
        this.content = content;
    }

    public ArrayList<bookroomInfoAdapter> startGetMeetingInfo() {
        final OkHttpClient okHttpClient = new OkHttpClient();

        helper4 = new meetingInfoDBHelper(content);
        helper1 = new userDBHelper(content);
        helper = new tokenDBHelper(content);
        zhanghu = userselect()[0];
        data1 = new ArrayList<bookroomInfoAdapter>();
        data2 = new ArrayList<bookroomInfoAdapter>();
        data3 = new ArrayList<bookroomInfoAdapter>();

        String Token1 = select();
        Map map = new HashMap();
        map.put("Token", Token1);

        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        final Request request = new Request.Builder()

                .url("http://39.96.68.13:8080/SmartRoom/MeetingMessageServlet")
                .post(body)
                .build();
        //异步方法
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(content, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                Log.d("aa", "res2   " + res);
                try {

                    JSONArray jsonArray = new JSONArray(res);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);


                        String BuildingNumber1 = jsonObj.getString("buildingNumber");
                        String RoomNumber1 = jsonObj.getString("roomNumber");
                        String Time1 = jsonObj.getString("time");
                        String Days = jsonObj.getString("days");
                        int hh = Integer.parseInt(dateToString.nowdateToString3());
                        int thishh = Integer.parseInt(Time1.substring(0, 2));
                        int day = Integer.parseInt(dateToString.nowdateToString4());
                        int thisday = Integer.parseInt(Days.substring(8, 10));
                        if (day > thisday) {
                            continue;
                        } else if (hh >= thishh && day == thisday) {
                            continue;
                        } else {
                            bookinsert(zhanghu, BuildingNumber1, RoomNumber1, Time1, Days);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Log.d("ddd", "1    这里要返回的data3   " + data3.toString());
        return data2;
    }

    private ArrayList<bookroomInfoAdapter> showRequestResult(final String zhanghu, final String BuildingNumber1, final String RoomNumber1, final String Time1, final String Days) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {

                if (BuildingNumber1.equals("-1")) {

                    Toast.makeText(content, "刷新失败！", Toast.LENGTH_LONG).show();
                    Looper.loop();

                } else {
                    bookroomInfoAdapter msg = new bookroomInfoAdapter();
                    msg.setBuildingNumber(BuildingNumber1);
                    msg.setDays(Days);
                    msg.setRoomNumber(RoomNumber1);
                    msg.setTime(Time1);
                    data1.add(msg);
                    data2 = data1;
                    bookinsert(zhanghu, BuildingNumber1, RoomNumber1, Time1, Days);
                    Log.d("ddd", "2     这里的data2" + data2.get(0).getBuildingNumber());
                    Toast.makeText(content, "刷新成功！", Toast.LENGTH_LONG).show();
                }

            }
        });
        Log.d("ddd", "3     这里的data2" + data2.get(0).getBuildingNumber());
        return data2;
    }

    public void relog() {
        Intent intent;
        intent = new Intent(this, Login_noToken.class);
        startActivityForResult(intent, 0);
        finish();
    }

    public void bookinsert(String zhanghu1, String BuildingNumber1, String RoomNumber1, String Time1, String Days) {

        SQLiteDatabase db = helper4.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("UUID", getUUID.getUUID32());
        values.put("BuildNumber", BuildingNumber1);
        values.put("RoomNumber", RoomNumber1);
        values.put("Time", Time1);
        values.put("days", Days);
        values.put("zhanghu", zhanghu1);
        long l = db.insert("meetinginfo", null, values);
        db.close();
    }

    public void bookupdate() {


        String zhanghu3 = userselect()[0];
        SQLiteDatabase db = helper4.getWritableDatabase();
        ContentValues values = new ContentValues();
        Log.d("ddd", "更新的预定信息   " + "");
        values.put("UUID", "");
        values.put("BuildNumber", "");
        values.put("RoomNumber", "");
        values.put("Time", "");
        values.put("days", "");
        int i = db.update("meetinginfo", values, "zhanghu=?", new String[]{zhanghu3});
//        if (i == 0) {
//            Toast.makeText(content, "更新预定信息不成功", Toast.LENGTH_SHORT).show();
//            Looper.loop();
//        } else {
//            Toast.makeText(content, "更新预定信息成功", Toast.LENGTH_SHORT).show();
//            Looper.loop();
//        }
        db.close();
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

    public void bookdelete() {

        SQLiteDatabase db = helper4.getWritableDatabase();
        int i = db.delete("meetinginfo", "zhanghao=?", new String[]{userselect()[0]});
        if (i == 0) {
//            Toast.makeText(this, "删除用户信息不成功",Toast.LENGTH_SHORT).show();
//        }else{  Toast.makeText(this, "删除用户信息成功",Toast.LENGTH_SHORT).show();
        }
        db.close();

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

            View view = View.inflate(meetingInfoServer.this, R.layout.bookroom_info_adp_layout, null);


            TextView BuildingNumber = view.findViewById(R.id.BuildNumber);
            TextView RoomNumber = view.findViewById(R.id.RoomNumber);
            TextView Time = view.findViewById(R.id.Time);
            TextView booktime = view.findViewById(R.id.booktime);
            TextView Days = view.findViewById(R.id.Days3);

            BuildingNumber.setText(data.get(position).getBuildingNumber());
            booktime.setText(data.get(position).getNowTime());
            RoomNumber.setText(data.get(position).getRoomNumber());
            Time.setText(data.get(position).getTime());
            Days.setText(data.get(position).getDays());
            return view;
        }
    }
}