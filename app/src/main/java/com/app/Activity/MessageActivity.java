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
import com.app.Adapter.Message_MultiAdapter;
import com.app.Bean.Multi;
import com.app.Bean.PayType;
import com.app.DBManager.DBManager;
import com.app.Model.CommentInfo;
import com.app.Model.LookInfo;
import com.third.app.R;
import com.app.Tool.RandomTool;
import com.app.Tool.ScreenTool;
import com.app.Tool.VipTool;
import com.app.View.T;
import com.wjdz.rmgljtsc.wxapi.PayActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MessageActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.message_recyclerview)
    RecyclerView messageRecyclerview;
    @Bind(R.id.message_swipe_fresh)
    SwipeRefreshLayout messageSwipeFresh;
    private int delayMillis = 10;
    private boolean isFresh = false;
    private List<Multi> message_Multi = new ArrayList<>();
    private Message_MultiAdapter message_multiAdapter;
    private List<LookInfo> look_hot;
    private List<CommentInfo> message_hot;
    private MessageActivity mContext;
    private LookInfo lookInfo;
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
                    messageSwipeFresh.setRefreshing(false);
                    break;
                case GO_JSON_DATA_FAILED:
                    T.showTextToast(mContext, "系统繁忙!!请稍候下拉刷新试试!!");
                    getProgressBar().dismiss();
                    messageSwipeFresh.setRefreshing(false);
                    break;
                case GO_CLOSE_DIAOLOG:
                    getProgressBar().dismiss();
                    messageSwipeFresh.setRefreshing(false);
                    break;
                case GO_FREFRESH_DATA:
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
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        mContext = this;
        initView();
        initData();
    }

    private void initView() {
        ScreenTool.setLight(mContext, 255);
        getProgressBar().show();
        messageSwipeFresh.setRefreshing(false);
        messageRecyclerview.setHasFixedSize(true);
        messageSwipeFresh.setOnRefreshListener(this);
        messageRecyclerview.setLayoutManager(new GridLayoutManager(mContext, 2));
        message_multiAdapter = new Message_MultiAdapter(message_Multi, mContext);
        message_multiAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int i) {
                return message_Multi.get(i).getSpanSize();
            }
        });
        message_multiAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        messageRecyclerview.setAdapter(message_multiAdapter);
    }

    private void initData() {
        lookInfo = (LookInfo) getIntent().getSerializableExtra("videoInfo");
        look_hot = DBManager.getDBManager(mContext).queryLookCodeAll();
        message_hot = DBManager.getDBManager(mContext).queryCommentAll();
        message_Multi.addAll(initMessageData());
        adapterData();
    }


    private List<Multi> initMessageData() {
        List<Multi> messagelMutils_Data = new ArrayList<>();
        if (lookInfo == null || look_hot == null || look_hot.size() == 0 || message_hot == null || message_hot.size() == 0) {
            return messagelMutils_Data;
        }
        messagelMutils_Data.add(new Multi(lookInfo, Multi.MESSAGE_HEADER, Multi.LOOK_NORMAL_SPAN_SIZE));
        messagelMutils_Data.add(new Multi(new Object(), Multi.MESSAGE_MIDDLE, Multi.LOOK_NORMAL_SPAN_SIZE));
        messagelMutils_Data.add(new Multi(look_hot.get(RandomTool.getRandom(0, look_hot.size() - 1)), Multi.LOOK_ITEM, Multi.ITEM_SPAN_SIZE));
        messagelMutils_Data.add(new Multi(look_hot.get(RandomTool.getRandom(1, look_hot.size() - 2)), Multi.LOOK_ITEM, Multi.ITEM_SPAN_SIZE));
        messagelMutils_Data.add(new Multi(look_hot.get(RandomTool.getRandom(2, look_hot.size() - 3)), Multi.LOOK_ITEM, Multi.ITEM_SPAN_SIZE));
        messagelMutils_Data.add(new Multi(look_hot.get(RandomTool.getRandom(3, look_hot.size() - 4)), Multi.LOOK_ITEM, Multi.ITEM_SPAN_SIZE));
        messagelMutils_Data.add(new Multi(new Object(), Multi.MESSAGE_HOT, Multi.LOOK_NORMAL_SPAN_SIZE));
        Collections.shuffle(message_hot);
        for (CommentInfo commentInfo : message_hot) {
            messagelMutils_Data.add(new Multi(commentInfo, Multi.MESSAGE_ITEM, Multi.LOOK_NORMAL_SPAN_SIZE));
        }
        messagelMutils_Data.add(new Multi(new Object(), Multi.MESSAGE_BOTTOM, Multi.LOOK_NORMAL_SPAN_SIZE));

        return messagelMutils_Data;
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


    private void adapterData() {
        message_multiAdapter.notifyDataSetChanged();
        getProgressBar().dismiss();
    }

    public void initPayLevel() {
        Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
        if (Multi.isShowDialog)
            alertDialogPay();
        else
            Multi.isShowDialog = false;
    }

    @Override
    public void onRefresh() {
        messageSwipeFresh.setRefreshing(true);
        message_multiAdapter.setEnableLoadMore(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                messageSwipeFresh.setRefreshing(false);
                message_multiAdapter.setEnableLoadMore(true);
            }
        }, delayMillis);
    }

    @Override
    public void onBackPressed() {
        Multi.Moon_LEVE = Multi.Moon_LEVE1;
        mContext.finish();
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
        MobclickAgent.onPageStart("午夜啪啪二级(有评论)"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("午夜啪啪二级(有评论)"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }
}
