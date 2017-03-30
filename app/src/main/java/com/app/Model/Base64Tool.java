package com.app.Model;
  
import java.io.UnsupportedEncodingException;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
  
public class Base64Tool {
    // 加密  
    public static String getBase64(String str) {  
        byte[] b = null;  
        String s = null;  
        try {  
            b = str.getBytes("utf-8");  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
        if (b != null) {  
            s = new BASE64Encoder().encode(b);
        }  
        return s;  
    }  
  
    // 解密  
    public static String getFromBase64(String s) {  
        byte[] b = null;  
        String result = null;  
        if (s != null) {  
            BASE64Decoder decoder = new BASE64Decoder();
            try {  
                b = decoder.decodeBuffer(s);  
                result = new String(b, "utf-8");  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        return result;  
    }
    
    public static void main(String[] args) {
    	System.out.println(getBase64("北京欢迎你，为你开天辟地"));
    	System.out.println(getBase64("开始结束播放"));
    	System.out.println(getBase64("追"));
    	System.out.println(getBase64("13888888888"));
    	System.out.println(getBase64("183.60.203.202"));
    	System.out.println(getFromBase64("NQ\u003d\u003d"));
    }
}  