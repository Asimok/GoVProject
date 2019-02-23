package com.example.mq661.govproject.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class userDBHelper extends SQLiteOpenHelper {

    public userDBHelper(Context context) {
        super(context, "user.db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table user(zhanghao varchar(20) primary key,name varchar(30)) ");
        System.out.println("第一次");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        System.out.println("第二次");
    }


}
