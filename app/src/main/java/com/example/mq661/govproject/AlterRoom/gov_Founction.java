package com.example.mq661.govproject.AlterRoom;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.mq661.govproject.BookRoom.CancelBook_handler;
import com.example.mq661.govproject.BookRoom.bookroom;
import com.example.mq661.govproject.BookRoom.smartbook;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.SearchRoom.searchroom_handler;

public class gov_Founction extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wty_maininterface_layout);
    }

    public void searchroom(View view) {
        Intent intent;
        intent = new Intent(this, searchroom_handler.class);
        startActivityForResult(intent, 0);
    }

    public void bookroom(View view) {
        Intent intent;
        intent = new Intent(this, bookroom.class);
        startActivityForResult(intent, 0);
    }


    public void smartbookroom(View view) {
        Intent intent;
        intent = new Intent(this, smartbook.class);
        startActivityForResult(intent, 0);

    }

    public void cancelroom(View view) {
        Intent intent;
        intent = new Intent(this, CancelBook_handler.class);
        startActivityForResult(intent, 0);

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
}
