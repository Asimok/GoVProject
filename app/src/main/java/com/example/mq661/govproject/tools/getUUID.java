package com.example.mq661.govproject.tools;

import java.util.UUID;

public class getUUID {

    public static String getUUID32(){
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        return uuid;
        //  return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

}
