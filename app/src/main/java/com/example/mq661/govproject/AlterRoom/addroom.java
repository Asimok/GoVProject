package com.example.mq661.govproject.AlterRoom;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mq661.govproject.Login_Register.Login;
import com.example.mq661.govproject.Login_Register.Login_noToken;
import com.example.mq661.govproject.tools.saveDeviceInfo;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.tools.MyNotification;
import com.example.mq661.govproject.tools.tokenDBHelper;

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

public class addroom extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    EditText BuildNumber, RoomNumber, Time, Size;
    Spinner Function, MeetingRoomLevel;
    Button commit;
    Map<String, String> usertoken;
    private OkHttpClient okhttpClient;
    private tokenDBHelper helper;
    private String BuildNumber1, RoomNumber1, Time1, Size1, Function1, MeetingRomeLevel1, Token1, level = null, Function2, MeetingRomeLevel2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wty_addroom_layout);
        helper = new tokenDBHelper(this);

        initView();

    }

    private void initView() {

        BuildNumber = findViewById(R.id.BuildNumber);
        RoomNumber = findViewById(R.id.RoomNumber);
       // Time = findViewById(R.id.Time);
        Size = findViewById(R.id.Size);
        Function = findViewById(R.id.function);
        MeetingRoomLevel = findViewById(R.id.MeetingRomeLevel);
//        zjl = findViewById(R.id.zjl);
//        bmjl = findViewById(R.id.bmjl);
//        dsz = findViewById(R.id.dsz);
        commit = findViewById(R.id.commit);
        Function.setOnItemSelectedListener(this);
        MeetingRoomLevel.setOnItemSelectedListener(this);
        commit.setOnClickListener(this);
        // 提交修改
        //  usertoken = savetoken.getUsertoken(this);//用作读取本地token

//        MeetingRoomLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                // 获取用户选中的性别
//                //String sex = "";
//                switch (checkedId) {
//                    case R.id.dsz:
//                        level = "董事长";
//                        break;
//                    case R.id.zjl:
//                        level = "总经理";
//                        break;
//                    case R.id.bmjl:
//                        level = "部门经理";
//                        break;
//                    default:
//                        break;
//                }
//
//                // 消息提示
//                Toast.makeText(addroom.this,
//                        "最低使用权限是：" + level, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public void onClick(View v) {
//        String time = Time.getText().toString().trim();
//        if ((time.length() == 11)) {
//            if (!(time.substring(5, 6).equals("-") && time.substring(2, 3).equals(":") && time.substring(8, 9).equals(":"))) {
//                Toast.makeText(this, "时间格式不合法", Toast.LENGTH_LONG).show();
//            } else if (Integer.parseInt(time.substring(0, 2)) > Integer.parseInt(time.substring(6, 8))) {
//                Toast.makeText(this, "开始时间不能大于结束时间", Toast.LENGTH_LONG).show();
//            } else {
                BuildNumber1 = BuildNumber.getText().toString().trim();
                RoomNumber1 = RoomNumber.getText().toString().trim();
               // Time1 = Time.getText().toString().trim();
                Size1 = Size.getText().toString().trim();
                //Function1 =   Function.getText().toString().trim());
                Function2 = Function1;

                MeetingRomeLevel2 = level;
                Token1 = select();//读本地

                if (TextUtils.isEmpty(BuildNumber1)) {
                    Toast.makeText(this, "请输入楼号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(RoomNumber1)) {
                    Toast.makeText(this, "请输入房间号", Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (TextUtils.isEmpty(Time1)) {
//                    Toast.makeText(this, "请输入时间段", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                if (TextUtils.isEmpty(Size1)) {
                    Toast.makeText(this, "请输入容量", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Function2)) {
                    Toast.makeText(this, "请输入功能", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(MeetingRomeLevel2)) {
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

                        sendRequest(BuildNumber1, RoomNumber1, Time1, Size1, Function2, MeetingRomeLevel2, Token1);
                    }

                }).start();

            }
       // }
//        else {
//            Toast.makeText(this, "时间格式不合法", Toast.LENGTH_LONG).show();
//
//        }


  //  }


    private void sendRequest(String BuildNumber1, String RoomNumber1, String Time1, String Size1,
                             String Function1, String MeetingRoomLevel1, String Token1) {
        Map map = new HashMap();
        map.put("BuildingNumber", BuildNumber1);
        map.put("RoomNumber", RoomNumber1);
        //map.put("Time", Time1);
        map.put("Size", Size1);
        map.put("Function", Function1);
        map.put("MeetingRoomLevel", MeetingRoomLevel1);
        map.put("Token", Token1);


        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();

        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                //dafeng 192.168.2.176
                .url("http://39.96.68.13:8080/SmartRoom/AddServlet")
                // .url("http://192.168.43.174:8080/LoginProject/login")
                // .url("http://39.96.68.13:8080/SmartRoom/RegistServlet") //服务器
//                .url("http://192.168.43.174:8080/SmartRoom/AddServlet") //马琦IP
                .url("http://39.96.68.13:8080/SmartRoom/AddServlet") //马琦IP2
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
                        Toast.makeText(com.example.mq661.govproject.AlterRoom.addroom.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
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
                if (status.equals("-2")) {
                    Toast.makeText(addroom.this, "增加房间信息不合法!请重新输入！", Toast.LENGTH_SHORT).show();
                } else if (status.equals("0")) {
                    MyNotification notify=new MyNotification(getApplicationContext());
                    notify.MyNotification("智能会议室","房间增加成功",R.drawable.add,"addroom","增加房间",5,"增加");
                    Toast.makeText(addroom.this, "增加成功！", Toast.LENGTH_SHORT).show();
                } else if (status.equals("-3")) {
                    Toast.makeText(addroom.this, "token失效，请重新登录！", Toast.LENGTH_SHORT).show();
                    delete(Token1);
                    saveDeviceInfo.savelogin(getApplicationContext(),"0");
                    relog();
                } else if (status.equals("-1")) {
                    Toast.makeText(addroom.this, "增加失败！", Toast.LENGTH_SHORT).show();
                } else if (status.equals("-4")) {
                    Toast.makeText(addroom.this, "增加失败,房间信息重复！", Toast.LENGTH_SHORT).show();
                }
                else if (status.equals("-5")) {
                    Toast.makeText(addroom.this, "您没有进行此项操作的权限！", Toast.LENGTH_SHORT).show();
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
            token1 = cursor.getString(0);
        }
        db.close();
        return token1;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String content = parent.getItemAtPosition(position).toString();
        switch (parent.getId()) {
            case R.id.function:
                if (content.equals("多媒体房间")) {
                    Toast.makeText(addroom.this, "选择的功能是：" + content,
                            Toast.LENGTH_SHORT).show();
                } else if (content.equals("普通房间")) {
                    Toast.makeText(addroom.this, "选择的功能是：" + content,
                            Toast.LENGTH_SHORT).show();
                }
                Function1 = content;
                break;
            case R.id.MeetingRomeLevel:
                if (content.equals("董事长")) {
                    Toast.makeText(addroom.this, "选择的最低可使用职务是：" + content,
                            Toast.LENGTH_SHORT).show();
                } else if (content.equals("总经理")) {
                    Toast.makeText(addroom.this, "选择的最低可使用职务是：" + content,
                            Toast.LENGTH_SHORT).show();
                } else if (content.equals("部门经理")) {
                    Toast.makeText(addroom.this, "选择的最低可使用职务是：" + content,
                            Toast.LENGTH_SHORT).show();
                }
                level = content;
                break;
            default:
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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


