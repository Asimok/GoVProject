package com.example.mq661.govproject.SearchRoom;

import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mq661.govproject.Login_Register.saveinfo;
import com.example.mq661.govproject.R;

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

public class searchroom extends AppCompatActivity implements View.OnClickListener {
    TextView BuildNumber,RoomNumber,Time,Size,Function,MeetingRoomLevel;

    Button commit;
    Map<String, String> Token;
    private OkHttpClient okhttpClient;
    private String Token1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchroom_layout);
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


        Token1 =Token.get("Token");
        Toast.makeText(this, Token1, Toast.LENGTH_SHORT).show();



        new Thread(new Runnable() {
            @Override
            public void run() {

                sendRequest(Token1);
            }
        }).start();
    }

    private void sendRequest(String Token1) {
        Map map = new HashMap();
        map.put("Token", Token1);

        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();

//        Log.d("这将JSON对象转换为json字符串", jsonString);
        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                //dafeng 192.168.2.176
                //  .url("http://192.168.2.176:8080/LoginProject/login")
                // .url("http://192.168.43.174:8080/LoginProject/login")
                // .url("http://39.96.68.13:8080/SmartRoom/RegistServlet") //服务器
                .url("http://192.168.43.174:8080/SmartRoom/RegistServlet") //马琦IP
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
                        Toast.makeText(searchroom.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                try {
                    JSONObject jsonObj = new JSONObject(res);

                    String BuildNumber1 = jsonObj.getString("BuildNumber");
                    String RoomNumber1 = jsonObj.getString("RoomNumber");
                    String Time1 = jsonObj.getString("Time");
                    String  Size1 = jsonObj.getString("Size");
                    String  Function1 =jsonObj.getString("Function");
                    String  MettingRomeLevel1 = jsonObj.getString("MeetingRoomLevel");


                    showRequestResult(BuildNumber1, RoomNumber1, Time1, Size1, Function1, MettingRomeLevel1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void showRequestResult(final String BuildNumber1,final String RoomNumber1,final String Time1,final String Size1,
                                   final    String Function1,final String MettingRomeLevel1) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {

                 BuildNumber.setText(BuildNumber1);
                 RoomNumber.setText(RoomNumber1 );
                 Time.setText(Time1 );
                 Size.setText(Size1 );
                 Function.setText(Function1 );
                MeetingRoomLevel.setText(MettingRomeLevel1 );

            }
        });
    }
}


