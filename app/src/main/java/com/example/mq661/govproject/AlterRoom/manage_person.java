package com.example.mq661.govproject.AlterRoom;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.mq661.govproject.Participants.Sign_getSigninfo_handler;
import com.example.mq661.govproject.Participants.after_Payment_Person_handler;
import com.example.mq661.govproject.Participants.sub_Person_getRoom_handler;
import com.example.mq661.govproject.R;

public class manage_person extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wty_manageperson_layout);
    }

    public void aferPayment(View view) {
        Intent intent;
        intent = new Intent(this, after_Payment_Person_handler.class);
        startActivityForResult(intent, 0);
    }

    public void subPerson(View view) {
        Intent intent;
        intent = new Intent(this, sub_Person_getRoom_handler.class);
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

    public void SignInfo(View view) {
        Intent intent;
        intent = new Intent(this, Sign_getSigninfo_handler.class);
        startActivityForResult(intent, 0);
    }
}
