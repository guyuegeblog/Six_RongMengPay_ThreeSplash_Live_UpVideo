package com.app.ZhiFuBao;

/**
 * Created by Administrator on 2016/3/30.
 */
public class Alipay {

    public String partner;//商户Id
    public String seller;//商户收款账号
    public String rsa_private;//商户密钥
    public String rsa_public;//支付宝公钥
    public String sdk_pay_flag;
    public String notify_url;
    public String respCode;

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getRsa_private() {
        return rsa_private;
    }

    public void setRsa_private(String rsa_private) {
        this.rsa_private = rsa_private;
    }

    public String getRsa_public() {
        return rsa_public;
    }

    public void setRsa_public(String rsa_public) {
        this.rsa_public = rsa_public;
    }

    public String getSdk_pay_flag() {
        return sdk_pay_flag;
    }

    public void setSdk_pay_flag(String sdk_pay_flag) {
        this.sdk_pay_flag = sdk_pay_flag;
    }

}
