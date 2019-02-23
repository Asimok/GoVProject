package com.example.mq661.govproject.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class roomSortDBHelper extends SQLiteOpenHelper {

    public roomSortDBHelper(Context context) {
        super(context, "roomsort.db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table allroom( BuildNumber varchar(30) ,RoomNumber varchar(30) ,Time varchar(30) ,Days varchar(30),Functions varchar(30),Size varchar(30),isMeeting varchar(30) ,primary key(BuildNumber,RoomNumber,Time,Days)) ");
        db.execSQL("create table BuildAndRoomNumber( BuildNumber varchar(30),RoomNumber varchar(30), primary key(BuildNumber,RoomNumber)) ");
        // db.execSQL("create table RoomNumber(RoomNumber varchar(30) primary key) ");
        db.execSQL("create table Time( Time varchar(30) primary key) ");
        db.execSQL("create table Days( Days varchar(30) primary key) ");
        db.execSQL("create table Functions1( Functions1 varchar(60) primary key) ");
        db.execSQL("create table Size( Size varchar(30) primary key) ");
        System.out.println("第一次");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        System.out.println("第二次");
    }
}