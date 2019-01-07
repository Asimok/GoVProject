package com.example.mq661.govproject.AlterRoom;

import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mq661.govproject.Login_Register.saveinfo;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.tools.tounicode;

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

public class changeroom extends AppCompatActivity implements View.OnClickListener {
    EditText BuildNumber,RoomNumber,Time,Size,Function,MeetingRoomLevel;
    Button commit;
    Map<String, String> Token;
    private OkHttpClient okhttpClient;
    private String BuildNumber1,RoomNumber1,Time1,Size1,Function1,MeetingRoomLevel1,Token1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changeroom_layout);
        initView();

    }

    private void initView() {

        BuildNumber = findViewById(R.id.BuildNumber);
        RoomNumber = findViewById(R.id.RoomNumber);
        Time = findViewById(R.id.Time);
        Size = findViewById(R.id.Size);
        Function=findViewById(R.id.Function);
        MeetingRoomLevel=findViewById(R.id.MettingRomeLevel);
        commit=findViewById(R.id.commit);

        commit.setOnClickListener(this);
        // 提交修改

        Token = saveinfo.getUserInfo(this);
    }

    @Override
    public void onClick(View v) {
        // Toast.makeText(this,"登陆成功",Toast.LENGTH_LONG).show();

        BuildNumber1 = BuildNumber.getText().toString().trim();
        RoomNumber1 = RoomNumber.getText().toString().trim();
        Time1 = Time.getText().toString().trim();
        Size1 = Size.getText().toString().trim();
        Function1 = tounicode.gbEncoding(Function.getText().toString().trim());
        MeetingRoomLevel1 = tounicode.gbEncoding(MeetingRoomLevel.getText().toString().trim());
        Token1 =Token.get("Token");
        Toast.makeText(this, Token1, Toast.LENGTH_SHORT).show();
        if (TextUtils.isEmpty(BuildNumber1)) {
            Toast.makeText(this, "请输入楼号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(RoomNumber1)) {
            Toast.makeText(this, "请输入房间号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Time1)) {
            Toast.makeText(this, "请输入时间段", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(Size1)) {
            Toast.makeText(this, "请输入容量", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Function1)) {
            Toast.makeText(this, "请输入功能", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(MeetingRoomLevel1)) {
            Toast.makeText(this, "请填写会议室等级", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Token1)) {
            Toast.makeText(this, "未获取到Token", Toast.LENGTH_SHORT).show();
            return;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {

                sendRequest(BuildNumber1, RoomNumber1, Time1, Size1, Function1, MeetingRoomLevel1, Token1);
            }
        }).start();
    }

    private void sendRequest(String BuildNumber1,String RoomNumber1,String Time1,String Size1,
                             String Function1,String MettingRomeLevel1 ,String Token1) {
        Map map = new HashMap();
        map.put("BuildNumber", BuildNumber1);
        map.put("RoomNumber", RoomNumber1);
        map.put("Time", Time1);
        map.put("Size", Size1);
        map.put("Function", Function1);
        map.put("MeetingRoomLevel", MeetingRoomLevel1);
        map.put("Token", Token1);


        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();

//        Log.d("这将JSON对象转换为json字符串", jsonString);
        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                //dafeng 192.168.2.176
                 .url("http://192.168.2.176:8080/SmartRoom/ChangeServlet")
                // .url("http://192.168.43.174:8080/LoginProject/login")
                // .url("http://39.96.68.13:8080/SmartRoom/RegistServlet") //服务器
                .url("http://192.168.43.174:8080/SmartRoom/ChangeServlet") //马琦IP
                // .url("http://192.168.2.176:8080/SmartRoom/login")
                .post(body)
                .build();
        Call call = okhttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(changeroom.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
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
                    Toast.makeText(changeroom.this, "修改失败！", Toast.LENGTH_SHORT).show();
                } else if (status.equals("1")) {
                    Toast.makeText(changeroom.this, "修改成功！", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}


