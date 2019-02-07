package com.example.mq661.govproject.repassword;


import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mq661.govproject.Login_Register.Login;
import com.example.mq661.govproject.Login_Register.Login_noToken;
import com.example.mq661.govproject.tools.saveDeviceInfo;
import com.example.mq661.govproject.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class inputmail extends AppCompatActivity implements View.OnClickListener {
    EditText edremail;
    Button commit;
    private OkHttpClient okhttpClient;
    private String remail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inputmail_layout);
        initView();

    }

    private void initView() {

        edremail = findViewById(R.id.remail);
        commit = findViewById(R.id.commit);
        commit.setOnClickListener(this);

            }


    @Override
    public void onClick(View v) {

        remail = edremail.getText().toString().trim();



        if (TextUtils.isEmpty(remail)) {
            Toast.makeText(this, "请输入注册邮箱", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRequest(remail);
            }
        }).start();
    }

    private void sendRequest(String remail1) {

        Map map = new HashMap();
        map.put("Email", remail1);

        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
        RequestBody body = RequestBody.create(null, jsonString);  //以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                //dafeng 192.168.2.176
                //  .url("http://192.168.2.176:8080/LoginProject/login")
                // .url("http://192.168.43.174:8080/LoginProject/login")
                // .url("http://39.96.68.13:8080/SmartRoom/LoginServlet")
                .url("http://39.96.68.13:8080/SmartRoom/ForgotPwdServlet")//MQ
                // .url("http://192.168.2.176:8080/SmartRoom/login")
                .post(body)
                .build();
        okhttp3.Call call = okhttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(inputmail.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {

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
                                  Toast.makeText(inputmail.this, "邮箱不存在，查找失败！", Toast.LENGTH_SHORT).show();
                              } else if (status.equals("0")) {
                                  Toast.makeText(inputmail.this, "查找成功，请查收邮件！", Toast.LENGTH_LONG).show();
                                  saveDeviceInfo.savelogin(getApplicationContext(),"0");
                                  relog();

                              }
                              else if (status.equals("-2")) {
                                  Toast.makeText(inputmail.this, "邮箱非法！请重新输入", Toast.LENGTH_LONG).show();}
                          }
                      }
        );
    }


    public void remima5()
    {
        Intent intent;
        intent = new Intent(this, repassword.class);
        startActivityForResult(intent, 0);

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
        if (newConfig.fontScale != 1){
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
}


