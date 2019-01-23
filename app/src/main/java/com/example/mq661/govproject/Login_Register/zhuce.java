package com.example.mq661.govproject.Login_Register;



import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mq661.govproject.R;
import com.example.mq661.govproject.tools.tounicode;

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

public class zhuce extends AppCompatActivity implements View.OnClickListener {
    EditText zhanghu,mima,Name,PhoneNumber,Email,Ministry,remima;
    RadioGroup Sex;
    RadioButton man,woman;
    Button zhuce;
    private OkHttpClient okhttpClient;
    private String zhanghu1,mima1,Name1,PhoneNumber1,Email1,Ministry1,remima1;
    private int Sex1=3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wty_zhuce_layout);
        initView();

    }

    private void initView() {

        zhanghu = findViewById(R.id.zhanghao);
        mima = findViewById(R.id.mima);
        remima = findViewById(R.id.remima);
        Name = findViewById(R.id.Name);
        Sex=findViewById(R.id.Sex);
        PhoneNumber=findViewById(R.id.PhoneNumber);
        Email=findViewById(R.id.Email);
        Ministry=findViewById(R.id.Ministry);
        zhuce=findViewById(R.id.zhuce);
        man=findViewById(R.id.man);
        woman=findViewById(R.id.woman);

        zhuce.setOnClickListener(this);
        // 取出号码

        Sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // 获取用户选中的性别
                //String sex = "";
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
                if (Sex1 == 1) {
                    Toast.makeText(zhuce.this,
                            "选择的性别是：男", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(zhuce.this,
                        "选择的性别是：女", Toast.LENGTH_SHORT).show();
            }
        });

        }

    @Override
    public void onClick(View v) {
        // Toast.makeText(this,"登陆成功",Toast.LENGTH_LONG).show();

         zhanghu1 = zhanghu.getText().toString().trim();
         mima1 = mima.getText().toString().trim();
         remima1 = remima.getText().toString().trim();
         Name1 = tounicode.gbEncoding(Name.getText().toString().trim());

         PhoneNumber1 = PhoneNumber.getText().toString().trim();
         Email1 = Email.getText().toString().trim();
         Ministry1 = tounicode.gbEncoding(Ministry.getText().toString().trim());

        if (TextUtils.isEmpty(zhanghu1)) {
            Toast.makeText(this, "请输入员工号", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(Name1)) {
            Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Sex1==3) {
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
        if (TextUtils.isEmpty(mima1)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!remima1.equals(mima1)) {
            Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
//                sendRequest(zhanghu.getText().toString(),mima.getText().toString(),
//                        tounicode.gbEncoding(Name.getText().toString()),Sex.getText().toString(),PhoneNumber.getText().toString(),Email.getText().toString(),Ministry.getText().toString());
//            }
                sendRequest(zhanghu1, mima1, Name1, Sex1, PhoneNumber1, Email1, Ministry1);
            }
        }).start();
    }

    private void sendRequest(String zhanghu1,String mima1,String Name1,int Sex1,String PhoneNumber1,String Email1 ,String Ministry1) {
        Map map = new HashMap();
        map.put("zhanghu", zhanghu1);
        map.put("mima", mima1);
        map.put("Name", Name1);
        map.put("Sex", Sex1);
        map.put("PhoneNumber", PhoneNumber1);
        map.put("Email", Email1);
        map.put("Ministry", Ministry1);




        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();

//        Log.d("这将JSON对象转换为json字符串", jsonString);
        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                //dafeng 192.168.2.176
                //  .url("http://192.168.2.176:8080/LoginProject/login")
                // .url("http://192.168.43.174:8080/LoginProject/login")
               // .url("http://39.96.68.13:8080/SmartRoom/RegistServlet") //服务器
                .url("http://39.96.68.13:8080/SmartRoom/RegistServlet") //马琦IP
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
                        Toast.makeText(zhuce.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(zhuce.this, "注册成功！", Toast.LENGTH_SHORT).show();
                } else if (status.equals("-1")) {
                    Toast.makeText(zhuce.this, "信息不存在，注册失败！", Toast.LENGTH_SHORT).show();
                }
                else if (status.equals("-4")) {
                    Toast.makeText(zhuce.this, "该员工号已注册！请重新输入！", Toast.LENGTH_SHORT).show();
                }
                else if (status.equals("-3")) {
                    Toast.makeText(zhuce.this, "您不是该公司员工！", Toast.LENGTH_SHORT).show();
                }
                else if (status.equals("-2")) {
                    Toast.makeText(zhuce.this, "账户名非法！请重新登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

