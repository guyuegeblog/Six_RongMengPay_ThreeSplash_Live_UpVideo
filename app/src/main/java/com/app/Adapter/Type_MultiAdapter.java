package com.app.Adapter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.app.Activity.ChannelActivity;
import com.app.Activity.SuperVideoDetailsActivity;
import com.app.Bean.Multi;
import com.app.Constant.Constant;
import com.app.Model.ChanneLnfo;
import com.app.Model.VideoInfo;
import com.third.app.R;
import com.app.Tool.FileTool;
import com.app.Tool.VipTool;
import com.app.View.T;

import java.util.List;

/**
 * Created by lin on 2016/11/24.
 */
public class Type_MultiAdapter extends BaseMultiItemQuickAdapter<Multi, BaseViewHolder> {

    private ChannelActivity mContext;

    public Type_MultiAdapter(List<Multi> data, ChannelActivity mContext) {
        super(data);
        addItemType(Multi.CHANNEL_TWO_HEADER, R.layout.channel_two_header);
        addItemType(Multi.CHANNEL_TYPE_ITEM, R.layout.channel_two_item);
        addItemType(Multi.CHANNEL_TWO_BOTTOM, R.layout.channeltwo_bottom);
        this.mContext = mContext;
    }


    @Override
    protected void convert(BaseViewHolder baseViewHolder, final Multi multi) {
        switch (baseViewHolder.getItemViewType()) {
            case Multi.CHANNEL_TYPE_ITEM:
                Glide.with(mContext).load(multi.getChanneLnfo().getPic()).placeholder(R.drawable.allloading).
                        error(R.drawable.allloading).into((ImageView) baseViewHolder.getView(R.id.channe2_images));
                ((TextView) baseViewHolder.getView(R.id.fragment_tuku_text)).setText(multi.getChanneLnfo().getName());
                ((ImageView) baseViewHolder.getView(R.id.channe2_images)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doPlay(multi.getChanneLnfo(), true);
                    }
                });
                ((TextView) baseViewHolder.getView(R.id.fragment_tuku_text)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doPlay(multi.getChanneLnfo(), true);
                    }
                });
                break;

            case Multi.CHANNEL_TWO_BOTTOM:
                //...
                break;
            case Multi.CHANNEL_TWO_HEADER:
                //...
                if (multi.getChanneType().equals("1")) {
                    ((TextView) baseViewHolder.getView(R.id.activity_showvideo_title)).setText("午夜快播");
                } else if (multi.getChanneType().equals("2")) {
                    ((TextView) baseViewHolder.getView(R.id.activity_showvideo_title)).setText("欧美专区");
                } else if (multi.getChanneType().equals("3")) {
                    ((TextView) baseViewHolder.getView(R.id.activity_showvideo_title)).setText("激情女优");
                } else if (multi.getChanneType().equals("4")) {
                    ((TextView) baseViewHolder.getView(R.id.activity_showvideo_title)).setText("伦理档案");
                } else if (multi.getChanneType().equals("5")) {
                    ((TextView) baseViewHolder.getView(R.id.activity_showvideo_title)).setText("美女主播");
                } else if (multi.getChanneType().equals("6")) {
                    ((TextView) baseViewHolder.getView(R.id.activity_showvideo_title)).setText("邪恶动漫");
                } else if (multi.getChanneType().equals("7")) {
                    ((TextView) baseViewHolder.getView(R.id.activity_showvideo_title)).setText("女优专场");
                } else if (multi.getChanneType().equals("8")) {
                    ((TextView) baseViewHolder.getView(R.id.activity_showvideo_title)).setText("欧美风情");
                } else if (multi.getChanneType().equals("9")) {
                    ((TextView) baseViewHolder.getView(R.id.activity_showvideo_title)).setText("无码专区");
                } else if (multi.getChanneType().equals("10")) {
                    ((TextView) baseViewHolder.getView(R.id.activity_showvideo_title)).setText("艳情写真");
                } else if (multi.getChanneType().equals("11")) {
                    ((TextView) baseViewHolder.getView(R.id.activity_showvideo_title)).setText("美腿丝袜");
                } else if (multi.getChanneType().equals("12")) {
                    ((TextView) baseViewHolder.getView(R.id.activity_showvideo_title)).setText("主播丽人");
                }
                ((ImageView) baseViewHolder.getView(R.id.activity_showvideo_back)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mContext.finish();
                    }
                });
                break;
        }
    }

    public void doPlay(ChanneLnfo channeLnfo, boolean isResult) {
        if (!VipTool.userIsLoginSucces(mContext)) {
            T.showTextToast(mContext, "请您重新启动App,完成自动登录后再播放视频!!");
            return;
        }
//        if (VipTool.canVip1(mContext)) {
//            six_Total();
//            Intent intent = new Intent(mContext, SuperVideoDetailsActivity.class);
//            Bundle bundle = new Bundle();
//            VideoInfo videoInfo = new VideoInfo();
//            videoInfo.setId("0");
//            videoInfo.setAddress_hd(channeLnfo.getAddress_hd());
//            videoInfo.setAddress_sd(channeLnfo.getAddress_sd());
//            videoInfo.setName(channeLnfo.getName());
//            bundle.putSerializable("videoInfo", videoInfo);
//            intent.putExtras(bundle);
//            intent.putExtra("isLive", false);
//            if (isResult) {
//                Multi.Moon_LEVE = Multi.Moon_LEVE2;
//                mContext.startActivityForResult(intent, Multi.Moon_LEVE);
//            } else {
//                mContext.startActivity(intent);
//            }
//        } else {
//            alertVipPay(mContext);
//        }
        if (VipTool.getUserVipType(mContext) == Multi.VIP_CROWN_TYPE) {
            six_Total();
            Intent intent = new Intent(mContext, SuperVideoDetailsActivity.class);
            Bundle bundle = new Bundle();
            VideoInfo videoInfo = new VideoInfo();
            videoInfo.setId("0");
            videoInfo.setAddress_hd(channeLnfo.getAddress_hd());
            videoInfo.setAddress_sd(channeLnfo.getAddress_sd());
            videoInfo.setName(channeLnfo.getName());
            videoInfo.setThreeVideo(false);
            bundle.putSerializable("videoInfo", videoInfo);
            intent.putExtras(bundle);
            intent.putExtra("isLive", false);
            if (isResult) {
                Multi.Moon_LEVE = Multi.Moon_LEVE2;
                mContext.startActivityForResult(intent, Multi.Moon_LEVE);
            } else {
                mContext.startActivity(intent);
            }
        } else {
            mContext.alertDialogPay();
        }
    }

    private void six_Total() {
        VipTool.Six_Total(mContext);
        if (VipTool.canVip1(mContext)) return;
        if (!TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.TV_USER_LIVE_UNBIND)))
            return;//表示已经解绑
        if (VipTool.than_Six_Total(mContext)) {
            Multi.Moon_LIVE_LEVE = Multi.Moon_LIVE_LEVE2;
            mContext.finish();
        }
    }

//    Dialog dialog_pay_time;
//
//    public void alertVipPay(Activity context) {
//        ScreenTool.setLight(mContext, 250);
//        LayoutInflater inflater = LayoutInflater.from(context);
//        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_gold, null);
//        //对话框
//        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
//        if (dialog_pay_time == null) {
//            dialog_pay_time = new Dialog(context, R.style.Dialog);
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
//            if (VipTool.getUserVipType() == Multi.VIP_NOT_VIP_TYPE) {
//                vip_level_decription.setText("成为白银会员享受更多...");
//            } else if (VipTool.getUserVipType() == Multi.VIP_SILVER_TYPE) {
//                vip_level_decription.setText("成为黄金会员享受更多...");
//            } else if (VipTool.getUserVipType() == Multi.VIP_GOLD_TYPE) {
//                vip_level_decription.setText("成为白金会员享受更多...");
//            } else if (VipTool.getUserVipType() == Multi.VIP_PLAT_NIUM_TYPE) {
//                vip_level_decription.setText("成为钻石会员享受更多...");
//            } else {
//                vip_level_decription.setText("成为钻石会员享受更多...");
//            }
//        } else {
//            dialog_pay_time.show();
//        }
//    }
}
