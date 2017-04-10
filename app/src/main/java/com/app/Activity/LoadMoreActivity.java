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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.app.Adapter.More_MultiAdapter;
import com.app.Bean.Multi;
import com.app.Bean.PayType;
import com.app.DBManager.DBManager;
import com.app.Model.LookInfo;
import com.app.Model.RequestIp;
import com.app.Net.JsonUtils;
import com.app.Net.NetInterface;
import com.app.Net.OkHttp;
import com.third.app.R;
import com.app.Tool.AesTool;
import com.app.Tool.NetTool;
import com.app.Tool.ScreenTool;
import com.app.Tool.VipTool;
import com.app.View.Look_MarginDecoration;
import com.app.View.T;
import com.wjdz.rmgljtsc.wxapi.PayActivity;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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

public class LoadMoreActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.fragment_wuma_back)
    ImageButton fragmentWumaBack;
    @Bind(R.id.fragment_wuye_title)
    TextView fragmentWuyeTitle;
    @Bind(R.id.fragment_wuye_seatchbuttn)
    ImageButton fragmentWuyeSeatchbuttn;
    @Bind(R.id.titlepanel)
    RelativeLayout titlepanel;
    @Bind(R.id.loadmore_recyclerview)
    RecyclerView loadmoreRecyclerview;
    @Bind(R.id.loadmore_swipe_fresh)
    SwipeRefreshLayout loadmoreSwipeFresh;
    private List<Multi> more_Multi = new ArrayList<>();
    private More_MultiAdapter more_mutil_adapter;
    private boolean isFresh = false;
    private int delayMillis = 10;

    private List<LookInfo> aesLookInfoList;
    private String where;
    private LoadMoreActivity mContext;
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
                    loadmoreSwipeFresh.setRefreshing(false);
                    break;
                case GO_JSON_DATA_FAILED:
                    T.showTextToast(mContext, "系统繁忙!!请稍候下拉刷新试试!!");
                    getProgressBar().dismiss();
                    loadmoreSwipeFresh.setRefreshing(false);
                    break;
                case GO_CLOSE_DIAOLOG:
                    getProgressBar().dismiss();
                    loadmoreSwipeFresh.setRefreshing(false);
                    break;
                case GO_FREFRESH_DATA:
                    refreshData();
                    break;
                case GO_NOTIFY_DATA:
                    adapterData();
                    break;
                case GO_RECEIVE_EMPTY:
                    T.showTextToast(mContext, "系统繁忙!!请稍候下拉刷新试试!!");
                    getProgressBar().dismiss();
                    loadmoreSwipeFresh.setRefreshing(false);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_more);
        ButterKnife.bind(this);
        mContext = this;
        initView();
        initData();

    }

    private void initData() {
        where = getIntent().getStringExtra("typeId");
        if (where.equals("1")) {
            fragmentWuyeTitle.setText("岛国经典");
        } else if (where.equals("2")) {
            fragmentWuyeTitle.setText("激情少妇");
        } else if (where.equals("3")) {
            fragmentWuyeTitle.setText("美女嫩模");
        } else if (where.equals("4")) {
            fragmentWuyeTitle.setText("美腿丝袜");
        } else if (where.equals("5")) {
            fragmentWuyeTitle.setText("岛国素人");
        } else if (where.equals("6")) {
            fragmentWuyeTitle.setText("明星女优");
        } else if (where.equals("7")) {
            fragmentWuyeTitle.setText("制服诱惑");
        } else if (where.equals("8")) {
            fragmentWuyeTitle.setText("宅男福利");
        } else if (where.equals("9")) {
            fragmentWuyeTitle.setText("最近更新");
        }
        getNetLookData();
    }

    private void initView() {
        ScreenTool.setLight(mContext, 255);
        getProgressBar().show();
        loadmoreSwipeFresh.setOnRefreshListener(this);
        loadmoreSwipeFresh.setRefreshing(false);
        loadmoreRecyclerview.addItemDecoration(new Look_MarginDecoration(mContext));
        loadmoreRecyclerview.setHasFixedSize(true);
        loadmoreRecyclerview.setLayoutManager(new GridLayoutManager(mContext, 2));
        more_mutil_adapter = new More_MultiAdapter(more_Multi, mContext);
        more_mutil_adapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int i) {
                return more_Multi.get(i).getSpanSize();
            }
        });
//       look_multiAdapter.setOnLoadMoreListener(this);
        more_mutil_adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        loadmoreRecyclerview.setAdapter(more_mutil_adapter);
    }

    private void adapterData() {
        getProgressBar().dismiss();
        more_mutil_adapter.notifyDataSetChanged();
    }

    private void getNetLookData() {
        getProgressBar().show();
        if (!NetTool.isConnected(mContext)) {
            T.showTextToast(mContext, "您的网络没有连接，请检查您的网络");
            getProgressBar().dismiss();
            loadmoreSwipeFresh.setRefreshing(false);
            return;
        }
        List<LookInfo> lookList = DBManager.getDBManager(mContext).queryLookMoreAll();
        if (lookList.size() != 0) {
            more_Multi.addAll(initLookMoreMutilData());
            mHandler.sendEmptyMessage(GO_NOTIFY_DATA);
            return;
        }
        String ip = AesTool.encrypt(NetInterface.REQUEST_IP);
        RequestIp requestIp = new RequestIp();
        requestIp.setIp(AesTool.encrypt(ip));

        JSONObject json = new JSONObject();
        try {
            json.put("ip", requestIp.getIp());
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        String aesJson = JSONObject.toJSONString(requestIp);
        String aesJson = json.toString();
        OkHttpClient mOkHttpClient = OkHttp.getInstance();
        RequestBody formBody = new FormBody.Builder()
                .add(NetInterface.REQUEST_HEADER, aesJson)
                .build();
        Request request = new Request.Builder()
                .url(NetInterface.USER_LOOK_TWO_DATA)
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
                        aesLookInfoList.add(lookInfo);
                    }
                    if (aesLookInfoList == null || aesLookInfoList.size() == 0) {
                        mHandler.sendEmptyMessage(GO_JSON_DATA_FAILED);
                        return;
                    }
                    DBManager.getDBManager(mContext).addLookMoreData(aesLookInfoList);
                    if (isFresh) {
                        mHandler.sendEmptyMessage(GO_FREFRESH_DATA);
                    } else {
                        more_Multi.addAll(initLookMoreMutilData());
                        mHandler.sendEmptyMessage(GO_NOTIFY_DATA);
                    }
                    mHandler.sendEmptyMessage(GO_CLOSE_DIAOLOG);
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(GO_JSON_DATA_FAILED);
                }
            }
        });
    }

    private List<Multi> initLookMoreMutilData() {
        List<Multi> multiList = new ArrayList<>();
        List<LookInfo> lookInfoList = DBManager.getDBManager(mContext).queryLookMoreByType(where);
        for (LookInfo lookInfo : lookInfoList) {
            multiList.add(new Multi(lookInfo, Multi.MORE_ITEM, Multi.ITEM_SPAN_SIZE));
        }
        return multiList;
    }

    @Override
    public void onRefresh() {
        isFresh = true;
        loadmoreSwipeFresh.setRefreshing(false);
        getNetLookData();
    }

    private void refreshData() {
        loadmoreSwipeFresh.setRefreshing(false);
        more_mutil_adapter.setEnableLoadMore(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                more_Multi.clear();
                more_Multi.addAll(initLookMoreMutilData());
                loadmoreSwipeFresh.setRefreshing(false);
                more_mutil_adapter.setEnableLoadMore(true);
                more_mutil_adapter.setNewData(more_Multi);
            }
        }, delayMillis);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Multi.Moon_LEVE2:
                mContext.finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Multi.Moon_LEVE = Multi.Moon_LEVE1;
        mContext.finish();
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
    protected void onResume() {
        super.onResume();
        initPayLevel();
        MobclickAgent.onPageStart("午夜啪啪更多"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("午夜啪啪更多"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.fragment_wuma_back)
    public void onClick() {
        mContext.finish();
    }

}
