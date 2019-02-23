package com.example.mq661.govproject.AlterRoom;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.mq661.govproject.R;

public class gov_Admin extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wty_manageroom_layout);
    }

    public void addroom(View v) {
        Intent intent;
        intent = new Intent(this, addroom.class);
        startActivityForResult(intent, 0);
    }

    public void deleteroom(View v) {
        Intent intent;
        intent = new Intent(this, deleteroom.class);
        startActivityForResult(intent, 0);
    }

    public void changeroom(View v) {
        Intent intent;
        intent = new Intent(this, changeroom.class);
        startActivityForResult(intent, 0);
    }


    public void manageperson(View view) {
        Intent intent;
        intent = new Intent(this, manage_person.class);
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
