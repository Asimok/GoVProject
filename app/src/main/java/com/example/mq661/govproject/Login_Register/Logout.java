package com.example.mq661.govproject.Login_Register;



import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mq661.govproject.AlterRoom.addroom;
import com.example.mq661.govproject.AlterRoom.alterroom;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.mytoken.tokenDBHelper;
import com.example.mq661.govproject.tools.TokenUtil;
import com.example.mq661.govproject.tools.tomd5;

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

public class Logout extends AppCompatActivity implements View.OnClickListener {
    TextView quit;
    Button logout;
    private OkHttpClient okhttpClient;
   // Map<String, String> usertoken;
    private String Token;
    private tokenDBHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logout_layout);
        helper=new tokenDBHelper(this);

        initView();

    }

    private void initView() {

        quit = findViewById(R.id.quit);
        logout=findViewById(R.id.logout);
        logout.setOnClickListener(this);
       // usertoken = savetoken.getUsertoken(this);
    }


    @Override
    public void onClick(View v) {

//        String Token1=usertoken.get("Token");//读本地
//        Toast.makeText(Logout.this,"查出来的"+Token1,Toast.LENGTH_SHORT).show();
//        //   TokenUtil.verificationToken(TokenUtil.genToken());
//        if(Token1==null)
//        {
//            Token =TokenUtil.genToken();
//            //savetoken.saveUsertoken(this, Token);//存本地
//            Toast.makeText(Logout.this,"新的"+Token,Toast.LENGTH_SHORT).show();
//        }
//        else Token =Token1;

         Token=select();
        Toast.makeText(Logout.this,"查出来的"+Token,Toast.LENGTH_SHORT).show();

//        if(Token1==null)
//        {
//            Token =TokenUtil.genToken();
//            // savetoken.saveUsertoken(this, Token);//存本地
//            insert(Token);
//            Toast.makeText(Logout.this,"新的"+Token,Toast.LENGTH_SHORT).show();
//        }
//        else Token =Token1;


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
                .url("http://192.168.43.174:8080/SmartRoom/LogoutServlet")//MQ
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
                    relog();
                } else if (status.equals("quit")) {
                    Toast.makeText(Logout.this, "注销成功！", Toast.LENGTH_SHORT).show();
                    //savetoken.saveUsertoken(Logout.this, null);
                    delete(Token);
                    relog();

                }

            }
        });
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
//            mytoken token= new mytoken();
//            token.setMytoken(cursor.getString(0));
            token1=cursor.getString(0);
        }
        db.close();
        return token1;
    }
    public void relog() {
        Intent intent;
        intent = new Intent(this, Login.class);
        startActivityForResult(intent, 0);
        finish();
    }
}

