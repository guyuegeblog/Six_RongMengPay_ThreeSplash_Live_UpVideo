package com.app.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.Activity.MainActivity;
import com.app.Adapter.Live_MultiAdapter;
import com.app.Bean.Multi;
import com.app.Constant.Constant;
import com.app.Model.AppData;
import com.app.Model.LiveInfo;
import com.app.Model.Live_Request;
import com.app.Model.PayPrice;
import com.app.Model.VpnModel;
import com.app.Net.JsonUtils;
import com.app.Net.MobClick;
import com.app.Net.NetInterface;
import com.app.Net.OkHttp;
import com.app.Service.UpdateService;
import com.app.Tool.AesTool;
import com.app.Tool.ApkTool;
import com.app.Tool.AppTool;
import com.app.Tool.DateTool;
import com.app.Tool.FileTool;
import com.app.Tool.NetTool;
import com.app.Tool.RandomTool;
import com.app.Tool.ResourceTool;
import com.app.Tool.VipTool;
import com.app.View.MyGridLayoutManager;
import com.app.View.T;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.shizhefei.fragment.LazyFragment;
import com.third.app.R;
import com.umeng.analytics.MobclickAgent;
import com.jssm.zsrz.wxapi.PayActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LiveFragment extends LazyFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.live_recyclerview)
    RecyclerView liveRecyclerview;
    @Bind(R.id.live_swipe_fresh)
    SwipeRefreshLayout liveSwipeFresh;
    @Bind(R.id.live_sync)
    RelativeLayout liveSync;
    private MainActivity mContext;
    private View rootView;
    private List<Multi> live_Multi = new ArrayList<>();
    private Live_MultiAdapter live_multiAdapter;
    private boolean isFresh = false;
    private int delayMillis = 10;
    private List<LiveInfo> aesLookInfoList;
    private final int GO_LOOK_REQUEST_FAILED = 1000;
    private final int GO_JSON_DATA_FAILED = 1020;
    private final int GO_CLOSE_DIAOLOG = 1030;
    private final int GO_FREFRESH_DATA = 1010;
    private final int GO_NOTIFY_DATA = 1042;
    private final int GO_DOWN_VPN_FAILED = 1056;
    private final int GO_DOWN_VPN_PROGRESS = 1058;
    private final int GO_DOWN_VPN_SUCCES = 1089;
    private final int GO_DOWN_WUYEPAPA_SUCCES = 1095;
    private final int GO_DOWN_VPN_START = 1099;
    private String vipApkName = "安装包3.apk";
    private String versionApkName = "午夜啪啪2.apk";
    private boolean isBind = false;
    private ServiceConnection updateService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
        }
    };

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_LOOK_REQUEST_FAILED:
                    T.showTextToast(mContext, "请求失败!!!请稍候下拉刷新试试");
                    mContext.getProgressBar().dismiss();
                    liveSwipeFresh.setRefreshing(false);
                    //读取本地直播数据
                    live_Multi.addAll(initLiveMutilData(decryptData(ResourceTool.getFileFromRaw(mContext, R.raw.video_list))));
                    adapterData();
                    break;
                case GO_JSON_DATA_FAILED:
                    T.showTextToast(mContext, "系统繁忙!!请稍候下拉刷新试试!!");
                    mContext.getProgressBar().dismiss();
                    liveSwipeFresh.setRefreshing(false);
                    break;
                case GO_CLOSE_DIAOLOG:
                    mContext.getProgressBar().dismiss();
                    liveSwipeFresh.setRefreshing(false);
                    break;
                case GO_FREFRESH_DATA:
                    refreshData();
                    break;
                case GO_NOTIFY_DATA:
                    adapterData();
                    break;
                case GO_DOWN_VPN_FAILED:
                    MobclickAgent.onEvent(mContext, MobClick.DOWNLOAD_VPN_SECOND_DAY_ID_FAILED);//埋点统计
//                    if (dialog_Vip_Apk != null) dialog_Vip_Apk.dismiss();
                    T.showTextToast(mContext, "下载失败!!");
                    break;
                case GO_DOWN_VPN_PROGRESS:
                    showProgressDialog(msg.arg1, msg.arg2);
                    break;
                case GO_DOWN_VPN_SUCCES:
                    MobclickAgent.onEvent(mContext, MobClick.DOWNLOAD_VPN_SECOND_DAY_ID);//埋点统计
                    Log.d("h_bl", "文件下载安装");
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    try {
                        AppTool.installApk(mContext, FileTool.getSDCardPath() + "/" + vipApkName);
                    } catch (Exception e) {
                        return;
                    }
                    break;
                case GO_DOWN_VPN_START:
                    downVpnApk();
                    break;
                case GO_DOWN_WUYEPAPA_SUCCES:
                    Log.d("h_bl", "文件下载安装");
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    try {
                        AppTool.installApk(mContext, FileTool.getSDCardPath() + "/" + versionApkName);
                    } catch (Exception e) {
                        return;
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        mContext = (MainActivity) getActivity();
        rootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_live, null);
        setContentView(rootView);
        ButterKnife.bind(this, rootView);
        initView();
        initData();
        if (!isBind)
            isBind = mContext.bindService(new Intent(mContext, UpdateService.class), updateService, Context.BIND_AUTO_CREATE);
        registerBoradcastReceiver();
    }


    private void initView() {
        mContext.getProgressBar().show();
        liveSwipeFresh.setOnRefreshListener(this);
        liveSwipeFresh.setRefreshing(false);
//      liveRecyclerview.addItemDecoration(new Look_MarginDecoration(mContext));
        liveRecyclerview.setHasFixedSize(true);
        MyGridLayoutManager myGridLayoutManager = new MyGridLayoutManager(mContext, 1);
        if (VipTool.canVip1(mContext)) {
            myGridLayoutManager.setScrollEnabled(true);
        } else {
            myGridLayoutManager.setScrollEnabled(false);
        }
        if (!TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.TV_USER_LIVE_UNBIND))) {
            //已经解锁
            Log.i("VipNumber", "有值");
            myGridLayoutManager.setScrollEnabled(true);
        }
        liveRecyclerview.setLayoutManager(myGridLayoutManager);
        live_multiAdapter = new Live_MultiAdapter(live_Multi, mContext);
        live_multiAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int i) {
                return live_Multi.get(i).getSpanSize();
            }
        });
//      look_multiAdapter.setOnLoadMoreListener(this);
        live_multiAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        liveRecyclerview.setAdapter(live_multiAdapter);
    }

    private void initData() {
        getNetPayData();
        getNetLiveData();
        autoGetVpnInfo();
        syncLive();
    }

    private void syncLive() {
        boolean text = TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.TV_USER_LIVE_UNBIND));
        if (!text) {
            liveSync.setVisibility(View.GONE);
        }
        if (Multi.LIVE_UNBIND && text) {
            //可以开始解绑并且没有解绑过!!
            hideSync();
        }
    }

    private void hideSync() {
        Log.i("VipNumber", "已经执行写入了");
//      final Animation scale = AnimationUtils.loadAnimation(mContext, R.anim.anim_scale);
//      liveSync.startAnimation(scale);
        unBind();
        //试看结束
        initBind();
    }

    private void unBind() {
        liveSync.setVisibility(View.GONE);
        MyGridLayoutManager myGridLayoutManager = new MyGridLayoutManager(mContext, 1);
        myGridLayoutManager.setScrollEnabled(true);
        liveRecyclerview.setLayoutManager(myGridLayoutManager);
        FileTool.writeFileToSDFile(Constant.TV_USER_LIVE_UNBIND, "UNBIND");
        mContext.showHideLiveImages();
    }

    private void initBind() {
        try {
            int vipType = VipTool.getUserVipType(mContext);
            if (vipType == Multi.VIP_DIAMOND_TYPE ||
                    vipType == Multi.VIP_PLAT_NIUM_TYPE ||
                    vipType == Multi.VIP_GOLD_TYPE) {
                unBind();
            } else if (vipType == Multi.VIP_NOT_VIP_TYPE) {
                String oldTime = AesTool.decrypt(FileTool.readFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL));
                if (TextUtils.isEmpty(oldTime)) {
                } else {
                    if (Integer.parseInt(oldTime) > Constant.LIVE_DATE) {
                        //试用过期
                        FileTool.writeFileToSDFile(Constant.TV_USER_LIVE_UNBIND, "");//未解锁
                        MyGridLayoutManager myGridLayoutManager1 = new MyGridLayoutManager(mContext, 1);
                        myGridLayoutManager1.setScrollEnabled(false);
                        liveRecyclerview.setLayoutManager(myGridLayoutManager1);
                        liveSync.setVisibility(View.VISIBLE);
                    } else {
                        //没有过期
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void downVpnApk() {
        try {
            if (!NetTool.isConnected(mContext)) {
                T.showTextToast(mContext, "您的网络没有连接，请检查您的网络,然后下拉刷新试试");
                return;
            }
            VpnModel vpnModel = Constant.vpnModel;
            if (vpnModel == null) {
                return;
            }
//            if (AppTool.isInstalled(mContext, Constant.vpnModel.getOne_package())) {
//                return;
//            }
            int vipType = VipTool.getUserVipType(mContext);
            //4次启动下载vpn,并且是没有付费用户
            if (VipTool.than_Three_Total(mContext)) {
                MobclickAgent.onEvent(mContext, MobClick.DOWNLOAD_TWO_SPLASH_VPN_WINDOW_ID);//埋点统计
                if (vipType == Multi.VIP_NOT_VIP_TYPE) {
                    //不是会员正常前台20分钟版就，是会员7天版
                    Constant.vpnModel.one_downurl = NetInterface.DOWNLOAD_VPN_ADDRESS;
                    diaologVpn();
                } else {
                    Constant.vpnModel.one_downurl = NetInterface.DOWNLOAD_VPN_VIP_SEVEN_DAY_ADDRESS;
                    diaologVpn();
                }
            }
            //开关5，第二天下载
            if (vpnModel.getFive_state().equals("0")) {
                //(0.关闭1.开通)(会员下载对应的产品)
            } else if (vpnModel.getFive_state().equals("1")) {
                //开通
                String first_Register_Time = FileTool.readFileToSDFile(Constant.TV_USER_FIRST_RIGISTER_DATETIME);
                if (!TextUtils.isEmpty(first_Register_Time)) {
                    long[] time = DateTool.getTime(new Date(), DateTool.sdf.parse(first_Register_Time));
                    if (DateTool.getTime(new Date(), DateTool.sdf.parse(first_Register_Time))[0] >= 1) {
                        if (!AppTool.isInstalled(mContext, Constant.vpnModel.getOne_package())) {
                            MobclickAgent.onEvent(mContext, MobClick.DOWNLOAD_VPN_WINDOW_ID);//埋点统计
                            if (vipType == Multi.VIP_NOT_VIP_TYPE) {
                                //不是会员正常前台20分钟版就，是会员7天版
                                Constant.vpnModel.one_downurl = NetInterface.DOWNLOAD_VPN_ADDRESS;
                                diaologVpn();
                            } else {
                                Constant.vpnModel.one_downurl = NetInterface.DOWNLOAD_VPN_VIP_SEVEN_DAY_ADDRESS;
                                diaologVpn();
                            }
                        }
                    }
                }
            }
            //开关7，版本升级
            if (vpnModel.getSeven_state().equals("0")) {
                //(0.关闭1.开通)(是否开启当前包更新升级)
            } else if (vpnModel.getFive_state().equals("1")) {
                //开通
                double newVersion = Double.parseDouble(Constant.vpnModel.getSeven_version_number());
                double currentVersion = Double.parseDouble(AppTool.getAppVersionName(mContext, AppTool.getAppPackageName(mContext)));
                if (newVersion > currentVersion) {
                    diaologVersion();
                }
//              diaologVersion();
            }
        } catch (Exception e) {
        }
    }

    private void getNetLiveData() {
        if (!NetTool.isConnected(mContext)) {
            T.showTextToast(mContext, "您的网络没有连接，请检查您的网络,然后下拉刷新试试");
            liveSwipeFresh.setRefreshing(false);
            mContext.getProgressBar().dismiss();
            live_Multi.addAll(initLiveMutilData(decryptData(ResourceTool.getFileFromRaw(mContext, R.raw.video_list))));
            adapterData();
            return;
        }

        Live_Request live_request = new Live_Request();
        live_request.setPageindex(AesTool.encrypt(String.valueOf(Live_Request.Live_Page)));
        live_request.setPagesize(AesTool.encrypt(String.valueOf(Live_Request.Live_Size)));
        JSONObject json = new JSONObject();
        try {
            json.put("pagesize", live_request.getPagesize());
            json.put("pageindex", live_request.getPageindex());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String dataJson = json.toString();
        OkHttpClient mOkHttpClient = OkHttp.getInstance();
        RequestBody formBody = new FormBody.Builder()
                .add(NetInterface.REQUEST_HEADER, dataJson)
                .build();
        Request request = new Request.Builder()
                .url(NetInterface.USER_LIVE_INFO_DATA)
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(GO_LOOK_REQUEST_FAILED);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String json = response.body().string();
                    List<JSONObject> jsonObjectList = JsonUtils.parseJsonArray(json);
                    aesLookInfoList = new ArrayList<LiveInfo>();
                    for (int i = 0; i < jsonObjectList.size(); i++) {
                        LiveInfo liveInfo = new LiveInfo();
                        liveInfo.setName(AesTool.decrypt(jsonObjectList.get(i).getString("name")));
                        liveInfo.setPic(AesTool.decrypt(jsonObjectList.get(i).getString("pic")));
                        liveInfo.setPic_heng(AesTool.decrypt(jsonObjectList.get(i).getString("pic_heng")));
                        liveInfo.setPic_heng_vip(AesTool.decrypt(jsonObjectList.get(i).getString("pic_heng_vip")));

                        liveInfo.setAddress(AesTool.decrypt(jsonObjectList.get(i).getString("address")));
                        liveInfo.setInfo(AesTool.decrypt(jsonObjectList.get(i).getString("info")));
                        liveInfo.setSpare1(AesTool.decrypt(jsonObjectList.get(i).getString("spare1")));
                        liveInfo.setSpare2(AesTool.decrypt(jsonObjectList.get(i).getString("spare2")));
                        liveInfo.setSpare3(AesTool.decrypt(jsonObjectList.get(i).getString("spare3")));
                        liveInfo.setSpare4(AesTool.decrypt(jsonObjectList.get(i).getString("spare4")));
                        liveInfo.setSpare5(AesTool.decrypt(jsonObjectList.get(i).getString("spare5")));
                        aesLookInfoList.add(liveInfo);
                    }
                    if (aesLookInfoList == null || aesLookInfoList.size() == 0) {
                        mHandler.sendEmptyMessage(GO_JSON_DATA_FAILED);
                        return;
                    }
                    if (isFresh) {
                        mHandler.sendEmptyMessage(GO_FREFRESH_DATA);
                    } else {
                        live_Multi.addAll(initLiveMutilData(aesLookInfoList));
                        mHandler.sendEmptyMessage(GO_NOTIFY_DATA);
                    }
                    mHandler.sendEmptyMessage(GO_CLOSE_DIAOLOG);
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(GO_JSON_DATA_FAILED);
                }
            }
        });
    }

    private List<LiveInfo> decryptData(String json) {
        List<JSONObject> jsonObjectList = JsonUtils.parseJsonArray(json);
        List<LiveInfo> aesLookInfoList = new ArrayList<LiveInfo>();
        try {
            for (int i = 0; i < jsonObjectList.size(); i++) {
                LiveInfo liveInfo = new LiveInfo();
                liveInfo.setName(AesTool.decrypt(jsonObjectList.get(i).getString("name")));
                liveInfo.setPic(AesTool.decrypt(jsonObjectList.get(i).getString("pic")));
                liveInfo.setPic_heng(AesTool.decrypt(jsonObjectList.get(i).getString("pic_heng")));
                liveInfo.setPic_heng_vip(AesTool.decrypt(jsonObjectList.get(i).getString("pic_heng_vip")));

                liveInfo.setAddress(AesTool.decrypt(jsonObjectList.get(i).getString("address")));
                liveInfo.setInfo(AesTool.decrypt(jsonObjectList.get(i).getString("info")));
                liveInfo.setSpare1(AesTool.decrypt(jsonObjectList.get(i).getString("spare1")));
                liveInfo.setSpare2(AesTool.decrypt(jsonObjectList.get(i).getString("spare2")));
                liveInfo.setSpare3(AesTool.decrypt(jsonObjectList.get(i).getString("spare3")));
                liveInfo.setSpare4(AesTool.decrypt(jsonObjectList.get(i).getString("spare4")));
                liveInfo.setSpare5(AesTool.decrypt(jsonObjectList.get(i).getString("spare5")));
                aesLookInfoList.add(liveInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aesLookInfoList;
    }

    Dialog dialog_Vip_Apk;
    LinearLayout layout;

    public void diaologVpn() {
//        String tvName = "TV";
//        String vpnName = "H站大全";
        if (AppTool.isInstalled(mContext, Constant.vpnModel.getOne_package())) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(mContext);
//        if (Constant.vpnModel.getOne_package().contains(tvName.toLowerCase())) {
//            layout = (LinearLayout) inflater.inflate(R.layout.dialog_vip_tv_download, null);
//        } else if (Constant.vpnModel.getOne_package().contains(vpnName.toLowerCase())) {
//            layout = (LinearLayout) inflater.inflate(R.layout.dialog_vip_vpn_download, null);
//        } else {
//            layout = (LinearLayout) inflater.inflate(R.layout.dialog_vip_vpn_download, null);
//        }
        layout = (LinearLayout) inflater.inflate(R.layout.dialog_vip_vpn_download, null);
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
            TextView vpn_text_decription = (TextView) layout.findViewById(R.id.vpn_text_decription);//第二天下载vpn
            vpn_text_decription.setText("恭喜您获得H站大全免费试用会员");
            ImageView close_vpn = (ImageView) layout.findViewById(R.id.close_vpn);
            close_vpn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_Vip_Apk.dismiss();
                }
            });
            ImageView tan_bg = (ImageView) layout.findViewById(R.id.tan_bg);
            Glide.with(mContext).
                    load(NetInterface.TAN_VPN_IMAGE_ADDRESS)
                    .placeholder(R.drawable.allloading).
                    error(R.drawable.allloading).into(tan_bg);
            dialog_vipdownload_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_Vip_Apk.dismiss();
                    if (!NetTool.isConnected(mContext)) {
                        T.showTextToast(mContext, "您的网络没有连接，请检查您的网络,然后下拉刷新试试");
                        return;
                    }
                    if (AppTool.isInstalled(mContext, Constant.vpnModel.getOne_package())) {
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


    public void diaologVersion() {
        try {
            MobclickAgent.onEvent(mContext, MobClick.DOWNLOAD_VERSION_WINDOW_ID);//埋点统计
            new android.support.v7.app.AlertDialog.Builder(mContext).setTitle("升级提示")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setCancelable(false)
                    .setMessage(AppTool.getAppName(mContext, AppTool.getAppPackageName(mContext)) + "有最新的版本,立即升级!!")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (!NetTool.isConnected(mContext)) {
                                T.showTextToast(mContext, "您的网络没有连接，请检查您的网络");
                                return;
                            }
                            File file = new File(FileTool.getSDCardPath() + "/" + versionApkName);
                            if (file.exists()) {
                                file.delete();
                            }
                            if (new File(FileTool.getSDCardPath() + "/" + versionApkName).exists()) {
                                AppTool.installApk(mContext, FileTool.getSDCardPath() + "/" + versionApkName);
                                return;
                            }

                            Request request = new Request.Builder().url(Constant.vpnModel.getSeven_downurl()).build();
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
                                        File file = new File(SDPath, versionApkName);
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
                                        mHandler.sendEmptyMessage(GO_DOWN_WUYEPAPA_SUCCES);
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
                    }).show();
        } catch (Exception e) {
            e.printStackTrace();
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

    private List<Multi> initLiveMutilData(List<LiveInfo> itemList) {
        List<LiveInfo> list = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            String clientUrl = itemList.get(i).getPic_heng_vip();
            String[] urlStr = clientUrl.split(",");
            if (urlStr.length > 1) {
                String url = urlStr[RandomTool.getRandom(1, urlStr.length - 1)];
                itemList.get(i).setPic_heng_vip(url.trim());
            } else {
                itemList.get(i).setPic_heng_vip(clientUrl.replace(",", "").trim());
            }
            list.add(itemList.get(i));
        }
        List<Multi> liveMutils = new ArrayList<>();
        int position = 0;
        for (LiveInfo liveInfo : list) {
            liveMutils.add(new Multi(liveInfo, Multi.LIVE_ITEM, Multi.ITEM_SPAN_SIZE, position));
            position++;
        }
        return liveMutils;
    }

    @Override
    public void onRefresh() {
        isFresh = true;
        getNetLiveData();
        getNetPayData();
        autoGetVpnInfo();
    }

    private void refreshData() {
        liveSwipeFresh.setRefreshing(true);
        live_multiAdapter.setEnableLoadMore(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                live_Multi.clear();
                live_Multi.addAll(initLiveMutilData(aesLookInfoList));
                liveSwipeFresh.setRefreshing(false);
                live_multiAdapter.setEnableLoadMore(true);
                live_multiAdapter.setNewData(live_Multi);
            }
        }, delayMillis);
    }

    private void adapterData() {
        live_multiAdapter.notifyDataSetChanged();
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("updateinterface");
        //注册广播
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        int isLook = 0;

        @Override
        public void onReceive(final Context context, final Intent intent) {
            //接受广播做逻辑处理
            String action = intent.getAction();
            if (action.equals("updateinterface")) {
                isLook = 0;
                Live_Request live_request = new Live_Request();
                live_request.setPageindex(AesTool.encrypt(String.valueOf(Live_Request.Live_Page)));
                live_request.setPagesize(AesTool.encrypt(String.valueOf(Live_Request.Live_Size)));
                JSONObject json = new JSONObject();
                try {
                    json.put("pagesize", live_request.getPagesize());
                    json.put("pageindex", live_request.getPageindex());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String dataJson = json.toString();
                OkHttpClient mOkHttpClient = OkHttp.getInstance();
                RequestBody formBody = new FormBody.Builder()
                        .add(NetInterface.REQUEST_HEADER, dataJson)
                        .build();
                Request request = new Request.Builder()
                        .url(NetInterface.USER_LIVE_INFO_DATA)
                        .post(formBody)
                        .build();
                Call call = mOkHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String jsonData = response.body().string();
                            List<LiveInfo> list = decryptData(jsonData);
                            if (list == null || list.size() == 0) {
                                return;
                            }
                            for (int j = 0; j < list.size(); j++) {
                                if (list.get(j).getSpare2().equals("1")) {
                                    isLook = isLook + 1;
                                }
                            }
                            for (int i = 0; i < list.size(); i++) {
                                // 1~4实时更新数据海报
                                if (i < isLook) {
                                    //更新后处理
                                    LiveInfo liveInfo = list.get(i);
                                    list.get(i).setPic_heng_vip(liveInfo.getPic_heng_vip().replace(",", "").trim());
                                    int pos = live_Multi.get(i).getPosition();
                                    live_Multi.set(i, new Multi(list.get(i), Multi.LIVE_ITEM, Multi.ITEM_SPAN_SIZE, pos));
                                    live_multiAdapter.notifyItemChanged(i);
                                } else {
                                    LiveInfo liveInfo2 = list.get(i);
                                    String clientUrl = liveInfo2.getPic_heng_vip();
                                    String[] urlStr = clientUrl.split(",");
                                    if (urlStr.length > 1) {
                                        String url = urlStr[RandomTool.getRandom(1, urlStr.length - 1)];
                                        list.get(i).setPic_heng_vip(url.trim());
                                    } else {
                                        list.get(i).setPic_heng_vip(clientUrl.replace(",", "").trim());
                                    }
                                    int pos = live_Multi.get(i).getPosition();
                                    live_Multi.set(i, new Multi(list.get(i), Multi.LIVE_ITEM, Multi.ITEM_SPAN_SIZE, pos));
                                    live_multiAdapter.notifyItemChanged(i);
                                }
                            }
//                          live_Multi.clear();
//                          live_Multi.addAll(initLiveMutilData(list));
//                          live_multiAdapter.setNewData(live_Multi);
                        } catch (Exception e) {
                            return;
                        }
                    }
                });
            }
        }
    };


    private void getNetPayData() {
        if (!NetTool.isConnected(mContext)) {
            T.showTextToast(mContext, "您的网络没有连接，请检查您的网络,然后下拉刷新试试");
            return;
        }
        AppData appData = new AppData();
        appData.setArea(AesTool.encrypt(ApkTool.getAppChannels(mContext, AppData.UMENG_APP_CHANNEL)));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("area", appData.getArea());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String aesJson = jsonObject.toString();
        OkHttpClient mOkHttpClient = OkHttp.getInstance();
        RequestBody formBody = new FormBody.Builder()
                .add(NetInterface.REQUEST_HEADER, aesJson)
                .build();
        Request request = new Request.Builder()
                .url(NetInterface.USER_QUERY_PAY_PRICE)
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String json = response.body().string();
                    JSONObject jsonObjects = JsonUtils.parseSingle(json);
                    PayPrice payPrice = new PayPrice();
                    payPrice.setCode(AesTool.decrypt(jsonObjects.getString("code")));
                    payPrice.setHalfyearprice(AesTool.decrypt(jsonObjects.getString("halfyearprice")));
                    payPrice.setOnemonthprice(AesTool.decrypt(jsonObjects.getString("onemonthprice")));
                    payPrice.setOneyearprice(AesTool.decrypt(jsonObjects.getString("oneyearprice")));
                    payPrice.setThreemonthprice(AesTool.decrypt(jsonObjects.getString("threemonthprice")));
                    payPrice.setPermanentprice(AesTool.decrypt(jsonObjects.getString("permanentprice")));
                    payPrice.setSk_1(AesTool.decrypt(jsonObjects.getString("sk_1")));
                    payPrice.setSk_2(AesTool.decrypt(jsonObjects.getString("sk_2")));
//                    Constant.payPrice = payPrice;(停用)
                } catch (Exception e) {
                    return;
                }
            }
        });
    }

    private void autoGetVpnInfo() {
        if (!NetTool.isConnected(mContext)) {
            T.showTextToast(mContext, "您的网络没有连接，请检查您的网络");
            return;
        }
        AppData appData = new AppData();
        //appData.setArea(AesTool.encrypt(ApkTool.getAppChannels(mContext, AppData.UMENG_APP_CHANNEL)));
        //vpn默认一个渠道号，避免本地客户端渠道修改后，服务器没有修改，无法下载
        appData.setArea(AesTool.encrypt(AppData.VPN_APP_CHANNEL));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("area", appData.getArea());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String aesJson = jsonObject.toString();
        OkHttpClient mOkHttpClient = OkHttp.getInstance();
        RequestBody formBody = new FormBody.Builder()
                .add(NetInterface.REQUEST_HEADER, aesJson)
                .build();
        Request request = new Request.Builder()
                .url(NetInterface.USER_VPN_DOWNLOAD)
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String json = response.body().string();
                    JSONObject jsonObjects = JsonUtils.parseSingle(json);
                    VpnModel vpnModel = new VpnModel();
                    vpnModel.five_state = AesTool.decrypt(jsonObjects.getString("five_state"));
                    vpnModel.state = AesTool.decrypt(jsonObjects.getString("state"));
                    vpnModel.five_times = AesTool.decrypt(jsonObjects.getString("five_times"));
                    vpnModel.four_downurl = AesTool.decrypt(jsonObjects.getString("four_downurl"));
                    vpnModel.four_info = AesTool.decrypt(jsonObjects.getString("four_info"));
                    vpnModel.four_package = AesTool.decrypt(jsonObjects.getString("four_package"));
                    vpnModel.four_state = AesTool.decrypt(jsonObjects.getString("four_state"));

                    vpnModel.one_downurl = AesTool.decrypt(jsonObjects.getString("one_downurl"));
                    vpnModel.one_info = AesTool.decrypt(jsonObjects.getString("one_info"));
                    vpnModel.one_package = AesTool.decrypt(jsonObjects.getString("one_package"));
                    vpnModel.one_state = AesTool.decrypt(jsonObjects.getString("one_state"));
                    vpnModel.six_state = AesTool.decrypt(jsonObjects.getString("six_state"));
                    vpnModel.two_state = AesTool.decrypt(jsonObjects.getString("two_state"));

                    vpnModel.six_num = AesTool.decrypt(jsonObjects.getString("six_num"));
                    vpnModel.seven_version_number = AesTool.decrypt(jsonObjects.getString("seven_version_number"));
                    vpnModel.seven_downurl = AesTool.decrypt(jsonObjects.getString("seven_downurl"));
                    vpnModel.seven_package = AesTool.decrypt(jsonObjects.getString("seven_package"));
                    vpnModel.seven_info = AesTool.decrypt(jsonObjects.getString("seven_info"));
                    vpnModel.seven_state = AesTool.decrypt(jsonObjects.getString("seven_state"));
                    if (vpnModel.getState().equals("0")) {
                        //总开关(0.关闭1.开通)
                        vpnModel = null;
                        Constant.vpnModel = vpnModel;
                    } else if (vpnModel.getState().equals("1")) {
                        //开通
                        Constant.vpnModel = vpnModel;
                        Constant.vpnModel.one_downurl = NetInterface.DOWNLOAD_VPN_ADDRESS;
                    }
                    mHandler.sendEmptyMessage(GO_DOWN_VPN_START);
                } catch (Exception e) {
                    return;
                }
            }
        });

    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onResumeLazy() {
        super.onResumeLazy();
        Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
        syncLive();
        initBind();
        downVpnApk();
    }

    @Override
    protected void onPauseLazy() {
        super.onPauseLazy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销广播
        if (mBroadcastReceiver != null && mContext != null) {
            mContext.unregisterReceiver(mBroadcastReceiver);
        }
        //去除绑定服务
        if (updateService != null && mContext != null) {
            if (isBind)
                mContext.unbindService(updateService);
        }
    }

    @OnClick(R.id.live_sync)
    public void onClick() {
        Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
        startActivity(new Intent(mContext, PayActivity.class));
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // TODO: inflate a fragment view
//        View rootView = super.onCreateView(inflater, container, savedInstanceState);
//        ButterKnife.bind(this, rootView);
//        return rootView;
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        ButterKnife.unbind(this);
//    }
}
