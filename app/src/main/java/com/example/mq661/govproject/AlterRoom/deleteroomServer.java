package com.example.mq661.govproject.AlterRoom;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.mq661.govproject.Login_Register.Login_noToken;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.tools.MyNotification;
import com.example.mq661.govproject.tools.bookinfoDBHelper;
import com.example.mq661.govproject.tools.saveDeviceInfo;
import com.example.mq661.govproject.tools.tokenDBHelper;
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

public class deleteroomServer extends AppCompatActivity {
    private bookinfoDBHelper helper3;
    private userDBHelper helper1;
    private tokenDBHelper helper;
    private String Token1;
    private Context content;

    public Context getContent() {
        return content;
    }

    public void setContent(Context content) {
        this.content = content;
    }

    public void startdeleteroom(String BuildNumber1, String RoomNumber1, String Token) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        Token1 = Token;
        Map map = new HashMap();
        map.put("BuildingNumber", BuildNumber1);
        map.put("RoomNumber", RoomNumber1);
        map.put("Token", Token1);
        helper3 = new bookinfoDBHelper(content);
        helper1 = new userDBHelper(content);
        helper = new tokenDBHelper(content);
        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        final Request request = new Request.Builder()

                .url("http://39.96.68.13:8080/SmartRoom/DeleteServlet")

                .post(body)
                .build();
        //异步方法
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(deleteroomServer.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {

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

                if (status.equals("-1")) {
                    Toast.makeText(content, "删除失败！", Toast.LENGTH_LONG).show();
                } else if (status.equals("-8")) {
                    Toast.makeText(content, "删除失败，房间占用！", Toast.LENGTH_LONG).show();

                } else if (status.equals("-9")) {
                    Toast.makeText(content, "删除失败，房间不存在！", Toast.LENGTH_LONG).show();

                } else if (status.equals("0")) {
                    MyNotification notify = new MyNotification(content);
                    notify.MyNotification("智能会议室", "删除房间成功", R.drawable.dete2, "deleteroom", "删除房间", 7, "删除");
                    Toast.makeText(content, "删除成功！", Toast.LENGTH_LONG).show();

                } else if (status.equals("-3")) {
                    Toast.makeText(content, "token失效，请重新登录！", Toast.LENGTH_SHORT).show();
                    delete(Token1);
                    saveDeviceInfo.savelogin(getApplicationContext(), "0");
                    relog();
                } else if (status.equals("-5")) {
                    Toast.makeText(content, "您没有进行此项操作的权限！", Toast.LENGTH_SHORT).show();
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
}
