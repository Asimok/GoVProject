package com.example.mq661.govproject.AlterRoom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.mq661.govproject.BookRoom.bookroom;
import com.example.mq661.govproject.Login_Register.zhuce;
import com.example.mq661.govproject.R;
import com.example.mq661.govproject.SearchRoom.searchroom;
import com.example.mq661.govproject.SearchRoom.searchroomAdapter;

public class alterroom extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alterroom_layout);
    }

    public void addroom(View v)
    {
        Intent intent;
        intent = new Intent(this, addroom.class);
        startActivityForResult(intent, 0);
    }
    public void deleteroom(View v)
    {
        Intent intent;
        intent = new Intent(this, deleteroom.class);
        startActivityForResult(intent, 0);
    }
    public void changeroom(View v)
    {
        Intent intent;
        intent = new Intent(this, changeroom.class);
        startActivityForResult(intent, 0);
    }

    public void searchroom(View view) {
        Intent intent;
        intent = new Intent(this, searchroom.class);
        startActivityForResult(intent, 0);
    }

    public void searchroomAdapter(View view) {
        Intent intent;
        intent = new Intent(this, searchroomAdapter.class);
        startActivityForResult(intent, 0);
    }
    public void bookroom(View view) {
        Intent intent;
        intent = new Intent(this, bookroom.class);
        startActivityForResult(intent, 0);
    }

}
