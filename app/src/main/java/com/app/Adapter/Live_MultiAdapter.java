package com.app.Adapter;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.Activity.MainActivity;
import com.app.Activity.SuperVideoDetailsActivity;
import com.app.Bean.Multi;
import com.app.Bean.PayType;
import com.app.Constant.Constant;
import com.app.Model.LiveInfo;
import com.app.Model.Live_Id;
import com.app.Model.ProgramInfo;
import com.app.Net.JsonUtils;
import com.app.Net.MobClick;
import com.app.Net.NetInterface;
import com.app.Net.OkHttp;
import com.app.Tool.AesTool;
import com.app.Tool.FileTool;
import com.app.Tool.NetTool;
import com.app.Tool.RandomTool;
import com.app.Tool.ScreenTool;
import com.app.Tool.VipTool;
import com.app.View.SelfGridView;
import com.app.View.T;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.third.app.R;
import com.umeng.analytics.MobclickAgent;
import com.jssm.zsrz.wxapi.PayActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lin on 2016/11/24.
 */
public class Live_MultiAdapter extends BaseMultiItemQuickAdapter<Multi, BaseViewHolder> {

    private MainActivity mContext;
    private boolean isShow = false;

    public Live_MultiAdapter(List<Multi> data, MainActivity mContext) {
        super(data);
        addItemType(Multi.LIVE_ITEM, R.layout.live_item);
        this.mContext = mContext;
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0000:
                    String json = msg.obj.toString();
                    do_view(json);
                    break;
                case 1111:
                    ((ProgressBar) baseViewHolders.getView(R.id.more_progress)).setVisibility(View.GONE);
                    break;
            }
        }
    };


    @Override
    protected void convert(final BaseViewHolder baseViewHolder, final Multi multi) {
        switch (baseViewHolder.getItemViewType()) {
            case Multi.LIVE_ITEM:
                ImageView imageView = (ImageView) baseViewHolder.getView(R.id.live_images);
                ImageView live_logo = (ImageView) baseViewHolder.getView(R.id.live_logo);
                ImageView sd_images = (ImageView) baseViewHolder.getView(R.id.sd_images);
                Glide.with(mContext).
                        load(multi.getLiveInfo().getPic_heng_vip())
                        .diskCacheStrategy(DiskCacheStrategy.NONE).
                        skipMemoryCache(true)
                        .placeholder(R.drawable.allloading).
                        error(R.drawable.allloading).into(imageView);

                Glide.with(mContext).
                        load(multi.getLiveInfo().getPic())
                        .placeholder(R.drawable.allloading).
                        error(R.drawable.allloading).into(live_logo);

                if (multi.getLiveInfo().getSpare2().equals("1")) {
                    ((ImageView) baseViewHolder.getView(R.id.look)).setImageResource(R.drawable.look_at2);
                } else {
                    ((ImageView) baseViewHolder.getView(R.id.look)).setImageResource(R.drawable.look_vip2);
                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doPlay_Do(multi);
                    }
                });
                if (multi.getPosition() == 0) {
                    sd_images.setVisibility(View.VISIBLE);
                } else {
                    sd_images.setVisibility(View.GONE);
                }
                ((TextView) baseViewHolder.getView(R.id.live_name)).setText("《" + multi.getLiveInfo().getName() + "》");
                ((TextView) baseViewHolder.getView(R.id.live_count)).setText(RandomTool.getRandomNumbers(5));
                TextPaint tp = ((TextView) baseViewHolder.getView(R.id.sort)).getPaint();
                tp.setFakeBoldText(true);
                ((TextView) baseViewHolder.getView(R.id.sort)).setText(multi.getPosition() + 1 + "");
                ((ImageView) baseViewHolder.getView(R.id.play)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doPlay_Do(multi);
                    }
                });
                LinearLayout video_list = baseViewHolder.getView(R.id.return_bg);
                video_list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doEnvent(baseViewHolder, multi.getPosition(), multi);
                    }
                });
                LinearLayout play_in = baseViewHolder.getView(R.id.play_in);
                play_in.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doPlay_Do(multi);
                    }
                });
                break;
        }
    }

    private void doPlay_Do(Multi multi) {
        if (multi.getPosition() == 0) {
            MobclickAgent.onEvent(mContext, MobClick.TV_VIDEO_1_ID);//埋点统计
        } else if (multi.getPosition() == 1) {
            MobclickAgent.onEvent(mContext, MobClick.TV_VIDEO_2_ID);//埋点统计
        } else if (multi.getPosition() == 2) {
            MobclickAgent.onEvent(mContext, MobClick.TV_VIDEO_3_ID);//埋点统计
        } else if (multi.getPosition() == 3) {
            MobclickAgent.onEvent(mContext, MobClick.TV_VIDEO_4_ID);//埋点统计
        } else if (multi.getPosition() == 4) {
            MobclickAgent.onEvent(mContext, MobClick.TV_VIDEO_5_ID);//埋点统计
        }
        //空值表示没有解锁
        if (TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.TV_USER_LIVE_UNBIND))) {
            return;
        }
        LiveInfo info = multi.getLiveInfo();
        if (!VipTool.userIsLoginSucces(mContext)) {
            T.showTextCenterToast(mContext, "请重启app客户端完成自动登录");
            return;
        } else {
            int vipType = VipTool.getUserVipType(mContext);
            if (vipType == Multi.VIP_NOT_VIP_TYPE) {
                alertDialogPay();
            } else {
                if (vipType == Multi.VIP_SILVER_TYPE ||
                        vipType == Multi.VIP_DIAMOND_TYPE ||
                        vipType == Multi.VIP_PLAT_NIUM_TYPE ||
                        vipType == Multi.VIP_GOLD_TYPE ||
                        vipType == Multi.VIP_RED_DIAMOND_TYPE ||
                        vipType == Multi.VIP_CROWN_TYPE) {
                    if (info.getSpare2().equals("1")) {
                        //试看
                        doPlay(info, true);
                    } else if (info.getSpare2().equals("0")) {
                        if (vipType == Multi.VIP_CROWN_TYPE) {
                            T.showTextCenterToast(mContext, "后台维护升级中...敬请期待!!");
                        } else {
                            mContext.alertDialogPay();
                        }
                    }
                } else if (vipType == Multi.VIP_NOT_VIP_TYPE && !TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.TV_USER_LIVE_UNBIND))) {
                    //不是会员，但已经解锁
                    if (info.getSpare2().equals("1")) {
                        //试看
                        File file = new File(Constant.TV_SHIYONG_M3U8_ALL);
                        if (!file.exists()) {
                            //继续执行
                            doPlay(info, true);
                        } else {
                            try {
                                String oldTime = AesTool.decrypt(FileTool.readFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL));
                                if (TextUtils.isEmpty(oldTime)) {
                                    doPlay(info, true);
                                } else {
                                    if (Integer.parseInt(oldTime) > Constant.LIVE_DATE) {
                                        //试用过期
                                        mContext.alertDialogPay();
                                    } else {
                                        //没有过期
                                        doPlay(info, true);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (info.getSpare2().equals("0")) {
                        mContext.alertDialogPay();
                    }
                }
            }
        }
    }

    private void doEnvent(final BaseViewHolder baseViewHolder, final int position, Multi multi) {
        if (isShow == false) {
            isShow = true;
            ((ImageView) baseViewHolder.getView(R.id.hide_return)).setImageResource(R.drawable.hide_show);
            ((SelfGridView) baseViewHolder.getView(R.id.tv_list_grid)).setVisibility(View.GONE);
        } else {
            isShow = false;
            ((ImageView) baseViewHolder.getView(R.id.hide_return)).setImageResource(R.drawable.hide_return);
            ((SelfGridView) baseViewHolder.getView(R.id.tv_list_grid)).setVisibility(View.VISIBLE);
            ((ProgressBar) baseViewHolder.getView(R.id.more_progress)).setVisibility(View.VISIBLE);
            doData(baseViewHolder, position, multi);
        }
    }

    private BaseViewHolder baseViewHolders;

    private void doData(BaseViewHolder baseViewHolder, int position, Multi multi) {
        if (!NetTool.isConnected(mContext)) {
            T.showTextToast(mContext, "您的网络没有连接，请检查您的网络,然后下拉刷新试试");
            return;
        }
        baseViewHolders = baseViewHolder;
        Live_Id infoLive = new Live_Id();
        infoLive.setType(AesTool.encrypt(multi.getLiveInfo().getSpare1()));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", infoLive.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
        OkHttpClient mOkHttpClient = OkHttp.getInstance();
        RequestBody formBody = new FormBody.Builder()
                .add(NetInterface.REQUEST_HEADER, json)
                .build();
        Request request = new Request.Builder()
                .url(NetInterface.TV_JIEMU_LIST)
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(1111);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Message message = new Message();
                    message.what = 0000;
                    message.obj = response.body().string();
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    return;
                }
            }
        });
    }

    private void do_view(String jsonData) {
        if (baseViewHolders == null) {
            return;
        }
        try {
            ((ProgressBar) baseViewHolders.getView(R.id.more_progress)).setVisibility(View.GONE);
            List<JSONObject> jsonObjectList = JsonUtils.parseJsonArray(jsonData);
            List<ProgramInfo> programInfoList = new ArrayList<ProgramInfo>();
            for (int i = 0; i < jsonObjectList.size(); i++) {
                ProgramInfo programInfo = new ProgramInfo();
                programInfo.setTime(AesTool.decrypt(jsonObjectList.get(i).getString("time")));
                programInfo.setInfo(AesTool.decrypt(jsonObjectList.get(i).getString("info")));
                programInfoList.add(programInfo);
            }
            ProgramAdapter programAdapter = new ProgramAdapter(mContext);
            programAdapter.setList(programInfoList);
            ((SelfGridView) baseViewHolders.getView(R.id.tv_list_grid)).setAdapter(programAdapter);
            ((SelfGridView) baseViewHolders.getView(R.id.tv_list_grid)).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doPlay(LiveInfo info, boolean isResult) {
        Intent liveIntentV = new Intent(mContext, SuperVideoDetailsActivity.class);
        liveIntentV.putExtra("isLive", true);
        liveIntentV.putExtra("url", info.getAddress());
        liveIntentV.putExtra("title", info.getName());
        if (isResult) {
            Multi.Moon_LEVE = Multi.Moon_LEVE1;
            mContext.startActivityForResult(liveIntentV, Multi.Moon_LEVE);
        } else {
            mContext.startActivity(liveIntentV);
        }
    }

    //    Dialog dialog_pay_time;
//
//    public void alertVipPay() {
//        ScreenTool.setLight(mContext, 250);
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_gold, null);
//        //对话框
//        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
//        if (dialog_pay_time == null) {
//            dialog_pay_time = new Dialog(mContext, R.style.Dialog);
//            dialog_pay_time.show();
//            dialog_pay_time.setCancelable(true);
//            Window window = dialog_pay_time.getWindow();
//            window.getDecorView().setPadding(0, 0, 0, 0);
//            WindowManager.LayoutParams lp = window.getAttributes();
////            layout.getBackground().setAlpha(150);
//            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;//ScreenTool.getWidth(this) / 5 * 3;
//            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            window.setAttributes(lp);
//            window.setContentView(layout);
//            ImageView pay_gold = (ImageView) layout.findViewById(R.id.pay_gold);
//            pay_gold.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog_pay_time.dismiss();
//                    Multi.Moon_LEVE = Multi.Moon_LEVE1;
//                    Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
//                    mContext.startActivity(new Intent(mContext, WXPayEntryActivity.class));
//                }
//            });
//            TextView vip_level_decription = (TextView) layout.findViewById(R.id.vip_level_decription);
//            if (VipTool.getUserVipType(mContext) == Multi.VIP_NOT_VIP_TYPE) {
//                vip_level_decription.setText("成为白银会员观看");
//            } else if (VipTool.getUserVipType(mContext) == Multi.VIP_SILVER_TYPE) {
//                vip_level_decription.setText("成为黄金会员观看");
//            } else if (VipTool.getUserVipType(mContext) == Multi.VIP_GOLD_TYPE) {
//                vip_level_decription.setText("成为白金会员观看");
//            } else if (VipTool.getUserVipType(mContext) == Multi.VIP_PLAT_NIUM_TYPE) {
//                vip_level_decription.setText("成为钻石会员观看");
//            } else {
//                vip_level_decription.setText("成为钻石会员观看");
//            }
//        } else {
//            dialog_pay_time.show();
//        }
//    }
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
                    pay_text_1.setText("解锁劲爆TV频道");
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
                    pay_text_1.setText("解锁劲爆TV频道");
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
                        pay_text_1.setText("解锁劲爆TV频道");
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
                        pay_text_1.setText("解锁劲爆TV频道");
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
}
