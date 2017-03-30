package com.app.Model;

/**
 * Created by ASUS on 2016/12/1.
 */
public class RegisterMode {
    private String device_id;
    private String imsi_no;
    private String tele_supo;
    private String area;
    private String random;
    private String password;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getImsi_no() {
        return imsi_no;
    }

    public void setImsi_no(String imsi_no) {
        this.imsi_no = imsi_no;
    }

    public String getTele_supo() {
        return tele_supo;
    }

    public void setTele_supo(String tele_supo) {
        this.tele_supo = tele_supo;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
