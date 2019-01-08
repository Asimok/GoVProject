package com.example.mq661.govproject.SearchRoom;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mq661.govproject.Login_Register.saveinfo;
import com.example.mq661.govproject.R;

import java.util.ArrayList;
import java.util.List;

public class searchroomAdapter extends AppCompatActivity {
    private List<roomAdapterInfo> data;
    private ListView searchroomlv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchroom_lv_layout);

        searchroomlv=findViewById(R.id.searchroomlv);
        data=new ArrayList<roomAdapterInfo>();

        roomAdapterInfo  map1=new roomAdapterInfo();
        map1.setBuildingNumber("淘宝");
        map1.setSize("120M");
        data.add(map1);

        roomAdapterInfo  map2=new roomAdapterInfo();
        map2.setBuildingNumber("京东");
        map2.setSize("190M");
        data.add(map2);

        roomAdapterInfo  map3=new roomAdapterInfo();
        map3.setBuildingNumber("微信");
        map3.setSize("220M");
        data.add(map3);

        // MyAdapter myadp=new MyAdapter();
        //lv.setAdapter(myadp);
        searchroomlv.setAdapter(new MyAdapter());
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

            View view =View.inflate(searchroomAdapter.this,R.layout.searchroom_adp_layout,null);

            TextView name=view.findViewById(R.id.name);
            TextView size=view.findViewById(R.id.size);

            name.setText(data.get(position).getBuildingNumber());
            size.setText(data.get(position).getSize());

            return view;
        }
    }
}
