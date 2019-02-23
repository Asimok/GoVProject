package com.example.mq661.govproject.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class tokenDBHelper extends SQLiteOpenHelper {

    public tokenDBHelper(Context context) {
        super(context, "token.db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table token(token varchar(20) primary key) ");
        System.out.println("第一次");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        System.out.println("第二次");
    }


}
