package com.app.Model;

/**
 * Created by ASUS on 2017/1/17.
 */
public class ProductType {

    //产品ID(1、LX产品2、旭东产品,3 VPN)
    public static final String productType_XuDong = "2";
    public static final String productType_LX = "1";
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
