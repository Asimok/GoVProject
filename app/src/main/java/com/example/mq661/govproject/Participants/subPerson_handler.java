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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mq661.govproject.Login_Register.Login_noToken;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.SearchRoom.personName;
import com.example.mq661.govproject.SearchRoom.roomAdapterInfo;
import com.example.mq661.govproject.tools.tokenDBHelper;

import org.json.JSONArray;
import org.json.JSONException;
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

public class subPerson_handler extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    public static List<personName> personName1 = new ArrayList<>();
    public static subPerson_handler instance = null;
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
    private CheckBox mMainCkb;
    private MyAdapter_Person_handler_forSubPerson mMyAdapter;
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
        subPerson_handler.instance.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_listview);
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
        mMainCkb = findViewById(R.id.ckb_main);
        bumen = findViewById(R.id.bumen);
        mListView.setOnItemClickListener(this);       //设置短按事件
        mListView.setOnItemLongClickListener(this);   //设置长按事件
    }

    /**
     * 数据绑定
     */
    private void initViewOper() {
        mMyAdapter = new MyAdapter_Person_handler_forSubPerson(models, this, new AllCheckListener() {

            @Override
            public void onCheckedChanged(boolean b) {
                //根据不同的情况对maincheckbox做处理
                if (!b && !mMainCkb.isChecked()) {
                    return;
                } else if (!b && mMainCkb.isChecked()) {
                    mIsFromItem = true;
                    mMainCkb.setChecked(false);
                } else if (b) {
                    mIsFromItem = true;
                    mMainCkb.setChecked(true);
                }
            }
        });
        mListView.setAdapter(mMyAdapter);
        //全选的点击监听
        mMainCkb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //当监听来源为点击item改变maincbk状态时不在监听改变，防止死循环
                ParticipantsInfoAllCheck = new ArrayList<>();
                if (mIsFromItem) {
                    mIsFromItem = false;
                    Log.e("aa", "此时我不可以触发");
                    return;
                }
//TODO 全选

                //刷新listview
                mMyAdapter.notifyDataSetChanged();
                if (mMainCkb.isChecked()) {
                    //改变数据
                    for (int i = 0; i < models.size(); i++) {
                        if (models.get(i).getMinistry().equals(bumen.getText().toString())) {
                            models.get(i).setIscheck(b);
                            addName(models.get(i).getName(), models.get(i).getEmployeeNumber1());
                            Log.d("aa", "全选   " + models.get(i).getName());
                        }
                    }

                    Toast.makeText(subPerson_handler.this, "全选  " + bumen.getText().toString() + "  员工", Toast.LENGTH_SHORT).show();
                } else {
                    //改变数据
                    for (int i = 0; i < models.size(); i++) {
                        if (models.get(i).getMinistry().equals(bumen.getText().toString())) {
                            //  if(models.get(i).getMinistry().equals("总经理")) {
                            models.get(i).setIscheck(b);
                            removeName(models.get(i).getName(), models.get(i).getEmployeeNumber1());
                        }
                    }
                    Toast.makeText(subPerson_handler.this, "取消全选  " + bumen.getText().toString() + "  员工", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendRequest(String Token1) {
        intent = getIntent();
        BuildingNumber = intent.getStringExtra("BuildingNumber");
        RoomNumber = intent.getStringExtra("RoomNumber");
        Days = intent.getStringExtra("Days");
        Time = intent.getStringExtra("Time");
        Map map = new HashMap();
        map.put("Token", Token1);
        map.put("BuildingNumber", BuildingNumber);
        map.put("RoomNumber", RoomNumber);
        map.put("Days", Days);
        map.put("Time", Time);

        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
        Log.d("aa", "jsonString   " + jsonString);
        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                //dafeng 192.168.2.176
                //  .url("http://192.168.2.176:8080/LoginProject/login")
                // .url("http://192.168.43.174:8080/LoginProject/login")
                // .url("http://39.96.68.13:8080/SmartRoom/RegistServlet") //服务器
                //  .url("http://192.168.43.174:8080/SmartRoom4/PersonServlet") //马琦IP
                .url("http://39.96.68.13:8080/SmartRoom/ReturnPersonServlet")
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
                        Toast.makeText(subPerson_handler.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
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
                        showRequestResult(employeeNumber, ministry, name);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showRequestResult(final String employeeNumber, final String ministry, final String name) {
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

    //生成列表
    public void getinfo() {
        ParticipantsInfo = new ArrayList<>();
        ParticipantsInfoAllCheck = new ArrayList<>();
        intent = getIntent();
        // Log.d("aa","长度   "+mMyAdapter.getAddName().size());
        //单选name
        List<personName> danxuanName = mMyAdapter.getAddName();
        //多选name
        List<personName> duoxuanName = personName1;

        //混合列表
        for (int i = 0; i < danxuanName.size(); i++) {
            ParticipantsInfoAllCheck.add(danxuanName.get(i));
            Log.d("aa", "添加 单选    " + danxuanName.get(i).getNum());
        }
        for (int i = 0; i < duoxuanName.size(); i++) {
            ParticipantsInfoAllCheck.add(duoxuanName.get(i));
            Log.d("aa", "添加 多选   " + duoxuanName.get(i).getNum());
        }


        BuildingNumber = intent.getStringExtra("BuildingNumber");
        RoomNumber = intent.getStringExtra("RoomNumber");
        Days = intent.getStringExtra("Days");
        Time = intent.getStringExtra("Time");


        for (int i = 0; i < ParticipantsInfoAllCheck.size() - 1; i++) {
            for (int j = ParticipantsInfoAllCheck.size() - 1; j > i; j--) {
                if (ParticipantsInfoAllCheck.get(j).getNum().equals(ParticipantsInfoAllCheck.get(i).getNum())) {
                    Log.d("aa", "remove   " + ParticipantsInfoAllCheck.get(j).getNum());
                    ParticipantsInfoAllCheck.remove(j);
                }
            }
        }

        for (int i = 0; i < ParticipantsInfoAllCheck.size(); i++) {
            Log.d("aa", "for 里获取到第   " + i + "   个    " + ParticipantsInfoAllCheck.get(i).getNum() + "   " + ParticipantsInfoAllCheck.get(i).getName());

            ParticipantsInfo allinfo = new ParticipantsInfo();

            allinfo.setName(ParticipantsInfoAllCheck.get(i).getName());
            allinfo.setNum(ParticipantsInfoAllCheck.get(i).getNum());
            allinfo.setBuildingNumber(BuildingNumber);
            allinfo.setDays(Days);
            allinfo.setRoomNumber(RoomNumber);
            allinfo.setTime(Time);
            allinfo.setToken(Token1);
            ParticipantsInfo.add(allinfo);

        }
        // removeDuplicateWithOrder(ParticipantsInfo);
        //将LIST 转换成JSON
        JSONArray jsonArray = new JSONArray();
        JSONObject tmpObj = null;
        int count = ParticipantsInfo.size();
        for (int i = 0; i < count; i++) {
            tmpObj = new JSONObject();
            try {

                tmpObj.put("Name", ParticipantsInfo.get(i).getName());
                tmpObj.put("EmployeeNumber", ParticipantsInfo.get(i).getNum());
                tmpObj.put("BuildingNumber", ParticipantsInfo.get(i).getBuildingNumber());
                tmpObj.put("RoomNumber", ParticipantsInfo.get(i).getRoomNumber());
                tmpObj.put("Days", ParticipantsInfo.get(i).getDays());
                tmpObj.put("Time", ParticipantsInfo.get(i).getTime());
                tmpObj.put("Token", ParticipantsInfo.get(i).getToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(tmpObj);
            tmpObj = null;
        }
        String personInfos = jsonArray.toString(); // 将JSONArray转换得到String
        sspersonInfos = personInfos;
        //Log.d("aa","sspersonInfos   "+sspersonInfos);


        JSONArray jsonArray1 = new JSONArray();
        JSONObject tmpObj1 = null;
        int count1 = ParticipantsInfoAllCheck.size();
        for (int i = 0; i < count1; i++) {
            tmpObj1 = new JSONObject();
            try {
                tmpObj1.put("Name", ParticipantsInfoAllCheck.get(i).getName());
                tmpObj1.put("EmployeeNumber", ParticipantsInfoAllCheck.get(i).getNum());
                tmpObj1.put("Token", Token1);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray1.put(tmpObj1);
            tmpObj1 = null;
        }
        String namesInfos = jsonArray1.toString(); // 将JSONArray转换得到String
        ssnameInfos = namesInfos;
        //Log.d("aa","ssnameInfos   "+ssnameInfos);


    }

    public void Commit(View view) {
        getinfo();
        subPersonServer_First subPersonServer_First = new subPersonServer_First();
        subPersonServer_First.setContent(subPerson_handler.this);

        subPersonServer_First.startSubPerson(sspersonInfos, BuildingNumber, RoomNumber, Days, Time, Token1);

    }

    public void addName(String name, String num) {
        personName Name = new personName();
        Name.setName(name);
        Name.setNum(num);
        personName1.add(Name);
    }

    private void removeName(String name, String num) {

        for (int i = personName1.size() - 1; i >= 0; i--) {
            String value = personName1.get(i).getName();
            String num1 = personName1.get(i).getNum();
            if (value.equals(name) && num1.equals(num)) {
                Log.d("aa", " removeName  add_handler   " + personName1.get(i).getNum());
                personName1.remove(i);
            }
        }
    }

    //获取多选name
    public List<personName> getAddName() {
        for (int i = personName1.size() - 1; i >= 0; i--) {
            Log.d("aa", " return   " + personName1.get(i).getNum());
        }
        return personName1;
    }

    //对item导致maincheckbox改变做监听
    interface AllCheckListener {
        void onCheckedChanged(boolean b);
    }
}