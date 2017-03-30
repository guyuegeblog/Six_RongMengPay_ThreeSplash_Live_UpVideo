package com.app.Bean;

/**
 * Created by ASUS on 2016/12/15.
 */
public class PayType {
    public static final int PAY_DUANXIN = 1;//短信
    public static final int PAY_ZFB = 2;//支付宝
    public static final int PAY_WEIXIN = 3;//微信
    public static final int PAY_YINLIAN = 4;//银联
    public static final int PAY_CAIFUTONG = 5;//财付通
    public static final int PAY_SHENZHOUFU = 6;//神州付

//    public static String wx_huangjin_vip_price = "35";
//    public static String wx_zuanshi_vip_price = "28";
//    public static String zfb_huangjin_vip_price = "30";//23
//    public static String zfb_zuanshi_vip_price = "28";//26

    //会员等级支付价格
    public static String silver_Price = "49";//白银价格
    public static String gold_Price = "38";//黄金价格+5元
    public static String platMinum_Price = "35";//白金价格+5元
    public static String diamond_Price = "30";//钻石价格+5元
    public static String red_Diamond_Price = "25";//粉钻价格+5元
    public static String crown_Price = "20";//皇冠价格+5元

    //退出弹窗价格
    public static String exit_Dialog_WX = "30";//退出弹窗微信价格
    public static String exit_Dialog_ZFB = "25";//退出弹窗支付宝价格

}
