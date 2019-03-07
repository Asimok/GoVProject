package com.example.mq661.govproject.BookRoom;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.mq661.govproject.Login_Register.Login_noToken;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.tools.MyNotification;
import com.example.mq661.govproject.tools.bookinfoDBHelper;
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

public class CancelBookServer extends AppCompatActivity {
    private bookinfoDBHelper helper3;
    private userDBHelper helper1;
    private Context content;

    public Context getContent() {
        return content;
    }

    public void setContent(Context content) {
        this.content = content;
    }

    public void startCancelBook(String BuildNumber1, String RoomNumber1, String Time1, String Token1, String days1) {
        final OkHttpClient okHttpClient = new OkHttpClient();

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
        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        final Request request = new Request.Builder()

                .url("http://39.96.68.13:8080/SmartRoom/CancelBookServlet")

                .post(body)
                .build();
        //异步方法
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(CancelBookServer.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                try {
                    JSONObject jsonObj = new JSONObject(res);
                    String Status = jsonObj.getString("Status");
                    showRequestResult(Status);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showRequestResult(final String Status) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {

                if (Status.equals("-1")) {
                    Toast.makeText(content, "取消预约成功，信息定时清除，请勿重复操作！", Toast.LENGTH_LONG).show();
                    Looper.loop();
                } else if (Status.equals("0")) {
                    Toast.makeText(content, "取消预约成功！", Toast.LENGTH_LONG).show();
                    MyNotification notify = new MyNotification(content);
                    notify.MyNotification("智能会议室", "房间取消预约成功", R.drawable.book, "cancelroom", "取消预订", 8, "取消预约");
                    Looper.loop();
                } else if (Status.equals("-3")) {
                    Toast.makeText(content, "token失效，请重新登录！", Toast.LENGTH_SHORT).show();
                    saveDeviceInfo.savelogin(getApplicationContext(), "0");
                    relog();
                    Looper.loop();
                } else if (Status.equals("-5")) {
                    Toast.makeText(content, "您没有预定此房间！", Toast.LENGTH_SHORT).show();
                    Looper.loop();

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


}
