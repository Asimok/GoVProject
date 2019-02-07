package com.example.mq661.govproject.tools;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class savetoken {

    // 保存QQ号码和登录密码,到data.xml文件中
    public static boolean saveUsertoken(Context context,String Token)
    {
        SharedPreferences sp = context.getSharedPreferences("data",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        //number password字段为默认值
        edit.putString("Token", Token);
        edit.commit();
        return true;
    }
    // 从data.xml文件中获取存储的QQ号码和密码
    public static Map<String, String> getUsertoken(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences("data",
                Context.MODE_PRIVATE);
        String Token = sp.getString("Token", null);

        Map<String, String> userMap = new HashMap<String, String>();
        userMap.put("Token", Token);
        return userMap;

    }
}

