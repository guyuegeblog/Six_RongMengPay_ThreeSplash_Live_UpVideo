package com.app.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.app.Adapter.Type_MultiAdapter;
import com.app.Bean.Multi;
import com.app.Bean.PayType;
import com.app.Net.NetInterface;
import com.app.Model.ChanneLnfo;
import com.app.Model.TypeModel;
import com.app.Net.JsonUtils;
import com.app.Net.OkHttp;
import com.third.app.R;
import com.app.Tool.AesTool;
import com.app.Tool.NetTool;
import com.app.Tool.ScreenTool;
import com.app.Tool.VipTool;
import com.app.View.T;
import com.jssm.zsrz.wxapi.PayActivity;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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

public class ChannelActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.channe_recyclerview)
    RecyclerView channeRecyclerview;
    @Bind(R.id.channe_swipe_fresh)
    SwipeRefreshLayout channeSwipeFresh;
    private int delayMillis = 10;
    private boolean isFresh = false;
    private List<Multi> channel_two_Multi = new ArrayList<>();
    private Type_MultiAdapter type_multiAdapter;
    private List<ChanneLnfo> aesLookInfoList;
    private String channel_type;
    private ChannelActivity mContext;
    private final int GO_LOOK_REQUEST_FAILED = 1000;
    private final int GO_JSON_DATA_FAILED = 1020;
    private final int GO_CLOSE_DIAOLOG = 1030;
    private final int GO_FREFRESH_DATA = 1010;
    private final int GO_NOTIFY_DATA = 1042;
    private final int GO_RECEIVE_EMPTY = 1044;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_LOOK_REQUEST_FAILED:
                    T.showTextToast(mContext, "请求失败!!!");
                    getProgressBar().dismiss();
                    channeSwipeFresh.setRefreshing(false);
                    break;
                case GO_JSON_DATA_FAILED:
                    T.showTextToast(mContext, "系统繁忙!!请稍候下拉刷新试试!!");
                    getProgressBar().dismiss();
                    channeSwipeFresh.setRefreshing(false);
                    break;
                case GO_CLOSE_DIAOLOG:
                    getProgressBar().dismiss();
                    break;
                case GO_FREFRESH_DATA:
                    refreshData();
                    break;
                case GO_NOTIFY_DATA:
                    adapterData();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        ButterKnife.bind(this);
        mContext = this;
        initView();
        initData();
    }

    private void initView() {
        ScreenTool.setLight(mContext, 255);
        getProgressBar().show();
        channeSwipeFresh.setOnRefreshListener(this);
        channeSwipeFresh.setRefreshing(false);
//      channeRecyclerview.addItemDecoration(new Look_MarginDecoration(mContext));
        channeRecyclerview.setHasFixedSize(true);
        channeRecyclerview.setLayoutManager(new GridLayoutManager(mContext, 2));
        type_multiAdapter = new Type_MultiAdapter(channel_two_Multi, mContext);
        type_multiAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int i) {
                return channel_two_Multi.get(i).getSpanSize();
            }
        });
//      channel_multiAdapter.setOnLoadMoreListener(this);
        type_multiAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        channeRecyclerview.setAdapter(type_multiAdapter);
    }

    private void initData() {
        channel_type = getIntent().getStringExtra("channel_type");
        getNetLookData(channel_type);
    }

    private void getNetLookData(String channel_type) {
        getProgressBar().show();
        if (!NetTool.isConnected(mContext)) {
            T.showTextToast(mContext, "您的网络没有连接，请检查您的网络");
            channeSwipeFresh.setRefreshing(false);
            getProgressBar().dismiss();
            return;
        }
        TypeModel typeModel = new TypeModel();
        typeModel.setType(AesTool.encrypt(channel_type));
        JSONObject json = new JSONObject();
        try {
            json.put("type", typeModel.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        String aesJson = JSON.toJSONString(typeModel);
        String aesJson = json.toString();
        OkHttpClient mOkHttpClient = OkHttp.getInstance();
        RequestBody formBody = new FormBody.Builder()
                .add(NetInterface.REQUEST_HEADER, aesJson)
                .build();
        Request request = new Request.Builder()
                .url(NetInterface.USER_TWO_LEVEL_TYPE_DATA)
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
                    if (json.equals("[]")) {
                        mHandler.sendEmptyMessage(GO_RECEIVE_EMPTY);
                        return;
                    }
                    List<JSONObject> jsonObjectList = JsonUtils.parseJsonArray(json);
                    aesLookInfoList = new ArrayList<ChanneLnfo>();
                    for (int i = 0; i < jsonObjectList.size(); i++) {
                        ChanneLnfo channeLnfo = new ChanneLnfo();
                        channeLnfo.setName(AesTool.decrypt(jsonObjectList.get(i).getString("name")));
                        channeLnfo.setPic(AesTool.decrypt(jsonObjectList.get(i).getString("pic")));
                        channeLnfo.setType(AesTool.decrypt(jsonObjectList.get(i).getString("type")));
                        channeLnfo.setPic_heng(AesTool.decrypt(jsonObjectList.get(i).getString("pic_heng")));
                        channeLnfo.setIsvip(AesTool.decrypt(jsonObjectList.get(i).getString("isvip")));
                        channeLnfo.setAddress_sd(AesTool.decrypt(jsonObjectList.get(i).getString("address_sd")));
                        channeLnfo.setAddress_hd(AesTool.decrypt(jsonObjectList.get(i).getString("address_hd")));
                        aesLookInfoList.add(channeLnfo);
                    }
                    if (aesLookInfoList == null || aesLookInfoList.size() == 0) {
                        mHandler.sendEmptyMessage(GO_JSON_DATA_FAILED);
                        return;
                    }
                    if (isFresh) {
                        isFresh = false;
                        mHandler.sendEmptyMessage(GO_FREFRESH_DATA);
                    } else {
                        channel_two_Multi.addAll(initChannelData(aesLookInfoList));
                        mHandler.sendEmptyMessage(GO_NOTIFY_DATA);
                    }
                    mHandler.sendEmptyMessage(GO_CLOSE_DIAOLOG);
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(GO_JSON_DATA_FAILED);
                }
            }
        });
    }

    private List<Multi> initChannelData(List<ChanneLnfo> channeLnfoList) {
        List<Multi> chanelMutils_Data = new ArrayList<>();
        if (channeLnfoList == null || channeLnfoList.size() == 0) {
            return chanelMutils_Data;
        }
        chanelMutils_Data.add(new Multi(channel_type, Multi.CHANNEL_TWO_HEADER, Multi.LOOK_NORMAL_SPAN_SIZE));
        for (ChanneLnfo comprehenSiveInfo1 : channeLnfoList) {
            chanelMutils_Data.add(new Multi(comprehenSiveInfo1, Multi.CHANNEL_TYPE_ITEM, Multi.ITEM_SPAN_SIZE));
        }
        chanelMutils_Data.add(new Multi(new Object(), Multi.CHANNEL_TWO_BOTTOM, Multi.LOOK_NORMAL_SPAN_SIZE));
        return chanelMutils_Data;
    }


    public ProgressDialog progressBar;

    public ProgressDialog getProgressBar() {
        return showProgressDialog();
    }

    private ProgressDialog showProgressDialog() {
        if (progressBar == null) {
            progressBar = new ProgressDialog(this);
            progressBar.setMessage("正在获取数据,请稍后...");
            progressBar.setCancelable(false);
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        return progressBar;
    }

    @Override
    public void onRefresh() {
        type_multiAdapter.setEnableLoadMore(false);
        isFresh = true;
        getNetLookData(channel_type);
    }

    private void refreshData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                channel_two_Multi.clear();
                channel_two_Multi.addAll(initChannelData(aesLookInfoList));
//              three_Position = 0;
//              three_Id = 0;
                channeSwipeFresh.setRefreshing(false);
                type_multiAdapter.setEnableLoadMore(true);
                type_multiAdapter.setNewData(channel_two_Multi);
            }
        }, delayMillis);
    }

    private void adapterData() {
        type_multiAdapter.notifyDataSetChanged();
        getProgressBar().dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Multi.Moon_LEVE2:
                mContext.finish();
                break;
        }
    }

    public void initPayLevel() {
        Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
        if (Multi.isShowDialog)
            alertDialogPay();
        else
            Multi.isShowDialog = false;
    }

    Dialog dialog_pay;
    String choice = "zfb";

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
            ImageView close = (ImageView) layout.findViewById(R.id.close);
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

    @Override
    public void onBackPressed() {
        Multi.Moon_LEVE = Multi.Moon_LEVE1;
        mContext.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPayLevel();
        MobclickAgent.onPageStart("午夜啪啪频道二级"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("午夜啪啪频道二级"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }
}
