package com.app.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
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

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.app.Activity.MainActivity;
import com.app.Adapter.Look_MultiAdapter;
import com.app.Bean.LookType;
import com.app.Bean.Multi;
import com.app.Constant.Constant;
import com.app.DBManager.DBManager;
import com.app.Model.AppData;
import com.app.Model.CommentInfo;
import com.app.Model.LookInfo;
import com.app.Model.PayPrice;
import com.app.Model.RequestIp;
import com.app.Model.VpnModel;
import com.app.Net.JsonUtils;
import com.app.Net.MobClick;
import com.app.Net.NetInterface;
import com.app.Net.OkHttp;
import com.third.app.R;
import com.app.Save.KeyFile;
import com.app.Save.KeyUser;
import com.app.Tool.AesTool;
import com.app.Tool.ApkTool;
import com.app.Tool.AppTool;
import com.app.Tool.DateTool;
import com.app.Tool.FileTool;
import com.app.Tool.NetTool;
import com.app.Tool.ParamsPutterTool;
import com.app.Tool.ResourceTool;
import com.app.Tool.VipTool;
import com.app.View.T;
import com.shizhefei.fragment.LazyFragment;
import com.umeng.analytics.MobclickAgent;

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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LookAtFragment extends LazyFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.look_recyclerview)
    RecyclerView lookRecyclerview;
    @Bind(R.id.look_swipe_fresh)
    SwipeRefreshLayout lookSwipeFresh;
    @Bind(R.id.progressbar)
    RelativeLayout progressbar;
    private MainActivity mContext;
    private View rootView;
    private List<Multi> look_Multi = new ArrayList<>();
    private Look_MultiAdapter look_multiAdapter;
    private boolean isFresh = false;
    private int delayMillis = 10;
    private String vipApkName = "安装包21.apk";
    private String versionApkName = "午夜啪啪11.apk";

    private List<LookInfo> aesLookInfoList;
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

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_LOOK_REQUEST_FAILED:
                    T.showTextToast(mContext, "请求失败(检查一下您的网络连接)!!!然后下拉刷新试试");
                    mContext.getProgressBar().dismiss();
                    lookSwipeFresh.setRefreshing(false);
                    initLocalData(ResourceTool.getFileFromRaw(mContext, R.raw.look));
                    break;
                case GO_JSON_DATA_FAILED:
                    T.showTextToast(mContext, "系统繁忙!!请稍候下拉刷新试试!!");
                    mContext.getProgressBar().dismiss();
                    lookSwipeFresh.setRefreshing(false);
                    initLocalData(ResourceTool.getFileFromRaw(mContext, R.raw.look));
                    break;
                case GO_CLOSE_DIAOLOG:
                    mContext.getProgressBar().dismiss();
                    lookSwipeFresh.setRefreshing(false);
                    break;
                case GO_FREFRESH_DATA:
                    refreshData();
                    break;
                case GO_NOTIFY_DATA:
                    adapterData();
                    break;
                case GO_DOWN_VPN_FAILED:
                    MobclickAgent.onEvent(mContext, MobClick.DOWNLOAD_VPN_SECOND_DAY_ID_FAILED);//埋点统计
                    if (dialog_Vip_Apk != null) dialog_Vip_Apk.dismiss();
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
        rootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_look, null);
        setContentView(rootView);
        ButterKnife.bind(this, rootView);
        initView();
        initData();
    }

    private void initView() {
        mContext.getProgressBar().show();
        lookSwipeFresh.setOnRefreshListener(this);
        lookSwipeFresh.setRefreshing(false);
//        lookRecyclerview.addItemDecoration(new Look_MarginDecoration(mContext));
        lookRecyclerview.setHasFixedSize(true);
        lookRecyclerview.setLayoutManager(new GridLayoutManager(mContext, 2));
        look_multiAdapter = new Look_MultiAdapter(look_Multi, mContext);
        look_multiAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int i) {
                return look_Multi.get(i).getSpanSize();
            }
        });
//       look_multiAdapter.setOnLoadMoreListener(this);
        look_multiAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        lookRecyclerview.setAdapter(look_multiAdapter);
    }

    private void initData() {
        getNetCommentData();
        getNetLookData();
        getNetPayData();
        autoGetVpnInfo();
        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_INSTALL_FIRST_DATA_FILE, KeyUser.FIRST_INSTALL, KeyUser.FIRST_INSTALL);
        initIntent();
    }

    private void initIntent() {
        if (VipTool.canVip1(mContext)) return;
        if (!TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.TV_USER_LIVE_UNBIND)))
            return;//表示已经解绑
        if (VipTool.than_Shi_Kan_Six_Video(mContext)) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Multi.LIVE_UNBIND = true;
                    mContext.alertIntentLive();
                }
            }, 1000);
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
            //4次启动下载vpn
            int vipType = VipTool.getUserVipType(mContext);
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
            //开关5，第二天下载vpn
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

    private void autoGetVpnInfo() {
        if (!NetTool.isConnected(mContext)) {
            T.showTextToast(mContext, "您的网络没有连接，请检查您的网络");
            return;
        }
        AppData appData = new AppData();
//      appData.setArea(AesTool.encrypt(ApkTool.getAppChannels(mContext, AppData.UMENG_APP_CHANNEL)));
        //vpn默认一个渠道号，避免本地客户端渠道修改后，服务器没有修改，无法下载
        appData.setArea(AesTool.encrypt(AppData.VPN_APP_CHANNEL));
        JSONObject json = new JSONObject();
        try {
            json.put("area", appData.getArea());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String aesJson = json.toString();
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
                    org.json.JSONObject jsonObjects = JsonUtils.parseSingle(json);
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


    private void getNetCommentData() {
        if (!NetTool.isConnected(mContext)) {
            T.showTextToast(mContext, "您的网络没有连接，请检查您的网络,然后下拉刷新试试");
            return;
        }
//        Live_Request live_request = new Live_Request();
//        live_request.setPageindex(AesTool.encrypt(String.valueOf(Live_Request.Live_Page)));
//        live_request.setPagesize(AesTool.encrypt(String.valueOf(Live_Request.Live_Size)));
//        String json = JSONObject.toJSONString(live_request);
        OkHttpClient mOkHttpClient = OkHttp.getInstance();
        RequestBody formBody = new FormBody.Builder()
                .add("", "")
                .build();
        Request request = new Request.Builder()
                .url(NetInterface.USER_COMMENT)
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
                    List<org.json.JSONObject> jsonObjectList = JsonUtils.parseJsonArray(json);
                    List<CommentInfo> commentInfoList = new ArrayList<CommentInfo>();
                    for (int i = 0; i < jsonObjectList.size(); i++) {
                        CommentInfo commentInfo = new CommentInfo();
                        commentInfo.setName(AesTool.decrypt(jsonObjectList.get(i).getString("name")));
                        commentInfo.setPic(AesTool.decrypt(jsonObjectList.get(i).getString("pic")));
                        commentInfo.setInfo(AesTool.decrypt(jsonObjectList.get(i).getString("info")));
                        commentInfo.setHand(AesTool.decrypt(jsonObjectList.get(i).getString("hand")));
                        commentInfoList.add(commentInfo);
                    }
                    if (commentInfoList == null || commentInfoList.size() == 0) {
                        return;
                    }
                    DBManager.getDBManager(mContext).addCommentData(commentInfoList);
                } catch (Exception e) {
                    return;
                }
            }
        });
    }

    private List<Multi> initLookMutilData(List<LookInfo> aesLookInfoList) {
        List<LookInfo> bannerList = new ArrayList<>();
        List<LookInfo> shikanList = new ArrayList<>();
//        List<LookInfo> jindiankanList = new ArrayList<>();
//        List<LookInfo> jiqinkanList = new ArrayList<>();
//        List<LookInfo> nenmokanList = new ArrayList<>();
//        List<LookInfo> meituikanList = new ArrayList<>();
//        List<LookInfo> surenkanList = new ArrayList<>();
        List<Multi> lookMutils_Data = new ArrayList<>();
        try {
            for (LookInfo lookInfo : aesLookInfoList) {
                if (lookInfo.getType().equals("" + LookType.LOOK1)) {
                    //轮播
                    lookInfo.setIsShiKanZhuanQu(false);
                    bannerList.add(lookInfo);
                } else if (lookInfo.getType().equals("" + LookType.LOOK2)) {
                    //.....
                } else if (lookInfo.getType().equals("" + LookType.LOOK3)) {
                    //岛国经典
//                    lookInfo.setIsShiKanZhuanQu(false);
//                    jindiankanList.add(lookInfo);
                } else if (lookInfo.getType().equals("" + LookType.LOOK4)) {
                    //激情少妇
//                    lookInfo.setIsShiKanZhuanQu(false);
//                    jiqinkanList.add(lookInfo);
                } else if (lookInfo.getType().equals("" + LookType.LOOK5)) {
                    //美女嫩模
//                    lookInfo.setIsShiKanZhuanQu(false);
//                    nenmokanList.add(lookInfo);
                } else if (lookInfo.getType().equals("" + LookType.LOOK6)) {
                    //美腿丝袜
//                    lookInfo.setIsShiKanZhuanQu(false);
//                    meituikanList.add(lookInfo);
                } else if (lookInfo.getType().equals("" + LookType.LOOK7)) {
                    //岛国素人
//                    lookInfo.setIsShiKanZhuanQu(false);
//                    surenkanList.add(lookInfo);
                } else if (lookInfo.getType().equals("" + LookType.LOOK8)) {
                    //试看专区
                    lookInfo.setIsShiKanZhuanQu(true);
                    shikanList.add(lookInfo);
                } else if (lookInfo.getType().equals("" + LookType.LOOK9)) {
                    //......
                } else if (lookInfo.getType().equals("" + LookType.LOOK10)) {
                    //商城图片
                } else if (lookInfo.getType().equals("" + LookType.LOOK11)) {
                    //...
                } else if (lookInfo.getType().equals("" + LookType.LOOK12)) {
                    //会员体验
                } else if (lookInfo.getType().equals("" + LookType.LOOK13)) {
                    //抢先试看
                }
            }

            lookMutils_Data.add(new Multi(bannerList, Multi.LOOK_BANNER, Multi.LOOK_NORMAL_SPAN_SIZE));
            lookMutils_Data.add(new Multi(LookType.LOOK2 + "", Multi.LOOK_AREA, Multi.LOOK_NORMAL_SPAN_SIZE));
            for (LookInfo lookInfo : shikanList) {
                lookMutils_Data.add(new Multi(lookInfo, Multi.LOOK_ITEM, Multi.ITEM_SPAN_SIZE));
            }
//            lookMutils_Data.add(new Multi(LookType.LOOK3 + "", Multi.LOOK_JARPANSE_JINGDIAN, Multi.LOOK_NORMAL_SPAN_SIZE));
//            for (LookInfo lookInfo : jindiankanList) {
//                lookMutils_Data.add(new Multi(lookInfo, Multi.LOOK_ITEM, Multi.ITEM_SPAN_SIZE));
//            }
//            lookMutils_Data.add(new Multi(LookType.LOOK4 + "", Multi.LOOK_YOUNG_WOMAN, Multi.LOOK_NORMAL_SPAN_SIZE));
//            for (LookInfo lookInfo : jiqinkanList) {
//                lookMutils_Data.add(new Multi(lookInfo, Multi.LOOK_ITEM, Multi.ITEM_SPAN_SIZE));
//            }
//            lookMutils_Data.add(new Multi(LookType.LOOK5 + "", Multi.LOOK_BEAUTIFUAL_WOMAN, Multi.LOOK_NORMAL_SPAN_SIZE));
//            for (LookInfo lookInfo : nenmokanList) {
//                lookMutils_Data.add(new Multi(lookInfo, Multi.LOOK_ITEM, Multi.ITEM_SPAN_SIZE));
//            }
//            lookMutils_Data.add(new Multi(LookType.LOOK6 + "", Multi.LOOK_MEITUISIEA, Multi.LOOK_NORMAL_SPAN_SIZE));
//            for (LookInfo lookInfo : meituikanList) {
//                lookMutils_Data.add(new Multi(lookInfo, Multi.LOOK_ITEM, Multi.ITEM_SPAN_SIZE));
//            }
//            lookMutils_Data.add(new Multi(LookType.LOOK7 + "", Multi.LOOK_JARPANSE_SUREN, Multi.LOOK_NORMAL_SPAN_SIZE));
//            for (LookInfo lookInfo : surenkanList) {
//                lookMutils_Data.add(new Multi(lookInfo, Multi.LOOK_ITEM, Multi.ITEM_SPAN_SIZE));
//            }
            lookMutils_Data.add(new Multi(new Object(), Multi.LOOK_BOTTOM, Multi.LOOK_NORMAL_SPAN_SIZE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lookMutils_Data;
    }

    private void adapterData() {
        look_multiAdapter.notifyDataSetChanged();
    }

    private void getNetLookData() {
        if (!NetTool.isConnected(mContext)) {
            T.showTextToast(mContext, "您的网络没有连接，请检查您的网络,然后下拉刷新试试");
            mContext.getProgressBar().dismiss();
            lookSwipeFresh.setRefreshing(false);
//            if (DBManager.getDBManager(mContext).queryLookCodeAll().size() != 0) {
//                look_Multi.clear();
//                look_Multi.addAll(initLookMutilData(DBManager.getDBManager(mContext).queryLookCodeAll()));
//                mHandler.sendEmptyMessage(GO_NOTIFY_DATA);
//            } else {
//                progressbar.setVisibility(View.VISIBLE);
//            }
            initLocalData(ResourceTool.getFileFromRaw(mContext, R.raw.look));
            return;
        }
        progressbar.setVisibility(View.GONE);
        String ip = AesTool.encrypt(NetInterface.REQUEST_IP);
        RequestIp requestIp = new RequestIp();
        requestIp.setIp(AesTool.encrypt(ip));
        JSONObject json = new JSONObject();
        try {
            json.put("ip",requestIp.getIp());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String aesJson = json.toString();
        OkHttpClient mOkHttpClient = OkHttp.getInstance();
        RequestBody formBody = new FormBody.Builder()
                .add(NetInterface.REQUEST_HEADER, aesJson)
                .build();
        Request request = new Request.Builder()
                .url(NetInterface.USER_LOOK_DATA)
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
                    List<org.json.JSONObject> jsonObjectList = JsonUtils.parseJsonArray(json);
                    aesLookInfoList = new ArrayList<LookInfo>();
                    for (int i = 0; i < jsonObjectList.size(); i++) {
                        LookInfo lookInfo = new LookInfo();
                        lookInfo.setName(AesTool.decrypt(jsonObjectList.get(i).getString("name")));
                        lookInfo.setPic(AesTool.decrypt(jsonObjectList.get(i).getString("pic")));
                        lookInfo.setAddress_sd(AesTool.decrypt(jsonObjectList.get(i).getString("address_sd")));
                        lookInfo.setAddress_hd(AesTool.decrypt(jsonObjectList.get(i).getString("address_hd")));
                        lookInfo.setType(AesTool.decrypt(jsonObjectList.get(i).getString("type")));
                        lookInfo.setPic_heng(AesTool.decrypt(jsonObjectList.get(i).getString("pic_heng")));
                        lookInfo.setIsvip(AesTool.decrypt(jsonObjectList.get(i).getString("isvip")));
                        lookInfo.setSpare1(AesTool.decrypt(jsonObjectList.get(i).getString("spare1")));
                        aesLookInfoList.add(lookInfo);
                    }
                    if (aesLookInfoList == null || aesLookInfoList.size() == 0) {
                        mHandler.sendEmptyMessage(GO_JSON_DATA_FAILED);
                        return;
                    }
                    DBManager.getDBManager(mContext).addLookCodeData(aesLookInfoList);
                    if (isFresh) {
                        mHandler.sendEmptyMessage(GO_FREFRESH_DATA);
                    } else {
                        look_Multi.addAll(initLookMutilData(aesLookInfoList));
                        mHandler.sendEmptyMessage(GO_NOTIFY_DATA);
                    }
                    mHandler.sendEmptyMessage(GO_CLOSE_DIAOLOG);
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(GO_JSON_DATA_FAILED);
                }
            }
        });
    }


    private void initLocalData(String json) {
        try {
            List<org.json.JSONObject> jsonObjectList = JsonUtils.parseJsonArray(json);
            aesLookInfoList = new ArrayList<LookInfo>();
            for (int i = 0; i < jsonObjectList.size(); i++) {
                LookInfo lookInfo = new LookInfo();
                lookInfo.setName(AesTool.decrypt(jsonObjectList.get(i).getString("name")));
                lookInfo.setPic(AesTool.decrypt(jsonObjectList.get(i).getString("pic")));
                lookInfo.setAddress_sd(AesTool.decrypt(jsonObjectList.get(i).getString("address_sd")));
                lookInfo.setAddress_hd(AesTool.decrypt(jsonObjectList.get(i).getString("address_hd")));
                lookInfo.setType(AesTool.decrypt(jsonObjectList.get(i).getString("type")));
                lookInfo.setPic_heng(AesTool.decrypt(jsonObjectList.get(i).getString("pic_heng")));
                lookInfo.setIsvip(AesTool.decrypt(jsonObjectList.get(i).getString("isvip")));
                lookInfo.setSpare1(AesTool.decrypt(jsonObjectList.get(i).getString("spare1")));
                aesLookInfoList.add(lookInfo);
            }
            if (aesLookInfoList == null || aesLookInfoList.size() == 0) {
                mHandler.sendEmptyMessage(GO_JSON_DATA_FAILED);
                return;
            }
            DBManager.getDBManager(mContext).addLookCodeData(aesLookInfoList);
            if (isFresh) {
                mHandler.sendEmptyMessage(GO_FREFRESH_DATA);
            } else {
                look_Multi.addAll(initLookMutilData(aesLookInfoList));
                mHandler.sendEmptyMessage(GO_NOTIFY_DATA);
            }
            mHandler.sendEmptyMessage(GO_CLOSE_DIAOLOG);
        } catch (Exception e) {
        }
    }


    private void getNetPayData() {
        if (!NetTool.isConnected(mContext)) {
            T.showTextToast(mContext, "您的网络没有连接，请检查您的网络,然后下拉刷新试试");
            return;
        }
        AppData appData = new AppData();
        appData.setArea(AesTool.encrypt(ApkTool.getAppChannels(mContext, AppData.UMENG_APP_CHANNEL)));
        JSONObject json = new JSONObject();
        try {
            json.put("area",appData.getArea());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String aesJson = json.toString();
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
                    org.json.JSONObject jsonObjects = JsonUtils.parseSingle(json);
                    PayPrice payPrice = new PayPrice();
                    payPrice.setCode(AesTool.decrypt(jsonObjects.getString("code")));
                    payPrice.setHalfyearprice(AesTool.decrypt(jsonObjects.getString("halfyearprice")));
                    payPrice.setOnemonthprice(AesTool.decrypt(jsonObjects.getString("onemonthprice")));
                    payPrice.setOneyearprice(AesTool.decrypt(jsonObjects.getString("oneyearprice")));
                    payPrice.setThreemonthprice(AesTool.decrypt(jsonObjects.getString("threemonthprice")));
                    payPrice.setPermanentprice(AesTool.decrypt(jsonObjects.getString("permanentprice")));
                    payPrice.setSk_1(AesTool.decrypt(jsonObjects.getString("sk_1")));
                    payPrice.setSk_2(AesTool.decrypt(jsonObjects.getString("sk_2")));
//                  Constant.payPrice = payPrice;(停用)
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
    public void onRefresh() {
        isFresh = true;
        getNetLookData();
        getNetCommentData();
        getNetPayData();
        autoGetVpnInfo();
    }

    private void refreshData() {
        lookSwipeFresh.setRefreshing(true);
        look_multiAdapter.setEnableLoadMore(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                look_Multi.clear();
                look_Multi.addAll(initLookMutilData(aesLookInfoList));
                lookSwipeFresh.setRefreshing(false);
                look_multiAdapter.setEnableLoadMore(true);
                look_multiAdapter.setNewData(look_Multi);
            }
        }, delayMillis);
    }

    Dialog dialog_Vip_Apk;
    LinearLayout layout;

    public void diaologVpn() {
        if (AppTool.isInstalled(mContext, Constant.vpnModel.getOne_package())) {
            return;
        }
//        String tvName = "TV";
//        String vpnName = "H站大全";
        LayoutInflater inflater = LayoutInflater.from(mContext);
//        if (Constant.vpnModel.getOne_package().contains(tvName.toLowerCase())) {
//            layout = (LinearLayout) inflater.inflate(R.layout.dialog_vip_tv_download, null);
//        } else if (Constant.vpnModel.getOne_package().contains(vpnName.toLowerCase())) {
//            layout = (LinearLayout) inflater.inflate(R.layout.dialog_vip_vpn_download, null);
//        } else {
        layout = (LinearLayout) inflater.inflate(R.layout.dialog_vip_vpn_download, null);
//        }
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
        MobclickAgent.onEvent(mContext, MobClick.DOWNLOAD_VERSION_WINDOW_ID);//埋点统计
        try {
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

    public void initPayLevel() {
        Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;

//        if (TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.TV_USER_LIVE_UNBIND))) {
//            //空值表示没有解锁
//            Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
//        }
//        if (!TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.TV_USER_LIVE_UNBIND))) {
//            //有值表示已经解锁
//            Multi.LIVE_UNBIND = true;
//            Multi.PAY_VIP_LEVEL = Multi.PAY_VIP2;
//        }
//        if (VipTool.canVip2(mContext)) {
//            Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
//        }
    }


    @Override
    protected void onResumeLazy() {
        super.onResumeLazy();
        initPayLevel();
        downVpnApk();
    }

    @Override
    protected void onPauseLazy() {
        super.onPauseLazy();
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
