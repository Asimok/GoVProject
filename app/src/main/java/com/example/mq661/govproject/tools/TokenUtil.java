package com.example.mq661.govproject.tools;

import org.apache.commons.lang.StringUtils;

import java.util.Random;
import java.util.concurrent.TimeUnit;


public class TokenUtil {

    private static final String[] codeBase= {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","shape_toastutils_bg","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};

    private static Random rand= new Random();

    /** XXTEA加密解密的密钥 */
    private static String secKey = "mq";

    /** token超时门限(天) */
    private static long expire = 30;


    /** 验证码字符数 */
    private static int charCount = 4;

    public static final  String  genToken() {
        StringBuffer sb= new StringBuffer();
        for(int i=0; i<charCount; i++){
            int randInt= Math.abs(rand.nextInt());
            sb.append(codeBase[randInt % codeBase.length]);
        }
        long timestamp= System.currentTimeMillis();
        String token= null;

        System.out.println("密钥:  "+secKey);
        System.out.println("前缀:  "+sb.toString());//前缀
        System.out.println("时间戳:  "+timestamp);//时间戳

        token= String.format("%s_%d", sb.toString(), timestamp);
        System.out.println("未加密的token: "+token);

        token= XXTEAUtil.encrypt(token, secKey);
        System.out.println("加密的token: "+token);
        return token;
    }

    public static String verificationToken(String token)   {
        //原始
        System.out.println("传入的token:"+token);

        //解密
        String plainText= XXTEAUtil.decrypt(token,secKey);
        System.out.println("解密的token: "+plainText);


        if (StringUtils.isBlank(plainText)){
            System.out.println("解密失败,token可能遭到篡改");
            return "解密失败";
        }


        String[] plainTextArr= plainText.split("_");
        if (plainTextArr.length!=2){
            System.out.println("token数据格式错误");
            return "数据格式错误";
        }
        System.out.println("数据格式: "+plainTextArr);


        long timestamp= 0;
        try{
            timestamp= Long.parseLong(plainTextArr[1]);
        }catch(NumberFormatException e){
            System.out.println("时间戳无效");
            return "时间戳无效";
        }


        if ((System.currentTimeMillis() - timestamp)>TimeUnit.MILLISECONDS.convert(expire+5, TimeUnit.DAYS)){
            System.out.println("token已过期");
            return "token已过期";
        }
        return "成功";
    }
}
