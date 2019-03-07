package com.example.mq661.govproject.Login_Register;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.mq661.govproject.tools.registInfoDBHelper;
import com.example.mq661.govproject.tools.saveDeviceInfo;

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

public class searchRegistServer extends AppCompatActivity {

    public static String Name, PhoneNumber, Email, Ministry, Sex;
    private registInfoDBHelper helper;
    private Context content;

    public Context getContent() {
        return content;
    }

    public void setContent(Context content) {
        this.content = content;
    }

    public void startsearchRegist(String token) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        helper = new registInfoDBHelper(content);
        Map map = new HashMap();

        map.put("Token", token);

        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        final Request request = new Request.Builder()

                .url("http://39.96.68.13:8080/SmartRoom/SearchPersonServlet")
                .post(body)
                .build();
        //异步方法
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(searchRegistServer.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                try {
                    JSONObject jsonObj = new JSONObject(res);
                    String Name2 = jsonObj.getString("Name");
                    String PhoneNumber2 = jsonObj.getString("PhoneNumber");
                    String Email2 = jsonObj.getString("Email");
                    String Ministry2 = jsonObj.getString("Ministry");
                    String Sex2 = jsonObj.getString("Sex");

                    Log.d("aaaa", "Name    " + Name2);
                    Log.d("aaaa", "PhoneNumber    " + PhoneNumber2);
                    Log.d("aaaa", "Email    " + Email2);
                    Log.d("aaaa", "Ministry    " + Ministry2);
                    Log.d("aaaa", "Sex    " + Sex2);

                    showRequestResult(Name2, PhoneNumber2, Email2, Ministry2, Sex2);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showRequestResult(final String Name1, final String PhoneNumber1, final String Email1, final String Ministry1, final String Sex1) {

        if (Name1.equals("-1")) {
            Toast.makeText(content, "查询失败！", Toast.LENGTH_LONG).show();
            Looper.loop();
        } else if (Name1.equals("-3")) {

            Toast.makeText(content, "token失效，请重新登录！", Toast.LENGTH_SHORT).show();
            saveDeviceInfo.savelogin(getApplicationContext(), "0");
            relog();
        } else if (Name1.equals("-2")) {
            Toast.makeText(content, "员工不存在，请重新注册！", Toast.LENGTH_SHORT).show();
        } else {
            Name = Name1;
            PhoneNumber = PhoneNumber1;
            Email = Email1;
            Ministry = Ministry1;
            Sex = Sex1;

            insertAllRoom(Name, PhoneNumber, Email, Sex, Ministry);
        }
    }

    public void relog() {
        Intent intent;
        intent = new Intent(this, Login_noToken.class);
        startActivityForResult(intent, 0);
        finish();
    }

    public void insertAllRoom(String Name1, String PhoneNumber1, String Email1, String Sex1, String Ministry1) {


        //自定义增加数据
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("Ministry", Ministry1);
        values.put("Name", Name1);
        values.put("PhoneNumber", PhoneNumber1);
        values.put("Email", Email1);
        values.put("Sex", Sex1);

        long l = db.replace("registinfo", null, values);

        if (l == -1) {
            Log.d("aaa", "registinfo 插入不成功");
        } else {
            Log.d("aaa", "registinfo 插入成功" + l);
        }
        db.close();
    }
}
