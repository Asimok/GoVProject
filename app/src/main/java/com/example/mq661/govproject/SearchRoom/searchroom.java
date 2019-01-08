package com.example.mq661.govproject.SearchRoom;

import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mq661.govproject.AlterRoom.changeroom;
import com.example.mq661.govproject.Login_Register.saveinfo;
import com.example.mq661.govproject.R;
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

public class searchroom extends AppCompatActivity implements View.OnClickListener {
    TextView BuildingNumber,RoomNumber,Time,Size,Function,IsMeeting;
    private List<roomAdapterInfo>  data;
    Button commit;
    Map<String, String> Token;
    private OkHttpClient okhttpClient;
    private String Token1;
    private ListView searchroomlv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchroom_lv_layout);
        initView();



    }

    private void initView() {

       // searchroomlv.setAdapter(new searchroom.MyAdapter());
        // 提交修改
        searchroomlv=findViewById(R.id.searchroomlv);
        commit=findViewById(R.id.commit);
        commit.setOnClickListener(this);
        Token = saveinfo.getUserInfo(this);
    }

    @Override
    public void onClick(View v) {
        data=new ArrayList<roomAdapterInfo>();
        Toast.makeText(this,"点击成功",Toast.LENGTH_LONG).show();
        Token1 =Token.get("Token");
        Toast.makeText(this, Token1, Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                sendRequest(Token1);
            }
        }).start();
    }

    private void sendRequest(String Token1) {
        Map map = new HashMap();
        map.put("Token", Token1);

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
             //  .url("http://192.168.43.174:8080/SmartRoom4/SelectServlet") //马琦IP
                .url("http://192.168.43.174:8080/SmartRoom/SearchServlet")
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
                        Toast.makeText(searchroom.this, "连接服务器失败！", Toast.LENGTH_SHORT).show();
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
                                String mapx="map"+i;
                                showRequestResult(BuildingNumber1, RoomNumber1, Time1, Size1, Function1, IsMeeting,mapx);
                               }





                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void showRequestResult(final String BuildNumber1,final String RoomNumber1,final String Time1,final String Size1,final String Function1,final String IsMeeting1,final String mapx) {
        runOnUiThread(new Runnable() {
            @Override
            /**
             * 实时更新，数据库信息改变时，客户端内容发生改变
             */
            public void run() {
                Toast.makeText(searchroom.this, "修改失败！", Toast.LENGTH_SHORT).show();
//                data=new ArrayList<roomAdapterInfo>();
                roomAdapterInfo  mapx=new roomAdapterInfo();
                mapx.setBuildingNumber(BuildNumber1);
                mapx.setRoomNumber(RoomNumber1);
                mapx.setFunction(Function1);
                mapx.setSize(Size1);
                mapx.setTime(Time1);
                mapx.setIsMeeting(IsMeeting1);
                data.add(mapx);

                searchroomlv.setAdapter(new searchroom.MyAdapter());

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
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view =View.inflate(searchroom.this,R.layout.searchroom_adp_layout,null);


            TextView BuildingNumber = view.findViewById(R.id.BuildNumber);
            TextView  RoomNumber = view.findViewById(R.id.RoomNumber);
            TextView Time = view.findViewById(R.id.Time);
            TextView Size = view.findViewById(R.id.Size);
            TextView Function=view.findViewById(R.id.Function);
            TextView IsMeeting=view.findViewById(R.id.IsMeeting);
//           Button select=view.findViewById(R.id.select);
//
//            select.setOnClickListener((View.OnClickListener) this);

            BuildingNumber.setText(data.get(position).getBuildingNumber());
            Size.setText(data.get(position).getSize());
            RoomNumber.setText(data.get(position).getRoomNumber());
            Time.setText(data.get(position).getTime());
            Function.setText(data.get(position).getFunction());
            IsMeeting.setText(data.get(position).getIsMeeting());
            return view;
        }
    }
}


