package com.example.mq661.govproject.BookRoom;


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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mq661.govproject.Login_Register.Login_noToken;
import com.example.mq661.govproject.Participants.addPerson_handler;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.SearchRoom.searchroom_handler_forbook;
import com.example.mq661.govproject.tools.Dateadd;
import com.example.mq661.govproject.tools.MyNotification;
import com.example.mq661.govproject.tools.dateToString;
import com.example.mq661.govproject.tools.saveDeviceInfo;
import com.example.mq661.govproject.tools.tokenDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class bookroom extends AppCompatActivity implements View.OnClickListener {
    EditText BuildNumber, RoomNumber, Time;
    Button commit;
    TextView bookinfo;
    //Map<String, String> usertoken;
    private tokenDBHelper helper;
    private OkHttpClient okhttpClient;
    RadioButton today1, today2, today3;
    RadioGroup Days;
    private String BuildNumber1, RoomNumber1, Time1, Token1, IsMeeting2, today, tomorrow, afterTomorrow;
    private String days, days2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wty_bookroom_layout);
        helper = new tokenDBHelper(this);
        initView();

    }

    private void initView() {

        BuildNumber = findViewById(R.id.BuildNumber);
        RoomNumber = findViewById(R.id.RoomNumber);
        Time = findViewById(R.id.Time);
        commit = findViewById(R.id.commit);
        bookinfo = findViewById(R.id.bookinfo);
        Days = findViewById(R.id.Days);
        today1 = findViewById(R.id.today1);
        today2 = findViewById(R.id.today2);
        today3 = findViewById(R.id.today3);

        commit.setOnClickListener(this);
        // 提交修改

        //  usertoken = savetoken.getUsertoken(this);//用作读取本地token

        Days.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // 获取用户选中的性别
                //String sex = "";
                switch (checkedId) {
                    case R.id.today1:
                        try {
                            days = Dateadd.mydays(dateToString.nowdateToString(), 0);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.today2:
                        try {
                            days = Dateadd.mydays(dateToString.nowdateToString(), 1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.today3:
                        try {
                            days = Dateadd.mydays(dateToString.nowdateToString(), 2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        break;
                }

                // 消息提示
                Toast.makeText(bookroom.this,
                        "选择的日期是：" + days, Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onClick(View v) {
        String time = Time.getText().toString().trim();
        if ((time.length() == 11)) {
            if (!(time.substring(5, 6).equals("-") && time.substring(2, 3).equals(":") && time.substring(8, 9).equals(":"))) {
                Toast.makeText(this, "时间格式不合法", Toast.LENGTH_LONG).show();
            } else if (Integer.parseInt(time.substring(0, 2)) > Integer.parseInt(time.substring(6, 8))) {
                Toast.makeText(this, "开始时间不能大于结束时间", Toast.LENGTH_LONG).show();
            } else {
                days2 = days;

                BuildNumber1 = BuildNumber.getText().toString().trim();
                RoomNumber1 = RoomNumber.getText().toString().trim();
                Time1 = Time.getText().toString().trim();

                Token1 = select();
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


                if (TextUtils.isEmpty(Token1)) {
                    Toast.makeText(this, "未获取到Token", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(bookroom.this, addPerson_handler.class);
                intent.putExtra("BuildingNumber", BuildNumber1);
                intent.putExtra("RoomNumber", RoomNumber1);
                intent.putExtra("Days", days2);
                intent.putExtra("Time", Time1);
                startActivity(intent);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // sendRequest(BuildNumber1, RoomNumber1, Time1, Token1,days2);
                    }
                }).start();
            }
        } else {
            Toast.makeText(this, "时间格式不合法", Toast.LENGTH_LONG).show();

        }
    }

    private void sendRequest(String BuildNumber1, String RoomNumber1, String Time1, String Token1, String days1) {
        Map map = new HashMap();
        map.put("BuildingNumber", BuildNumber1);
        map.put("RoomNumber", RoomNumber1);
        map.put("Time", Time1);
        map.put("Token", Token1);
        map.put("Days", days1);

        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();

//        Log.d("这将JSON对象转换为json字符串", jsonString);
        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                //dafeng 192.168.2.176
                //  .url("http://192.168.2.176:8080/SmartRoom/DeleteServlet")
                // .url("http://192.168.43.174:8080/LoginProject/login")
                // .url("http://39.96.68.13:8080/SmartRoom/RegistServlet") //服务器
                .url("http://39.96.68.13:8080/SmartRoom/BookRoomServlet") //马琦IP
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
                        Toast.makeText(bookroom.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                try {
                    JSONObject jsonObj = new JSONObject(res);
                    String Status = jsonObj.getString("Status");
                    Log.d("aa", "Status   " + Status);
                    String EmployeeNumber = jsonObj.getString("EmployeeNumber");
                    String Name = jsonObj.getString("Name");
                    showRequestResult(Status, EmployeeNumber, Name);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void showRequestResult(final String Status, final String EmployeeNumber, final String Name) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {

                if (Status.equals("-1")) {
                    Toast.makeText(bookroom.this, "预定失败！", Toast.LENGTH_LONG).show();
                } else if (Status.equals("-3")) {
                    Toast.makeText(bookroom.this, "token失效，请重新登录！", Toast.LENGTH_SHORT).show();
                    delete(Token1);
                    saveDeviceInfo.savelogin(getApplicationContext(), "0");
                    relog();
                } else if (Status.equals("-5")) {
                    Toast.makeText(bookroom.this, "您不具有预定此房间的权限！", Toast.LENGTH_SHORT).show();
                } else if (Status.equals("-6")) {
                    Toast.makeText(bookroom.this, "该会议室已被预订！", Toast.LENGTH_SHORT).show();

                } else if (Status.equals("-7")) {
                    Toast.makeText(bookroom.this, "该会议室正在维修！", Toast.LENGTH_SHORT).show();

                } else if (Status.equals("0")) {
                    Toast.makeText(bookroom.this, "预定成功！", Toast.LENGTH_LONG).show();
                    bookinfo.setText("姓名:" + Name + " 员工号:" + EmployeeNumber);
                    MyNotification notify = new MyNotification(getApplicationContext());
                    notify.MyNotification("智能会议室", "房间预定成功", R.drawable.book, "bookroom", "预定房间", 7, "预定");
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


    public void insert(String token) {


        //自定义增加数据
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //String token =mytoken.getMytoken();

        values.put("token", token);
        long l = db.insert("token", null, values);

        if (l == -1) {
            Toast.makeText(this, "插入不成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "插入成功" + l, Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    public void update(String token) {


        //自定义更新
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //     String oldtoken=mytoken.getMytoken();
        values.put("token", token);
//        int i = db.update("token", values, "token=?",new String[]{oldtoken});
        int i = db.update("token", values, null, null);
        if (i == 0) {
            Toast.makeText(this, "更新不成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "更新成功" + i, Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    public void delete(String token) {

        SQLiteDatabase db = helper.getWritableDatabase();


        int i = db.delete("token", "token=?", new String[]{token});
        if (i == 0) {
            Toast.makeText(this, "删除不成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "删除成功" + i, Toast.LENGTH_SHORT).show();
        }
        db.close();

    }

    //查找
    public String select() {

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from token", null);
        String token1 = null;
        while (cursor.moveToNext()) {
//            mytoken token= new mytoken();
//            token.setMytoken(cursor.getString(0));
            token1 = cursor.getString(0);
        }
        db.close();
        return token1;
    }

    public void searchroom1(View v) {
        Intent intent;
        intent = new Intent(this, searchroom_handler_forbook.class);
        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((data.getStringExtra("BuildingNumber").equals("空的"))) {
            BuildNumber.setHint("请输入楼号");
            RoomNumber.setHint("请输入房间号");
            Time.setHint("输入格式:08:00-08:45");

        } else {
            BuildNumber.setText(data.getStringExtra("BuildingNumber"));
            RoomNumber.setText(data.getStringExtra("RoomNumber"));
            Time.setText(data.getStringExtra("Time"));
            today = dateToString.nowdateToString();
            try {
                tomorrow = Dateadd.mydays(today, 1);
                afterTomorrow = Dateadd.mydays(today, 2);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (today.equals(data.getStringExtra("Days"))) {
                Days.check(R.id.today1);
                Log.d("aaa", "设置选中已执行");
            } else if (tomorrow.equals(data.getStringExtra("Days"))) {
                Days.check(R.id.today2);
                Log.d("aaa", "设置选中已执行");
            } else if (afterTomorrow.equals(data.getStringExtra("Days"))) {
                Days.check(R.id.today3);
                Log.d("aaa", "设置选中已执行");
            }
        }
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
}



