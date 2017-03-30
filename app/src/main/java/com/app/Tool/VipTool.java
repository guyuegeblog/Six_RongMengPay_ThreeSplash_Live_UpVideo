package com.app.Tool;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.app.Bean.Multi;
import com.app.DBManager.DBManager;
import com.app.Save.KeyFile;
import com.app.Save.KeyUser;

/**
 * Created by ASUS on 2016/12/7.
 */
public class VipTool {

    public static final int NO_VIP = 0;//不是会员
    public static final int YES_VIP = 1;//是会员

    public static boolean isVip1 = false;
    public static boolean isVip2 = false;
    public static boolean userIsLoginSucces = false;
    public static boolean thansix = false;
    public static boolean thanthree = false;
    public static boolean thanShiKanSixVideo = false;
    public static int vipType = Multi.VIP_NOT_VIP_TYPE;

    public static boolean canVip1(Activity mContext) {
        if (TextUtils.isEmpty(AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext,
                KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.USER_VIPS_KEY)))) {
            isVip1 = false;
        } else {
            isVip1 = Integer.parseInt(AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE,
                    KeyUser.USER_VIPS_KEY))) == YES_VIP ? true : false;
        }
        return isVip1;
    }

    public static boolean canVip2(Activity mContext) {
        if (TextUtils.isEmpty(AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext,
                KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIPS_TWOS_KEY)))) {
            isVip2 = false;
        } else {
            isVip2 = Integer.parseInt(AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE,
                    KeyUser.VIPS_TWOS_KEY))) == YES_VIP ? true : false;
        }
        return isVip2;
    }

    public static String getUserRandom(Activity activity) {
        String userRandom = "";
        try {
            userRandom = AesTool.decrypt(DBManager.getDBManager(activity).queryRandomInfo().get(0).getRandom_text());
        } catch (Exception e) {
            userRandom = "";
        }
        return userRandom;
    }

    public static String getUserName(Activity mContext) {
        String userName;
        if (TextUtils.isEmpty(VipTool.getUserRandom(mContext))) {
            //防止本地数据库被删除
            userName = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.USER_NAME_KEY));
        } else {
            userName = VipTool.getUserRandom(mContext) + "" + PhoneTool.getPhoneTool(mContext).getAndroidId(mContext);
        }
        if (TextUtils.isEmpty(userName)) {
            userName = PhoneTool.getPhoneTool(mContext).getAndroidId(mContext);
        }
        return userName;
    }

    public static boolean userIsLoginSucces(Activity mContext) {
        if (TextUtils.isEmpty(AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext,
                KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.USER_NAME_KEY))) ||
                TextUtils.isEmpty(AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext,
                        KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.USER_VIPS_KEY))) ||
                TextUtils.isEmpty(AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext,
                        KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIP_TIMES_KEY)))) {
            userIsLoginSucces = false;
        } else {
            userIsLoginSucces = true;
        }
        return userIsLoginSucces;
    }

    public static void Six_Total(Activity mContext) {
        int three_total = TextUtils.isEmpty(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_SIX_TOTAL,
                KeyUser.SIX_TOTAL)) ? 1 : Integer.parseInt(ParamsPutterTool.sharedPreferencesReadData(mContext,
                KeyFile.SAVE_SIX_TOTAL,
                KeyUser.SIX_TOTAL)) + 1;
        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_SIX_TOTAL, KeyUser.SIX_TOTAL, String.valueOf(three_total));
    }


    public static boolean than_Six_Total(Activity mContext) {
        if (!TextUtils.isEmpty(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_SIX_TOTAL,
                KeyUser.SIX_TOTAL))) {
            Log.i("VipNumber", ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_SIX_TOTAL, KeyUser.SIX_TOTAL));
            if (Integer.parseInt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_SIX_TOTAL, KeyUser.SIX_TOTAL)) >= 6) {
                thansix = true;
            }
        }
        return thansix;
    }

    public static boolean than_Three_Total(Activity mContext) {
        if (!TextUtils.isEmpty(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_THREE_TOTAL,
                KeyUser.THREE_TOTAL))) {
            //改动成了4次启动app
            if (Integer.parseInt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_THREE_TOTAL, KeyUser.THREE_TOTAL)) >= 2) {
                thanthree = true;
            }
        }
        return thanthree;
    }

    public static void shi_Kan_Six_Video(Activity mContext) {
        //本地存储(方式1)
//        int shiKan_Six_Video = TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.TV_ShiKan_MP4_ALL)) ? 1 :
//                Integer.parseInt(FileTool.readFileToSDFile(Constant.TV_ShiKan_MP4_ALL)) + 1;
//        FileTool.writeFileToSDFile(Constant.TV_ShiKan_MP4_ALL, String.valueOf(shiKan_Six_Video));
        //app存储(方式2)
        int shiKan_Six_Video = TextUtils.isEmpty(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.LOOK_EIGHT_VIDEO, KeyUser.LOOK_EIGHT_VIDEO)) ? 1 :
                Integer.parseInt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.LOOK_EIGHT_VIDEO, KeyUser.LOOK_EIGHT_VIDEO)) + 1;
        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.LOOK_EIGHT_VIDEO, KeyUser.LOOK_EIGHT_VIDEO, String.valueOf(shiKan_Six_Video));
    }

    public static boolean than_Shi_Kan_Six_Video(Activity mContext) {
        String text = ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.LOOK_EIGHT_VIDEO, KeyUser.LOOK_EIGHT_VIDEO);
        if (TextUtils.isEmpty(text)) {
            thanShiKanSixVideo = false;
        } else {
            if (Integer.parseInt(text) >= 10) {//已经改动10个试看
                thanShiKanSixVideo = true;
            } else {
                thanShiKanSixVideo = false;
            }
        }
        return thanShiKanSixVideo;
    }

    public static String get_ShiKan_Video_Count(Activity mContext) {
//        String text = FileTool.readFileToSDFile(Constant.TV_ShiKan_MP4_ALL);
        String text = ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.LOOK_EIGHT_VIDEO, KeyUser.LOOK_EIGHT_VIDEO);
        if (TextUtils.isEmpty(text)) {
            text = "1";
        } else {
            text = String.valueOf(Integer.parseInt(text) + 1);
        }
        return text;
    }

    public static void write_User_Pay_Count(Activity mContext) {
//        int userPayCount = TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.TV_USER_PAY_ALL)) ? 1 :
//                Integer.parseInt(FileTool.readFileToSDFile(Constant.TV_USER_PAY_ALL)) + 1;
//        FileTool.writeFileToSDFile(Constant.TV_USER_PAY_ALL, String.valueOf(userPayCount));

        int userPayCount = TextUtils.isEmpty(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.PAY_MONEY_COUNT, KeyUser.PAY_MONEY_COUNT)) ? 1 :
                Integer.parseInt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.PAY_MONEY_COUNT, KeyUser.PAY_MONEY_COUNT)) + 1;
        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.PAY_MONEY_COUNT, KeyUser.PAY_MONEY_COUNT, String.valueOf(userPayCount));

    }

    public static String read_User_Pay_Count(Activity mContext) {
        int userPayCount = TextUtils.isEmpty(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.PAY_MONEY_COUNT, KeyUser.PAY_MONEY_COUNT)) ? 0 :
                Integer.parseInt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.PAY_MONEY_COUNT, KeyUser.PAY_MONEY_COUNT));

        return String.valueOf(userPayCount);
    }

    public static int getUserVipType(Activity mContext) {
        String userPayCount = read_User_Pay_Count(mContext);
        if (Integer.parseInt(userPayCount) <= 0) {
            vipType = Multi.VIP_NOT_VIP_TYPE;
        } else if (Integer.parseInt(userPayCount) == 1) {
            vipType = Multi.VIP_SILVER_TYPE;//白银
        } else if (Integer.parseInt(userPayCount) == 2) {
            vipType = Multi.VIP_GOLD_TYPE;//黄金
        } else if (Integer.parseInt(userPayCount) == 3) {
            vipType = Multi.VIP_PLAT_NIUM_TYPE;//白金
        } else if (Integer.parseInt(userPayCount) == 4) {
            vipType = Multi.VIP_DIAMOND_TYPE;//钻石
        } else if (Integer.parseInt(userPayCount) == 5) {
            vipType = Multi.VIP_RED_DIAMOND_TYPE;//粉钻
        } else if (Integer.parseInt(userPayCount) >= 6) {
            vipType = Multi.VIP_CROWN_TYPE;//皇冠
        }
        Log.i("viptype", "==========" + userPayCount);
        return vipType;
    }


}
