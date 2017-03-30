package com.app.Tool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ASUS on 2016/12/6.
 */
public class ParamsPutterTool {

    /***
     * SharedPreferences 写入数据
     */
    public static void sharedPreferencesWriteData(Context context, String filename, String key, String value) {
        //实例化SharedPreferences对象,参数1是存储文件的名称，参数2是文件的打开方式，当文件不存在时，直接创建，如果存在，则直接使用
        SharedPreferences mySharePreferences = context.getSharedPreferences(filename, Activity.MODE_PRIVATE);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = mySharePreferences.edit();

        //用putString的方法保存数据
        editor.putString(key, value);

        //提交数据
        editor.commit();
    }

    /**
     * 获取数据
     */
    public static String sharedPreferencesReadData(Context context, String filename, String key) {
        //实例化SharedPreferences对象
        SharedPreferences mySharePerferences = context.getSharedPreferences(filename, Activity.MODE_PRIVATE);

        //用getString获取值
        return mySharePerferences.getString(key, "");
    }

    /***
     * shared删除文件中的全部数据
     *
     * @param context
     * @param filename
     */
    public static void sharedPreferencesDelByFileAllData(Context context, String filename) {
        //实例化SharedPreferences对象,参数1是存储文件的名称，参数2是文件的打开方式，当文件不存在时，直接创建，如果存在，则直接使用
        SharedPreferences mySharePreferences = context.getSharedPreferences(filename, Activity.MODE_PRIVATE);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = mySharePreferences.edit();

        //用clear()的方法删除数据
        editor.clear();
        editor.clear();
        editor.clear();
        editor.clear();
        editor.clear();

        //提交数据
        editor.commit();
    }

    /***
     * shared删除指定的数据
     *
     * @param context
     * @param filename
     */
    public static void sharedPreferencesDelOrderData(Context context, String filename, String key) {
        //实例化SharedPreferences对象,参数1是存储文件的名称，参数2是文件的打开方式，当文件不存在时，直接创建，如果存在，则直接使用
        SharedPreferences mySharePreferences = context.getSharedPreferences(filename, Activity.MODE_PRIVATE);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = mySharePreferences.edit();

        //删除指定数据
        editor.remove(key);

        //提交数据
        editor.commit();
    }

}
