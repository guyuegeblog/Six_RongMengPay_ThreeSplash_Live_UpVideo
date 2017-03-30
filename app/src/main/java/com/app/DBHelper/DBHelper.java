package com.app.DBHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ASUS on 2016/11/21.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "night_pop_tv_firsts_four100015.db";
    private static final int DATABASE_VERSION = 3;

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
//        //创建三级表
//        db.execSQL("CREATE TABLE IF NOT EXISTS ThreeVideo" +
//                "(id INTEGER PRIMARY KEY, createtime VARCHAR,name VARCHAR,area VARCHAR," +
//                "address_sd VARCHAR,address_hd VARCHAR,pic VARCHAR,pic_heng VARCHAR," +
//                "lasttime VARCHAR,score VARCHAR,showtime VARCHAR,state VARCHAR,isLook VARCHAR)");
//
//        //创建大片表
//        db.execSQL("CREATE TABLE IF NOT EXISTS BigVideoInfo" +
//                "(id INTEGER PRIMARY KEY, createtime VARCHAR,name VARCHAR,area VARCHAR," +
//                "address_sd VARCHAR,address_hd VARCHAR,pic VARCHAR,pic_heng VARCHAR," +
//                "lasttime VARCHAR,score VARCHAR,showtime VARCHAR,state VARCHAR,isLook VARCHAR)");
//
//        //创建评论表
        db.execSQL("CREATE TABLE IF NOT EXISTS CommentInfo" +
                "(name VARCHAR," + "pic VARCHAR,hand VARCHAR,info VARCHAR)");
//
//        //创建直播表
//        db.execSQL("CREATE TABLE IF NOT EXISTS LiveInfo" +
//                "(id INTEGER PRIMARY KEY, isLook VARCHAR,live_Url VARCHAR,logo_url VARCHAR," +
//                "remarks VARCHAR,video_name VARCHAR,wonderful VARCHAR,tv_name VARCHAR,description VARCHAR," +
//                "create_Time VARCHAR,clientPic_Url VARCHAR,client_FirstPic_Url VARCHAR,orderBy VARCHAR)");
        //创建用户信息表
        db.execSQL("CREATE TABLE IF NOT EXISTS RandomInfo" +
                "(random_Text VARCHAR)");

        //创建试看无码表
        db.execSQL("CREATE TABLE IF NOT EXISTS Look_Code" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR,pic VARCHAR,address_sd VARCHAR," +
                "address_hd VARCHAR,type VARCHAR,pic_heng VARCHAR,isvip VARCHAR,spare1 VARCHAR)");

        //创建试看更多表
        db.execSQL("CREATE TABLE IF NOT EXISTS Look_More" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR,pic VARCHAR,address_sd VARCHAR," +
                "address_hd VARCHAR,type VARCHAR,pic_heng VARCHAR,isvip VARCHAR)");

        //创建视频时间表
        db.execSQL("CREATE TABLE IF NOT EXISTS night_Video_Time" +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT,video_time_random VARCHAR)");

    }

    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("ALTER TABLE ThreeVideo ADD COLUMN other STRING");
//        db.execSQL("ALTER TABLE BigVideoInfo ADD COLUMN other STRING");
        db.execSQL("ALTER TABLE CommentInfo ADD COLUMN other STRING");
//        db.execSQL("ALTER TABLE LiveInfo ADD COLUMN other STRING");
        db.execSQL("ALTER TABLE RandomInfo ADD COLUMN other STRING");
        db.execSQL("ALTER TABLE Look_Code ADD COLUMN other STRING");
        db.execSQL("ALTER TABLE Look_More ADD COLUMN other STRING");
        db.execSQL("ALTER TABLE night_Video_Time ADD COLUMN other STRING");
    }
}
