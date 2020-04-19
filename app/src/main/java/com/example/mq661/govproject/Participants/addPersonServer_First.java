package com.example.mq661.govproject.Participants;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.mq661.govproject.BookRoom.bookroomServer;
import com.example.mq661.govproject.Login_Register.Login_noToken;
import com.example.mq661.govproject.MainInterface.tab;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.tools.MyNotification;
import com.example.mq661.govproject.tools.bookinfoDBHelper;
import com.example.mq661.govproject.tools.userDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class addPersonServer_First extends AppCompatActivity {

    private bookinfoDBHelper helper3;
    private userDBHelper helper1;
    private String BuildingNumber, RoomNumber, Time, Token, Days;
    private Context content;

    public Context getContent() {
        return content;
    }

    public void setContent(Context content) {
        this.content = content;
    }

    public void startAddPerson(String personInfos, String BuildNumber1, String RoomNumber1, String days1, String Time1, String Token1) {
        final OkHttpClient okHttpClient = new OkHttpClient();

        helper3 = new bookinfoDBHelper(content);
        helper1 = new userDBHelper(content);
        BuildingNumber = BuildNumber1;
        RoomNumber = RoomNumber1;
        Time = Time1;
        Token = Token1;
        Days = days1;
        if(personInfos.equals("[]"))
        {
            Toast.makeText(content, "提交人员不能为空！", Toast.LENGTH_SHORT).show();
            Log.d("eeee",personInfos);
        }
        else {
            Log.d("eeee","有数据"+personInfos);
            RequestBody body = RequestBody.create(null, personInfos);//以字符串方式
            final Request request = new Request.Builder()

                    .url("http://39.96.68.13:8080/SmartRoom/CommitServlet")
                    .post(body)
                    .build();
            //异步方法
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(addPersonServer_First.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                }


                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String res = response.body().string();//获取到传过来的字符串
                    Log.d("aa", "res    " + res);
                    try {
                        JSONObject jsonObj = new JSONObject(res);
                        String status = jsonObj.getString("Name");
                        Log.d("aa", "res Name   " + status);
                        showRequestResult(status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void showRequestResult(final String Status) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {

                if (Status.equals("-1")) {
                    Toast.makeText(content, "添加失败！", Toast.LENGTH_LONG).show();
                } else if (Status.equals("0")) {
                    // Toast.makeText(content, "添加成功！", Toast.LENGTH_LONG).show();
//                    MyNotification notify = new MyNotification(content);
//
                    bookroomServer book = new bookroomServer();
                    book.setContent(content);
                    try {
                        book.startbookroom(BuildingNumber, RoomNumber, Time, Token, Days);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//
//                    notify.MyNotification("智能会议室", "参会人员添加成功", R.drawable.book, "addPerson", "添加人员", 15, "添加");
                    addPerson_handler.instance.finish();

                } else if (Status.equals("-3")) {
                    Toast.makeText(content, "用户认证失效，请重新登录！", Toast.LENGTH_LONG).show();
                    relog();
                } else if (Status.equals("-4")) {
                    Toast.makeText(content, "提交前请选择至少一名员工！", Toast.LENGTH_LONG).show();
                } else {
                    MyNotification notify = new MyNotification(content);

                    bookroomServer book = new bookroomServer();
                    book.setContent(content);
                    try {
                        book.startbookroom(BuildingNumber, RoomNumber, Time, Token, Days);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    notify.MyNotification("智能会议室", "部分参会人员添加成功", R.drawable.book, "addPerson1", "添加人员", 16, "添加");
                    showMultiBtnDialog(Status);
//
                }

            }
        });

    }

    public void relog() {
        Intent intent;
        intent = new Intent(content, Login_noToken.class);
        intent.setClass(content, tab.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        content.startActivity(intent);
        addPerson_handler.instance.finish();
    }

    public void showMultiBtnDialog(final String res) {


        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(content);
        normalDialog.setIcon(R.drawable.manageperson);
        normalDialog.setTitle("以下人员已经参加会议").setMessage(res + "\n\n其余人员添加成功！");
        normalDialog.setCancelable(false);

        normalDialog.setPositiveButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent;
                        intent = new Intent(content, tab.class);
                        intent.setClass(content, tab.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        content.startActivity(intent);
                        addPerson_handler.instance.finish();
                    }
                });

        normalDialog.setNegativeButton("继续添加其他人员", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent;
                intent = new Intent(content, after_Payment_Person_handler.class);
                intent.setClass(content, after_Payment_Person_handler.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                content.startActivity(intent);
                addPerson_handler.instance.finish();
            }
        });

        // 创建实例并显示
        normalDialog.show();
    }
}
