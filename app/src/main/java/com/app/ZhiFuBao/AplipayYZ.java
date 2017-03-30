package com.app.ZhiFuBao;

/**
 * 支付宝验证实体
 */
public class AplipayYZ {
    private String out_trade_no;
    private String userName;
    private String imeiLastId;
    private String payTime;

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
