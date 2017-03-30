package com.app.Bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import com.app.Model.ChanneLnfo;
import com.app.Model.CommentInfo;
import com.app.Model.ComprehenSiveInfo;
import com.app.Model.LiveInfo;
import com.app.Model.LookInfo;
import com.app.Tool.RandomTool;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lin on 2016/11/22.
 */
public class Multi implements MultiItemEntity, Serializable {

    public static final int LOOK_AT = 199;
    public static final int LOOK_VIP = 299;

    public static final int Moon_LEVE2 = 111;
    public static final int Moon_LEVE1 = 112;
    public static int Moon_LEVE = Moon_LEVE1;

    public static final int Moon_LIVE_LEVE2 = 211;
    public static final int Moon_LIVE_LEVE1 = 212;
    public static int Moon_LIVE_LEVE = Moon_LIVE_LEVE1;

    //  public static final int PAY_VIP2 = 512;
    public static final int PAY_VIP1 = 511;
    public static int PAY_VIP_LEVEL = PAY_VIP1;

    public static boolean LIVE_UNBIND = false;

    public static final int LOOK_BANNER = 166;
    public static final int LOOK_ITEM = 266;
    public static final int LOOK_AREA = 366;//试看专区
    public static final int LOOK_JARPANSE_JINGDIAN = 466;//岛国经典
    public static final int LOOK_YOUNG_WOMAN = 566;//激情少妇
    public static final int LOOK_BEAUTIFUAL_WOMAN = 666;//美女嫩模
    public static final int LOOK_MEITUISIEA = 766;//美腿丝袜
    public static final int LOOK_JARPANSE_SUREN = 866;//岛国素人
    public static final int LOOK_BOTTOM = 966;//底部

    public static final int CODE_MINGXINGNVYOU = 11;//明星女友
    public static final int CODE_ZHIFUYOUHUO = 12;//制服诱惑
    public static final int CODE_ZHAINANFULI = 13;//宅男福利
    public static final int CODE_ZUIJINGENGXIN = 14;//最近更新
    public static final int CODE_GIF = 15;//gif
    public static final int CODE_ITEM = 23;

    public static final int JAR_HEADER = 26;//头部
    public static final int JAR_ITEM = 27;//底部
    public static final int JAR_EMPTY = 28;//底部
    public static final int THREE_ITEM = 29;//底部

    public static final int COMPREHENSIVE_DIANYING = 1000;//三级
    public static final int COMPREHENSIVE_DAPIAN = 2000;//大片
    public static final int COMPREHENSIVE_ZONGYI = 3000;//综艺
    public static final int COMPREHENSIVE_DUANSHIPIN = 4000;//短视频
    public static final int COMPREHENSIVE_XIEZHEN = 5000;//写真
    public static int COMPREHENSIVE_CURRENT = COMPREHENSIVE_DIANYING;//写真

    public static final int CHANNEL_ITEM = 56;//频道
    public static final int CHANNEL_TWO_HEADER = 59;//
    public static final int CHANNEL_TYPE_ITEM = 57;//
    public static final int CHANNEL_TWO_BOTTOM = 58;//

    public static final int MESSAGE_HEADER = 75;
    public static final int MESSAGE_MIDDLE = 76;
    public static final int MESSAGE_HOT = 77;
    public static final int MESSAGE_ITEM = 78;
    public static final int MESSAGE_BOTTOM = 79;

    public static final int MORE_ITEM = 67;//
    public static final int VIP_SIZE = 100;//
    public static boolean isShowLiveDialog = true;

    public static final int LIVE_ITEM = 70;//

    public static final int ITEM_SPAN_SIZE = 1;
    public static final int LOOK_NORMAL_SPAN_SIZE = 2;
    public static final int JARPAN_NORMAL_SPAN_SIZE = 3;

    public static final int VIDEO_TIME_DATA_SIZE = 1000;

    public static final int VIP_NOT_VIP_TYPE = 16558;//非会员
    public static final int VIP_SILVER_TYPE = 16560;//白银
    public static final int VIP_GOLD_TYPE = 16555;//黄金
    public static final int VIP_PLAT_NIUM_TYPE = 16556;//白金
    public static final int VIP_DIAMOND_TYPE = 16557;//钻石
    public static final int VIP_RED_DIAMOND_TYPE = 17001;//粉钻
    public static final int VIP_CROWN_TYPE = 17002;//皇冠

    public static boolean USER_PAY_SUCCES = false;
    public static boolean isShowDialog = false;
    public static int Wx_Pay_Succes = 2;//0成功，1失败,2无支付

    public static String Ip_Adress = "";//default four data ip adress0
    public static String dynamic_Ip_Adress_1 = "60.205.56.207";//default four data ip adress1
    public static String dynamic_Ip_Adress_2 = "139.129.228.86";//default four data ip adress2
    public static String dynamic_Ip_Adress_3 = "139.129.218.6";//default four data ip adress3

    private List<LookInfo> lookInfoList;
    private LookInfo lookInfo;
    private LiveInfo liveInfo;
    private ComprehenSiveInfo comprehenSiveInfo;
    private ChanneLnfo channeLnfo;
    private CommentInfo commentInfo;
    private Object object;
    private String channeType;
    private int position;


    private int itemType;
    private int spanSize;


    public static String getDynamicIp() {
        try {
            int random = RandomTool.getRandom(1, 3);
            if (random == 1) {
                return dynamic_Ip_Adress_1;
            } else if (random == 2) {
                return dynamic_Ip_Adress_2;
            } else if (random == 3) {
                return dynamic_Ip_Adress_3;
            } else {
                return dynamic_Ip_Adress_3;
            }
        } catch (Exception e) {
            return dynamic_Ip_Adress_2;
        }
    }

    public Multi(List<LookInfo> lookInfoList, int itemType, int spanSize) {
        this.lookInfoList = lookInfoList;
        this.itemType = itemType;
        this.spanSize = spanSize;
    }

    public Multi(ComprehenSiveInfo comprehenSiveInfo, int itemType, int spanSize) {
        this.comprehenSiveInfo = comprehenSiveInfo;
        this.itemType = itemType;
        this.spanSize = spanSize;
    }

    public Multi(LookInfo lookInfo, int itemType, int spanSize) {
        this.lookInfo = lookInfo;
        this.itemType = itemType;
        this.spanSize = spanSize;
    }

    public Multi(LiveInfo liveInfo, int itemType, int spanSize, int position) {
        this.liveInfo = liveInfo;
        this.itemType = itemType;
        this.spanSize = spanSize;
        this.position = position;
    }

    public Multi(CommentInfo commentInfo, int itemType, int spanSize) {
        this.commentInfo = commentInfo;
        this.itemType = itemType;
        this.spanSize = spanSize;
    }

    public Multi(ChanneLnfo channeLnfo, int itemType, int spanSize) {
        this.channeLnfo = channeLnfo;
        this.itemType = itemType;
        this.spanSize = spanSize;
    }

    public Multi(Object object, int itemType, int spanSize) {
        this.object = object;
        this.itemType = itemType;
        this.spanSize = spanSize;
    }

    public Multi(String channeType, int itemType, int spanSize) {
        this.channeType = channeType;
        this.itemType = itemType;
        this.spanSize = spanSize;
    }

    public List<LookInfo> getLookInfoList() {
        return lookInfoList;
    }

    public void setLookInfoList(List<LookInfo> lookInfoList) {
        this.lookInfoList = lookInfoList;
    }

    public LookInfo getLookInfo() {
        return lookInfo;
    }

    public void setLookInfo(LookInfo lookInfo) {
        this.lookInfo = lookInfo;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getSpanSize() {
        return spanSize;
    }

    public void setSpanSize(int spanSize) {
        this.spanSize = spanSize;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public ComprehenSiveInfo getComprehenSiveInfo() {
        return comprehenSiveInfo;
    }

    public void setComprehenSiveInfo(ComprehenSiveInfo comprehenSiveInfo) {
        this.comprehenSiveInfo = comprehenSiveInfo;
    }

    public ChanneLnfo getChanneLnfo() {
        return channeLnfo;
    }

    public void setChanneLnfo(ChanneLnfo channeLnfo) {
        this.channeLnfo = channeLnfo;
    }

    public String getChanneType() {
        return channeType;
    }

    public void setChanneType(String channeType) {
        this.channeType = channeType;
    }

    public LiveInfo getLiveInfo() {
        return liveInfo;
    }

    public void setLiveInfo(LiveInfo liveInfo) {
        this.liveInfo = liveInfo;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public CommentInfo getCommentInfo() {
        return commentInfo;
    }

    public void setCommentInfo(CommentInfo commentInfo) {
        this.commentInfo = commentInfo;
    }
}
