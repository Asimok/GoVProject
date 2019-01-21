package com.example.mq661.govproject.BookRoom;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mq661.govproject.AlterRoom.addroom;
import com.example.mq661.govproject.AlterRoom.changeroom;
import com.example.mq661.govproject.AlterRoom.deleteroom;
import com.example.mq661.govproject.Login_Register.Login;
import com.example.mq661.govproject.Login_Register.saveinfo;
import com.example.mq661.govproject.Login_Register.savetoken;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.SearchRoom.roomAdapterInfo;
import com.example.mq661.govproject.SearchRoom.searchroom;
import com.example.mq661.govproject.mytoken.tokenDBHelper;
import com.example.mq661.govproject.tools.tounicode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class smartbook extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemSelectedListener {
    private String ssBuildingNumber,ssRoomNumber,ssTime,ssSize,ssFunction,ssIsMeeting,ssDays,size1,functions1,Functions,IsMeeting2;
    private List<roomAdapterInfo>  data;
    EditText size;
    Spinner functions;
    Button commit;
    Intent ssdata=new Intent();
    private OkHttpClient okhttpClient;
    // Map<String, String> usertoken;
    private tokenDBHelper helper;
    private String Token1;
    private ListView searchroomlv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smartbook_lv_layout);
        helper=new tokenDBHelper(this);
        initView();

    }

    private void initView() {

        size=findViewById(R.id.Size);
        functions=findViewById(R.id.functions);
        searchroomlv=findViewById(R.id.searchroomlv);
        functions.setOnItemSelectedListener(this);
        commit=findViewById(R.id.commit);
        commit.setOnClickListener(this);

        searchroomlv.setOnItemClickListener(this);       //设置短按事件
        searchroomlv.setOnItemLongClickListener(this);   //设置长按事件


    }

    @Override
    public void onClick(View v) {
        if(TextUtils.isEmpty(size.getText()))
        {
            Toast.makeText(this, "请输入预期容量", Toast.LENGTH_SHORT).show();
        }

       else {
            size1 = size.getText().toString();
            functions1 = Functions;


            data = new ArrayList<roomAdapterInfo>();
            Token1 = select();
            //  Toast.makeText(this, "读本地"+Token1, Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {

                    sendRequest(Token1);
                }
            }).start();
        }
    }
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
//        ssBuildingNumber=data.get(position).getBuildingNumber();
//        ssSize=data.get(position).getSize();
//        ssRoomNumber=data.get(position).getRoomNumber();
//        ssTime=data.get(position).getTime();
//        ssFunction=data.get(position).getFunction();
//        ssIsMeeting=data.get(position).getIsMeeting();
//        ssDays=data.get(position).getDays();
//
//        Toast.makeText(this, "短按显示", Toast.LENGTH_LONG).show();
//        ssdata.putExtra("BuildingNumber", ssBuildingNumber);
//        ssdata.putExtra("Size", ssSize);
//        ssdata.putExtra("RoomNumber", ssRoomNumber);
//        ssdata.putExtra("Time", ssTime);
//        ssdata.putExtra("Function", ssFunction);
//        ssdata.putExtra("IsMeeting", ssIsMeeting);
//        ssdata.putExtra("IsMeeting", ssDays);
//        setResult(1, ssdata);
//        finish();
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ssBuildingNumber=data.get(position).getBuildingNumber();
        ssSize=data.get(position).getSize();
        ssRoomNumber=data.get(position).getRoomNumber();
        ssTime=data.get(position).getTime();
        ssFunction=data.get(position).getFunction();
        ssIsMeeting=data.get(position).getIsMeeting();
        ssDays=data.get(position).getDays();
//        Toast.makeText(this, "长按显示"
//                , Toast.LENGTH_LONG).show();
        showMultiBtnDialog(ssBuildingNumber,ssSize,ssRoomNumber,ssTime,ssFunction,ssIsMeeting,ssDays);
        return true;      //返回true时可以解除长按与短按的冲突。


    }


    private void sendRequest(String Token1) {
        Map map = new HashMap();
        map.put("Token", Token1);
        map.put("Size", size1);
        map.put("Functions", tounicode.gbEncoding(functions1));

        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
        RequestBody body = RequestBody.create(null, jsonString);//以字符串方式
        okhttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                //dafeng 192.168.2.176
                //  .url("http://192.168.2.176:8080/LoginProject/login")
                // .url("http://192.168.43.174:8080/LoginProject/login")
                // .url("http://39.96.68.13:8080/SmartRoom/RegistServlet") //服务器
                //  .url("http://192.168.43.174:8080/SmartRoom4/SelectServlet") //马琦IP
                .url("http://39.96.68.13:8080/SmartRoom/SmartBookServlet")
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
                        Toast.makeText(smartbook.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String res = response.body().string();//获取到传过来的字符串
                try {

                    // JSONObject jsonObj = new JSONObject(res);
                    //  JSONObject json = new JSONObject(res);
                    JSONArray jsonArray = new JSONArray(res);
                    for (int i=0; i < jsonArray.length(); i++)    {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);


                        String BuildingNumber1 = tounicode.decodeUnicode(jsonObj.getString("buildingNumber"));
                        String RoomNumber1 = jsonObj.getString("roomNumber");
                        String Time1 = tounicode.decodeUnicode( jsonObj.getString("time"));
                        String  Size1 = jsonObj.getString("size");
                        String  Function1 =tounicode.decodeUnicode(jsonObj.getString("functions"));
                        String  IsMeeting = jsonObj.getString("isMeeting");
                        if(IsMeeting.equals("0"))
                        {
                            IsMeeting2="空闲";
                        }
                        else{
                            IsMeeting2="未知";
                        }

                        String  Days = tounicode.decodeUnicode(jsonObj.getString("days"));
                        String mapx="map"+i;
                        if(BuildingNumber1.equals("-1")&&RoomNumber1.equals("-1")&&Time1.equals("-1")) {
                            showRequestResult(BuildingNumber1, RoomNumber1, Time1, Size1, Function1, IsMeeting2,Days, mapx);

                            break; }
                        else if(BuildingNumber1.equals("-2")&&RoomNumber1.equals("-2")&&Time1.equals("-2")) {
                            showRequestResult(BuildingNumber1, RoomNumber1, Time1, Size1, Function1, IsMeeting2,Days, mapx);

                            break; }

                        else  showRequestResult(BuildingNumber1, RoomNumber1, Time1, Size1, Function1, IsMeeting2,Days, mapx);
                    }





                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void showRequestResult(final String BuildNumber1,final String RoomNumber1,final String Time1,final String Size1,final String Function1,final String IsMeeting1,final String Days1,final String mapx) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {

                if(BuildNumber1.equals("-1")&&RoomNumber1.equals("-1")&&Time1.equals("-1")) {
                    Toast.makeText(smartbook.this, "查询不成功！", Toast.LENGTH_SHORT).show();
                }
                else if(BuildNumber1.equals("-2")&&RoomNumber1.equals("-2")&&Time1.equals("-2")) {
                    Toast.makeText(smartbook.this, "容量不合法！", Toast.LENGTH_SHORT).show();
                }
                else if(BuildNumber1.equals("-3")&&RoomNumber1.equals("-3")&&Time1.equals("-3")) {
                    Toast.makeText(smartbook.this, "token失效！请重新登录", Toast.LENGTH_SHORT).show();
                    relog();
                }
                else {
                    roomAdapterInfo mapx = new roomAdapterInfo();
                    mapx.setBuildingNumber(BuildNumber1);
                    mapx.setRoomNumber(RoomNumber1);
                    mapx.setFunction(Function1);
                    mapx.setSize(Size1);
                    mapx.setTime(Time1);
                    mapx.setIsMeeting(IsMeeting1);
                    mapx.setDays(Days1);
                    data.add(mapx);

                    searchroomlv.setAdapter(new smartbook.MyAdapter());
                }
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void select(View view) {
        Intent intent=new Intent(this,tosmartroom.class);
        startActivityForResult(intent,1);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String content = parent.getItemAtPosition(position).toString();
        if (content.equals("多媒体房间")) {
            Toast.makeText(smartbook.this, "选择的功能是：" + content,
                    Toast.LENGTH_SHORT).show();
        } else if (content.equals("普通房间")) {
            Toast.makeText(smartbook.this, "选择的功能是：" + content,
                    Toast.LENGTH_SHORT).show();
        }
        Functions = content;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

            View view =View.inflate(smartbook.this,R.layout.searchroom_adp_layout,null);


            TextView BuildingNumber = view.findViewById(R.id.BuildNumber);
            TextView  RoomNumber = view.findViewById(R.id.RoomNumber);
            TextView Time = view.findViewById(R.id.Time);
            TextView Size = view.findViewById(R.id.Size);
            TextView Function=view.findViewById(R.id.Function);
            TextView IsMeeting=view.findViewById(R.id.IsMeeting);
            TextView Days=view.findViewById(R.id.Days3);
//           Button select=view.findViewById(R.id.select);
//
//            select.setOnClickListener((View.OnClickListener) this);

            BuildingNumber.setText(data.get(position).getBuildingNumber());
            Size.setText(data.get(position).getSize());
            RoomNumber.setText(data.get(position).getRoomNumber());
            Time.setText(data.get(position).getTime());
            Function.setText(data.get(position).getFunction());
            IsMeeting.setText(data.get(position).getIsMeeting());
            Days.setText(data.get(position).getDays());
            return view;
        }
    }

    /* @setNeutralButton 设置中间的按钮
     * 若只需一个按钮，仅设置 setPositiveButton 即可
     */
    public void showMultiBtnDialog(final String BuildingNumber, String Size, final String RoomNumber,
                                   final String Time, String Function, String IsMeeting, final String Days){


        AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(smartbook.this);
        normalDialog.setIcon(R.drawable.app);
        normalDialog.setTitle("GoV").setMessage("房间信息：\n"+"楼号："+BuildingNumber+" 房间号："+RoomNumber+" 容量："+Size+" 时间段："+Time+" 功能："+Function+" 是否开会："+IsMeeting
                +" 日期： "+Days
        );

        normalDialog.setPositiveButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

//        normalDialog.setNeutralButton("删除",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        deleteroom();
//                    }
//                });
        normalDialog.setNegativeButton("预约", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                bookroom();
                bookroomserver book=new bookroomserver();
                book.setContent(smartbook.this);
                try {
                    book.startbookroom(BuildingNumber,RoomNumber,Time,Token1,Days);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // 创建实例并显示
        normalDialog.show();
    }
    public void bookroom()
    {
        Intent intent;
        intent = new Intent(this, bookroom.class);
        startActivityForResult(intent, 0);

        // finish();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        size1=data.getStringExtra("Size");
        functions1=data.getStringExtra("Functions");
    }
}


