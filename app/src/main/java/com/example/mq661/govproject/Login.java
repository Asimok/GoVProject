package com.example.mq661.govproject;



import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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

public class Login extends AppCompatActivity implements View.OnClickListener {
    EditText zhanghu,mima;
    CheckBox CK;
    Button login;
    private OkHttpClient okhttpClient;
    private String zhanghu1,mima1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yuangong_login);
        initView();

    }

    private void initView() {

        zhanghu = findViewById(R.id.zhanghao);
        mima = findViewById(R.id.mima);
        CK = findViewById(R.id.checkBox);
        login=findViewById(R.id.login);
        login.setOnClickListener(this);
        // 记住密码功能
        Map<String, String> userInfo = saveinfo.getUserInfo(this);
        try {
            if (userInfo != null) {
                // 显示在界面上

                if (userInfo.get("number").equals("请输入员工号")) {
                    zhanghu.setHint("请输入员工号");
                } else {
                    zhanghu.setText(userInfo.get("number"));
                }


                if (userInfo.get("password").equals("请输入密码")) {
                    mima.setHint("请输入密码");
                } else {
                    mima.setText(userInfo.get("password"));
                }

            }
            //CK.setChecked(true);
        }
        catch (Exception e)
        {}
    }



    public void check(View v) {
    }


    public void login(View v) {

    }

    @Override
    public void onClick(View v) {


        String number = zhanghu.getText().toString().trim();
        String password = mima.getText().toString();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "请输入员工号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }


        try {
            if (CK.isChecked()) {

                boolean isSaveSuccess = saveinfo.saveUserInfo(this, number, password);
                if (isSaveSuccess) {
                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                number = "请输入员工号";
                password = "请输入密码";

                saveinfo.saveUserInfo(this, number, password);
            }
        }
        catch (Exception e)
        {}

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRequest(zhanghu.getText().toString(),mima.getText().toString());
            }
        }).start();
    }

    private void sendRequest(String zhanghu1,String mima1) {
        Map map = new HashMap();
        map.put("zhanghu", zhanghu1);
        map.put("mima", mima1);
        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
//        Log.d("这将JSON对象转换为json字符串", jsonString);
        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                //dafeng 192.168.2.176
              //  .url("http://192.168.2.176:8080/LoginProject/login")
              // .url("http://192.168.43.174:8080/LoginProject/login")
                .url("http://39.96.68.13:8080/SmartRoom/LoginServlet")
                //.url("http://192.168.43.174:8080/SmartRoom/LoginServlet")
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
                        Toast.makeText(Login.this, "登录失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                try {
                    JSONObject jsonObj = new JSONObject(res);
                    String zhanghu3 = jsonObj.getString("zhanghu");
                    String mima3 = jsonObj.getString("mima");
                    showRequestResult(zhanghu3,mima3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void showRequestResult(final String zhanghu4,final String mima4) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {
                if(zhanghu.getText().toString().equals(zhanghu4)&&mima.getText().toString().equals(mima4)) {
                    Toast.makeText(Login.this,"登录成功！",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(Login.this,"密码错误！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void zhuce(View view) {
        Intent intent;
        intent = new Intent(this, zhuce.class);
        startActivityForResult(intent, 0);
    }
}

