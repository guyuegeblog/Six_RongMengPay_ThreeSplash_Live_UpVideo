package com.app.WeiXin;

/**
 * Created by ASUS on 2017/1/11.
 */
public class ReqEntity {
    private String appid;
    private String partnerid;
    private String prepayid;
    private String noncestr;
    private String timestamp;
    private String packages;
    private String sign_two;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }

    public String getSign_two() {
        return sign_two;
    }

    public void setSign_two(String sign_two) {
        this.sign_two = sign_two;
    }
}
