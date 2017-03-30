package com.app.Model;

/**
 * Created by ASUS on 2017/3/3.
 */
public class Live_Request {
    public String pageindex;
    public String pagesize;
    public static int Live_Page = 1;
    public static int Live_Size = 20;

    public String getPageindex() {
        return pageindex;
    }

    public void setPageindex(String pageindex) {
        this.pageindex = pageindex;
    }

    public String getPagesize() {
        return pagesize;
    }

    public void setPagesize(String pagesize) {
        this.pagesize = pagesize;
    }
}
