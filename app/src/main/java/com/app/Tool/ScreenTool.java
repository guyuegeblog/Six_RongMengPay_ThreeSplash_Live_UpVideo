package com.app.Tool;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * 常用单位转换的辅助类，涉及到单位转换的方法都在这里
 * </ul>
 **/
public class ScreenTool {


    private ScreenTool() {
        throw new AssertionError();
    }

    /**
     * dp转px
     */
    public static float dp2Px(Context context, float dp) {
        if (context == null) {
            return -1;
        }
        return dp * context.getResources().getDisplayMetrics().density;
    }

    /**
     * px转dp
     */
    public static float px2Dp(Context context, float px) {
        if (context == null) {
            return -1;
        }
        return px / context.getResources().getDisplayMetrics().density;
    }

    /**
     * sp转px
     */
    public static int sp2Px(Context context, float spVal) {
        if (context == null) {
            return -1;
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px转sp
     */
    public static float px2Sp(Context context, float pxVal) {
        if (context == null) {
            return -1;
        }
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }

    /**
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dp2PxInt(Context context, float dp) {
        return (int)(dp2Px(context, dp) + 0.5f);
    }

    /**
     *
     * @param context
     * @param px
     * @return
     */
    public static int pxToDpCeilInt(Context context, float px) {
        return (int)(px2Dp(context, px) + 0.5f);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getWidth(Activity activity) {
        WindowManager wm = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    /**
     * 获取屏幕高度
     */
    public static int getHeight(Activity activity) {
        WindowManager wm = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void setLight(Activity context, int brightness) {
//        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
//        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
//        context.getWindow().setAttributes(lp);
    }
}
