package com.app.Application;

import android.app.Application;
import android.os.Handler;
import android.os.Message;
import com.app.Activity.MainActivity;
import com.szrm.pay.RMAPIFactory;
import com.third.app.R;

/**
 * Author: Jan
 * CreateTime:on 2016/10/28.
 */
public class TvApplication extends Application implements
        Thread.UncaughtExceptionHandler{

//    private static DemoHandler handler;
//    public static MainActivity demoActivity;

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();

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
//        if (handler == null) {
//            handler = new DemoHandler();
//        }
//        RMPay.getInstance().init(this, "1004", "100193", "442758520b9460549a2c52a6824af7df");
        RMAPIFactory.init(this, "1002", "100193", "0d117252099b66deaca187e0090907dc");
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

//    public static void sendMessage(Message msg) {
//        handler.sendMessage(msg);
//    }
//
//    public static class DemoHandler extends Handler {
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    if (demoActivity != null) {
//                        payloadData.append((String) msg.obj);
//                        payloadData.append("\n");
//                    }
//                    break;
//
//                case 1:
//                    if (demoActivity != null) {
//                    }
//                    break;
//            }
//        }
//    }
}
