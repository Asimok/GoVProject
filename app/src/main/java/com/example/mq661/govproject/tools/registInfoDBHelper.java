package com.example.mq661.govproject.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class registInfoDBHelper extends SQLiteOpenHelper {

    public registInfoDBHelper(Context context) {
        super(context, "registinfo.db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table registinfo(PhoneNumber varchar(60) primary key,Name varchar(30),Sex varchar(30), Email varchar(30),Ministry varchar(30)) ");
        System.out.println("第一次");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        System.out.println("第二次");
    }
}