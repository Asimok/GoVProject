package com.example.mq661.govproject.Login_Register;


import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mq661.govproject.tools.bookinfo;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.tools.MyNotification;
import com.example.mq661.govproject.tools.bookinfoDBHelper;
import com.example.mq661.govproject.tools.saveDeviceInfo;
import com.example.mq661.govproject.tools.tokenDBHelper;
import com.example.mq661.govproject.tools.userDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Logout extends AppCompatActivity implements View.OnClickListener {
    Button logout;
    bookinfo mybook=new bookinfo();
    private OkHttpClient okhttpClient;
    private userDBHelper helper1;
    private String Token,zhanghu,name;
    TextView tvzhanghu,tvname,bookinfo;
    Map<String, String> countlogin;
    private tokenDBHelper helper;
    private bookinfoDBHelper helper3;
    ArrayList<bookinfo> bookinfos;
    private ListView bookinfo2;
    private ArrayList<bookroomInfoAdapter> data;
    LinearLayout linear;
    String room[];
    String room1;
   // int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wty_logout_layout);
        helper=new tokenDBHelper(this);
        helper1=new userDBHelper(this);
        helper3=new bookinfoDBHelper(this);
        bookinfos=new ArrayList<bookinfo>();
        linear=findViewById(R.id.linear3);
        //bookinfo2=findViewById(R.id.bookinfo1);
        data=new ArrayList<bookroomInfoAdapter>();
       // linear.removeAllViews();
        initView();
        initdata();
    }

    private void initView() {

        logout=findViewById(R.id.logout);
        tvname=findViewById(R.id.name);
        bookinfo=findViewById(R.id.bookinfo);
        tvzhanghu=findViewById(R.id.zhanghu);
        logout.setOnClickListener(this);
        Toast.makeText(this,"刷新了" ,Toast.LENGTH_SHORT ).show();
    }
    private void initdata() {
        zhanghu=userselect()[0];
        name=userselect()[1];
        tvzhanghu.setText(zhanghu);
        tvname.setText(name);

        bookinfoServer bookinfo1=new bookinfoServer();
        bookinfo1.setContent(Logout.this);
        try {
            data=bookinfo1.startGetInfo();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<bookinfo> data=bookselect();

        for (int i = 0; i < data.size(); i++) {
            Log.d("ccc", i+data.get(i).getBuildNumber());
            TextView tv =new TextView(this);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            tv.setText(data.get(i).getBuildNumber()+"   "+data.get(i).getRoomNumber()+"\n预约日期:    "+data.get(i).getDays()+"  "+data.get(i).getTime()+"\n预定时间:    "+data.get(i).getBooktime());
            linear.addView(tv);

                    }
        bookdelete();
       // bookinfo.setText(room.toString());
//        bookinfoServer bookinfo1=new bookinfoServer();
//        bookinfo1.setContent(Logout.this);
//        try {
//            data=bookinfo1.startGetInfo();
//            Log.d("ddd",data.toString());
//            bookinfo2.setAdapter(new MyAdapter());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onClick(View v) {
         Token=select();
        Toast.makeText(Logout.this,"查出来的"+Token,Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRequest(Token);
            }
        }).start();
    }

    private void sendRequest(String Token) {
        Map map = new HashMap();
        map.put("Token",Token);

        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
//        Log.d("这将JSON对象转换为json字符串", jsonString);
        RequestBody body = RequestBody.create(null, jsonString);  //以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                //dafeng 192.168.2.176
                //  .url("http://192.168.2.176:8080/LoginProject/login")
                // .url("http://192.168.43.174:8080/LoginProject/login")
                // .url("http://39.96.68.13:8080/SmartRoom/LoginServlet")
                .url("http://39.96.68.13:8080/SmartRoom/LogoutServlet")//MQ
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
                        Toast.makeText(Logout.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
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
                if (status.equals("-3")) {
                    Toast.makeText(Logout.this, "token出错！返回重新登陆", Toast.LENGTH_SHORT).show();
                    delete(Token);
                    saveDeviceInfo.savelogin(getApplicationContext(),"0");
                    relog();
                } else if (status.equals("quit")) {
                    MyNotification notify=new MyNotification(getApplicationContext());
                    notify.MyNotification("智能会议室","注销成功",R.drawable.logout,"Logout","注销",9,"注销");
                    delete(Token);
                    String zhanghao=userselect()[0];
                    Log.d("ddd", "要删除的账户   "+zhanghao);
                    userdelete(zhanghao);
                    saveDeviceInfo.savelogin(getApplicationContext(),"0");
                    relog();
                    Toast.makeText(Logout.this, "注销成功！", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private class MyAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return data.size();

        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view =View.inflate(Logout.this,R.layout.bookroom_info_adp_layout,null);


            TextView BuildingNumber = view.findViewById(R.id.BuildNumber);
            TextView  RoomNumber = view.findViewById(R.id.RoomNumber);
            TextView Time = view.findViewById(R.id.Time);
            TextView booktime=view.findViewById(R.id.booktime);
            TextView Days=view.findViewById(R.id.Days3);

            BuildingNumber.setText(data.get(position).getBuildingNumber());
            booktime.setText(data.get(position).getNowTime());
            RoomNumber.setText(data.get(position).getRoomNumber());
            Time.setText(data.get(position).getTime());
            Days.setText(data.get(position).getDays());
            return view;
        }
    }
    public void insert(String token){


        //自定义增加数据
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values=new ContentValues();
        //String token =mytoken.getMytoken();

        values.put("token", token);
        long l = db.insert("token", null, values);

        if(l==-1){
            Toast.makeText(this, "插入不成功",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "插入成功"+l,Toast.LENGTH_SHORT).show();}
        db.close();
    }

    public void update(String token){


        //自定义更新
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values=new ContentValues();
        //     String oldtoken=mytoken.getMytoken();
        values.put("token", token);
//        int i = db.update("token", values, "token=?",new String[]{oldtoken});
        int i = db.update("token", values, null,null);
        if(i==0){
            Toast.makeText(this, "更新不成功",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "更新成功"+i,Toast.LENGTH_SHORT).show();}
        db.close();
    }

    public void delete(String token){

        SQLiteDatabase db = helper.getWritableDatabase();



        int i = db.delete("token", "token=?",new String[]{token});
        if(i==0){
            Toast.makeText(this, "删除不成功",Toast.LENGTH_SHORT).show();
        }else{  Toast.makeText(this, "删除成功"+i,Toast.LENGTH_SHORT).show();}
        db.close();

    }

    //查找
    public String select(){

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from token", null);
        String token1=null;
        while(cursor.moveToNext()){
            token1=cursor.getString(0);
        }
        db.close();
        return token1;
    }
    public String []  userselect(){

        SQLiteDatabase db = helper1.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from user", null);
        String zhanghao=null;
        String name=null;
        while(cursor.moveToNext()){
            zhanghao=cursor.getString(cursor.getColumnIndex("zhanghao"));
            name=cursor.getString(cursor.getColumnIndex("name"));
        }
        String [] user={zhanghao,name};
        Log.d("ddd", "查出来的  账户"+user[0]+"  姓名   "+user[1]);
        db.close();
        return user;

    }
    public void userdelete(String zhanghu3){

        SQLiteDatabase db = helper1.getWritableDatabase();
        int i = db.delete("user", "zhanghao=?",new String[]{zhanghu3});
        if(i==0){
            Toast.makeText(this, "删除用户信息不成功",Toast.LENGTH_SHORT).show();
        }else{  Toast.makeText(this, "删除用户信息成功",Toast.LENGTH_SHORT).show();
            }
        db.close();

    }
    public void bookdelete(){

        SQLiteDatabase db = helper3.getWritableDatabase();
        int i = db.delete("bookinfo", "zhanghu=?",new String[]{userselect()[0]});
        if(i==0){
            Toast.makeText(this, "删除用户信息不成功",Toast.LENGTH_SHORT).show();
        }else{  Toast.makeText(this, "删除用户信息成功",Toast.LENGTH_SHORT).show();
        }
        db.close();

    }

    public List<bookinfo> bookselect(){
        SQLiteDatabase db = helper3.getReadableDatabase();
        String zhanghu=userselect()[0];
        Cursor cursor = db.rawQuery("select * from bookinfo where zhanghu=?", new String[]{zhanghu});
        String BuildNumber=null;
        String RoomNumber=null;
        String Time=null;
        String days=null;
        String booktime=null;
        while(cursor.moveToNext()){
            bookinfo data=new bookinfo();
            BuildNumber=cursor.getString(cursor.getColumnIndex("BuildNumber"));
            RoomNumber=cursor.getString(cursor.getColumnIndex("RoomNumber"));
            Time=cursor.getString(cursor.getColumnIndex("Time"));
            days=cursor.getString(cursor.getColumnIndex("days"));
            booktime=cursor.getString(cursor.getColumnIndex("booktime"));
            data.setDays(days);
            data.setTime(Time);
            data.setRoomNumber(RoomNumber);
            data.setBuildNumber(BuildNumber);
            data.setBooktime(booktime);
            Log.d("ccc", "select 里的"+BuildNumber);
            bookinfos.add(data);
        }
        //String [] bookinfo={BuildNumber,RoomNumber,Time,days};
//        Log.d("ddd", "查出来的  楼号"+bookinfos.get(0).getBuildNumber()+"  房间号   "+bookinfos.get(0).getRoomNumber());
        db.close();
        return bookinfos;
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

