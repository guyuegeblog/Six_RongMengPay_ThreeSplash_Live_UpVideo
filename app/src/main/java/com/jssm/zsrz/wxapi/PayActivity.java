package com.jssm.zsrz.wxapi;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import com.app.Activity.VipInfoActivity;
import com.app.Bean.Multi;
import com.app.Bean.PayType;
import com.app.Constant.Constant;
import com.app.Interface.PayInterface;
import com.app.Model.AppData;
import com.app.Model.PayResultQuery;
import com.app.Net.MobClick;
import com.app.Net.NetInterface;
import com.app.Net.OkHttp;

import com.app.WeiXin.*;
import com.app.WeiXin.Utils;
import com.bumptech.glide.Glide;
import com.rongmeng.pay.RMEvent;
import com.rongmeng.pay.RMPay;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.third.app.R;

import com.app.Save.KeyFile;
import com.app.Save.KeyUser;
import com.app.Tool.AesTool;
import com.app.Tool.ApkTool;
import com.app.Tool.DateTool;
import com.app.Tool.FileTool;
import com.app.Tool.NetTool;
import com.app.Tool.ParamsPutterTool;
import com.app.Tool.PhoneTool;
import com.app.Tool.RandomTool;
import com.app.Tool.ScreenTool;
import com.app.Tool.VipTool;
import com.app.Ui.FullScreenVideo;
import com.app.View.T;
import com.app.ZhiFuBao.Alipay;
import com.app.ZhiFuBao.PayResult;
import com.app.ZhiFuBao.SignUtils;

import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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

public class PayActivity extends AppCompatActivity implements IWXAPIEventHandler, PayInterface, Handler.Callback, Runnable {

    @Bind(R.id.fragment_user_video)
    FullScreenVideo fragmentUserVideo;
    @Bind(R.id.fragment_user_bar)
    ProgressBar fragmentUserBar;
    @Bind(R.id.fragment_user_image)
    ImageView fragmentUserImage;
    @Bind(R.id.wxpay)
    ImageView wxpay;
    @Bind(R.id.zfbpay)
    ImageView zfbpay;
    //    @Bind(R.id.one_month_day_count)
//    TextView oneMonthDayCount;
//    @Bind(R.id.one_month_yuanprice)
//    TextView oneMonthYuanprice;
//    @Bind(R.id.one_month_price)
//    TextView oneMonthPrice;
//    @Bind(R.id.liner_one_month)
//    LinearLayout linerOneMonth;
//    @Bind(R.id.one_year_day_count)
//    TextView oneYearDayCount;
//    @Bind(R.id.one_year_yuanprice)
//    TextView oneYearYuanprice;
//    @Bind(R.id.one_year_price)
//    TextView oneYearPrice;
//    @Bind(R.id.liner_one_year)
//    LinearLayout linerOneYear;
    @Bind(R.id.viptext)
    TextView viptext;
    @Bind(R.id.liner_one_month)
    LinearLayout linerOneMonth;
    @Bind(R.id.vip_description)
    TextView vipDescription;
    @Bind(R.id.pay_type)
    TextView payType;
    @Bind(R.id.liner_one_year)
    LinearLayout linerOneYear;
    @Bind(R.id.back)
    TextView back;
    @Bind(R.id.vip_images)
    ImageView vipImages;
    @Bind(R.id.pay_btn)
    LinearLayout payBtn;
    @Bind(R.id.pay_images)
    ImageView payImages;
    @Bind(R.id.pay_tishi)
    ImageView payTishi;
    @Bind(R.id.pay_tishi_bg)
    LinearLayout payTishiBg;
    @Bind(R.id.threelun_three_month_type)
    TextView threelunThreeMonthType;
    @Bind(R.id.threelun_three_month_price)
    TextView threelunThreeMonthPrice;
    @Bind(R.id.threelun_liner_theemonth)
    LinearLayout threelunLinerTheemonth;
    @Bind(R.id.threelun_year_type)
    TextView threelunYearType;
    @Bind(R.id.threelun_year_price)
    TextView threelunYearPrice;
    @Bind(R.id.threelun_liner_year)
    LinearLayout threelunLinerYear;
    @Bind(R.id.three_liner)
    LinearLayout threeLiner;

    private PayActivity mContext;
    private PayInterface payInterface;
    private String choicePay = "zfb";
    //支付宝
    // 商户PID
    public final String PARTNER = "2088421974805855";
    // 商户收款账号
    public final String SELLER = "2031301903@qq.com";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIkmAk1OM8d4KtItnp6Bpax" +
            "UXwDW4z0b80qRU4zXz/oXQyujThahWxJugpmmkgJMhEfnGWZQ0ocRsP8JAjkAEzDDhAC922JmpbhV/eAGPVFZHXPLP5+5vUV3s6fjy+Q" +
            "1aMdRu1TRszFEjifJ/X34bGzcc96jNG3YTDenhoOFbyuxAgMBAAECgYBuq/pNkZ6dBy5e3qtSM0KS1p63WbCTvntMIAuw6vIMWTs9+pDo" +
            "GBsau4yuGslWC5LvRuCrPOs2TaNrmkRt5M6Wal+NQ/2DYpngs7C7iumYXfePmNUhlmYfooxNy6LC0P4Jq79biQooO0qj72bl/gYEwsgZj" +
            "aV9JorANxVWJNHVQQJBAM3blBfrChnhSNVIBoYWyd7iPQwHJSPi9gr2Y+vZMjB7EPkGZrn0AHUr3hCDq4uQfu1ikT3G1vxU4tGQAH6hfNk" +
            "CQQCqjgKwCt2BZ1t1pbMu8n2L5nIdu0qrbJ64wz83HIz4RBnVXTcgkC+s2d8QgyclvBkb5KA+8cKyR1C41XVexz6ZAkAttyeqMB4sJSWhh" +
            "787I/BsuN1JwIR09cAiKxmBlNhaf9oDE3LYtOEtJDLKhBMiiVJGsW8DwxaXLtq4IzyY4o/xAkAEYpRVHGJnklW6p6D8wwVRVIJ4mzknQSq" +
            "vi22TqCtDstSLNNNXBf4DzrI6hXS7NqPaoL0yARtFZCoCJpLW9f55AkEAreHy+PEzaCTdqsY8UbkDG660sE0vQI0mIdZL4mfG3DD6inJoDl" +
            "2t+CPYCJFgHZUJPflEb2sOhk6RMvkNFmcGcw==";
    // 支付宝公钥
    public final String rsa_public = "";
    private String Zfb_out_trade_no = "";//支付宝订单号
    private String WX_out_trade_no = "";//微信订单号
    private final int SDK_PAY_FLAG = 1;
    //微信
    private IWXAPI api;
    private String appId = null;
    //  private String vipHalfYearPrice = "48";
//    private String vipYearPrice = "365";
    //  private String vipOneMonthPrice = "66";
//    private String vipThreeMonthPrice = "120";
    private String vipSilverPrice = PayType.silver_Price;//白银价格
    private String vipGoldPrice = PayType.gold_Price;//黄金价格
    private String vipPlatNiumPrice = PayType.platMinum_Price;//白金价格
    private String vipDiamondPrice = PayType.diamond_Price;//钻石价格
    private String vipRedDiamondPrice = PayType.red_Diamond_Price;//粉钻价格
    private String vipCrownPrice = PayType.crown_Price;//皇冠价格

    private String vip_Three_Lun_ThreeMonthPrice = "150";//3轮三个月的价格
    private String vip_Three_Lun_YearPrice = "365";//3轮一年的价格

    //  private String vipHalfYearType = "半年";
    private String vipYearType = "全年";
    //  private String vipOneMonthType = "一月";
    private String vipThreeMonthType = "三月";
    private String vipOneMonthType = "一月";

    //  public String body_HalfYear = "午夜啪啪半年会员";
    public String body_year = "涩爱影视VIP会员";
    public String body_threeMonth = "涩爱影视VIP会员";
    //  public String body_oneMonth = "午夜啪啪一个月会员";
    public String body_oneMonth = "涩爱影视VIP会员";
    public String body = body_oneMonth;
    public String VIP_TIME = vipOneMonthType;
    public String pay_Price = vipGoldPrice;
    public Dialog dialog, dialogPay;

    // private final int GO_ZFB_QUERY_TRADLE_FAILED = 1000;
    // private final int GO_QUERY_ZFB_TRADLE_SUCCES = 1021;
    private final int GO_QUERY_ZFB_PAY_SUCCES = 1023;
    private final int GO_QUERY_ZFB_PAY_FAILED = 1024;

    private final int GO_PAY_SYSTEM_EXCEPTION = 1025;

    //  private final int GO_WX_QUERY_TRADLE_FAILED = 1020;
    // private final int GO_QUERY_WX_TRADLE_SUCCES = 1022;
    private final int GO_REQUEST_WEIXIN_FINISH = 1122;
    private final int GO_REQUEST_WEIXIN_FAILED = 1123;
    private final int GO_TIMER_PAY_REQUEST = 1129;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
//                case GO_WX_QUERY_TRADLE_FAILED:
//                    T.showTextToast(mContext, "系统繁忙!!请稍候!!");
//                    break;
//                case GO_QUERY_WX_TRADLE_SUCCES:
//
//                    break;
//                case GO_ZFB_QUERY_TRADLE_FAILED:
//                    T.showTextToast(mContext, "支付宝订单查询失败!!");
//                    break;
//                case GO_QUERY_ZFB_TRADLE_SUCCES:
//                    query_Zfb_PayResesult();
//                    break;
                case GO_QUERY_ZFB_PAY_SUCCES:
                    disMissDialog();
                    destroyTimer();
                    dialog_pay_succes();
                    break;
                case GO_QUERY_ZFB_PAY_FAILED:
//                  T.showTextToast(mContext, "支付失败!!请联系客服解决!!");
//                  payFailedDialog();
                    disMissDialog();
                    sendPayRequest();
                    break;
                case GO_PAY_SYSTEM_EXCEPTION:
                    T.showTextToast(mContext, "支付系统异常!!请联系客服解决!!");
                    break;
                case GO_REQUEST_WEIXIN_FINISH:
                    sendReg();
                    break;
                case GO_REQUEST_WEIXIN_FAILED:
                    T.showTextToast(mContext, "请求微信失败!!");
                    dialog.dismiss();
                    break;
                case GO_TIMER_PAY_REQUEST:
                    //定时验证用户支付
                    disMissDialog();
                    T.showTextCenterToast(mContext, "正在验证用户您的支付状态,请停留等待!!");
                    query_Zfb_PayResesult();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxpay_entry);
        ButterKnife.bind(this);
        mContext = this;
        payInterface = this;
//        api = WXAPIFactory.createWXAPI(mContext, Constant.APP_ID);
//        api.registerApp(Constant.APP_ID);
//        api.handleIntent(mContext.getIntent(), this);
        initWx();
        initView();
        initData();
        initPlayer();
        initDialogPay();
    }

    private void initWx() {
//        CNPay.getInstance().Init(this, "50054", "1180", "d9c200b3e30b40a2b83338c7d0179f3b", null); // CNPay.getInstance().SetNotice(notice_url);// 同步回调地址，可选
//        CNPay.getInstance().SetNotice(NetInterface.ZHONGQIN_NOTIFY_WEIXIN);// 同步回调地址，可选
    }

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }

    private void initView() {
        ScreenTool.setLight(mContext, 255);
        if (dialog == null) {
            dialog = createLoadingDialog(this, "正在初始化支付环境请稍候...");
        }
        if (dialogPay == null) {
            dialogPay = createLoadingDialog(this, "正在验证您的支付状态请稍候...");
        }
    }

    public Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.progressdialog_no_deal, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.anim);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

        loadingDialog.setCancelable(false);// 可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        loadingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                } else {
                    return true;
                }
            }
        });
        return loadingDialog;
    }

    private void initData() {
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_NOT_VIP_TYPE) {
            //白银
            payTishiBg.setVisibility(View.VISIBLE);
            vipImages.setImageResource(R.drawable.silver_bg);
            payImages.setImageResource(R.drawable.silver_button);
            vipDescription.setTextColor(mContext.getResources().getColor(R.color.jin));
            vipDescription.setText("充值成为白银会员可获得所有专区观看权");
            if (Constant.payPrice == null) {
                //没有获取到价格数据(使用默认)
                pay_Price = vipSilverPrice;
                if (choicePay.equals("zfb")) {
                    int oneMonth = Integer.parseInt(vipSilverPrice) - 10;
                    payType.setText("白银会员" + oneMonth + "元");
                } else {
                    int oneMonth = Integer.parseInt(vipSilverPrice);
                    payType.setText("白银会员" + oneMonth + "元");
                }
            } else {
                //获取到了服务器的价格数据(从低到高);
//                vipSilverPrice = Constant.payPrice.getOnemonthprice();
//                pay_Price = vipSilverPrice;
//                if (choicePay.equals("zfb")) {
//                    int oneMonth = Integer.parseInt(vipSilverPrice) - 5;
//                    payType.setText("白银会员" + oneMonth + "元");
//                } else {
//                    int oneMonth = Integer.parseInt(vipSilverPrice);
//                    payType.setText("白银会员" + oneMonth + "元");
//                }
            }
        } else if (vipType == Multi.VIP_SILVER_TYPE) {
            //黄金
            payTishiBg.setVisibility(View.GONE);
            vipImages.setImageResource(R.drawable.huangjin);
            payImages.setImageResource(R.drawable.huangjin_button);
            vipDescription.setTextColor(mContext.getResources().getColor(R.color.jin));
            vipDescription.setText("充值成为黄金会员可获得所有专区观看权");
            if (Constant.payPrice == null) {
                //没有获取到价格数据(使用默认)
                pay_Price = vipGoldPrice;
                if (choicePay.equals("zfb")) {
                    int oneMonth = Integer.parseInt(vipGoldPrice) - 5;
                    payType.setText("黄金会员" + oneMonth + "元");
                } else {
                    int oneMonth = Integer.parseInt(vipGoldPrice);
                    payType.setText("黄金会员" + oneMonth + "元");
                }
            } else {
                //获取到了服务器的价格数据(从低到高);
//                vipGoldPrice = Constant.payPrice.getThreemonthprice();
//                pay_Price = vipGoldPrice;
//                if (choicePay.equals("zfb")) {
//                    int oneMonth = Integer.parseInt(vipGoldPrice);
//                    payType.setText("黄金会员" + oneMonth + "元");
//                } else {
//                    int oneMonth = Integer.parseInt(vipGoldPrice);
//                    payType.setText("黄金会员" + oneMonth + "元");
//                }
            }
        } else if (vipType == Multi.VIP_GOLD_TYPE) {
            //白金
            payTishiBg.setVisibility(View.GONE);
            vipImages.setImageResource(R.drawable.baijin);
            payImages.setImageResource(R.drawable.baijin_button);
            vipDescription.setTextColor(mContext.getResources().getColor(R.color.jin));
            vipDescription.setText("充值成为白金会员可获得所有专区观看权");
            if (Constant.payPrice == null) {
                //没有获取到价格数据(使用默认)
                pay_Price = vipPlatNiumPrice;
                if (choicePay.equals("zfb")) {
                    int oneMonth = Integer.parseInt(vipPlatNiumPrice) - 5;
                    payType.setText("白金会员" + oneMonth + "元");
                } else {
                    int oneMonth = Integer.parseInt(vipPlatNiumPrice);
                    payType.setText("白金会员" + oneMonth + "元");
                }
            } else {
                //获取到了服务器的价格数据(从低到高);
//                vipPlatNiumPrice = Constant.payPrice.getHalfyearprice();
//                pay_Price = vipPlatNiumPrice;
//                if (choicePay.equals("zfb")) {
//                    int oneMonth = Integer.parseInt(vipPlatNiumPrice);
//                    payType.setText("白金会员" + oneMonth + "元");
//                } else {
//                    int oneMonth = Integer.parseInt(vipPlatNiumPrice);
//                    payType.setText("白金会员" + oneMonth + "元");
//                }
            }
        } else if (vipType == Multi.VIP_PLAT_NIUM_TYPE) {
            //钻石
            payTishiBg.setVisibility(View.GONE);
            vipImages.setImageResource(R.drawable.zuanshi);
            payImages.setImageResource(R.drawable.zuanshi_button);
            vipDescription.setTextColor(mContext.getResources().getColor(R.color.zuan));
            vipDescription.setText("充值成为钻石会员可获得所有专区观看权");
            if (Constant.payPrice == null) {
                //没有获取到价格数据(使用默认)
                pay_Price = vipDiamondPrice;
                if (choicePay.equals("zfb")) {
                    int oneMonth = Integer.parseInt(vipDiamondPrice) - 5;
                    payType.setText("钻石会员" + oneMonth + "元");
                } else {
                    int oneMonth = Integer.parseInt(vipDiamondPrice);
                    payType.setText("钻石会员" + oneMonth + "元");
                }
            } else {
                //获取到了服务器的价格数据(从低到高);
//                vipDiamondPrice = Constant.payPrice.getOneyearprice();
//                pay_Price = vipDiamondPrice;
//                if (choicePay.equals("zfb")) {
//                    int oneMonth = Integer.parseInt(vipDiamondPrice);
//                    payType.setText("钻石会员" + oneMonth + "元");
//                } else {
//                    int oneMonth = Integer.parseInt(vipDiamondPrice);
//                    payType.setText("钻石会员" + oneMonth + "元");
//                }
            }
        } else if (vipType == Multi.VIP_DIAMOND_TYPE) {
            //粉钻
            payTishiBg.setVisibility(View.GONE);
            vipImages.setImageResource(R.drawable.red_diamond);
            payImages.setImageResource(R.drawable.red_diamond_button);
            vipDescription.setTextColor(mContext.getResources().getColor(R.color.zuan));
            vipDescription.setText("充值成为粉钻会员可获得所有专区观看权");
            if (Constant.payPrice == null) {
                //没有获取到价格数据(使用默认)
                pay_Price = vipRedDiamondPrice;
                if (choicePay.equals("zfb")) {
                    int oneMonth = Integer.parseInt(vipRedDiamondPrice) - 5;
                    payType.setText("粉钻会员" + oneMonth + "元");
                } else {
                    int oneMonth = Integer.parseInt(vipRedDiamondPrice);
                    payType.setText("粉钻会员" + oneMonth + "元");
                }
            } else {
                //获取到了服务器的价格数据(从低到高);
//                vipDiamondPrice = Constant.payPrice.getOneyearprice();
//                pay_Price = vipDiamondPrice;
//                if (choicePay.equals("zfb")) {
//                    int oneMonth = Integer.parseInt(vipDiamondPrice);
//                    payType.setText("钻石会员" + oneMonth + "元");
//                } else {
//                    int oneMonth = Integer.parseInt(vipDiamondPrice);
//                    payType.setText("钻石会员" + oneMonth + "元");
//                }
            }
        } else if (vipType == Multi.VIP_RED_DIAMOND_TYPE) {
            //皇冠
            payTishiBg.setVisibility(View.GONE);
            vipImages.setImageResource(R.drawable.crown);
            payImages.setImageResource(R.drawable.crown_button);
            vipDescription.setTextColor(mContext.getResources().getColor(R.color.zuan));
            vipDescription.setText("充值成为皇冠会员可获得所有专区观看权");
            if (Constant.payPrice == null) {
                //没有获取到价格数据(使用默认)
                pay_Price = vipCrownPrice;
                if (choicePay.equals("zfb")) {
                    int oneMonth = Integer.parseInt(vipCrownPrice) - 5;
                    payType.setText("皇冠会员" + oneMonth + "元");
                } else {
                    int oneMonth = Integer.parseInt(vipCrownPrice);
                    payType.setText("皇冠会员" + oneMonth + "元");
                }
            } else {
                //获取到了服务器的价格数据(从低到高);
//                vipDiamondPrice = Constant.payPrice.getOneyearprice();
//                pay_Price = vipDiamondPrice;
//                if (choicePay.equals("zfb")) {
//                    int oneMonth = Integer.parseInt(vipDiamondPrice);
//                    payType.setText("钻石会员" + oneMonth + "元");
//                } else {
//                    int oneMonth = Integer.parseInt(vipDiamondPrice);
//                    payType.setText("钻石会员" + oneMonth + "元");
//                }
            }
        } else if (vipType == Multi.VIP_CROWN_TYPE) {
            //给全年和三个月
//            linerOneMonth.setVisibility(View.GONE);
//            linerOneYear.setVisibility(View.GONE);
//            threeLiner.setVisibility(View.VISIBLE);
//
//            payTishiBg.setVisibility(View.GONE);
//            vipImages.setImageResource(R.drawable.zuanshi);
//            payImages.setImageResource(R.drawable.zuanshi_button);
//            vipDescription.setTextColor(mContext.getResources().getColor(R.color.zuan));
//            vipDescription.setText("充值成为皇冠会员可获得所有专区观看权");
//            if (Constant.payPrice == null) {
//                //没有获取到价格数据(使用默认)
//                pay_Price = vip_Three_Lun_ThreeMonthPrice;
//                if (choicePay.equals("zfb")) {
////                    int oneMonth = Integer.parseInt(vipDiamondPrice);
////                    payType.setText("钻石会员" + oneMonth + "元");
//                    threelunThreeMonthPrice.setText("皇冠会员" + vip_Three_Lun_ThreeMonthPrice + "元");
//                    threelunYearPrice.setText("皇冠会员" + vip_Three_Lun_YearPrice + "元");
//                } else {
////                    int oneMonth = Integer.parseInt(vipDiamondPrice);
////                    payType.setText("钻石会员" + oneMonth + "元");
//                    threelunThreeMonthPrice.setText("皇冠会员" + vip_Three_Lun_ThreeMonthPrice + "元");
//                    threelunYearPrice.setText("皇冠会员" + vip_Three_Lun_YearPrice + "元");
//                }
//            } else {
//                //获取到了服务器的价格数据(从低到高);
////                pay_Price = vip_Three_Lun_ThreeMonthPrice;
////                if (choicePay.equals("zfb")) {
//////                    int oneMonth = Integer.parseInt(vipDiamondPrice);
//////                    payType.setText("钻石会员" + oneMonth + "元");
////                    threelunThreeMonthPrice.setText("钻石会员" + vip_Three_Lun_ThreeMonthPrice + "元");
////                    threelunYearPrice.setText("钻石会员" + vip_Three_Lun_YearPrice + "元");
////                } else {
//////                    int oneMonth = Integer.parseInt(vipDiamondPrice);
//////                    payType.setText("钻石会员" + oneMonth + "元");
////                    threelunThreeMonthPrice.setText("钻石会员" + vip_Three_Lun_ThreeMonthPrice + "元");
////                    threelunYearPrice.setText("钻石会员" + vip_Three_Lun_YearPrice + "元");
////                }
//            }
            payTishiBg.setVisibility(View.GONE);
            vipImages.setImageResource(R.drawable.crown);
            payImages.setImageResource(R.drawable.crown_button);
            vipDescription.setTextColor(mContext.getResources().getColor(R.color.zuan));
            vipDescription.setText("充值成为皇冠会员可获得所有专区观看权");
            if (Constant.payPrice == null) {
                //没有获取到价格数据(使用默认)
                pay_Price = vipCrownPrice;
                if (choicePay.equals("zfb")) {
                    int oneMonth = Integer.parseInt(vipCrownPrice) - 5;
                    payType.setText("皇冠会员" + oneMonth + "元");
                } else {
                    int oneMonth = Integer.parseInt(vipCrownPrice);
                    payType.setText("皇冠会员" + oneMonth + "元");
                }
            } else {
                //获取到了服务器的价格数据(从低到高);
//                vipDiamondPrice = Constant.payPrice.getOneyearprice();
//                pay_Price = vipDiamondPrice;
//                if (choicePay.equals("zfb")) {
//                    int oneMonth = Integer.parseInt(vipDiamondPrice);
//                    payType.setText("钻石会员" + oneMonth + "元");
//                } else {
//                    int oneMonth = Integer.parseInt(vipDiamondPrice);
//                    payType.setText("钻石会员" + oneMonth + "元");
//                }
            }
        }
    }

    boolean isDialaogPay = false;

    private void initDialogPay() {
        //退出弹窗支付
        String payMethod = getIntent().getStringExtra("payMethod");
        String payPrice = getIntent().getStringExtra("payprice");
        String payTime = getIntent().getStringExtra("payTime");
        //进入弹窗支付
        String diaologPay = getIntent().getStringExtra("diaologPay");
        if (!TextUtils.isEmpty(diaologPay)) {
            if (diaologPay.equals("wx")) {
                initWeiXinPay();
                initPayBtn();
            } else if (diaologPay.equals("zfb")) {
                initZfbPay();
                initPayBtn();
            }
        }

        if (!TextUtils.isEmpty(payMethod)) {
            isDialaogPay = true;
            Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
            if (payMethod.equals("wx")) {
                choicePay = "wx";
                VIP_TIME = payTime;
                if (VIP_TIME.equals("一月")) {
                    body = body_oneMonth;
                }
                pay_Price = payPrice;
                payInterface.payClick();
            } else if (payMethod.equals("zfb")) {
                choicePay = "zfb";
                VIP_TIME = payTime;
                if (VIP_TIME.equals("一月")) {
                    body = body_oneMonth;
                }
                pay_Price = payPrice;
                payInterface.payClick();
            }
        }
    }

    private void initZfb() {
        try {
            Alipay alipayInfo = new Alipay();
            //支付宝初始化是否成功
            //获得支付宝支付参数
            alipayInfo.partner = PARTNER;
            alipayInfo.seller = SELLER;
            alipayInfo.rsa_private = RSA_PRIVATE;
            alipayInfo.notify_url = NetInterface.USER_NOTIFY_ZFB;
            //RSA_PUBLIC = alipayInfo.getRsa_public();
            startZFBPayHandler.sendEmptyMessage(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ReqEntity reqEntity;

    private void initWeiXin() {
        try {
//            dialog.show();
//            final String appid = Constant.APP_ID;   //应用ID
//            final String mch_id = "1436017502";  //商户号
//            final String nonce_str = Convert.getNonceStr(); //随机字符串
//            String sign = "";      //签名
//            String out_trade_no = Utils.getOutTradeNo();  //商户订单号
//            String notify_url = NetInterface.USER_NOTIFY_WEIXIN;    //通知地址
//            String trade_type = "APP";  //交易类型
//            String spbill_create_ip = GetIP.getIpAddr(); //终端IP
//            SortedMap<String, String> packageParams = new TreeMap<String, String>();
//            packageParams.put("appid", appid);
//            packageParams.put("mch_id", mch_id);
//            packageParams.put("nonce_str", nonce_str);
//            packageParams.put("out_trade_no", out_trade_no);
//            packageParams.put("notify_url", notify_url);
//            packageParams.put("trade_type", trade_type);
//            packageParams.put("body", body);
//            String price = String.valueOf((Integer.parseInt(pay_Price) * 100));
//            packageParams.put("total_fee", price);//价格单位是分  //String.valueOf((Integer.parseInt(pay_Price) * 100))
//            packageParams.put("spbill_create_ip", spbill_create_ip);
//
//            //保存订单号
//            WX_out_trade_no = out_trade_no;
//
//            final RequestHandler reqHandler = new RequestHandler();
//            sign = reqHandler.createSign(packageParams);
//            String xml = "<xml>" + "<appid>" + appid + "</appid>" + "<mch_id>"
//                    + mch_id + "</mch_id>" + "<nonce_str>" + nonce_str
//                    + "</nonce_str>" + "<sign>" + sign + "</sign>"
//                    + "<body><![CDATA[" + body + "]]></body>" + "<out_trade_no>"
//                    + out_trade_no + "</out_trade_no>" + "<total_fee>" + price + "</total_fee>"
//                    + "<spbill_create_ip>" + spbill_create_ip
//                    + "</spbill_create_ip>" + "<notify_url>" + notify_url
//                    + "</notify_url>" + "<trade_type>" + trade_type
//                    + "</trade_type>" + "</xml>";
//
//            String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
//            //获取预支付会话ID
////            String prepay_id = new GetWxOrderno().getPayNo(createOrderURL, xml,mContext);
//            String prepay_id = "";
//            RequestBody body = RequestBody.create(MediaType.parse("text/xml;charset=UTF-8"), xml);
//            Request request = new Request.Builder().url(createOrderURL)
//                    .post(body).build();
//            Call call = new OkHttpClient().newCall(request);
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    mHandler.sendEmptyMessage(GO_REQUEST_WEIXIN_FAILED);
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    try {
//                        String prepay_id = "";
//                        String jsonStr = response.body().string();
//                        Log.d("zgx", "response=====" + jsonStr);
//                        if (jsonStr.indexOf("FAIL") != -1) {
//                        }
//                        Map map = null;
//                        try {
//                            map = new GetWxOrderno().doXMLParse(jsonStr);
//                        } catch (Exception e) {
//                            mHandler.sendEmptyMessage(GO_REQUEST_WEIXIN_FAILED);
//                        }
//                        prepay_id = (String) map.get("prepay_id");
//                        if (!TextUtils.isEmpty(prepay_id)) {
//                            SortedMap<String, String> packageParam = new TreeMap<String, String>();
//                            String timestamp = Convert.getTimeStamp();
//                            packageParam.put("appid", appid);
//                            packageParam.put("partnerid", mch_id);
//                            packageParam.put("prepayid", prepay_id);
//                            packageParam.put("noncestr", nonce_str);
//                            packageParam.put("timestamp", timestamp);
//                            packageParam.put("package", "Sign=WXPay");
//                            String sign_two = reqHandler.createSign(packageParam);
//                            reqEntity = new ReqEntity();
//                            reqEntity.setAppid(appid);
//                            reqEntity.setPartnerid(mch_id);
//                            reqEntity.setPrepayid(prepay_id);
//                            reqEntity.setNoncestr(nonce_str);
//                            reqEntity.setTimestamp(timestamp);
//                            reqEntity.setPackages("Sign=WXPay");
//                            reqEntity.setSign_two(sign_two);
//                            mHandler.sendEmptyMessage(GO_REQUEST_WEIXIN_FINISH);
//                        } else {
//                            mHandler.sendEmptyMessage(GO_REQUEST_WEIXIN_FAILED);
//                        }
//                    } catch (Exception e) {
//                        mHandler.sendEmptyMessage(GO_REQUEST_WEIXIN_FAILED);
//                    }
//                }
//            });
            //保存订单号
            WX_out_trade_no = Utils.getOutTradeNo();
            WX_out_trade_no = "9" + WX_out_trade_no;
            String areaChannel = ApkTool.getAppChannels(mContext, AppData.UMENG_APP_CHANNEL);
            int price = Integer.parseInt(pay_Price) * 100;
            RMPay.getInstance().pay(mContext, String.valueOf(price), body, body, WX_out_trade_no, areaChannel,
                    "http:\\www.365you.com/paycallback", new RMEvent() {
                        @Override
                        public void on_Result(int paramInt, String paramString) {
                            if (paramInt == 0) {// 支付成功
                                Toast.makeText(mContext, paramString, Toast.LENGTH_LONG).show();
                                query_Zfb_PayResesult();
                            } else if (paramInt == -2) {// 支付取消
                                Toast.makeText(mContext, paramString, Toast.LENGTH_LONG).show();
                                Multi.Moon_LEVE = Multi.Moon_LEVE2;
                                Multi.LIVE_UNBIND = true;
                                alertIntentLive();
                            } else {// 支付失败
                                Toast.makeText(mContext, paramString, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (Exception e) {
            mHandler.sendEmptyMessage(GO_REQUEST_WEIXIN_FAILED);
            Log.i("exce", e.getMessage());
        }
    }

    private void sendReg() {
//        boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
//        if (isPaySupported) {
//            //微信版本支持支付
//            PayReq req = new PayReq();
//            //支付调起参数
//            req.appId = reqEntity.getAppid();
//            req.partnerId = reqEntity.getPartnerid();
//            req.prepayId = reqEntity.getPrepayid();
//            req.nonceStr = reqEntity.getNoncestr();
//            req.timeStamp = reqEntity.getTimestamp();
//            req.packageValue = reqEntity.getPackages();
//            req.sign = reqEntity.getSign_two();
//            req.extData = "app data"; // optional
//            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
//            api.sendReq(req);
//        } else {
//            //微信版本不支持支付
//            T.showTextCenterToast(mContext, "您还没有使用微信应用,或者您的微信版本不支持支付,请下载微信最新版本");
//        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
//        api.handleIntent(intent, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
//
//        Toast.makeText(getApplicationContext(), "pay_result-->" + data.getStringExtra("pay_result"), 0).show();
    }

    @Override
    public void onReq(BaseReq baseResp) {
//        String result = "无返回";
//        switch (baseResp.errCode) {
//            case BaseResp.ErrCode.ERR_OK:
//                result = "发送成功";
//                query_Zfb_PayResesult();
//                break;
//            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                result = "发送取消";
//                T.showTextCenterToast(mContext, "您取消了支付!!");
//                Multi.Moon_LEVE = Multi.Moon_LEVE2;
//                Multi.LIVE_UNBIND = true;
//                alertIntentLive();
//                break;
//            case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                result = "发送被拒绝";
//                break;
//            default:
//                result = "请您登陆微信客户端再进行支付!!";
//                break;
//        }
    }

    @Override
    public void onResp(BaseResp baseResp) {

    }


    private void initPlayer() {
        Glide.with(mContext).
                load(NetInterface.LIVE_TAN_IMAGE_ADDRESS).into(fragmentUserImage);
        fragmentUserBar.setVisibility(View.VISIBLE);
        fragmentUserImage.setVisibility(View.VISIBLE);
        if (!NetTool.isConnected(mContext)) {
            return;
        }
        fragmentUserVideo.setVideoURI(Uri.parse(getVideoUrl()));
        fragmentUserVideo.start();
        fragmentUserVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Glide.with(mContext).
                        load(NetInterface.LIVE_TAN_IMAGE_ADDRESS).into(fragmentUserImage);
                fragmentUserImage.setVisibility(View.VISIBLE);
                fragmentUserBar.setVisibility(View.VISIBLE);
                initPlayer();
            }
        });
        fragmentUserVideo.setPlayPauseListener(new FullScreenVideo.PlayPauseListener() {
            @Override
            public void onPlay() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragmentUserImage.setVisibility(View.GONE);
                        fragmentUserBar.setVisibility(View.GONE);
                    }
                }, 2000);
            }

            @Override
            public void onPause() {
                fragmentUserImage.setVisibility(View.VISIBLE);
                fragmentUserBar.setVisibility(View.VISIBLE);
                initPlayer();
            }
        });
    }

//    private String getUrl() {
//        Random random = new Random();
//        int i = random.nextInt(2);
//        if (i == 0) {
//            return "http://www.kmxinyuemei.com/vipvideo/vip5-mp4";
//        } else {
//            return "http://www.kmxinyuemei.com/vipvideo/vip4-mp4";
//        }
//    }

    Handler startZFBPayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 10) {
                //支付参数确定
                if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(SELLER)) {
                    new AlertDialog.Builder(mContext).setTitle("警告").setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    dialoginterface.dismiss();
                                }
                            }).show();
                    return;
                }
                String bodyTitle = null;
                String payMoney = "";//
//                if (VIP_TIME.equals(vipYearType)) {
//                    if (isDialaogPay) {
//                        payMoney = (Integer.parseInt(pay_Price) - 2) + "";//支付金额
//                    } else {
//                        payMoney = (Integer.parseInt(pay_Price) - 10) + "";//支付金额
//                    }
//                    bodyTitle = body;
//                }
//                else if (VIP_TIME.equals(vipSixType)) {
//                    //payMoney = "99";//支付金额
//                    payMoney = vipSixPrice;//支付金额
//                    bodyTitle = body_six;
//                }
//                else if (VIP_TIME.equals(vipHalfYearType)) {
//                    //支付金额
//                    if (isDialaogPay) {
//                        payMoney = (Integer.parseInt(pay_Price) - 2) + "";//支付金额
//                    } else {
//                        payMoney = (Integer.parseInt(pay_Price) - 10) + "";//支付金额
//                    }
//                    bodyTitle = body;
//                }

                if (VIP_TIME.equals(vipOneMonthType)) {
                    //支付金额
                    if (isDialaogPay) {
                        payMoney = PayType.exit_Dialog_ZFB;//支付金额
                    } else {
                        if (VipTool.getUserVipType(mContext) == Multi.VIP_NOT_VIP_TYPE) {
                            payMoney = (Integer.parseInt(pay_Price) - 10) + "";//支付金额
                        } else {
                            payMoney = String.valueOf(Integer.parseInt(pay_Price) - 5);
                        }
                    }
                    bodyTitle = body;
                } else if (VIP_TIME.equals(vipThreeMonthType)) {
                    //支付金额
                    payMoney = pay_Price;
                    bodyTitle = body;
                } else if (VIP_TIME.equals(vipYearType)) {
                    //支付金额
                    payMoney = pay_Price;
                    bodyTitle = body;
                }
                String orderInfo = getOrderInfo(URLEncoder.encode(bodyTitle), URLEncoder.encode(body), payMoney);

//                //保存支付宝支付信息，应对支付失败的情况（重要）,代码之所以在这里，是因为订单号在这里才进行生成。
//                AplipayYZ aplipayYZ = new AplipayYZ();
//                aplipayYZ.setOut_trade_no(Zfb_out_trade_no);
//                aplipayYZ.setUserName(aesUtils.getInstance().decrypt(userName));
//                imeilLastId = aesUtils.getInstance().decrypt(userName).trim().substring(aesUtils.getInstance().decrypt(userName).length() - 1);
//                aplipayYZ.setImeiLastId(imeilLastId);
//                aplipayYZ.setPayTime(URLEncoder.encode(VIP_TIME));
//                final String zfbJson = JSON.toJSONString(aplipayYZ);
//                String aesJson = aesUtils.encrypt(zfbJson);
//                util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.ZHI_FU_BAO_USER_PAY_FAILED_FILE, pay_key, aesJson);

                /**
                 * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
                 */
                String sign = sign(orderInfo);
                try {
                    /**
                     * 仅需对sign 做URL编码
                     */
                    sign = URLEncoder.encode(sign, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                /**
                 * 完整的符合支付宝参数规范的订单信息
                 */
                final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

                Runnable payRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // 构造PayTask 对象
                        PayTask alipay = new PayTask(mContext);
                        // 调用支付接口，获取支付结果
                        String result = alipay.pay(payInfo, true);

                        Message msg = new Message();
                        msg.what = SDK_PAY_FLAG;
                        msg.obj = result;
                        zfbHandler.sendMessage(msg);
                    }
                };

                // 必须异步调用
                Thread payThread = new Thread(payRunnable);
                payThread.start();
            }
            super.handleMessage(msg);
        }
    };

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(mContext);
        String version = payTask.getVersion();
        Toast.makeText(mContext, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * create the order info. 创建订单信息
     */
    private String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        Zfb_out_trade_no = getOutTradeNo();
        orderInfo += "&out_trade_no=" + "\"" + Zfb_out_trade_no + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + URLDecoder.decode(subject) + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + URLDecoder.decode(body) + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + NetInterface.USER_NOTIFY_ZFB + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }


    //支付宝后台验证
    @SuppressLint("HandlerLeak")
    private Handler zfbHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    final String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        //后台验证支付宝支付是否成功
                        //支付宝后台验证
                        query_Zfb_PayResesult();
                    } else {
                        //支付宝支付失败
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(mContext, "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(mContext, "您取消了支付", Toast.LENGTH_SHORT).show();
                            //删除支付宝应对支付失败的记录
//                          util.sharedPreferencesDelByFileAllData(WXPayEntryActivity.this, KeyFile.ZHI_FU_BAO_USER_PAY_FAILED_FILE);
                            Multi.Moon_LEVE = Multi.Moon_LEVE2;
                            Multi.LIVE_UNBIND = true;
                            alertIntentLive();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    //支付宝后台查询结果(v1验证)
    private boolean isWrite = true;
    private boolean isSendPayRequest = true;

    private void query_Zfb_PayResesult() {
        if (!NetTool.isConnected(mContext)) {
            T.showTextCenterToast(mContext, "您的网络没有连接，请检查您的网络");
            return;
        }
        if (dialogPay == null) {
            dialogPay = createLoadingDialog(this, "正在验证您的支付状态请稍候...");
        }
        dialogPay.show();
        if (isSendPayRequest) {
            isSendPayRequest = false;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isSendPayRequest = true;
                }
            }, 2000);
        } else {
            //停止执行
            dialogPay.dismiss();
            return;
        }
        PayResultQuery resultQuery = new PayResultQuery();
        String userName2;
        String price = null;
        String count = null;
        String random = VipTool.getUserRandom(mContext);
        if (TextUtils.isEmpty(random)) {
            //防止本地数据库被删除
            userName2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.USER_NAME_KEY));
        } else {
            userName2 = random + "" + PhoneTool.getPhoneTool(mContext).getAndroidId(mContext);
        }
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_NOT_VIP_TYPE) {
            if (isDialaogPay) {
                if (choicePay.equals("wx")) {
                    price = PayType.exit_Dialog_WX;
                } else if (choicePay.equals("zfb")) {
                    String bPrice = PayType.exit_Dialog_ZFB;
                    price = bPrice + "";
                }
            } else {
                if (choicePay.equals("wx")) {
                    price = vipSilverPrice;
                } else if (choicePay.equals("zfb")) {
                    if (VipTool.getUserVipType(mContext) == Multi.VIP_NOT_VIP_TYPE) {
                        int bPrice = Integer.parseInt(vipSilverPrice) - 10;
                        price = bPrice + "";
                    } else {
                        price = String.valueOf(Integer.parseInt(vipSilverPrice) - 5);
                    }
                }
            }
        } else if (vipType == Multi.VIP_SILVER_TYPE) {
            if (choicePay.equals("wx")) {
                price = vipGoldPrice;
            } else if (choicePay.equals("zfb")) {
                price = String.valueOf(Integer.parseInt(vipGoldPrice) - 5);
            }
        } else if (vipType == Multi.VIP_GOLD_TYPE) {
            if (choicePay.equals("wx")) {
                price = vipPlatNiumPrice;
            } else if (choicePay.equals("zfb")) {
                price = String.valueOf(Integer.parseInt(vipPlatNiumPrice) - 5);
            }
        } else if (vipType == Multi.VIP_PLAT_NIUM_TYPE) {
            if (choicePay.equals("wx")) {
                price = vipDiamondPrice;
            } else if (choicePay.equals("zfb")) {
                price = String.valueOf(Integer.parseInt(vipDiamondPrice) - 5);
            }
        } else if (vipType == Multi.VIP_DIAMOND_TYPE) {
            if (choicePay.equals("wx")) {
                price = vipRedDiamondPrice;
            } else if (choicePay.equals("zfb")) {
                price = String.valueOf(Integer.parseInt(vipRedDiamondPrice) - 5);
            }
        } else if (vipType == Multi.VIP_RED_DIAMOND_TYPE) {
            if (choicePay.equals("wx")) {
                price = vipCrownPrice;
            } else if (choicePay.equals("zfb")) {
                price = String.valueOf(Integer.parseInt(vipCrownPrice) - 5);
            }
        } else if (vipType == Multi.VIP_CROWN_TYPE) {
//            if (VIP_TIME.equals(vipThreeMonthType)) {
//                price = vip_Three_Lun_ThreeMonthPrice;
//            } else if (VIP_TIME.equals(vipYearType)) {
//                price = vip_Three_Lun_YearPrice;
//            }
            if (choicePay.equals("wx")) {
                price = vipCrownPrice;
            } else if (choicePay.equals("zfb")) {
                price = String.valueOf(Integer.parseInt(vipCrownPrice) - 5);
            }
        }

        if (VIP_TIME.equals(vipOneMonthType)) {
            //一个月
            count = "1";
        }
        if (vipType == Multi.VIP_RED_DIAMOND_TYPE || vipType == Multi.VIP_CROWN_TYPE) {
            count = "1";
        }
        if (VIP_TIME.equals(vipThreeMonthType)) {
            //三个月
            count = "3";
        }

        if (VIP_TIME.equals(vipYearType)) {
            //全年
            count = "12";
        }
        resultQuery.setUsername(AesTool.encrypt(userName2));
        resultQuery.setCount(AesTool.encrypt(count));
        resultQuery.setMoney(AesTool.encrypt(price));
        if (choicePay.equals("wx"))

        {
            resultQuery.setType(AesTool.encrypt(String.valueOf(PayType.PAY_WEIXIN)));
            resultQuery.setOut_trade_no(AesTool.encrypt(WX_out_trade_no));
            Log.i("tradeOut", "微信订单号" + WX_out_trade_no);
        } else if (choicePay.equals("zfb"))

        {
            resultQuery.setType(AesTool.encrypt(String.valueOf(PayType.PAY_ZFB)));
            resultQuery.setOut_trade_no(AesTool.encrypt(Zfb_out_trade_no));
            Log.i("tradeOut", "支付宝订单号" + Zfb_out_trade_no);
        }
        JSONObject json = new JSONObject();
        try {
            json.put("username", resultQuery.getUsername());
            json.put("money", resultQuery.getMoney());
            json.put("type", resultQuery.getType());
            json.put("count", resultQuery.getCount());
            json.put("out_trade_no", resultQuery.getOut_trade_no());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String aesJson = json.toString();
        OkHttpClient mOkHttpClient = OkHttp.getInstance_Pay();
        final RequestBody formBody = new FormBody.Builder()
                .add(NetInterface.REQUEST_HEADER, aesJson)
                .build();
        Request request = null;
        if (vipType == Multi.VIP_RED_DIAMOND_TYPE || vipType == Multi.VIP_CROWN_TYPE || VIP_TIME.equals(vipYearType) || VIP_TIME.equals(vipThreeMonthType)) {
            //v2
            request = new Request.Builder()
                    .url(NetInterface.USER_QUERY_PAY_RESULT_VIP2)
                    .post(formBody)
                    .build();
        } else {
            //v1
            request = new Request.Builder()
                    .url(NetInterface.USER_QUERY_PAY_RESULT_VIP1)
                    .post(formBody)
                    .build();
        }
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
                         @Override
                         public void onFailure(Call call, IOException e) {
                             call.cancel();//失败后取消请求
                             mobclickPayFailed();
                             mHandler.sendEmptyMessage(GO_TIMER_PAY_REQUEST);
                         }

                         @Override
                         public void onResponse(Call call, Response response) throws IOException {
                             try {
                                 call.cancel();//成功后取消请求
                                 org.json.JSONObject jsonObject = new org.json.JSONObject(response.body().string());
                                 int vipType = VipTool.getUserVipType(mContext);
                                 if (vipType == Multi.VIP_NOT_VIP_TYPE ||
                                         vipType == Multi.VIP_GOLD_TYPE ||
                                         vipType == Multi.VIP_SILVER_TYPE ||
                                         vipType == Multi.VIP_DIAMOND_TYPE ||
                                         vipType == Multi.VIP_PLAT_NIUM_TYPE) {
                                     //v1
                                     String code = AesTool.decrypt(jsonObject.getString("code"));
                                     if (code.equals("0")) {
                                         //充值失败(无此用户)
                                         mobclickPayFailed();
//                                         mHandler.sendEmptyMessage(GO_TIMER_PAY_REQUEST);
                                     } else if (code.equals("1")) {
                                         //充值成功
                                         String vip = AesTool.decrypt(jsonObject.getString("vip"));
                                         String vipTime = AesTool.decrypt(jsonObject.getString("viptime"));
                                         ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.USER_VIPS_KEY,
                                                 AesTool.encrypt(vip));
                                         ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIP_TIMES_KEY,
                                                 AesTool.encrypt(vipTime));
                                         //写入用户第一次充值时间
                                         if (VipTool.getUserVipType(mContext) == Multi.VIP_NOT_VIP_TYPE) {
                                             FileTool.writeFileToSDFile(Constant.PAY_FILE, String.valueOf(DateTool.sdf.format(new Date())));
                                         }
                                         //保存用户充值次数
                                         if (isWrite) {
                                             VipTool.write_User_Pay_Count(mContext);
                                             isWrite = false;
                                             mHandler.postDelayed(new Runnable() {
                                                 @Override
                                                 public void run() {
                                                     isWrite = true;
                                                 }
                                             }, 5000);
                                         }
                                         Multi.USER_PAY_SUCCES = true;
                                         mHandler.sendEmptyMessage(GO_QUERY_ZFB_PAY_SUCCES);
                                     } else if (code.equals("2")) {
                                         //数据库连接异常!!
                                         mobclickPayFailed();
                                         mHandler.sendEmptyMessage(GO_PAY_SYSTEM_EXCEPTION);
                                     }
                                 }
                                 if (vipType == Multi.VIP_RED_DIAMOND_TYPE ||
                                         vipType == Multi.VIP_CROWN_TYPE) {
                                     //v2
                                     String code = AesTool.decrypt(jsonObject.getString("code"));
                                     if (code.equals("0")) {
                                         //充值失败(无此用户)
                                         mobclickPayFailed();
//                                         mHandler.sendEmptyMessage(GO_TIMER_PAY_REQUEST);
                                     } else if (code.equals("1")) {
                                         //充值成功
                                         String viptwo = AesTool.decrypt(jsonObject.getString("vip_two"));
                                         String vipTwoTime = AesTool.decrypt(jsonObject.getString("viptime_two"));
                                         ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIPS_TWOS_KEY,
                                                 AesTool.encrypt(viptwo));
                                         ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIP_TIME_TWO_KEY,
                                                 AesTool.encrypt(vipTwoTime));
                                         //写入用户第一次充值时间
                                         if (VipTool.getUserVipType(mContext) == Multi.VIP_NOT_VIP_TYPE) {
                                             FileTool.writeFileToSDFile(Constant.PAY_FILE, String.valueOf(DateTool.sdf.format(new Date())));
                                         }
                                         //保存用户充值次数
                                         if (isWrite) {
                                             VipTool.write_User_Pay_Count(mContext);
                                             isWrite = false;
                                             mHandler.postDelayed(new Runnable() {
                                                 @Override
                                                 public void run() {
                                                     isWrite = true;
                                                 }
                                             }, 5000);
                                         }
                                         Multi.USER_PAY_SUCCES = true;
                                         mHandler.sendEmptyMessage(GO_QUERY_ZFB_PAY_SUCCES);
                                     } else if (code.equals("2")) {
                                         //数据库连接异常!!
                                         mobclickPayFailed();
                                         mHandler.sendEmptyMessage(GO_PAY_SYSTEM_EXCEPTION);
                                     }
                                 }
                             } catch (Exception e) {
                                 mobclickPayFailed();
                                 mHandler.sendEmptyMessage(GO_QUERY_ZFB_PAY_FAILED);
                             }
                         }
                     }

        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        doWx();
        initData();
        initPlayer();
        MobclickAgent.onPageStart("午夜啪啪支付界面"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);
    }

//    private void doWx() {
//        switch (Multi.Wx_Pay_Succes) {
//            case 0:
//                //成功
//                query_Zfb_PayResesult();
//                Multi.Wx_Pay_Succes = 2;
//                break;
//            case 1:
//                //失败
//                T.showTextCenterToast(mContext, "您取消了支付!!");
//                Multi.Moon_LEVE = Multi.Moon_LEVE2;
//                Multi.LIVE_UNBIND = true;
//                alertIntentLive();
//                Multi.Wx_Pay_Succes = 2;
//                break;
//            case 2:
//                //无支付
//                break;
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("午夜啪啪支付界面"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

    @Override
    public void payClick() {
        if (choicePay.equals("wx")) {
            initWeiXin();
        }
        if (choicePay.equals("zfb")) {
            initZfb();
        }
    }

    @Override
    public void run() {
    }

    @OnClick({R.id.wxpay, R.id.zfbpay, R.id.liner_one_month, R.id.liner_one_year, R.id.back, R.id.viptext, R.id.pay_btn
            , R.id.threelun_liner_theemonth, R.id.threelun_liner_year})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wxpay:
                initWeiXinPay();
                break;
            case R.id.zfbpay:
                initZfbPay();
                break;
            case R.id.pay_btn:
                initPayBtn();
                break;
            case R.id.back:
                mContext.finish();
                break;
            case R.id.viptext:
                startActivity(new Intent(mContext, VipInfoActivity.class));
                break;
            case R.id.threelun_liner_theemonth:
                MobclickAgent.onEvent(mContext, MobClick.LAST_LUN_THREE_MONTH_ID);//埋点统计
                isDialaogPay = false;
                VIP_TIME = vipThreeMonthType;
                body = body_threeMonth;
                pay_Price = vip_Three_Lun_ThreeMonthPrice;
                payInterface.payClick();
                break;
            case R.id.threelun_liner_year:
                MobclickAgent.onEvent(mContext, MobClick.LAST_LUN_YEAR_ID);//埋点统计
                isDialaogPay = false;
                VIP_TIME = vipYearType;
                body = body_year;
                pay_Price = vip_Three_Lun_YearPrice;
                payInterface.payClick();
                break;
        }
    }

    private void initWeiXinPay() {
        payTishi.setImageResource(R.drawable.pay_gray);
        isDialaogPay = false;
        choicePay = "wx";
        wxpay.setImageResource(R.drawable.wxpay_select);
        zfbpay.setImageResource(R.drawable.zfb_pay);
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_NOT_VIP_TYPE) {
            if (choicePay.equals("wx")) {
                payType.setText("白银会员" + vipSilverPrice + "元");
            }
        } else if (vipType == Multi.VIP_SILVER_TYPE) {
            if (choicePay.equals("wx")) {
                payType.setText("黄金会员" + vipGoldPrice + "元");
            }
        } else if (vipType == Multi.VIP_GOLD_TYPE) {
            if (choicePay.equals("wx")) {
                payType.setText("白金会员" + vipPlatNiumPrice + "元");
            }
        } else if (vipType == Multi.VIP_PLAT_NIUM_TYPE) {
            if (choicePay.equals("wx")) {
                payType.setText("钻石会员" + vipDiamondPrice + "元");
            }
        } else if (vipType == Multi.VIP_DIAMOND_TYPE) {
            if (choicePay.equals("wx")) {
                payType.setText("红钻会员" + vipRedDiamondPrice + "元");
            }
        } else if (vipType == Multi.VIP_RED_DIAMOND_TYPE) {
            if (choicePay.equals("wx")) {
                payType.setText("皇冠会员" + vipCrownPrice + "元");
            }
        } else if (vipType == Multi.VIP_CROWN_TYPE) {
            if (choicePay.equals("wx")) {
                payType.setText("皇冠会员" + vipCrownPrice + "元");
            }
        }
    }

    private void initZfbPay() {
        payTishi.setImageResource(R.drawable.pay_light);
        isDialaogPay = false;
        choicePay = "zfb";
        wxpay.setImageResource(R.drawable.wx_pay);
        zfbpay.setImageResource(R.drawable.zfbpay_select);
        int vipType1 = VipTool.getUserVipType(mContext);
        if (vipType1 == Multi.VIP_NOT_VIP_TYPE) {
            if (choicePay.equals("zfb")) {
                int showPrice = Integer.parseInt(vipSilverPrice) - 10;
                payType.setText("白银会员" + showPrice + "元");
            }
        } else if (vipType1 == Multi.VIP_SILVER_TYPE) {
            if (choicePay.equals("zfb")) {
                int showPrice = Integer.parseInt(vipGoldPrice) - 5;
                payType.setText("黄金会员" + showPrice + "元");
            }
        } else if (vipType1 == Multi.VIP_GOLD_TYPE) {
            if (choicePay.equals("zfb")) {
                int showPrice = Integer.parseInt(vipPlatNiumPrice) - 5;
                payType.setText("白金会员" + showPrice + "元");
            }
        } else if (vipType1 == Multi.VIP_PLAT_NIUM_TYPE) {
            if (choicePay.equals("zfb")) {
                int showPrice = Integer.parseInt(vipDiamondPrice) - 5;
                payType.setText("钻石会员" + showPrice + "元");
            }
        } else if (vipType1 == Multi.VIP_DIAMOND_TYPE) {
            if (choicePay.equals("zfb")) {
                int showPrice = Integer.parseInt(vipRedDiamondPrice) - 5;
                payType.setText("红钻会员" + showPrice + "元");
            }
        } else if (vipType1 == Multi.VIP_RED_DIAMOND_TYPE) {
            if (choicePay.equals("zfb")) {
                int showPrice = Integer.parseInt(vipCrownPrice) - 5;
                payType.setText("皇冠会员" + showPrice + "元");
            }
        } else if (vipType1 == Multi.VIP_CROWN_TYPE) {
            if (choicePay.equals("zfb")) {
                int showPrice = Integer.parseInt(vipCrownPrice) - 5;
                payType.setText("皇冠会员" + showPrice + "元");
            }
        }
    }

    private void initPayBtn() {
        int vipType3 = VipTool.getUserVipType(mContext);
        if (vipType3 == Multi.VIP_NOT_VIP_TYPE) {
            if (choicePay.equals("wx")) {
                MobclickAgent.onEvent(mContext, MobClick.SILVIP_WX_ID);//埋点统计
            } else if (choicePay.equals("zfb")) {
                MobclickAgent.onEvent(mContext, MobClick.SILVIP_ZFB_ID);//埋点统计
            }
        } else if (vipType3 == Multi.VIP_GOLD_TYPE) {
            if (choicePay.equals("wx")) {
                MobclickAgent.onEvent(mContext, MobClick.SENCOND_WX_ID);//埋点统计
            } else if (choicePay.equals("zfb")) {
                MobclickAgent.onEvent(mContext, MobClick.SECOND_ZFB_ID);//埋点统计
            }
        } else if (vipType3 == Multi.VIP_PLAT_NIUM_TYPE) {
            if (choicePay.equals("wx")) {
                MobclickAgent.onEvent(mContext, MobClick.THREE_WX_ID);//埋点统计
            } else if (choicePay.equals("zfb")) {
                MobclickAgent.onEvent(mContext, MobClick.THREE_ZFB_ID);//埋点统计
            }
        } else if (vipType3 == Multi.VIP_DIAMOND_TYPE) {
            if (choicePay.equals("wx")) {
                MobclickAgent.onEvent(mContext, MobClick.RED_DIAMOND_WX_ID);//埋点统计
            } else if (choicePay.equals("zfb")) {
                MobclickAgent.onEvent(mContext, MobClick.RED_DIAMOND_ZFB_ID);//埋点统计
            }
        } else if (vipType3 == Multi.VIP_SILVER_TYPE) {
            if (choicePay.equals("wx")) {
                MobclickAgent.onEvent(mContext, MobClick.FIRST_WX_ID);//埋点统计
            } else if (choicePay.equals("zfb")) {
                MobclickAgent.onEvent(mContext, MobClick.FIRST_ZFB_ID);//埋点统计
            }
        } else if (vipType3 == Multi.VIP_RED_DIAMOND_TYPE) {
            if (choicePay.equals("wx")) {
                MobclickAgent.onEvent(mContext, MobClick.CROWN_WX_ID);//埋点统计
            } else if (choicePay.equals("zfb")) {
                MobclickAgent.onEvent(mContext, MobClick.CROWN_ZFB_ID);//埋点统计
            }
        } else if (vipType3 == Multi.VIP_CROWN_TYPE) {
            if (choicePay.equals("wx")) {
                MobclickAgent.onEvent(mContext, MobClick.CROWN_WX_ID);//埋点统计
            } else if (choicePay.equals("zfb")) {
                MobclickAgent.onEvent(mContext, MobClick.CROWN_ZFB_ID);//埋点统计
            }
        }
        isDialaogPay = false;
        VIP_TIME = vipOneMonthType;
        body = body_oneMonth;
        payInterface.payClick();
    }

    Dialog dialog_pay;

    public void dialog_pay_succes() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_succes, null);
        //对话框
        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
        if (dialog_pay == null) {
            dialog_pay = new Dialog(mContext, R.style.Dialog);
            dialog_pay.show();
            dialog_pay.setCancelable(false);
            Window window = dialog_pay.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            layout.getBackground().setAlpha(150);
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setContentView(layout);

            FrameLayout frameLayout = (FrameLayout) layout.findViewById(R.id.succes_bg);
            frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_pay.dismiss();
                    Multi.Moon_LEVE = Multi.Moon_LEVE2;
                    Multi.LIVE_UNBIND = true;
                    alertIntentLive();
                }
            });
        } else {
            dialog_pay.show();
        }
    }

    public void payFailedDialog() {
        new AlertDialog.Builder(mContext).setTitle("支付消息提示")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setCancelable(false)
                .setMessage("由于网络原因,验证您的支付状态失败,请您联系客服人员为您完成会员验证。")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @OnClick(R.id.viptext)
    public void onClick() {
        startActivity(new Intent(mContext, VipInfoActivity.class));
    }

    private String getVideoUrl() {
        int random = RandomTool.getRandom(0, 3);
        return Constant.urlString.get(random);
    }


    Dialog dialog_pay_time;

    public void alertIntentLive() {
        ScreenTool.setLight(mContext, 250);
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
//              layout.getBackground().setAlpha(150);
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
                        Multi.Moon_LIVE_LEVE = Multi.Moon_LIVE_LEVE2;
                        Multi.isShowLiveDialog = false;
                        mContext.finish();
                    }
                });

                final ImageView images = (ImageView) layout.findViewById(R.id.images);
                final FullScreenVideo videoView = (FullScreenVideo) layout.findViewById(R.id.video);
                videoView.setVideoURI(Uri.parse(getVideoUrl()));
                videoView.start();
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        videoView.setVideoURI(Uri.parse(getVideoUrl()));
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
                        videoView.setVideoURI(Uri.parse(getVideoUrl()));
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

    private Timer timer;
    private TimerTask timerTask;

    public void destroyTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        Log.i("timePay", "销毁了定时验证");
    }

    private void disMissDialog() {
        try {
            if (dialogPay != null)
                dialogPay.dismiss();
            ;
        } catch (Exception e) {
        }
    }

    private void mobclickPayFailed() {
        if (choicePay.equals("wx")) {
            MobclickAgent.onEvent(mContext, MobClick.WX_FAILED_ID);//埋点统计
        } else if (choicePay.equals("zfb")) {
            MobclickAgent.onEvent(mContext, MobClick.ZFB_FAILED_ID);//埋点统计
        }
    }


    private void sendPayRequest() {
        Log.i("timePay", "6轮进入了定时验证");
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = GO_TIMER_PAY_REQUEST;
                    mHandler.sendMessage(msg);
                }
            };
            timer = new Timer();
            timer.schedule(timerTask, 0, 5000);//网络请求的时间必须比定时上报的时间短
            Log.i("timePay", "6轮执行了定时验证");
        }
    }

}
