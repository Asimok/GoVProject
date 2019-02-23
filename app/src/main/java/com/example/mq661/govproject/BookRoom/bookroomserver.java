package com.example.mq661.govproject.BookRoom;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.mq661.govproject.Login_Register.Login_noToken;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.tools.MyNotification;
import com.example.mq661.govproject.tools.bookinfo;
import com.example.mq661.govproject.tools.bookinfoDBHelper;
import com.example.mq661.govproject.tools.getUUID;
import com.example.mq661.govproject.tools.saveDeviceInfo;
import com.example.mq661.govproject.tools.userDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class bookroomServer extends AppCompatActivity {
    bookinfo myinfo = new bookinfo();
    private bookinfoDBHelper helper3;
    private userDBHelper helper1;

    public Context getContent() {
        return content;
    }

    public void setContent(Context content) {
        this.content = content;
    }

    private Context content;

    public void startbookroom(String BuildNumber1, String RoomNumber1, String Time1, String Token1, String days1) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        myinfo.setBuildNumber(BuildNumber1);
        myinfo.setRoomNumber(RoomNumber1);
        myinfo.setTime("1  ");
        myinfo.setDays(days1);
        Map map = new HashMap();
        map.put("BuildingNumber", BuildNumber1);
        map.put("RoomNumber", RoomNumber1);
        map.put("Time", Time1);
        map.put("Token", Token1);
        map.put("Days", days1);
        helper3 = new bookinfoDBHelper(content);
        helper1 = new userDBHelper(content);
        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
        String zhanghu = userselect()[0];
        // bookinsert(BuildNumber1, RoomNumber1, Time1 , days1,zhanghu);

        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        final Request request = new Request.Builder()
                //dafeng 192.168.2.176
                //  .url("http://192.168.2.176:8080/SmartRoom/DeleteServlet")
                // .url("http://192.168.43.174:8080/LoginProject/login")
                // .url("http://39.96.68.13:8080/SmartRoom/RegistServlet") //服务器
                .url("http://39.96.68.13:8080/SmartRoom/BookRoomServlet") //马琦IP
                // .url("http://192.168.2.176:8080/SmartRoom/login")
                .post(body)
                .build();
        //异步方法
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(bookroomServer.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                try {
                    JSONObject jsonObj = new JSONObject(res);
                    String Status = jsonObj.getString("Status");
                    String EmployeeNumber = jsonObj.getString("EmployeeNumber");
                    String Name = jsonObj.getString("Name");
                    showRequestResult(Status, EmployeeNumber, Name);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showRequestResult(final String Status, final String EmployeeNumber, final String Name) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {

                if (Status.equals("-1")) {
                    Toast.makeText(content, "预定失败！", Toast.LENGTH_LONG).show();
                    setnull();
                    bookupdate();
                    Looper.loop();
                } else if (Status.equals("0")) {
                    Toast.makeText(content, "预定成功！", Toast.LENGTH_LONG).show();
                    MyNotification notify = new MyNotification(content);
                    notify.MyNotification("智能会议室", "房间预定成功", R.drawable.book, "bookroom", "预定房间", 7, "预定");
                    //     Looper.loop();
                } else if (Status.equals("-3")) {

                    Toast.makeText(content, "token失效，请重新登录！", Toast.LENGTH_SHORT).show();
                    saveDeviceInfo.savelogin(getApplicationContext(), "0");
                    bookupdate();
                    setnull();
                    relog();
                    //              Looper.loop();
                } else if (Status.equals("-5")) {
                    Toast.makeText(content, "您不具有预定此房间的权限！", Toast.LENGTH_SHORT).show();
                    bookupdate();
                    //                   Looper.loop();
                } else if (Status.equals("-6")) {

                    Toast.makeText(content, "该会议室已被预订！", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                } else if (Status.equals("-7")) {

                }

            }
        });

    }

    public void relog() {
        Intent intent;
        intent = new Intent(this, Login_noToken.class);
        startActivityForResult(intent, 0);
        finish();
    }

    public void setnull() {
        myinfo.setBuildNumber(null);
        myinfo.setRoomNumber(null);
        myinfo.setTime(null);
        myinfo.setDays(null);
    }

    public void bookinsert(String BuildNumber1, String RoomNumber1, String Time1, String days1, String zhanghu1) {

        SQLiteDatabase db = helper3.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("UUID", getUUID.getUUID32());
        values.put("BuildNumber", BuildNumber1);
        values.put("RoomNumber", RoomNumber1);
        values.put("Time", Time1);
        values.put("days", days1);
        values.put("zhanghu", zhanghu1);
        long l = db.insert("bookinfo", null, values);

        if (l == -1) {
            Toast.makeText(content, "插入预定信息不成功", Toast.LENGTH_SHORT).show();
            Looper.loop();
        } else {
            Toast.makeText(content, "插入预定信息成功", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
        db.close();
    }

    public void bookupdate() {


        String zhanghu3 = userselect()[0];
        SQLiteDatabase db = helper3.getWritableDatabase();
        ContentValues values = new ContentValues();
        Log.d("ddd", "更新的预定信息   " + "");
        values.put("UUID", getUUID.getUUID32());
        values.put("BuildNumber", "");
        values.put("RoomNumber", "");
        values.put("Time", "");
        values.put("days", "");
        int i = db.update("bookinfo", values, "zhanghu=?", new String[]{zhanghu3});

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

}
