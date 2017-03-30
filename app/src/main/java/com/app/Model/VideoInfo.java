package com.app.Model;

import java.io.Serializable;

/**
 * Author: Jan
 * CreateTime:on 2016/11/1.
 */
public class VideoInfo implements Serializable {

    private String address_hd;
    private String address_sd;
    private String spare1;
    private String id;
    private boolean isThreeVideo = false;//是否是三级
    private boolean isLookVideo = false;//是否是体验区

    public String getSpare1() {
        return spare1;
    }

    public void setSpare1(String spare1) {
        this.spare1 = spare1;
    }

    public boolean isLookVideo() {
        return isLookVideo;
    }

    public void setLookVideo(boolean lookVideo) {
        isLookVideo = lookVideo;
    }

    public boolean isThreeVideo() {
        return isThreeVideo;
    }

    public void setThreeVideo(boolean threeVideo) {
        isThreeVideo = threeVideo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    private String name;
}
