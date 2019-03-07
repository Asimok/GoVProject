package com.example.mq661.govproject.Login_Register;


import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mq661.govproject.MainInterface.tab;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.tools.MyNotification;
import com.example.mq661.govproject.tools.registInfoDBHelper;
import com.example.mq661.govproject.tools.saveDeviceInfo;
import com.example.mq661.govproject.tools.tokenDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReRegist extends AppCompatActivity implements View.OnClickListener {
    EditText Name, PhoneNumber, Email, Ministry;
    ArrayList<personinfo> thispersoninfo;
    RadioGroup Sex;
    RadioButton man, woman;
    Button zhuce;
    private OkHttpClient okhttpClient;
    private String Name1, PhoneNumber1, Email1, Ministry1;
    private int Sex1 = 3;
    private String Token1;
    private tokenDBHelper helper;
    private registInfoDBHelper helper1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wty_re_regist_layout);
        helper = new tokenDBHelper(this);
        helper1 = new registInfoDBHelper(this);
        thispersoninfo = new ArrayList<personinfo>();
        initView();
        initdata();
    }

    private void initdata() {
//        searchRegistServer serg=new searchRegistServer();
//        serg.setContent(this);
//        serg.startsearchRegist(select());

        List<personinfo> psinfo = selectPersoninfo();
        if (psinfo.size() > 0) {
            Name.setText(psinfo.get(0).getName1());
            PhoneNumber.setText(psinfo.get(0).getPhoneNumber1());
            Email.setText(psinfo.get(0).getEmail1());
            Ministry.setText(psinfo.get(0).getMinistry1());
            if (psinfo.get(0).getSex().equals("1")) {
                man.setChecked(true);
            } else if (psinfo.get(0).getSex().equals("0")) {
                woman.setChecked(true);
            }
            deletepersoninfo();
        } else {
            Intent intent = new Intent(this, tab.class);
            startActivity(intent);
            Toast.makeText(this, "请重新查询！", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void initView() {

        Name = findViewById(R.id.Name);
        Sex = findViewById(R.id.Sex);
        PhoneNumber = findViewById(R.id.PhoneNumber);
        Email = findViewById(R.id.Email);
        Ministry = findViewById(R.id.Ministry);
        zhuce = findViewById(R.id.zhuce);
        man = findViewById(R.id.man);
        woman = findViewById(R.id.woman);

        zhuce.setOnClickListener(this);
        // 取出号码

        Sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // 获取用户选中的性别

                switch (checkedId) {
                    case R.id.man:
                        Sex1 = 1;
                        break;
                    case R.id.woman:
                        Sex1 = 0;
                        break;
                    default:
                        break;
                }

                // 消息提示.
//                if (Sex1 == 1) {
//                   // Toast.makeText(ReRegist.this,
//                     //       "选择的性别是：男", Toast.LENGTH_SHORT).show();
//                } else Toast.makeText(ReRegist.this,
//                        "选择的性别是：女", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View v) {

        Name1 = Name.getText().toString().trim();
        PhoneNumber1 = PhoneNumber.getText().toString().trim();
        Email1 = Email.getText().toString().trim();
        Ministry1 = Ministry.getText().toString().trim();


        if (TextUtils.isEmpty(Name1)) {
            Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Sex1 == 3) {
            Toast.makeText(this, "请选择性别", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(PhoneNumber1)) {
            Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Email1)) {
            Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Ministry1)) {
            Toast.makeText(this, "请填写部门", Toast.LENGTH_SHORT).show();
            return;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {

                sendRequest(Name1, Sex1, PhoneNumber1, Email1, Ministry1);
            }
        }).start();
    }

    private void sendRequest(String Name1, int Sex1, String PhoneNumber1, String Email1, String Ministry1) {
        Map map = new HashMap();

        map.put("Token", select());
        map.put("Name", Name1);
        map.put("Sex", Sex1);
        map.put("PhoneNumber", PhoneNumber1);
        map.put("Email", Email1);
        map.put("Ministry", Ministry1);


        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();

        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()

                .url("http://39.96.68.13:8080/SmartRoom/ReRegistServlet")
                .post(body)
                .build();
        Call call = okhttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ReRegist.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
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
                if (status.equals("0")) {
                    MyNotification notify = new MyNotification(getApplicationContext());
                    notify.MyNotification("智能会议室", "修改个人信息成功", R.drawable.iconsmall, "reregist", "修改个人信息", 20, "修改");
                    Toast.makeText(ReRegist.this, "修改个人信息成功！", Toast.LENGTH_SHORT).show();
                    searchRegistServer serg = new searchRegistServer();
                    serg.setContent(ReRegist.this);
                    serg.startsearchRegist(select());
                    finish();
                } else if (status.equals("-1")) {
                    Toast.makeText(ReRegist.this, "修改个人信息失败！", Toast.LENGTH_SHORT).show();
                } else if (status.equals("-3")) {
                    Toast.makeText(ReRegist.this, "token失效，请重新登录！", Toast.LENGTH_SHORT).show();
                    saveDeviceInfo.savelogin(getApplicationContext(), "0");
                    relog();
                }
            }
        });
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

    public void relog() {
        Intent intent;
        intent = new Intent(this, Login_noToken.class);
        startActivityForResult(intent, 0);
        finish();
    }

    //查找
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

    public ArrayList<personinfo> selectPersoninfo() {
        SQLiteDatabase db = helper1.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from registinfo ", null);
        String Name1 = null;
        String PhoneNumber1 = null;
        String Email1 = null;
        String Ministry1 = null;
        String Sex1 = null;

        while (cursor.moveToNext()) {
            personinfo data = new personinfo();
            Name1 = cursor.getString(cursor.getColumnIndex("Name"));
            PhoneNumber1 = cursor.getString(cursor.getColumnIndex("PhoneNumber"));
            Email1 = cursor.getString(cursor.getColumnIndex("Email"));
            Ministry1 = cursor.getString(cursor.getColumnIndex("Ministry"));
            Sex1 = cursor.getString(cursor.getColumnIndex("Sex"));
            data.setSex(Sex1);
            data.setMinistry1(Ministry1);
            data.setName1(Name1);
            data.setPhoneNumber1(PhoneNumber1);
            data.setEmail1(Email1);

            thispersoninfo.add(data);
        }
//        Log.d("ddd", "查出来的  楼号"+bookinfos.get(0).getBuildNumber()+"  房间号   "+bookinfos.get(0).getRoomNumber());
        db.close();
        return thispersoninfo;
    }

    public void deletepersoninfo() {

        SQLiteDatabase db = helper1.getWritableDatabase();
        int i = db.delete("registinfo", null, null);
        if (i == 0) {
            Log.d("aaaa", "deletepersoninfo  删除不成功");
        } else {
            Log.d("aaaa", "deletepersoninfo  删除成功");
        }
        db.close();
    }


}

