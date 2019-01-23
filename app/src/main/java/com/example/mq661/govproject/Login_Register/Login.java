package com.example.mq661.govproject.Login_Register;



import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mq661.govproject.AlterRoom.alterroom;
import com.example.mq661.govproject.Main.MainInterfaceNow;
import com.example.mq661.govproject.Main.MainInterfaceNow_handler;
import com.example.mq661.govproject.Main.MainInterfaceToday;
import com.example.mq661.govproject.Main.MainInterfaceToday_handler;
import com.example.mq661.govproject.MainInterface.ViewPagerCustomActivity;
import com.example.mq661.govproject.MainInterface.tab;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.mytoast.ToastUtil;
import com.example.mq661.govproject.mytoken.tokenDBHelper;
import com.example.mq661.govproject.qqnavigationdemo.MainActivity;
import com.example.mq661.govproject.repassword.inputmail;
import com.example.mq661.govproject.tools.TokenUtil;

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
    Map<String, String> userInfo;
    private String zhanghu1,mima1,Token;
    private tokenDBHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yuangong_login);
        helper=new tokenDBHelper(this);
        initView();

    }

    private void initView() {

        zhanghu = findViewById(R.id.zhanghao);
        mima = findViewById(R.id.mima);
        CK = findViewById(R.id.checkBox);
        login=findViewById(R.id.login);
        login.setOnClickListener(this);
        // 记住密码功能
        userInfo = saveinfo.getUserInfo(this);
       // usertoken = savetoken.getUsertoken(this);
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

//                if(userInfo.get("Token").toString().isEmpty())
//                {
//                    Token =TokenUtil.genToken();
//                    Toast.makeText(Login.this,"新的"+Token,Toast.LENGTH_SHORT).show();
//                }
//                else if(!TokenUtil.verificationToken(TokenUtil.genToken()).equals("成功"))
//                {
//                    Token =TokenUtil.genToken();
//                    Toast.makeText(Login.this,"已经替换无效token"+Token,Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Token=userInfo.get("Token");
//                    Toast.makeText(Login.this,"旧的"+Token,Toast.LENGTH_SHORT).show();
//                }


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
       // String Token1=usertoken.get("Token");//读本地
        //   TokenUtil.verificationToken(TokenUtil.genToken());

        String Token1=select();
        Toast.makeText(Login.this,"查出来的"+Token1,Toast.LENGTH_SHORT).show();

        if(Token1==null)
        {
            Token =TokenUtil.genToken();
           // savetoken.saveUsertoken(this, Token);//存本地
            insert(Token);
            Toast.makeText(Login.this,"新的"+Token,Toast.LENGTH_SHORT).show();
        }
        else Token =Token1;

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
                sendRequest(zhanghu.getText().toString(),mima.getText().toString(),Token);
            }
        }).start();
    }

    private void sendRequest(String zhanghu1,String mima1,final String Token2) {
        Map map = new HashMap();
        map.put("zhanghu", zhanghu1);
        map.put("mima", mima1);
        map.put("Token",Token2);

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
                .url("http://39.96.68.13:8080/SmartRoom/LoginServlet")//MQ
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
                    Toast.makeText(Login.this, "登录失败！", Toast.LENGTH_SHORT).show();
                } else if (status.equals("0")) {
                    //Toast.makeText(Login.this, "登录成功！", Toast.LENGTH_LONG).show();



                    Toast toast=new Toast(getApplicationContext());

//创建一个填充物,用于填充Toast
                    LayoutInflater inflater = LayoutInflater.from(Login.this);

//填充物来自的xml文件,在这个改成一个view
//实现xml到view的转变哦
                    View view =inflater.inflate(R.layout.toast,null);

//不一定需要，找到xml里面的组件，设置组件里面的具体内容
                    ImageView imageView1=view.findViewById(R.id.iv_toast);
                    TextView textView1=view.findViewById(R.id.tv_toast);
                    imageView1.setImageResource(R.drawable.icon3);
                    textView1.setText("登录成功！");

//把填充物放进toast
                    toast.setView(view);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.setDuration(Toast.LENGTH_LONG);

//展示toast
                    //toast.show();

                    ToastUtil.makeText(Login.this,"登录成功！",ToastUtil.LENGTH_SHORT).show();



                    update(Token);
                    main();
                }
                else if (status.equals("-2")) {
                    Toast.makeText(Login.this, "账户名非法！请重新登录", Toast.LENGTH_SHORT).show();
                    relog();
                }

                else if (status.equals("-5")) {
                    Toast.makeText(Login.this, "账户名不存在！请注册", Toast.LENGTH_SHORT).show();
                }
                else if (status.equals("-3")) {
                    Toast.makeText(Login.this, "token为空，请重新登录！", Toast.LENGTH_SHORT).show();
                    relog();
                }

            }
        });
    }

    public void zhuce(View view) {
        Intent intent;
        intent = new Intent(this, zhuce.class);
        startActivityForResult(intent, 0);
    }

    public void room(View view) {
        Intent intent;
        intent = new Intent(this, alterroom.class);
        startActivityForResult(intent, 0);
    }
    public void relog() {
        Intent intent;
        intent = new Intent(this, Login.class);
        startActivityForResult(intent, 0);
        finish();
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

    public void forgetmima(View view) {
        Intent intent;
        intent = new Intent(this, inputmail.class);
        startActivityForResult(intent, 0);
        finish();
    }

    public void mainintrefaceNow(View view) {
        Intent intent;
        intent = new Intent(this, MainInterfaceNow_handler.class);
        startActivityForResult(intent, 0);
        //finish();
    }

    public void mainintrefaceToday(View view) {
        Intent intent;
        intent = new Intent(this, MainInterfaceToday_handler.class);
        startActivityForResult(intent, 0);
    }
    public void main()
    {
        Intent intent;
        intent = new Intent(this, tab.class);
        startActivityForResult(intent, 0);
    }

    public void main(View view) {
        Intent intent;
        intent = new Intent(this, ViewPagerCustomActivity.class);
        startActivityForResult(intent, 0);
    }

    public void QQ(View view) {
        Intent intent;
        intent = new Intent(this, tab.class);
        startActivityForResult(intent, 0);
    }
}


