package com.app.Tool;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.app.Constant.Constant;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 读取手机设备信息测试代码
 * http://www.souapp.com 搜应用网
 * song2c@163.com
 * 宋立波
 */
public class PhoneTool {

    private static TelephonyManager telephonyManager;
    private  static  PhoneTool phoneTool;
    private static Activity cxt;
    /**
     * 国际移动用户识别码
     */
    private String IMSI;

    public static PhoneTool getPhoneTool(Activity context) {
        cxt = context;
        if (phoneTool == null) {
            phoneTool = new PhoneTool();
            telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
        }
        return phoneTool;
    }


    /**
     * 获取电话号码
     */
    public static String getNativePhoneNumber() {
        String NativePhoneNumber = null;
        NativePhoneNumber = telephonyManager.getLine1Number();
        return NativePhoneNumber;
    }

    /**
     * 获取手机服务商信息
     */
    public String getProvidersName() {
        String ProvidersName = "N/A";
        try {
            IMSI = telephonyManager.getSubscriberId();
            // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
            System.out.println(IMSI);
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                ProvidersName = "中国移动";
            } else if (IMSI.startsWith("46001")) {
                ProvidersName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                ProvidersName = "中国电信";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ProvidersName;
    }

    public String getIMSI() {
        IMSI = telephonyManager.getSubscriberId();
        return IMSI;
    }


    public String getPhoneInfo() {
        TelephonyManager tm = (TelephonyManager) cxt.getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder sb = new StringBuilder();

        sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
        sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
        sb.append("\nLine1Number = " + tm.getLine1Number());
        sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
        sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
        sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
        sb.append("\nNetworkType = " + tm.getNetworkType());
        sb.append("\nPhoneType = " + tm.getPhoneType());
        sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
        sb.append("\nSimOperator = " + tm.getSimOperator());
        sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
        sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
        sb.append("\nSimState = " + tm.getSimState());
        sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
        sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
        return sb.toString();
    }

    /**
     * 获取运营商
     *
     * @return
     */
    private String tele_Supo_Name = null;

    public String getTele_Supo(String IMSI, Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = telManager.getSimOperator();
        if (operator != null) {
            if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
                //中国移动
                tele_Supo_Name = "中国移动";
            } else if (operator.equals("46001")) {
                //中国联通
                tele_Supo_Name = "中国联通";
            } else if (operator.equals("46003")) {
                //中国电信
                tele_Supo_Name = "中国电信";
            }
        }
        return tele_Supo_Name;
    }

    /**
     * Android SDK平台获取高唯一性设备识别码
     */
    public String getAndroidId(Context context) {
        String deviceId = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm.getDeviceId();//第一次获取
            if (TextUtils.isEmpty(deviceId)) {
                //没有获取到imel号码
                deviceId = autoCreateImeil(context);
            }
            if (!TextUtils.isEmpty(deviceId)) {
                String lastStr = deviceId.substring(deviceId.length() - 1);
                if (!NumberTool.isNumeric(lastStr)) {
                    deviceId = autoCreateImeil(context);
                }
            }
        } catch (Exception e) {
            deviceId = autoCreateImeil(context);
        }
        return deviceId;
    }

    /***
     * 为了处理某些安卓设备获取不到唯一识别码的情况，需要手动随机生成
     */
    public String autoCreateImeil(Context context) {
        String deviceId = "";
        File deviceFile = new File(Constant.UUID_AUTO_CREATE_DIRECTORY + Constant.UUID_AUTO_TWOFILE_DIRECTORY + Constant.UUID_AUTO_FILE_PATH);
        if (!deviceFile.exists()) {
            File file = new File(Constant.UUID_AUTO_CREATE_DIRECTORY);
            if (!file.exists()) {
                FileTool.createFileDir(Constant.UUID_AUTO_CREATE_DIRECTORY);
            }
            File fileTwo = new File(Constant.UUID_AUTO_CREATE_DIRECTORY + Constant.UUID_AUTO_TWOFILE_DIRECTORY);
            if (!fileTwo.exists()) {
                FileTool.createFileDir(Constant.UUID_AUTO_CREATE_DIRECTORY + Constant.UUID_AUTO_TWOFILE_DIRECTORY);
            }
            File filePath = new File(Constant.UUID_AUTO_CREATE_DIRECTORY + Constant.UUID_AUTO_TWOFILE_DIRECTORY + Constant.UUID_AUTO_FILE_PATH);
            if (!filePath.exists()) {
                FileTool.createFile(Constant.UUID_AUTO_CREATE_DIRECTORY + Constant.UUID_AUTO_TWOFILE_DIRECTORY + Constant.UUID_AUTO_FILE_PATH);
            }
            //生成
            long uuid_first = System.currentTimeMillis();
            deviceId = uuid_first + "" + NumberTool.createRandom();
            String aesJson = null;
            try {
                aesJson = AesTool.encrypt(deviceId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            FileTool.writeFileToSDFile(Constant.UUID_AUTO_CREATE_DIRECTORY + Constant.UUID_AUTO_TWOFILE_DIRECTORY + Constant.UUID_AUTO_FILE_PATH, aesJson);
        } else {
            //第二次读取
            String text = FileTool.readFileToSDFile(Constant.UUID_AUTO_CREATE_DIRECTORY + Constant.UUID_AUTO_TWOFILE_DIRECTORY + Constant.UUID_AUTO_FILE_PATH);
            try {
                deviceId = AesTool.decrypt(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deviceId;
    }

    /***
     * 手机号码验证表达式
     */
    public boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
