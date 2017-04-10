package com.app.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.Adapter.FragmentAdapter;
import com.app.Application.TvApplication;
import com.app.Bean.Multi;
import com.app.Bean.PayType;
import com.app.Constant.Constant;
import com.app.DBManager.DBManager;
import com.app.Model.ThirdProductInfo;
import com.app.Net.MobClick;
import com.app.Net.NetInterface;
import com.app.Net.OkHttp;
import com.bumptech.glide.Glide;
import com.third.app.R;
import com.app.Service.FifteenMinituesService;
import com.app.Tool.AppTool;
import com.app.Tool.DateTool;
import com.app.Tool.FileTool;
import com.app.Tool.NetTool;
import com.app.Tool.RandomTool;
import com.app.Tool.ScreenTool;
import com.app.Tool.VipTool;
import com.app.Ui.FullScreenVideo;
import com.app.View.T;
import com.paradoxie.autoscrolltextview.VerticalTextview;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.viewpager.SViewPager;
import com.wjdz.rmgljtsc.wxapi.PayActivity;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    @Bind(R.id.titletext)
    TextView titletext;
    @Bind(R.id.verticalview)
    VerticalTextview verticalview;
    @Bind(R.id.viewpager)
    SViewPager viewpager;
    @Bind(R.id.menu)
    FixedIndicatorView menu;
    @Bind(R.id.show_live_menu_tishi)
    ImageView showLiveMenuTishi;
    @Bind(R.id.show_live)
    RelativeLayout showLive;
    private static final String TAG = "GetuiSdkDemo";
    private static Activity mContext;
    private DBManager dbManager;
    private IndicatorViewPager indicatorViewPager;
    private boolean isBind = false;
    private String choice = "zfb";
    private boolean isShowLiveImage = true;
    private Timer timer = new Timer();
    private TimerTask timerTask;
    private FragmentAdapter fragmentAdapter;
    private ServiceConnection fifteenService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
        }
    };
    private String vipApkName = "橙人TV.apk";
    private String vpnApkName = "vpn.apk";
    private final int GO_HIDE_SHOW_LIVE_IMAGES = 1030;
    private final int GO_DOWN_VPN_FAILED = 1056;
    private final int GO_DOWN_VPN_PROGRESS = 1058;
    private final int GO_DOWN_VPN_SUCCES = 1089;
    private final int GO_DOWN_VPNSEVEN_DAY_SUCCES = 1093;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_HIDE_SHOW_LIVE_IMAGES:
                    if (TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.TV_USER_LIVE_UNBIND))) {
                        showLiveMenuTishi.setImageResource(R.drawable.wei_live_show);
                    } else {
                        showLiveMenuTishi.setImageResource(R.drawable.close_live);
                    }
                    if (isShowLiveImage) {
                        isShowLiveImage = false;
                        showLive.setVisibility(View.GONE);
                    } else {
                        isShowLiveImage = true;
                        showLive.setVisibility(View.VISIBLE);
                    }
                    break;
                case GO_DOWN_VPN_FAILED:
                    MobclickAgent.onEvent(mContext, MobClick.DOWNLOAD_TV_FIRST_PAY_ID_FAILED);//埋点统计
                    if (dialog_Vip_Apk != null) dialog_Vip_Apk.dismiss();
                    T.showTextToast(mContext, "下载失败!!");
                    break;
                case GO_DOWN_VPN_SUCCES:
                    MobclickAgent.onEvent(mContext, MobClick.DOWNLOAD_TV_FIRST_PAY_ID);//埋点统计
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    try {
                        AppTool.installApk(mContext, FileTool.getSDCardPath() + "/" + vipApkName);
                    } catch (Exception e) {
                        return;
                    }
                    break;
                case GO_DOWN_VPNSEVEN_DAY_SUCCES:
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    try {
                        AppTool.installApk(mContext, FileTool.getSDCardPath() + "/" + vpnApkName);
                    } catch (Exception e) {
                        return;
                    }
                    break;
                case GO_DOWN_VPN_PROGRESS:
                    showProgressDialog(msg.arg1, msg.arg2);
                    break;
            }
        }
    };

    // SDK服务是否启动.
//    private boolean isServiceRunning = false;
//    private Context context;
//
//    private String appkey = "";
//    private String appsecret = "";
//    private String appid = "";
//
//    private static final int REQUEST_PERMISSION = 0;
//
//    // DemoPushService.class 自定义服务名称, 核心服务
//    private Class userPushService = DemoPushService.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;
        initView();
        initUrl();
        initData();
        initService();
        initDownInfo();
//        initGeTui();
    }

//    private void initGeTui() {
//        TvApplication.demoActivity = this;
//        parseManifests();
//        PackageManager pkgManager = getPackageManager();
//
//        // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
//        boolean sdCardWritePermission =
//                pkgManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED;
//
//        // read phone state用于获取 imei 设备信息
//        boolean phoneSatePermission =
//                pkgManager.checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName()) == PackageManager.PERMISSION_GRANTED;
//
//        if (Build.VERSION.SDK_INT >= 23 && !sdCardWritePermission || !phoneSatePermission) {
//            requestPermission();
//        } else {
//            PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
//        }
//
//        // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
//        // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
//        // IntentService, 必须在 AndroidManifest 中声明)
//        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);
//
//        // 应用未启动, 个推 service已经被唤醒,显示该时间段内离线消息
//        if (TvApplication.payloadData != null) {
////            tLogView.append(TvApplication.payloadData);
//        }
//
//        // cpu 架构
////        Log.d(TAG, "cpu arch = " + (Build.VERSION.SDK_INT < 21 ? Build.CPU_ABI : Build.SUPPORTED_ABIS[0]));
//
//        // 检查 so 是否存在
//        File file = new File(this.getApplicationInfo().nativeLibraryDir + File.separator + "libgetuiext2.so");
////        Log.e(TAG, "libgetuiext2.so exist = " + file.exists());
//        if (isServiceRunning) {
////            Log.d(TAG, "stopping sdk...");
////            PushManager.getInstance().stopService(this.getApplicationContext());// 当前为运行状态，停止SDK服务
////            isServiceRunning = false;
//        } else {
//            Log.d(TAG, "reinitializing sdk...");// 当前未运行状态，启动SDK服务
//            PushManager.getInstance().initialize(this.getApplicationContext(), userPushService); // 重新初始化sdk
//            isServiceRunning = true;
//        }
//
//    }
//
//    private void parseManifests() {
//        String packageName = getApplicationContext().getPackageName();
//        try {
//            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
//            if (appInfo.metaData != null) {
//                appid = appInfo.metaData.getString("PUSH_APPID");
//                appsecret = appInfo.metaData.getString("PUSH_APPSECRET");
//                appkey = appInfo.metaData.getString("PUSH_APPKEY");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void requestPermission() {
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
//                REQUEST_PERMISSION);
//    }

    private void initView() {
        fragmentAdapter = fragmentAdapter == null ? new FragmentAdapter(getSupportFragmentManager(), mContext) : fragmentAdapter;
        indicatorViewPager = new IndicatorViewPager(menu, viewpager);
        indicatorViewPager.setAdapter(fragmentAdapter);
        // 禁止viewpager的滑动事件
        viewpager.setCanScroll(false);
        // 设置viewpager保留界面不重新加载的页面数量,算是这个框架的优化吧
        if (VipTool.getUserVipType(mContext) == Multi.VIP_GOLD_TYPE) {
            viewpager.setOffscreenPageLimit(5);
        } else if (VipTool.getUserVipType(mContext) == Multi.VIP_NOT_VIP_TYPE) {
            viewpager.setOffscreenPageLimit(6);
        } else if (VipTool.getUserVipType(mContext) == Multi.VIP_PLAT_NIUM_TYPE) {
            viewpager.setOffscreenPageLimit(5);
        } else if (VipTool.getUserVipType(mContext) == Multi.VIP_DIAMOND_TYPE) {
            viewpager.setOffscreenPageLimit(5);
        } else if (VipTool.getUserVipType(mContext) == Multi.VIP_SILVER_TYPE) {
            viewpager.setOffscreenPageLimit(6);
        } else if (VipTool.getUserVipType(mContext) == Multi.VIP_RED_DIAMOND_TYPE) {
            viewpager.setOffscreenPageLimit(5);
        } else if (VipTool.getUserVipType(mContext) == Multi.VIP_CROWN_TYPE) {
            viewpager.setOffscreenPageLimit(5);
        }
        //设置边距
//       RelativeLayout.LayoutParams llp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//       llp.setMargins(ScreenTool.getWidth(mContext) / 6 * 5, 0, 0, 0);
//       showLive.setLayoutParams(llp);
        showHideLiveImages();
        indicatorViewPager.setOnIndicatorPageChangeListener(new IndicatorViewPager.OnIndicatorPageChangeListener() {
            @Override
            public void onIndicatorPageChange(int i, int position) {
//                Log.i("viewpagerpos", "i===" + i + "  position=====" + position);
                int vipType = VipTool.getUserVipType(mContext);
                statisMenu(vipType, position);
            }
        });
    }

    private void initDownInfo() {
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_SILVER_TYPE ||
                vipType == Multi.VIP_GOLD_TYPE ||
                vipType == Multi.VIP_DIAMOND_TYPE ||
                vipType == Multi.VIP_PLAT_NIUM_TYPE ||
                vipType == Multi.VIP_RED_DIAMOND_TYPE ||
                vipType == Multi.VIP_CROWN_TYPE) {
            ThirdProductInfo thirdProductInfo = Constant.thirdProductInfo;
            if (thirdProductInfo != null) {
                if (!AppTool.isInstalled(mContext, Constant.thirdProductInfo.packages)) {
                    //用户第一次付费2小时后强制下载TV
//                    time[0] = days;//天数
//                    time[1] = hours;//小时
//                    time[2] = minutes;//分钟
//                    time[3] = second;
                    long[] paytime;
                    try {
                        paytime = TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.PAY_FILE)) ? null :
                                DateTool.getTime(new Date(), DateTool.sdf.parse(FileTool.readFileToSDFile(Constant.PAY_FILE)));
                    } catch (ParseException e) {
                        paytime = null;
                    }
                    if (paytime != null) {
                        if (paytime[2] >= Constant.FIRST_PAY_DOWNLOAD_TV) {
                            if (Constant.isThanFourMinitesSplashStart) {
                                downVpnApk();
                            }
                        }
                        if (paytime[0] >= 1 || paytime[1] >= 1) {
                            if (Constant.isThanFourMinitesSplashStart) {
                                diaologSevenVpn();
                            }
                        }
                    }
                }
            }
        }
    }

    public void notifyFragmentManager() {
        indicatorViewPager.notifyDataSetChanged();
    }

    public void showHideLiveImages() {
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_NOT_VIP_TYPE) {
            showLiveMenuTishi.setVisibility(View.VISIBLE);
        } else {
            showLiveMenuTishi.setVisibility(View.GONE);
        }
        if (vipType != Multi.VIP_DIAMOND_TYPE) {
//            showLiveMenuTishi.setVisibility(View.GONE);
            if (timerTask == null) {
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(GO_HIDE_SHOW_LIVE_IMAGES);
                    }
                };
                timer.schedule(timerTask, 500, 500);
            }
        } else {
            showLiveMenuTishi.setVisibility(View.GONE);
        }
    }


    private void initData() {
        ArrayList<String> vipList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (String.valueOf(i).contains("0") || String.valueOf(i).contains("2") || String.valueOf(i).contains("5") || String.valueOf(i).contains("8")) {
                vipList.add("恭喜" + RandomTool.getRandomNumbers(10) + "充值成为VIP半年会员");
            } else {
                vipList.add("恭喜" + RandomTool.getRandomNumbers(10) + "充值成为VIP全年会员");
            }
        }
        verticalview.setTextList(vipList);//加入显示内容,集合类型
        verticalview.setText(16, 5, Color.RED);//设置属性,具体跟踪源码
        verticalview.setTextStillTime(3000);//设置停留时长间隔
        verticalview.setAnimTime(300);//设置进入和退出的时间间隔
        dbManager = DBManager.getDBManager(this);
    }

    private void initService() {
        if (!isBind) {
            if (VipTool.canVip1(mContext)) return;
            isBind = mContext.bindService(new Intent(mContext, FifteenMinituesService.class), fifteenService, BIND_AUTO_CREATE);
            registerBoradcastReceiver();
        }
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("live_intent");
        //注册广播
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();
            if (action.equals("live_intent")) {
                if (!TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.TV_USER_LIVE_UNBIND))) {
                    //已经解锁
                } else {
                    Multi.LIVE_UNBIND = true;
                    alertIntentLive();
                }
            }
        }
    };


    private long mExitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        onBackApp();
        return true;
    }

    private void onBackApp() {
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_NOT_VIP_TYPE) {
            dialogExitPay();
        } else {
            //付费用户没有任何赠送行为，避免影响抡轮
            if (vipType == Multi.VIP_SILVER_TYPE ||
                    vipType == Multi.VIP_GOLD_TYPE ||
                    vipType == Multi.VIP_DIAMOND_TYPE ||
                    vipType == Multi.VIP_PLAT_NIUM_TYPE ||
                    vipType == Multi.VIP_RED_DIAMOND_TYPE ||
                    vipType == Multi.VIP_CROWN_TYPE) {

//                ThirdProductInfo thirdProductInfo = Constant.thirdProductInfo;
//                if (thirdProductInfo == null) {
//                    exitApp();
//                } else {
//                    if (AppTool.isInstalled(mContext, Constant.thirdProductInfo.packages)) {
//                        exitApp();
//                    } else {
//                        downVpnApk();
//                    }
//                }
                if (!AppTool.isInstalled(mContext, Constant.vpnModel.getOne_package())) {
                    diaologSevenVpn();
                } else {
                    exitApp();
                }
            }
        }
    }

    private void exitApp() {
        if ((System.currentTimeMillis() - mExitTime) > 5000) {
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
//            mContext.finish();
        }
    }

    private void downVpnApk() {
        try {
            if (!NetTool.isConnected(mContext)) {
                T.showTextToast(mContext, "您的网络没有连接，请检查您的网络,然后下拉刷新试试");
                return;
            }
            ThirdProductInfo thirdProductInfo = Constant.thirdProductInfo;
            if (thirdProductInfo == null) {
                return;
            }
            diaologVpn();
        } catch (Exception e) {
        }
    }

    Dialog dialog_Vip_Apk;
    LinearLayout layout;
    SpannableStringBuilder mSpannableStringBuilder;

    public  void diaologVpn() {
        if (AppTool.isInstalled(mContext, Constant.thirdProductInfo.packages)) {
            return;
        }
        MobclickAgent.onEvent(mContext, MobClick.DOWNLOAD_TV_WINDOW_ID);//埋点统计
//        String tvName = "TV";
//        String vpnName = "H站大全";
        LayoutInflater inflater = LayoutInflater.from(mContext);
//        if (Constant.vpnModel.getOne_package().contains(tvName.toLowerCase())) {
//            layout = (LinearLayout) inflater.inflate(R.layout.dialog_vip_tv_download, null);
//        } else if (Constant.vpnModel.getOne_package().contains(vpnName.toLowerCase())) {
//            layout = (LinearLayout) inflater.inflate(R.layout.dialog_vip_vpn_download, null);
//        } else {
//            layout = (LinearLayout) inflater.inflate(R.layout.dialog_vip_vpn_download, null);
//        }
        layout = (LinearLayout) inflater.inflate(R.layout.dialog_vip_crtv_download, null);
        final int vipType = VipTool.getUserVipType(mContext);
        //对话框
        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
        if (dialog_Vip_Apk == null) {
            dialog_Vip_Apk = new Dialog(mContext, R.style.Dialog);
            dialog_Vip_Apk.show();
            dialog_Vip_Apk.setCancelable(false);
            Window window = dialog_Vip_Apk.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
//          layout.getBackground().setAlpha(150);
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;//ScreenTool.getWidth(this) / 5 * 3;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setContentView(layout);
            ImageButton dialog_vipdownload_down = (ImageButton) layout.findViewById(R.id.dialog_vipdownload_down);
            TextView vpn_text_decription = (TextView) layout.findViewById(R.id.vpn_text_decription);
            if (vipType == Multi.VIP_NOT_VIP_TYPE) {
                mSpannableStringBuilder = new SpannableStringBuilder(getResources().getString(R.string.zhou0));
                mSpannableStringBuilder.setSpan
                        (new ForegroundColorSpan(this.getResources().getColor(R.color.danhuang)), 0, 15, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            } else if (vipType == Multi.VIP_SILVER_TYPE) {
                mSpannableStringBuilder = new SpannableStringBuilder(getResources().getString(R.string.zhou1));
            } else if (vipType == Multi.VIP_GOLD_TYPE) {
                mSpannableStringBuilder = new SpannableStringBuilder(getResources().getString(R.string.zhou2));
            } else if (vipType == Multi.VIP_PLAT_NIUM_TYPE) {
                mSpannableStringBuilder = new SpannableStringBuilder(getResources().getString(R.string.zhou3));
            } else if (vipType == Multi.VIP_DIAMOND_TYPE) {
                mSpannableStringBuilder = new SpannableStringBuilder(getResources().getString(R.string.zhou4));
            } else if (vipType == Multi.VIP_RED_DIAMOND_TYPE) {
                mSpannableStringBuilder = new SpannableStringBuilder(getResources().getString(R.string.zhou5));
            } else if (vipType == Multi.VIP_CROWN_TYPE) {
                mSpannableStringBuilder = new SpannableStringBuilder(getResources().getString(R.string.zhou6));
            }
            if (vipType == Multi.VIP_SILVER_TYPE ||
                    vipType == Multi.VIP_GOLD_TYPE ||
                    vipType == Multi.VIP_PLAT_NIUM_TYPE ||
                    vipType == Multi.VIP_DIAMOND_TYPE ||
                    vipType == Multi.VIP_RED_DIAMOND_TYPE ||
                    vipType == Multi.VIP_CROWN_TYPE) {
                mSpannableStringBuilder.setSpan
                        (new ForegroundColorSpan(this.getResources().getColor(R.color.danhuang)), 0, 15, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                mSpannableStringBuilder.setSpan
//                        (new ForegroundColorSpan(this.getResources().getColor(R.color.shenhong)), 5, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                mSpannableStringBuilder.setSpan
//                        (new ForegroundColorSpan(this.getResources().getColor(R.color.pricelv)), 9, 11, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//
//                mSpannableStringBuilder.setSpan
//                        (new ForegroundColorSpan(this.getResources().getColor(R.color.black)), 11, 18, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

            }
            ImageView tantv_bg = (ImageView) layout.findViewById(R.id.tantv_bg);
            ImageView close_vpn = (ImageView) layout.findViewById(R.id.close_vpn);
            close_vpn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_Vip_Apk.dismiss();
                }
            });
            Glide.with(mContext).
                    load(NetInterface.TAN_TV_IMAGE_ADDRESS)
                    .placeholder(R.drawable.allloading).
                    error(R.drawable.allloading).into(tantv_bg);
            vpn_text_decription.setText(mSpannableStringBuilder);
            dialog_vipdownload_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_Vip_Apk.dismiss();
                    dialog_Vip_Apk = null;
                    if (!NetTool.isConnected(mContext)) {
                        T.showTextToast(mContext, "您的网络没有连接，请检查您的网络,然后下拉刷新试试");
                        return;
                    }
                    if (AppTool.isInstalled(mContext, Constant.thirdProductInfo.packages)) {
                        return;
                    }
                    File file = new File(FileTool.getSDCardPath() + "/" + vipApkName);
                    if (file.exists()) {
                        file.delete();
                    }
                    if (new File(FileTool.getSDCardPath() + "/" + vipApkName).exists()) {
                        AppTool.installApk(mContext, FileTool.getSDCardPath() + "/" + vipApkName);
                        return;
                    }
                    String downApkUrl = "";
                    if (vipType == Multi.VIP_SILVER_TYPE) {
                        downApkUrl = Constant.thirdProductInfo.downurl1;
                    } else if (vipType == Multi.VIP_GOLD_TYPE) {
                        downApkUrl = Constant.thirdProductInfo.downurl2;
                    } else if (vipType == Multi.VIP_PLAT_NIUM_TYPE) {
                        downApkUrl = Constant.thirdProductInfo.downurl3;
                    } else if (vipType == Multi.VIP_DIAMOND_TYPE) {
                        downApkUrl = Constant.thirdProductInfo.downurl4;
                    } else if (vipType == Multi.VIP_RED_DIAMOND_TYPE) {
                        downApkUrl = Constant.thirdProductInfo.downurl5;
                    } else if (vipType == Multi.VIP_CROWN_TYPE) {
                        downApkUrl = Constant.thirdProductInfo.downurl6;
                    } else {
                        downApkUrl = Constant.thirdProductInfo.downurl1;
                    }

                    Request request = new Request.Builder().url(downApkUrl).build();
                    OkHttp.getInstance().newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("h_bl", "文件下载失败");
                            mHandler.sendEmptyMessage(GO_DOWN_VPN_FAILED);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            InputStream is = null;
                            byte[] buf = new byte[2048];
                            int len = 0;
                            FileOutputStream fos = null;
                            String SDPath = FileTool.getSDCardPath();
                            try {
                                is = response.body().byteStream();
                                long total = response.body().contentLength();
                                File file = new File(SDPath, vipApkName);
                                fos = new FileOutputStream(file);
                                long sum = 0;
                                while ((len = is.read(buf)) != -1) {
                                    fos.write(buf, 0, len);
                                    sum += len;
                                    int progress = (int) (sum * 1.0f / total * 100);
                                    Log.d("h_bl", "progress=" + progress);
                                    Message msg = mHandler.obtainMessage();
                                    msg.what = GO_DOWN_VPN_PROGRESS;
                                    msg.arg1 = progress;
                                    msg.arg2 = (int) total;
                                    mHandler.sendMessage(msg);
                                }
                                fos.flush();
                                Log.d("h_bl", "文件下载成功");
                                mHandler.sendEmptyMessage(GO_DOWN_VPN_SUCCES);
                            } catch (Exception e) {
                                Log.d("h_bl", "文件下载失败");
                            } finally {
                                try {
                                    if (is != null)
                                        is.close();
                                } catch (IOException e) {
                                }
                                try {
                                    if (fos != null)
                                        fos.close();
                                } catch (IOException e) {
                                    Log.d("h_bl", "文件下载失败" + e.getMessage());
                                }
                            }
                        }
                    });
                }
            });
        } else {
            dialog_Vip_Apk.show();
        }
    }

    ProgressDialog progressDialog;

    public void showProgressDialog(int current, int total) {
        //改变样式，水平样式的进度条可以显示出当前百分比进度
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("应用正在下载中，请稍候...");
        }
        //设置进度条最大值
        progressDialog.setProgress(current);
//      progressDialog.setMax(total / 1024 / 1024);
        progressDialog.setProgressNumberFormat(total / 1024 / 1024 + "MB");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    Dialog dialog_pay_eixt;

    public void dialogExitPay() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_exit_pay, null);
        //对话框
        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
        if (dialog_pay_eixt == null) {
            dialog_pay_eixt = new Dialog(mContext, R.style.Dialog);
            dialog_pay_eixt.show();
            dialog_pay_eixt.setCancelable(true);
            Window window = dialog_pay_eixt.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            layout.getBackground().setAlpha(150);
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setContentView(layout);

            ImageView exit_images = (ImageView) layout.findViewById(R.id.exit_images);
            Glide.with(mContext).
                    load(NetInterface.EXIT_TAN_IMAGE_ADDRESS).into(exit_images);

            LinearLayout dialog_newshikanpay_48 = (LinearLayout) layout.findViewById(R.id.dialog_newshikanpay_48);
            final TextView price = (TextView) layout.findViewById(R.id.price);
            final ImageButton dialog_newshikanpay_weixin = (ImageButton) layout.findViewById(R.id.dialog_newshikanpay_weixin);
            final ImageButton dialog_newshikanpay_zhifubao = (ImageButton) layout.findViewById(R.id.dialog_newshikanpay_zhifubao);
            dialog_newshikanpay_48.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, PayActivity.class);
                    intent.putExtra("payMethod", choice);
                    if (choice.equals("wx")) {
                        intent.putExtra("payprice", PayType.exit_Dialog_WX);
                    } else if (choice.equals("zfb")) {
                        intent.putExtra("payprice", PayType.exit_Dialog_ZFB);
                    }
                    intent.putExtra("payTime", "一月");
                    startActivity(intent);
                    dialog_pay_eixt.dismiss();
                }
            });
//            dialog_newshikanpay_68.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
//                    Intent intent = new Intent(mContext, WXPayEntryActivity.class);
//                    intent.putExtra("payMethod", choice);
//                    if (choice.equals("wx")) {
//                        intent.putExtra("payprice", PayType.wx_zuanshi_vip_price);
//                    } else if (choice.equals("zfb")) {
//                        intent.putExtra("payprice", PayType.zfb_zuanshi_vip_price);
//                    }
//                    intent.putExtra("payTime", "全年");
//                    startActivity(intent);
//                    dialog_pay_eixt.dismiss();
//                }
//            });
            if (choice.equals("zfb")) {
                price.setText(PayType.exit_Dialog_ZFB);
                dialog_newshikanpay_weixin.setBackgroundResource(R.drawable.newdialogweixin);
                dialog_newshikanpay_zhifubao.setBackgroundResource(R.drawable.newdialogzhifubaoselect);
            } else {
                price.setText(PayType.exit_Dialog_WX);
                dialog_newshikanpay_weixin.setBackgroundResource(R.drawable.newdialogweixinselect);
                dialog_newshikanpay_zhifubao.setBackgroundResource(R.drawable.newdialogzhifubao);
            }

            dialog_newshikanpay_weixin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    choice = "wx";
                    price.setText(PayType.exit_Dialog_WX);
                    dialog_newshikanpay_weixin.setBackgroundResource(R.drawable.newdialogweixinselect);
                    dialog_newshikanpay_zhifubao.setBackgroundResource(R.drawable.newdialogzhifubao);
                }
            });
            dialog_newshikanpay_zhifubao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    choice = "zfb";
                    price.setText(PayType.exit_Dialog_ZFB);
                    dialog_newshikanpay_weixin.setBackgroundResource(R.drawable.newdialogweixin);
                    dialog_newshikanpay_zhifubao.setBackgroundResource(R.drawable.newdialogzhifubaoselect);
                }
            });
        } else {
            dialog_pay_eixt.show();
        }
    }


    public ProgressDialog progressBar;

    public ProgressDialog getProgressBar() {
        return showProgressDialog();
    }

    private ProgressDialog showProgressDialog() {
        if (progressBar == null) {
            progressBar = new ProgressDialog(this);
            progressBar.setMessage("一大波数据正在赶来...");
            progressBar.setCancelable(true);
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        return progressBar;
    }

    public void goWhereFragment(int where) {
        indicatorViewPager.setCurrentItem(where, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Multi.Moon_LEVE1:
                if (!TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.TV_USER_LIVE_UNBIND))) {
                    //已经解锁
                } else {
                    alertIntentLive();
                }
                break;
        }
    }

    private void initViewPager() {
        fragmentAdapter = null;
        initView();
        Multi.USER_PAY_SUCCES = false;
        indicatorViewPager.setCurrentItem(0, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        verticalview.startAutoScroll();
        if (Multi.isShowDialog)
            alertDialogPay();
        else
            Multi.isShowDialog = false;
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_SILVER_TYPE && Multi.USER_PAY_SUCCES) {
            initViewPager();
        }
        if (vipType == Multi.VIP_GOLD_TYPE && Multi.USER_PAY_SUCCES) {
            initViewPager();
        }
        if (vipType == Multi.VIP_PLAT_NIUM_TYPE && Multi.USER_PAY_SUCCES) {
            initViewPager();
        }

        if (vipType == Multi.VIP_NOT_VIP_TYPE && VipTool.than_Shi_Kan_Six_Video(mContext)) {
            if (TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.TV_USER_LIVE_UNBIND)))
                alertIntentLive();
        }
        if (vipType == Multi.VIP_DIAMOND_TYPE && Multi.USER_PAY_SUCCES) {
            initViewPager();
        }
        if (vipType == Multi.VIP_RED_DIAMOND_TYPE && Multi.USER_PAY_SUCCES) {
            initViewPager();
        }

        if (vipType == Multi.VIP_CROWN_TYPE && Multi.USER_PAY_SUCCES) {
            if (Integer.parseInt(VipTool.read_User_Pay_Count(mContext)) > 6) return;
            else initViewPager();
        }

        if (Multi.Moon_LIVE_LEVE == Multi.Moon_LIVE_LEVE1) {
            //....
        } else if (Multi.Moon_LIVE_LEVE == Multi.Moon_LIVE_LEVE2) {
            if (Multi.isShowLiveDialog) {
                if (!TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.TV_USER_LIVE_UNBIND))) {
                    //已经解锁
                } else {
                    alertIntentLive();
                }
            } else {
                Multi.isShowLiveDialog = true;
            }
        }
        if (vipType == Multi.VIP_SILVER_TYPE || vipType == Multi.VIP_GOLD_TYPE || vipType == Multi.VIP_PLAT_NIUM_TYPE || vipType == Multi.VIP_DIAMOND_TYPE) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) showLive.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            showLive.setLayoutParams(params); //使layout更新
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) showLive.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            showLive.setLayoutParams(params); //使layout更新
        }
        initDownInfo();
    }


    @Override
    protected void onPause() {
        super.onPause();
        verticalview.stopAutoScroll();
    }

    @Override
    protected void onDestroy() {
        TvApplication.payloadData.delete(0, TvApplication.payloadData.length());
        super.onDestroy();
        dbManager.closeDB();
        //注销广播
        try {
            if (mBroadcastReceiver != null) {
                mContext.unregisterReceiver(mBroadcastReceiver);
            }
            //去除绑定服务
            mContext.unbindService(fifteenService);
        } catch (Exception e) {
        }
    }


    Dialog dialog_pay_time;

    public void alertIntentLive() {
        ScreenTool.setLight(mContext, 255);
        try {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_tv_video, null);
            //对话框
            //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
            if (dialog_pay_time == null) {
                dialog_pay_time = new Dialog(mContext, R.style.Dialog);
                dialog_pay_time.show();
                dialog_pay_time.setCancelable(false);
                Window window = dialog_pay_time.getWindow();
                window.getDecorView().setPadding(0, 0, 0, 0);
                WindowManager.LayoutParams lp = window.getAttributes();
//            layout.getBackground().setAlpha(150);
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;//ScreenTool.getWidth(this) / 5 * 3;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                window.setContentView(layout);
                ImageView imageslive = (ImageView) layout.findViewById(R.id.images);
                Glide.with(mContext).
                        load(NetInterface.LIVE_TAN_IMAGE_ADDRESS).into(imageslive);

                ImageButton dialog_vipdownload_down = (ImageButton) layout.findViewById(R.id.dialog_vipdownload_down);
                dialog_vipdownload_down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog_pay_time.dismiss();
                        Multi.LIVE_UNBIND = true;
                        int vipType = VipTool.getUserVipType(mContext);
                        if (vipType == Multi.VIP_SILVER_TYPE || vipType == Multi.VIP_GOLD_TYPE ||
                                vipType == Multi.VIP_PLAT_NIUM_TYPE || vipType == Multi.VIP_DIAMOND_TYPE ||
                                vipType == Multi.VIP_RED_DIAMOND_TYPE || vipType == Multi.VIP_CROWN_TYPE) {
                            goWhereFragment(0);
                        } else {
                            goWhereFragment(5);
                        }
                        Multi.Moon_LIVE_LEVE = Multi.Moon_LIVE_LEVE1;
                    }
                });

                final ImageView images = (ImageView) layout.findViewById(R.id.images);
                final FullScreenVideo videoView = (FullScreenVideo) layout.findViewById(R.id.video);
                videoView.setVideoURI(Uri.parse(getUrl()));
                videoView.start();
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        videoView.setVideoURI(Uri.parse(getUrl()));
                        videoView.start();
                    }
                });
                videoView.setPlayPauseListener(new FullScreenVideo.PlayPauseListener() {
                    @Override
                    public void onPlay() {
                        images.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPause() {
                        videoView.setVideoURI(Uri.parse(getUrl()));
                        videoView.start();
                    }
                });

            } else {
                dialog_pay_time.show();
            }
        } catch (Exception e) {
            return;
        }
    }

    private String getUrl() {
        int random = RandomTool.getRandom(0, 4);
        Log.i("tagpos", "random " + random);
        return Constant.urlString.get(random);
    }


    private void initUrl() {
//        Constant.urlString.add("http://video.crrxz.cn/recordings/z1.begin.beginlive1/1481610640_1481611003.m3u8");
//        Constant.urlString.add("http://video.crrxz.cn/recordings/z1.begin.beginlive3/1481615566_1481615986.m3u8");
//        Constant.urlString.add("http://video.crrxz.cn/recordings/z1.begin.beginlive4/1481615571_1481615989.m3u8");
//        Constant.urlString.add("http://video.crrxz.cn/recordings/z1.begin.beginlive2/1481614791_1481615202.m3u8");
        Constant.urlString.add("http://www.cffyl.cn/xshikan/xsk1-mp4");
        Constant.urlString.add("http://www.cffyl.cn/xshikan/xsk2-mp4");
        Constant.urlString.add("http://www.cffyl.cn/xshikan/xsk3-mp4");
        Constant.urlString.add("http://www.cffyl.cn/xshikan/xsk4-mp4");
        Constant.urlString.add("http://www.cffyl.cn/xshikan/xsk5-mp4");
    }

    @OnClick({R.id.show_live_menu_tishi, R.id.show_live})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.show_live_menu_tishi:
                break;
            case R.id.show_live:
                showLive.setVisibility(View.GONE);
                break;
        }
    }

    @OnClick(R.id.titletext)
    public void onClick() {
        initCurrentPayLevel();
        startActivity(new Intent(mContext, PayActivity.class));
    }

    private void initCurrentPayLevel() {
        int currentItem = indicatorViewPager.getCurrentItem();
        if (currentItem == 0) {
            Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
        } else if (currentItem == 1) {
            Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
        } else if (currentItem == 2) {
            Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
        } else if (currentItem == 3) {
            Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
        } else if (currentItem == 4) {
            Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
        } else if (currentItem == 5) {
            Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
        }
    }

    private void statisMenu(int vipType, int position) {
        switch (vipType) {
            case Multi.VIP_NOT_VIP_TYPE:
                switch (position) {
                    case 0:
                        //体验区
                        MobclickAgent.onEvent(mContext, MobClick.TIYAN_ID);//埋点统计
                        break;
                    case 1:
                        //无码区
                        MobclickAgent.onEvent(mContext, MobClick.WUMA_ID);//埋点统计
                        break;
                    case 2:
                        //白银区
                        MobclickAgent.onEvent(mContext, MobClick.BAIYIN_ID);//埋点统计
                        break;
                    case 3:
                        //频道专区
                        MobclickAgent.onEvent(mContext, MobClick.PINDAO_ID);//埋点统计
                        break;
                    case 4:
                        //三级片
                        MobclickAgent.onEvent(mContext, MobClick.SANJI_ID);//埋点统计
                        break;
                    case 5:
                        //成人电视
                        MobclickAgent.onEvent(mContext, MobClick.TV_ID);//埋点统计
                        break;
                }
                break;
            case Multi.VIP_SILVER_TYPE:
                switch (position) {
                    case 0:
                        //成人电视
                        MobclickAgent.onEvent(mContext, MobClick.TV_ID);//埋点统计
                        break;
                    case 1:
                        //白银区
                        MobclickAgent.onEvent(mContext, MobClick.BAIYIN_ID);//埋点统计
                        break;
                    case 2:
                        //黄金区
                        MobclickAgent.onEvent(mContext, MobClick.HUANGJIN_ID);//埋点统计
                        break;
                    case 3:
                        //频道专区
                        MobclickAgent.onEvent(mContext, MobClick.PINDAO_ID);//埋点统计
                        break;
                    case 4:
                        //三级片
                        MobclickAgent.onEvent(mContext, MobClick.SANJI_ID);//埋点统计
                        break;
                    case 5:
                        //
                        MobclickAgent.onEvent(mContext, MobClick.YINGYUAN_ID);//埋点统计
                        break;
                }
                break;
            case Multi.VIP_GOLD_TYPE:
                switch (position) {
                    case 0:
                        //成人电视
                        MobclickAgent.onEvent(mContext, MobClick.TV_ID);//埋点统计
                        break;
                    case 1:
                        //黄金区
                        MobclickAgent.onEvent(mContext, MobClick.HUANGJIN_ID);//埋点统计
                        break;
                    case 2:
                        //白金区
                        MobclickAgent.onEvent(mContext, MobClick.BAIJIN_ID);//埋点统计
                        break;
                    case 3:
                        //频道专区
                        MobclickAgent.onEvent(mContext, MobClick.PINDAO_ID);//埋点统计
                        break;
                    case 4:
                        //三级片
                        MobclickAgent.onEvent(mContext, MobClick.SANJI_ID);//埋点统计
                        break;
                }
                break;
            case Multi.VIP_PLAT_NIUM_TYPE:
                switch (position) {
                    case 0:
                        //成人电视
                        MobclickAgent.onEvent(mContext, MobClick.TV_ID);//埋点统计
                        break;
                    case 1:
                        //白金区
                        MobclickAgent.onEvent(mContext, MobClick.BAIJIN_ID);//埋点统计
                        break;
                    case 2:
                        //钻石区
                        MobclickAgent.onEvent(mContext, MobClick.ZUANSHI_ID);//埋点统计
                        break;
                    case 3:
                        //频道专区
                        MobclickAgent.onEvent(mContext, MobClick.PINDAO_ID);//埋点统计
                        break;
                    case 4:
                        //三级片
                        MobclickAgent.onEvent(mContext, MobClick.SANJI_ID);//埋点统计
                        break;
                }
                break;
            case Multi.VIP_DIAMOND_TYPE:
                switch (position) {
                    case 0:
                        //成人电视
                        MobclickAgent.onEvent(mContext, MobClick.TV_ID);//埋点统计
                        break;
                    case 1:
                        //钻石区
                        MobclickAgent.onEvent(mContext, MobClick.ZUANSHI_ID);//埋点统计
                        break;
                    case 2:
                        //粉钻区
                        MobclickAgent.onEvent(mContext, MobClick.FENZUAN_ID);//埋点统计
                        break;
                    case 3:
                        //频道专区
                        MobclickAgent.onEvent(mContext, MobClick.PINDAO_ID);//埋点统计
                        break;
                    case 4:
                        //三级片
                        MobclickAgent.onEvent(mContext, MobClick.SANJI_ID);//埋点统计
                        break;
                }
                break;
            case Multi.VIP_RED_DIAMOND_TYPE:
                switch (position) {
                    case 0:
                        //成人电视
                        MobclickAgent.onEvent(mContext, MobClick.TV_ID);//埋点统计
                        break;
                    case 1:
                        //粉钻区
                        MobclickAgent.onEvent(mContext, MobClick.FENZUAN_ID);//埋点统计
                        break;
                    case 2:
                        //皇冠区
                        MobclickAgent.onEvent(mContext, MobClick.HUANGGUAN_ID);//埋点统计
                        break;
                    case 3:
                        //频道专区
                        MobclickAgent.onEvent(mContext, MobClick.PINDAO_ID);//埋点统计
                        break;
                    case 4:
                        //三级片
                        MobclickAgent.onEvent(mContext, MobClick.SANJI_ID);//埋点统计
                        break;
                }
                break;
        }
    }

    Dialog dialog_pay;

    public void alertDialogPay() {
        ScreenTool.setLight(mContext, 250);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_paybg, null);
        //对话框
        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
        if (dialog_pay == null) {
            dialog_pay = new Dialog(mContext, R.style.Dialog);
            dialog_pay.show();
            dialog_pay.setCancelable(false);
            Window window = dialog_pay.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setContentView(layout);
            TextView pay_text_description = (TextView) layout.findViewById(R.id.pay_text_description);
            final TextView zfb_youhui = (TextView) layout.findViewById(R.id.zfb_youhui);
            final TextView pay_text_1 = (TextView) layout.findViewById(R.id.pay_text_1);
            final TextView pay_price_text = (TextView) layout.findViewById(R.id.pay_price_text);
            TextView pay_text_yuanjia = (TextView) layout.findViewById(R.id.pay_text_yuanjia);
            final ImageView zfbpay = (ImageView) layout.findViewById(R.id.zfbpay);
            final ImageView wxpay = (ImageView) layout.findViewById(R.id.wxpay);
            pay_text_yuanjia.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            final ImageView close = (ImageView) layout.findViewById(R.id.close);
            LinearLayout btn_pay = (LinearLayout) layout.findViewById(R.id.btn_pay);
            int vipType = VipTool.getUserVipType(mContext);
            if (choice.equals("wx")) {
                zfbpay.setImageResource(R.drawable.zfb_pay);
                wxpay.setImageResource(R.drawable.wxpay_select);
                if (vipType == Multi.VIP_NOT_VIP_TYPE) {
                    pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.silver_Price)) + "元");
                    pay_text_1.setText("升级白银会员");
                    zfb_youhui.setVisibility(View.VISIBLE);
                } else if (vipType == Multi.VIP_SILVER_TYPE) {
                    pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.gold_Price)) + "元");
                    pay_text_1.setText("升级黄金会员");
                } else if (vipType == Multi.VIP_GOLD_TYPE) {
                    pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.platMinum_Price)) + "元");
                    pay_text_1.setText("升级白金会员");
                } else if (vipType == Multi.VIP_PLAT_NIUM_TYPE) {
                    pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.diamond_Price)) + "元");
                    pay_text_1.setText("升级钻石会员");
                } else if (vipType == Multi.VIP_DIAMOND_TYPE) {
                    pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.red_Diamond_Price)) + "元");
                    pay_text_1.setText("升级粉钻会员");
                } else if (vipType == Multi.VIP_RED_DIAMOND_TYPE) {
                    pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.crown_Price)) + "元");
                    pay_text_1.setText("升级皇冠会员");
                } else {
                    pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.crown_Price)) + "元");
                    pay_text_1.setText("升级皇冠会员");
                }
            } else {
                zfbpay.setImageResource(R.drawable.zfbpay_select);
                wxpay.setImageResource(R.drawable.wx_pay);
                if (vipType == Multi.VIP_NOT_VIP_TYPE) {
                    pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.silver_Price) - 10) + "元");
                    pay_text_1.setText("升级白银会员");
                    zfb_youhui.setVisibility(View.VISIBLE);
                } else if (vipType == Multi.VIP_SILVER_TYPE) {
                    pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.gold_Price) - 5) + "元");
                    pay_text_1.setText("升级黄金会员");
                } else if (vipType == Multi.VIP_GOLD_TYPE) {
                    pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.platMinum_Price) - 5) + "元");
                    pay_text_1.setText("升级白金会员");
                } else if (vipType == Multi.VIP_PLAT_NIUM_TYPE) {
                    pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.diamond_Price) - 5) + "元");
                    pay_text_1.setText("升级钻石会员");
                } else if (vipType == Multi.VIP_DIAMOND_TYPE) {
                    pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.red_Diamond_Price) - 5) + "元");
                    pay_text_1.setText("升级粉钻会员");
                } else if (vipType == Multi.VIP_RED_DIAMOND_TYPE) {
                    pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.crown_Price) - 5) + "元");
                    pay_text_1.setText("升级皇冠会员");
                } else {
                    pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.crown_Price) - 5) + "元");
                    pay_text_1.setText("升级皇冠会员");
                }
            }
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Multi.isShowDialog = false;
                    dialog_pay.dismiss();
                    dialog_pay = null;
                }
            });
            btn_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //支付
                    if (choice.equals("zfb")) {
                        mContext.startActivity(new Intent(mContext, PayActivity.class).putExtra("diaologPay", "zfb"));
                    } else {
                        mContext.startActivity(new Intent(mContext, PayActivity.class).putExtra("diaologPay", "wx"));
                    }
                }
            });
            zfbpay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    choice = "zfb";
                    zfbpay.setImageResource(R.drawable.zfbpay_select);
                    wxpay.setImageResource(R.drawable.wx_pay);
                    int vipType = VipTool.getUserVipType(mContext);
                    if (vipType == Multi.VIP_NOT_VIP_TYPE) {
                        pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.silver_Price) - 10) + "元");
                        pay_text_1.setText("升级白银会员");
                        zfb_youhui.setVisibility(View.VISIBLE);
                    } else if (vipType == Multi.VIP_SILVER_TYPE) {
                        pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.gold_Price) - 5) + "元");
                        pay_text_1.setText("升级黄金会员");
                    } else if (vipType == Multi.VIP_GOLD_TYPE) {
                        pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.platMinum_Price) - 5) + "元");
                        pay_text_1.setText("升级白金会员");
                    } else if (vipType == Multi.VIP_PLAT_NIUM_TYPE) {
                        pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.diamond_Price) - 5) + "元");
                        pay_text_1.setText("升级钻石会员");
                    } else if (vipType == Multi.VIP_DIAMOND_TYPE) {
                        pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.red_Diamond_Price) - 5) + "元");
                        pay_text_1.setText("升级粉钻会员");
                    } else if (vipType == Multi.VIP_RED_DIAMOND_TYPE) {
                        pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.crown_Price) - 5) + "元");
                        pay_text_1.setText("升级皇冠会员");
                    } else {
                        pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.crown_Price) - 5) + "元");
                        pay_text_1.setText("升级皇冠会员");
                    }
                }
            });
            wxpay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    choice = "wx";
                    zfbpay.setImageResource(R.drawable.zfb_pay);
                    wxpay.setImageResource(R.drawable.wxpay_select);
                    int vipType = VipTool.getUserVipType(mContext);
                    if (vipType == Multi.VIP_NOT_VIP_TYPE) {
                        pay_price_text.setText("特价:￥" + String.valueOf(Integer.parseInt(PayType.silver_Price)) + "元");
                        pay_text_1.setText("升级白银会员");
                        zfb_youhui.setVisibility(View.VISIBLE);
                    } else if (vipType == Multi.VIP_SILVER_TYPE) {
                        pay_price_text.setText("特价:￥" + PayType.gold_Price + "元");
                        pay_text_1.setText("升级黄金会员");
                    } else if (vipType == Multi.VIP_GOLD_TYPE) {
                        pay_price_text.setText("特价:￥" + PayType.platMinum_Price + "元");
                        pay_text_1.setText("升级白金会员");
                    } else if (vipType == Multi.VIP_PLAT_NIUM_TYPE) {
                        pay_price_text.setText("特价:￥" + PayType.diamond_Price + "元");
                        pay_text_1.setText("升级钻石会员");
                    } else if (vipType == Multi.VIP_DIAMOND_TYPE) {
                        pay_price_text.setText("特价:￥" + PayType.red_Diamond_Price + "元");
                        pay_text_1.setText("升级粉钻会员");
                    } else if (vipType == Multi.VIP_RED_DIAMOND_TYPE) {
                        pay_price_text.setText("特价:￥" + PayType.crown_Price + "元");
                        pay_text_1.setText("升级皇冠会员");
                    } else {
                        pay_price_text.setText("特价:￥" + PayType.crown_Price + "元");
                        pay_text_1.setText("升级皇冠会员");
                    }
                }
            });
        } else {
            dialog_pay.show();
        }
    }


    //用户是vip会员，退出下载7天版vpn
    Dialog dialog_VpnSeven_Apk;
    LinearLayout seven_Layout;

    public void diaologSevenVpn() {
        if (AppTool.isInstalled(mContext, Constant.vpnModel.getOne_package())) {
            return;
        }
        Constant.vpnModel.one_downurl = NetInterface.DOWNLOAD_VPN_VIP_SEVEN_DAY_ADDRESS;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        seven_Layout = (LinearLayout) inflater.inflate(R.layout.dialog_vip_vpn_download, null);
        //对话框
        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
        if (dialog_VpnSeven_Apk == null) {
            dialog_VpnSeven_Apk = new Dialog(mContext, R.style.Dialog);
            dialog_VpnSeven_Apk.show();
            dialog_VpnSeven_Apk.setCancelable(false);
            Window window = dialog_VpnSeven_Apk.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
//          layout.getBackground().setAlpha(150);
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;//ScreenTool.getWidth(this) / 5 * 3;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setContentView(seven_Layout);
            ImageButton dialog_vipdownload_down = (ImageButton) seven_Layout.findViewById(R.id.dialog_vipdownload_down);
            TextView vpn_text_decription = (TextView) seven_Layout.findViewById(R.id.vpn_text_decription);//第二天下载vpn
            vpn_text_decription.setText("恭喜您获得H站大全7天免费试用会员");
            ImageView close_vpn = (ImageView) seven_Layout.findViewById(R.id.close_vpn);
            close_vpn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_VpnSeven_Apk.dismiss();
                }
            });
            ImageView tan_bg = (ImageView) seven_Layout.findViewById(R.id.tan_bg);
            Glide.with(mContext).
                    load(NetInterface.TAN_VPN_IMAGE_ADDRESS)
                    .placeholder(R.drawable.allloading).
                    error(R.drawable.allloading).into(tan_bg);
            dialog_vipdownload_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_VpnSeven_Apk.dismiss();
                    if (!NetTool.isConnected(mContext)) {
                        T.showTextToast(mContext, "您的网络没有连接，请检查您的网络,然后下拉刷新试试");
                        return;
                    }
                    if (AppTool.isInstalled(mContext, Constant.vpnModel.getOne_package())) {
                        return;
                    }
                    File file = new File(FileTool.getSDCardPath() + "/" + vpnApkName);
                    if (file.exists()) {
                        file.delete();
                    }
                    if (new File(FileTool.getSDCardPath() + "/" + vpnApkName).exists()) {
                        AppTool.installApk(mContext, FileTool.getSDCardPath() + "/" + vpnApkName);
                        return;
                    }
                    Request request = new Request.Builder().url(Constant.vpnModel.getOne_downurl()).build();
                    OkHttp.getInstance().newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("h_bl", "文件下载失败");
                            mHandler.sendEmptyMessage(GO_DOWN_VPN_FAILED);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            InputStream is = null;
                            byte[] buf = new byte[2048];
                            int len = 0;
                            FileOutputStream fos = null;
                            String SDPath = FileTool.getSDCardPath();
                            try {
                                is = response.body().byteStream();
                                long total = response.body().contentLength();
                                File file = new File(SDPath, vpnApkName);
                                fos = new FileOutputStream(file);
                                long sum = 0;
                                while ((len = is.read(buf)) != -1) {
                                    fos.write(buf, 0, len);
                                    sum += len;
                                    int progress = (int) (sum * 1.0f / total * 100);
                                    Log.d("h_bl", "progress=" + progress);
                                    Message msg = mHandler.obtainMessage();
                                    msg.what = GO_DOWN_VPN_PROGRESS;
                                    msg.arg1 = progress;
                                    msg.arg2 = (int) total;
                                    mHandler.sendMessage(msg);
                                }
                                fos.flush();
                                mHandler.sendEmptyMessage(GO_DOWN_VPNSEVEN_DAY_SUCCES);
                            } catch (Exception e) {
                                Log.d("h_bl", "文件下载失败");
                            } finally {
                                try {
                                    if (is != null)
                                        is.close();
                                } catch (IOException e) {
                                }
                                try {
                                    if (fos != null)
                                        fos.close();
                                } catch (IOException e) {
                                    Log.d("h_bl", "文件下载失败" + e.getMessage());
                                }
                            }
                        }
                    });
                }
            });
        } else {
            dialog_VpnSeven_Apk.show();
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == REQUEST_PERMISSION) {
//            if ((grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
//                PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
//            } else {
//                Log.e("aaa", "We highly recommend that you need to grant the special permissions before initializing the SDK, otherwise some "
//                        + "functions will not work");
//                PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
//            }
//        } else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

}
