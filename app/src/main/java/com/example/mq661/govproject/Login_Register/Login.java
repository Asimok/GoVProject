package com.example.mq661.govproject.Login_Register;


import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mq661.govproject.AlterRoom.gov_Founction;
import com.example.mq661.govproject.MainInterface.FirstStartActivity;
import com.example.mq661.govproject.MainInterface.tab;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.mytoast.ToastUtil;
import com.example.mq661.govproject.repassword.inputmail;
import com.example.mq661.govproject.tools.MyNotification;
import com.example.mq661.govproject.tools.TokenUtil;
import com.example.mq661.govproject.tools.saveDeviceInfo;
import com.example.mq661.govproject.tools.tokenDBHelper;
import com.example.mq661.govproject.tools.userDBHelper;

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
    EditText zhanghu, mima;
    CheckBox CK;
    Button login;
    Map<String, String> userInfo, countInfo, countlogin;
    String count = "0", logincount = "0";
    private OkHttpClient okhttpClient;
    private tokenDBHelper helper;
    private userDBHelper helper1;
    private String zhanghu2, mima2, Token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        helper = new tokenDBHelper(this);
        helper1 = new userDBHelper(this);

        countInfo = saveDeviceInfo.getcount(this);
        count = countInfo.get("count");
        //判断程序是第几次运行，如果是第一次运行则跳转到引导页面
        if (count.equals("0")) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), FirstStartActivity.class);
            startActivity(intent);
            saveDeviceInfo.savelogin(this, "0");
            Log.d("ddd", "logincount重置   " + logincount);
            this.finish();
        }
        saveDeviceInfo.savecount(this);

        countlogin = saveDeviceInfo.getlogin(this);
        logincount = countlogin.get("logincount");
        Log.d("ddd", "logincount   " + logincount);
        //判断程序是否已经登录，如果未注销第一次运行则跳转到主页面
        if (!logincount.equals("0")) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), tab.class);
            startActivity(intent);
            this.finish();
        }
        initView();

    }

    private void initView() {

        zhanghu = findViewById(R.id.zhanghao);
        mima = findViewById(R.id.mima);
        CK = findViewById(R.id.checkBox);
        login = findViewById(R.id.login);
        login.setOnClickListener(this);
        // 记住密码功能
        userInfo = saveDeviceInfo.getUserInfo(this);
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
        } catch (Exception e) {
        }
    }


    public void check(View v) {
    }


    public void login(View v) {

    }

    @Override
    public void onClick(View v) {

        String number = zhanghu.getText().toString().trim();
        zhanghu2 = number;
        String password = mima.getText().toString();
        mima2 = password;


        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "请输入员工号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        String Token1 = select();
        // Toast.makeText(Login.this, "查出来的" + Token1, Toast.LENGTH_SHORT).show();

        if (Token1 == null) {
            Token = TokenUtil.genToken();
            insert(Token);
            //  Toast.makeText(Login.this, "新的" + Token, Toast.LENGTH_SHORT).show();
        } else Token = Token1;

        try {
            if (CK.isChecked()) {

                boolean isSaveSuccess = saveDeviceInfo.saveUserInfo(this, number, password);
                if (isSaveSuccess) {
                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                number = "请输入员工号";
                password = "请输入密码";

                saveDeviceInfo.saveUserInfo(this, number, password);
            }
        } catch (Exception e) {
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRequest(zhanghu.getText().toString(), mima.getText().toString(), Token);
            }
        }).start();
    }

    private void sendRequest(String zhanghu1, String mima1, final String Token2) {
        Map map = new HashMap();
        map.put("zhanghu", zhanghu1);
        map.put("mima", mima1);
        map.put("Token", Token2);

        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();

        RequestBody body = RequestBody.create(null, jsonString);  //以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()

                .url("http://39.96.68.13:8080/SmartRoom/LoginServlet")//MQ
                .post(body)
                .build();
        okhttp3.Call call = okhttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Login.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
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
                    String Name = jsonObj.getString("Name");

                    showRequestResult(status, Name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void showRequestResult(final String status, final String name) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {
                if (status.equals("-1")) {
                    Toast.makeText(Login.this, "登录失败！", Toast.LENGTH_SHORT).show();
                } else if (status.equals("0")) {
                    //Toast.makeText(Login.this, "登录成功！", Toast.LENGTH_LONG).show();


                    Toast toast = new Toast(getApplicationContext());

//创建一个填充物,用于填充Toast
                    LayoutInflater inflater = LayoutInflater.from(Login.this);

//填充物来自的xml文件,在这个改成一个view
//实现xml到view的转变哦
                    View view = inflater.inflate(R.layout.toast, null);

//不一定需要，找到xml里面的组件，设置组件里面的具体内容
                    ImageView imageView1 = view.findViewById(R.id.iv_toast);
                    TextView textView1 = view.findViewById(R.id.tv_toast);
                    imageView1.setImageResource(R.drawable.icon3);
                    textView1.setText("登录成功！");

//把填充物放进toast
                    toast.setView(view);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);

//展示toast
                    //toast.show();

                    ToastUtil.makeText(Login.this, "登录成功！", ToastUtil.LENGTH_SHORT).show();

                    update(Token);

                    if (userselect()[0] == null) {
                        insertUser(zhanghu2, name);
                    } else
                        userupdate(zhanghu2, name);
                    saveDeviceInfo.savelogin(getApplicationContext(), "1");
                    main();
                    MyNotification notify = new MyNotification(getApplicationContext());
                    notify.MyNotification("智能会议室", "登录成功", R.drawable.book2, "登录1", "登录", 5, "登录");

                    finish();
                } else if (status.equals("-2")) {
                    Toast.makeText(Login.this, "账户名非法！请重新登录", Toast.LENGTH_SHORT).show();
                    delete(Token);
                    saveDeviceInfo.savelogin(getApplicationContext(), "0");
                    relog();
                } else if (status.equals("-5")) {
                    Toast.makeText(Login.this, "账户名不存在！请注册", Toast.LENGTH_SHORT).show();
                } else if (status.equals("-3")) {
                    Toast.makeText(Login.this, "token为空，请重新登录！", Toast.LENGTH_SHORT).show();
                    delete(Token);
                    saveDeviceInfo.savelogin(getApplicationContext(), "0");
                    relog();
                }

            }
        });
    }

    public void zhuce(View view) {
        Intent intent;
        intent = new Intent(this, Regist.class);
        startActivityForResult(intent, 0);
    }

    public void room(View view) {
        Intent intent;
        intent = new Intent(this, gov_Founction.class);
        startActivityForResult(intent, 0);
    }

    public void relog() {
        Intent intent;
        intent = new Intent(this, Login_noToken.class);
        startActivityForResult(intent, 0);
        finish();
    }


    public void insert(String token) {


        //自定义增加数据
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //String token =mytoken.getMytoken();

        values.put("token", token);
        long l = db.insert("token", null, values);


        db.close();
    }

    public void update(String token) {


        //自定义更新
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("token", token);
        int i = db.update("token", values, null, null);
        if (i == 0) {
            Toast.makeText(this, "更新不成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
        }
        db.close();
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

    public void insertUser(String zhanghu3, String name3) {
        //自定义增加数据
        SQLiteDatabase db1 = helper1.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("zhanghao", zhanghu3);
        values.put("name", name3);
        long l = db1.insert("user", null, values);


        db1.close();
    }

    public void userupdate(String zhanghu3, String name3) {


        //自定义更新
        SQLiteDatabase db = helper1.getWritableDatabase();
        ContentValues values = new ContentValues();
        Log.d("ddd", "更新的账户   " + zhanghu3);
        values.put("zhanghao", zhanghu3);
        values.put("name", name3);
        int i = db.update("user", values, null, null);
//        if (i == 0) {
//            Toast.makeText(this, "更新用户信息不成功", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "更新用户信息成功", Toast.LENGTH_SHORT).show();
//        }
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

    public void userdelete(String zhanghu3) {

        SQLiteDatabase db = helper1.getWritableDatabase();
        Log.d("ddd", "删除账户   " + zhanghu3);
        int i = db.delete("user", "zhanghao=?", new String[]{zhanghu3});
//        if(i==0){
//            Toast.makeText(this, "删除用户信息不成功",Toast.LENGTH_SHORT).show();
//        }else{  Toast.makeText(this, "删除用户信息成功",Toast.LENGTH_SHORT).show();}
        db.close();

    }

    public void forgetmima(View view) {
        Intent intent;
        intent = new Intent(this, inputmail.class);
        startActivityForResult(intent, 0);
        finish();
    }

    public void main() {
        Intent intent;
        intent = new Intent(this, tab.class);
        startActivityForResult(intent, 0);
        finish();
    }


    public void QQ(View view) {
        Intent intent;
        intent = new Intent(this, tab.class);
        startActivityForResult(intent, 0);

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


    public void sendSubscribeMsg(View view) {

        MyNotification notify = new MyNotification(getApplicationContext());
        notify.MyNotification("1", "2", R.drawable.icon3, "ct2", "sub", 3, "测试");
    }


}


