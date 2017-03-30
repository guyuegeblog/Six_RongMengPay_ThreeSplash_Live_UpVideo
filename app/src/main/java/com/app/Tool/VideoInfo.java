package com.app.Tool;

import java.io.Serializable;

/**
 * Author: Jan
 * CreateTime:on 2016/11/1.
 */
public class VideoInfo implements Serializable {

    private String address_hd;
    private String address_sd;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress_hd() {
        return address_hd;
    }

    public void setAddress_hd(String address_hd) {
        this.address_hd = address_hd;
    }

    public String getAddress_sd() {
        return address_sd;
    }

    public void setAddress_sd(String address_sd) {
        this.address_sd = address_sd;
    }
}
