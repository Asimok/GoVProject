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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mq661.govproject.Login_Register.Login_noToken;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.SearchRoom.searchroom_handler_forchange_only;
import com.example.mq661.govproject.tools.MyNotification;
import com.example.mq661.govproject.tools.saveDeviceInfo;
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

public class changeroom extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    EditText BuildNumber, RoomNumber, Size;
    TextView zhuangtai;
    Button commit;
    CheckBox weixiu;
    Spinner MeetingRoomLevel, Function;
    private OkHttpClient okhttpClient;
    private tokenDBHelper helper;
    private String BuildNumber1, RoomNumber1, Size1, Function1, Function2, MeetingRomeLevel2, Token1, weixiu1 = "0", level = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wty_changeroom_layout);
        helper = new tokenDBHelper(this);

        initView();

    }

    private void initView() {

        BuildNumber = findViewById(R.id.BuildNumber);
        RoomNumber = findViewById(R.id.RoomNumber);

        Size = findViewById(R.id.Size);
        Function = findViewById(R.id.Function);
        MeetingRoomLevel = findViewById(R.id.MeetingRomeLevel);
        weixiu = findViewById(R.id.weixiu);
        commit = findViewById(R.id.commit);
        zhuangtai = findViewById(R.id.zhuangtai);
        Function.setOnItemSelectedListener(this);
        MeetingRoomLevel.setOnItemSelectedListener(this);
        commit.setOnClickListener(this);
        // 提交修改


    }


    @Override
    public void onClick(View v) {

        BuildNumber1 = BuildNumber.getText().toString().trim();
        RoomNumber1 = RoomNumber.getText().toString().trim();
        Size1 = Size.getText().toString().trim();
        Function2 = Function1.trim();
        MeetingRomeLevel2 = level;

        Token1 = select();
        if (TextUtils.isEmpty(BuildNumber1)) {
            Toast.makeText(this, "请输入楼号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(RoomNumber1)) {
            Toast.makeText(this, "请输入房间号", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(Size1)) {
            Toast.makeText(this, "请输入容量", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Function2)) {
            Toast.makeText(this, "请输入功能", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(level)) {
            Toast.makeText(this, "请填写会议室等级", Toast.LENGTH_SHORT).show();
            return;
        }
        if (weixiu.isChecked()) {
            weixiu1 = "2";
        } else weixiu1 = "0";
        Log.d("bb", "weixiu1    " + weixiu1);
        if (TextUtils.isEmpty(Token1)) {
            Toast.makeText(this, "未获取到Token", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                sendRequest(BuildNumber1, RoomNumber1, Size1, Function2, MeetingRomeLevel2, Token1, weixiu1);
            }
        }).start();
    }

    private void sendRequest(String BuildNumber1, String RoomNumber1, String Size1,
                             String Function1, String MettingRomeLevel1, String Token1, String weixiu2) {
        Map map = new HashMap();
        map.put("BuildingNumber", BuildNumber1);
        map.put("RoomNumber", RoomNumber1);
        map.put("Size", Size1);
        map.put("Function", Function1);
        map.put("MeetingRoomLevel", MettingRomeLevel1);
        map.put("Token", Token1);
        map.put("IsMeeting", weixiu2);


        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();


        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()

                .url("http://39.96.68.13:8080/SmartRoom/ChangeServlet")

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
                } else if (status.equals("0")) {
                    MyNotification notify = new MyNotification(getApplicationContext());
                    notify.MyNotification("智能会议室", "房间修改成功", R.drawable.change, "changeroom", "修改房间", 6, "修改");
                    Toast.makeText(changeroom.this, "修改成功！", Toast.LENGTH_SHORT).show();
                } else if (status.equals("-3")) {
                    Toast.makeText(changeroom.this, "token失效，请重新登录！", Toast.LENGTH_SHORT).show();
                    delete(Token1);
                    saveDeviceInfo.savelogin(getApplicationContext(), "0");
                    relog();
                } else if (status.equals("-2")) {
                    Toast.makeText(changeroom.this, "容量一栏请输入数字！", Toast.LENGTH_SHORT).show();
                } else if (status.equals("-5")) {
                    Toast.makeText(changeroom.this, "您没有进行此项操作的权限！", Toast.LENGTH_SHORT).show();
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

        db.close();
    }

    public void delete(String token) {

        SQLiteDatabase db = helper.getWritableDatabase();


        int i = db.delete("token", "token=?", new String[]{token});

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
            case R.id.Function:
//                if (content.equals("多媒体房间")) {
//                    Toast.makeText(changeroom.this, "选择的功能是：" + content,
//                            Toast.LENGTH_SHORT).show();
//                } else if (content.equals("普通房间")) {
//                    Toast.makeText(changeroom.this, "选择的功能是：" + content,
//                            Toast.LENGTH_SHORT).show();
//                }
                Function1 = content;
                break;
            case R.id.MeetingRomeLevel:
//                if (content.equals("董事长")) {
//                    Toast.makeText(changeroom.this, "选择的最低可使用职务是：" + content,
//                            Toast.LENGTH_SHORT).show();
//                } else if (content.equals("总经理")) {
//                    Toast.makeText(changeroom.this, "选择的最低可使用职务是：" + content,
//                            Toast.LENGTH_SHORT).show();
//                } else if (content.equals("部门经理")) {
//                    Toast.makeText(changeroom.this, "选择的最低可使用职务是：" + content,
//                            Toast.LENGTH_SHORT).show();
//                }
                level = content;
                break;
            default:
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void searchroom2(View v) {
        Intent intent;
        intent = new Intent(this, searchroom_handler_forchange_only.class);
        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (!(data.getStringExtra("BuildingNumber").equals("空的"))) {
            BuildNumber.setText(data.getStringExtra("BuildingNumber"));
            RoomNumber.setText(data.getStringExtra("RoomNumber"));


            if (data.getStringExtra("IsMeeting").equals("空闲")) {
                weixiu.setChecked(false);
                zhuangtai.setText("空闲");
            } else if (data.getStringExtra("IsMeeting").equals("维修")) {
                weixiu.setChecked(true);
                zhuangtai.setText("维修");
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


