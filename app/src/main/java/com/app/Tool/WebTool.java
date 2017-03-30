package com.app.Tool;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.app.View.T;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 2016/12/6.
 */
public class WebTool {

    /***
     * 启动android手机的一些浏览器打开网站
     */
    private int ijk = 0;
    private boolean isHaveUc = false;

    public void starBrowser(String indexHtmlStr, Context context) {
        //根据获取的浏览器信息打开网站
        int count = 0;
        if (broserList.size() == 0) {
            //手机没有浏览器
            new T().centershow(context, "~请您安装网页浏览器,访问我们的网站。", 2000);
        } else {
            try {
                count = broserList.size() - 1;//浏览器总数  //最大小标取count
                for (int i = 0; i < broserList.size(); i++) {
                    ActivityInfo activityInfo = broserList.get(i);
                    //uc
                    if (activityInfo.packageName.equals("com.uc.browser")
                            || activityInfo.packageName.equals("com.UCMobile")) {
                        isHaveUc = true;
                        openHtml(context, i, indexHtmlStr);
                        break;
                    }
                }
                if (isHaveUc == true) {
                    return;
                }
                openHtml(context, ijk, indexHtmlStr);
            } catch (Exception e) {
                ijk++;
                if (ijk > count) {
                    new T().centershow(context, "~没有浏览器能打开这个网站~", 2000);
                } else {
                    openHtml(context, ijk, indexHtmlStr);
                }
            }
        }

    }

    public void openHtml(Context context, int ijk, String indexHtmlStr) {
        ActivityInfo info = broserList.get(ijk);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(indexHtmlStr);
        intent.setData(content_url);
        intent.setClassName(info.packageName, info.name);
        context.startActivity(intent);
    }

    /***
     * 获取当前手机所有浏览器信息
     */
    public List<ActivityInfo> broserList = new ArrayList<ActivityInfo>();

    public List<ActivityInfo> getAllBrowserInfo(Context context) {
        broserList.clear();
        String default_browser = "android.intent.category.DEFAULT";
        String browsable = "android.intent.category.BROWSABLE";
        String view = "android.intent.action.VIEW";

        Intent intent = new Intent(view);
        intent.addCategory(browsable);
        intent.addCategory(default_browser);
        Uri uri = Uri.parse("http://");
        intent.setDataAndType(uri, null);


        // 找出手机当前安装的所有浏览器程序
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
        if (resolveInfoList.size() > 0) {
            for (int i = 0; i < resolveInfoList.size(); i++) {
                ActivityInfo activityInfo = resolveInfoList.get(i).activityInfo;
                broserList.add(activityInfo);
            }
//            String packageName = activityInfo.packageName;
//            String className = activityInfo.name;
            return broserList;
        } else {
            return broserList;
        }
    }
}
