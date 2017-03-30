package com.app.WeiXin;

/**
 * Created by Administrator on 2016/3/29.
 */
public class WXYZ {
    private String out_trade_no;//订单号
    private String userName;//账户
    private String imeiLastId;//最后id
    private String payTime;//购买会员时长

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImeiLastId() {
        return imeiLastId;
    }

    public void setImeiLastId(String imeiLastId) {
        this.imeiLastId = imeiLastId;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }
}
