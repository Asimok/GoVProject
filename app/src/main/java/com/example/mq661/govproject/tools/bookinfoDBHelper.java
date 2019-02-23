package com.example.mq661.govproject.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class bookinfoDBHelper extends SQLiteOpenHelper {

    public bookinfoDBHelper(Context context) {
        super(context, "bookinfo.db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table bookinfo(UUID varchar(60) primary key,zhanghu varchar(30),booktime varchar(30), BuildNumber varchar(30),RoomNumber varchar(30),Time varchar(30),days varchar(30)) ");
        System.out.println("第一次");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        System.out.println("第二次");
    }
}