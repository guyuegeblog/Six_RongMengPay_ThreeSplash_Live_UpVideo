package com.app.View;


import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

//Toast统一管理类
public class T {

    public T() {
    }
    public boolean isShow = true;

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public void show(Context context, CharSequence message, int duration) {
        if (isShow) {
            Toast.makeText(context, message, duration).show();
        }
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public void show(Context context, int message, int duration) {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    public void centershow(Context context, String message, int duration) {
        if (isShow) {
            Toast toast = Toast.makeText(context, message, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private static Toast toast = null;
    private static   int toastDuration = 5000;

    public static void showTextToast(Activity activity, String msg) {
        if (toast == null) {
            toast = Toast.makeText(activity, msg, toastDuration);
//            toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }
    public static void showTextCenterToast(Activity activity, String msg) {
        if (toast == null) {
            toast = Toast.makeText(activity, msg, toastDuration);
            toast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

}
