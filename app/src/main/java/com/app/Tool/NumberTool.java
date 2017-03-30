package com.app.Tool;

import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by ASUS on 2016/12/6.
 */
public class NumberTool {

    /**
     * 判断是否是数字
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }


    /***
     * 生成指定位数的随机
     */

    public static String createRandom() {
        String strRand = "";
        for (int i = 0; i < 5; i++) {
            strRand += String.valueOf((int) (Math.random() * 10));
        }
        return strRand;
    }

    /**
     * @param activity
     */
    private Random random;

    public int createLiveRandom(int min, int jia) {
        if (random == null) {
            random = new Random();
        }
        int randNum = random.nextInt(jia) + min;
        return randNum;
    }
}
