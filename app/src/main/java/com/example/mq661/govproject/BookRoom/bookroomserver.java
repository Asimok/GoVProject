package com.example.mq661.govproject.BookRoom;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.mq661.govproject.Login_Register.Login;
import com.example.mq661.govproject.tools.tounicode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class bookroomserver extends AppCompatActivity {
    public Context getContent() {
        return content;
    }

    public void setContent(Context content) {
        this.content = content;
    }

    private Context content;
    public  void startbookroom(String BuildNumber1,String RoomNumber1,String Time1,String Token1,String days1) throws IOException, InterruptedException {
        final OkHttpClient okHttpClient= new OkHttpClient();

        Map map = new HashMap();
        map.put("BuildingNumber", tounicode.gbEncoding( BuildNumber1));
        map.put("RoomNumber", RoomNumber1);
        map.put("Time", tounicode.gbEncoding(Time1));
        map.put("Token", Token1);
        map.put("Days", tounicode.gbEncoding(days1));


        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();


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
                Toast.makeText(bookroomserver.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                try {
                    JSONObject jsonObj = new JSONObject(res);
                    String Status = jsonObj.getString("Status");

                    String EmployeeNumber =jsonObj.getString("EmployeeNumber");
                    String Name =jsonObj.getString("Name");
                    showRequestResult(Status,EmployeeNumber,Name);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showRequestResult(final String Status,final String EmployeeNumber,final String Name) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {
              //  Looper.prepare();
//                Toast.makeText(content, "进入判断！", Toast.LENGTH_LONG).show();
//                Looper.loop();
                if (Status.equals("-1")) {
                    Toast.makeText(content, "预定失败！", Toast.LENGTH_LONG).show();
                    Looper.loop();
                    //  bookinfo.setText("预定者姓名:"+Name+" 员工号:"+EmployeeNumber);
                } else if (Status.equals("0")) {
                    Toast.makeText(content, "预定成功！", Toast.LENGTH_LONG).show();
                    Looper.loop();
                  //  bookinfo.setText("预定者姓名:"+Name+" 员工号:"+EmployeeNumber);
                }
                else if (Status.equals("-3")) {
                    Toast.makeText(content, "token失效，请重新登录！", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    relog();
                }
                else if (Status.equals("-5")) {
                    Toast.makeText(content, "您不具有预定此房间的权限！", Toast.LENGTH_SHORT).show();
                    Looper.loop();

                }
                else if (Status.equals("-6")) {
                    Toast.makeText(content, "该会议室已被预订！", Toast.LENGTH_SHORT).show();
                    Looper.loop();

                }
                else if (Status.equals("-7")) {
                    Toast.makeText(content, "该会议室正在维修！", Toast.LENGTH_SHORT).show();
                    Looper.loop();

                }

            }
        });

    }
    public void relog() {
        Intent intent;
        intent = new Intent(this, Login.class);
        startActivityForResult(intent, 0);
        finish();
    }

}
