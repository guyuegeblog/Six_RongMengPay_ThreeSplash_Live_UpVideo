package com.app.Model;

import java.io.Serializable;

/**
 * Author: Jan
 * CreateTime:on 2016/10/27.
 */

public class LiveInfo implements Serializable {
    public String name;//台名
    public String pic; //台标
    public String pic_heng;//非vip图片(停用)
    public String pic_heng_vip;//vip图片
    public String address;//地址
    public String info;
    public String spare1;//一级分类ID
    public String spare2;//是否试看(0.不可以1.可以)
    public String spare3;//图片数组
    public String spare4;//视频数组
    public String spare5;//备用字段5

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPic_heng() {
        return pic_heng;
    }

    public void setPic_heng(String pic_heng) {
        this.pic_heng = pic_heng;
    }

    public String getPic_heng_vip() {
        return pic_heng_vip;
    }

    public void setPic_heng_vip(String pic_heng_vip) {
        this.pic_heng_vip = pic_heng_vip;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSpare1() {
        return spare1;
    }

    public void setSpare1(String spare1) {
        this.spare1 = spare1;
    }

    public String getSpare2() {
        return spare2;
    }

    public void setSpare2(String spare2) {
        this.spare2 = spare2;
    }

    public String getSpare3() {
        return spare3;
    }

    public void setSpare3(String spare3) {
        this.spare3 = spare3;
    }

    public String getSpare4() {
        return spare4;
    }

    public void setSpare4(String spare4) {
        this.spare4 = spare4;
    }

    public String getSpare5() {
        return spare5;
    }

    public void setSpare5(String spare5) {
        this.spare5 = spare5;
    }
}
