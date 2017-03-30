package com.app.Application;

import android.app.Application;

import com.rongmeng.pay.RMPay;
import com.third.app.R;

/**
 * Author: Jan
 * CreateTime:on 2016/10/28.
 */
public class TvApplication extends Application implements
        Thread.UncaughtExceptionHandler{

    @Override
    public void onCreate() {
        super.onCreate();
        //字体文件配置
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/DroidSerif-Regular.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build()
//        );
        //设置Thread Exception Handler(这行代码造成了程序捕获app应用异常后，无法在studio logcat里准确输出提示开发者的错误日志)
        //Thread.setDefaultUncaughtExceptionHandler(this);
        RMPay.getInstance().init(this, "1002", "100193", "0d117252099b66deaca187e0090907dc");
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
//        Log.i("uncaughtException","uncaughtException"+ex.getMessage());
//        System.exit(0);
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }
}
