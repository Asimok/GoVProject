package com.example.mq661.govproject.Participants;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mq661.govproject.Login_Register.Login_noToken;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.SearchRoom.personName;
import com.example.mq661.govproject.SearchRoom.roomAdapterInfo;
import com.example.mq661.govproject.tools.tokenDBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class getSignInfo_handler extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    public static List<personName> personName1 = new ArrayList<>();
    public static getSignInfo_handler instance = null;
    public List<personName> ParticipantsInfoAllCheck;
    //监听来源
    public boolean mIsFromItem = false;

    EditText bumen;
    Intent intent;
    //handler 处理返回的请求结果
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
        }
    };
    private List<roomAdapterInfo> data;
    private OkHttpClient okhttpClient;
    private tokenDBHelper helper;
    private String Token1, sspersonInfos, ssnameInfos;
    private String BuildingNumber, RoomNumber, Time, Days;
    private ListView mListView;
    private List<Model> models;
    private List<ParticipantsInfo> ParticipantsInfo;
    private MyAdapter_Person_handler_forSign mMyAdapter;
    //新线程进行网络请求
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            data = new ArrayList<roomAdapterInfo>();
            Token1 = select();
            sendRequest(Token1);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", "请求结果");
            msg.setData(data);
            handler.sendMessage(msg);
        }


    };

    // 删除ArrayList中重复元素，保持顺序
    public static List removeDuplicateWithOrder1(List list) {
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            Object element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
        list.clear();
        list.addAll(newList);
        return list;

    }

    public static List removeDuplicate(List list) {
        List listTemp = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            Log.d("aa", "list   " + list.get(i));
            if (!listTemp.contains(list.get(i))) {
                listTemp.add(list.get(i));

            }
        }
        return listTemp;
    }

    public static void Tofinish() {
        getSignInfo_handler.instance.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_listview_forsign);
        models = new ArrayList<>();
        instance = this;
        initView();
        //initData();

        new Thread(runnable).start();  //启动子线程
        helper = new tokenDBHelper(this);
        initView();
    }

    private void initView() {
        mListView = findViewById(R.id.list_main);

        mListView.setOnItemClickListener(this);       //设置短按事件
        mListView.setOnItemLongClickListener(this);   //设置长按事件
    }

    /**
     * 数据绑定
     */
    private void initViewOper() {
        mMyAdapter = new MyAdapter_Person_handler_forSign(models, this, new AllCheckListener() {

            @Override
            public void onCheckedChanged(boolean b) {

            }
        });
        mListView.setAdapter(mMyAdapter);
        //刷新listview
        mMyAdapter.notifyDataSetChanged();

    }

    private void sendRequest(String Token1) {
        intent = getIntent();
        BuildingNumber = intent.getStringExtra("BuildingNumber");
        RoomNumber = intent.getStringExtra("RoomNumber");
        Days = intent.getStringExtra("Days");
        Time = intent.getStringExtra("Time");
        Map map = new HashMap();
        map.put("Token", Token1);
        map.put("RoomNumber", RoomNumber);
        map.put("Days", Days);
        map.put("Time", Time);
        map.put("BuildingNumber", BuildingNumber);

        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()

                .url("http://39.96.68.13:8080/SmartRoom/SignMessageServlet")

                .post(body)
                .build();
        Call call = okhttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getSignInfo_handler.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();//获取到传过来的字符串
                try {
                    JSONArray jsonArray = new JSONArray(res);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        String employeeNumber = jsonObj.getString("employeeNumber");
                        String ministry = jsonObj.getString("ministry");
                        String name = jsonObj.getString("name");
                        String time = jsonObj.getString("signTime");
                        String statue = jsonObj.getString("signStatus");
                        if (statue.equals("0")) {
                            time = "未签到";
                            statue = "未签到";
                        } else {
                            statue = "已签到";
                        }
                        Log.d("ee", "time    " + i + "    " + time);
                        showRequestResult(employeeNumber, ministry, name, time, statue);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showRequestResult(final String employeeNumber, final String ministry, final String name, final String time, final String statue) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {
                Model model = new Model();
                model.setEmployeeNumber1(employeeNumber);
                model.setMinistry(ministry);
                model.setName(name);
                model.setTime(time);
                model.setStatue(statue);
                model.setIscheck(false);
                models.add(model);
                initViewOper();

            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        return true;      //返回true时可以解除长按与短按的冲突。
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

    public void relog() {
        Intent intent;
        intent = new Intent(this, Login_noToken.class);
        startActivityForResult(intent, 0);
        finish();
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


    //对item导致maincheckbox改变做监听
    interface AllCheckListener {
        void onCheckedChanged(boolean b);
    }
}