package com.example.mq661.govproject.Login_Register;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import java.util.UUID;

public class saveinfo {

        // 保存QQ号码和登录密码,到data.xml文件中
        public static boolean saveUserInfo(Context context, String number, String password)
        {
            SharedPreferences sp = context.getSharedPreferences("data",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            //number password字段为默认值
            edit.putString("userName", number);
            edit.putString("pwd", password);
            edit.commit();
            return true;
        }
        // 从data.xml文件中获取存储的QQ号码和密码
        public static Map<String, String> getUserInfo(Context context)
        {
            SharedPreferences sp = context.getSharedPreferences("data",
                    Context.MODE_PRIVATE);
            String number = sp.getString("userName", null);
            String password = sp.getString("pwd", null);

            Map<String, String> userMap = new HashMap<String, String>();
            userMap.put("number", number);
            userMap.put("password", password);
            return userMap;

        }
    }


