package com.app.Net;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by ASUS on 2016/12/6.
 */
public class OkHttp {
    //饿汉式单例类.在类初始化时，已经自行实例化
    private OkHttp() {
    }

    private static final OkHttpClient okHttpClient = new OkHttpClient().newBuilder().connectTimeout(NetConfig.CONN_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(NetConfig.READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(NetConfig.WRITE_TIMEOUT, TimeUnit.MILLISECONDS).build();

    private static final OkHttpClient okHttpClient_Pay = new OkHttpClient().newBuilder().connectTimeout(4000, TimeUnit.MILLISECONDS)
            .readTimeout(4000, TimeUnit.MILLISECONDS)
            .writeTimeout(4000, TimeUnit.MILLISECONDS).build();

    //静态工厂方法
    public static OkHttpClient getInstance() {
        return okHttpClient;
    }

    public static OkHttpClient getInstance_Pay() {
        return okHttpClient_Pay;
    }
}
