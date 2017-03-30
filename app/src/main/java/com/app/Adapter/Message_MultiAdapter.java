package com.app.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.app.Activity.MessageActivity;
import com.app.Activity.SuperVideoDetailsActivity;
import com.app.Bean.Multi;
import com.app.Constant.Constant;
import com.app.Model.LookInfo;
import com.app.Model.VideoInfo;
import com.third.app.R;
import com.app.Tool.FileTool;
import com.app.Tool.RandomTool;
import com.app.Tool.ScreenTool;
import com.app.Tool.VipTool;
import com.app.View.CropCircleTransformation;
import com.app.View.T;
import com.jssm.zsrz.wxapi.PayActivity;

import java.util.List;

/**
 * Created by lin on 2016/11/24.
 */
public class Message_MultiAdapter extends BaseMultiItemQuickAdapter<Multi, BaseViewHolder> {

    private MessageActivity mContext;
    private boolean isResult;
    private LookInfo contextLookInfo;

    public Message_MultiAdapter(List<Multi> data, MessageActivity mContext) {
        super(data);
        addItemType(Multi.MESSAGE_HEADER, R.layout.message_header);
        addItemType(Multi.MESSAGE_MIDDLE, R.layout.message_middle);
        addItemType(Multi.LOOK_ITEM, R.layout.message_look_item);
        addItemType(Multi.MESSAGE_HOT, R.layout.hot_message);
        addItemType(Multi.MESSAGE_ITEM, R.layout.message_item);
        addItemType(Multi.MESSAGE_BOTTOM, R.layout.message_bottom);
        this.mContext = mContext;
    }


    @Override
    protected void convert(BaseViewHolder baseViewHolder, final Multi multi) {
        switch (baseViewHolder.getItemViewType()) {
            case Multi.MESSAGE_HEADER:
                //...
                baseViewHolder.getView(R.id.activity_showvideo_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mContext.finish();
                    }
                });
                ImageView imageView_Pic = (ImageView) baseViewHolder.getView(R.id.activity_showvideo_netimageview);
                Glide.with(mContext).load(multi.getLookInfo().getPic()).placeholder(R.drawable.allloading).
                        error(R.drawable.allloading).into(imageView_Pic);
                ((TextView) baseViewHolder.getView(R.id.activity_showvideo_title)).setText(multi.getLookInfo().getName());
                baseViewHolder.getView(R.id.activity_showvideo_bofang).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        contextLookInfo = multi.getLookInfo();
                        isResult = true;
                        int vipType = VipTool.getUserVipType(mContext);
                        if (vipType == Multi.VIP_NOT_VIP_TYPE) {
                            if (VipTool.than_Shi_Kan_Six_Video(mContext)) {
                               mContext.alertDialogPay();
                            } else {
                                doPlay(multi.getLookInfo(), true);
                            }
                        } else {
                            doPlay(multi.getLookInfo(), true);
                        }
                    }
                });
                imageView_Pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        contextLookInfo = multi.getLookInfo();
                        isResult = true;
                        int vipType = VipTool.getUserVipType(mContext);
                        if (vipType == Multi.VIP_NOT_VIP_TYPE) {
                            if (VipTool.than_Shi_Kan_Six_Video(mContext)) {
                                mContext.alertDialogPay();
                            } else {
                                doPlay(multi.getLookInfo(), true);
                            }
                        } else {
                            doPlay(multi.getLookInfo(), true);
                        }
                    }
                });
                break;
            case Multi.MESSAGE_MIDDLE:
                //...
                break;
            case Multi.LOOK_ITEM:
                //...
                ImageView imageView = (ImageView) baseViewHolder.getView(R.id.images);
                Glide.with(mContext).load(multi.getLookInfo().getPic()).placeholder(R.drawable.allloading).
                        error(R.drawable.allloading).into(imageView);
                ((TextView) baseViewHolder.getView(R.id.see_count)).setText(RandomTool.getRandomNumbers(5));
                ((TextView) baseViewHolder.getView(R.id.name)).setText(multi.getLookInfo().getName());
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        contextLookInfo = multi.getLookInfo();
                        isResult = true;
                        if (VipTool.getUserVipType(mContext) == Multi.VIP_NOT_VIP_TYPE) {
                            if (VipTool.than_Shi_Kan_Six_Video(mContext)) {
                                mContext.alertDialogPay();
                            } else {
                                doPlay(multi.getLookInfo(), true);
                            }
                        } else {
                            doPlay(multi.getLookInfo(), true);
                        }
                    }
                });
                break;
            case Multi.MESSAGE_HOT:
                //...
                break;
            case Multi.MESSAGE_ITEM:
                //...
                Glide.with(mContext).load(multi.getCommentInfo().getPic()).bitmapTransform(new CropCircleTransformation(mContext)).placeholder(R.drawable.allloading).
                        error(R.drawable.allloading).into((ImageView) baseViewHolder.getView(R.id.pinglun_gridview_netimageview));
                ((TextView) baseViewHolder.getView(R.id.pinglun_gridview_name)).setText(multi.getCommentInfo().getName());
                ((TextView) baseViewHolder.getView(R.id.pinglun_gridview_info)).setText(multi.getCommentInfo().getInfo());
                ((TextView) baseViewHolder.getView(R.id.pinglun_gridview_zan)).setText(RandomTool.getRandomNumbers(5));
                break;
            case Multi.MESSAGE_BOTTOM:
                //...
                final EditText editText = (EditText) baseViewHolder.getView(R.id.activity_showvideo_edittext);
                ImageButton submit = (ImageButton) baseViewHolder.getView(R.id.activity_showvideo_tijiaobutton);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String result = editText.getText().toString();
                        if (!TextUtils.isEmpty(result)) {
                            T.showTextToast(mContext, "评论发表成功!!!");
                            editText.setText("");
                            editText.clearFocus();
                        } else {
                            T.showTextToast(mContext, "请输入评论内容!!!");
                        }
                    }
                });
                break;
        }
    }

    public void doPlay(LookInfo lookInfo, boolean isResult) {
        if (!VipTool.userIsLoginSucces(mContext)) {
            T.showTextToast(mContext, "请您重新启动App,完成自动登录后再播放视频!!");
            return;
        }
        startPlay(lookInfo, isResult);
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

    private void startPlay(LookInfo lookInfo, boolean isResult) {
        six_Total();
        Intent intent = new Intent(mContext, SuperVideoDetailsActivity.class);
        Bundle bundle = new Bundle();
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setId(lookInfo.getId());
        videoInfo.setAddress_hd(lookInfo.getAddress_hd());
        videoInfo.setAddress_sd(lookInfo.getAddress_sd());
//        videoInfo.setAddress_hd("http://www.cffyl.cn/xshikan/xsk17-mp4");
//        videoInfo.setAddress_sd("http://www.cffyl.cn/xshikan/xsk17-mp4");
        Log.i("videoStr", "评论 " + lookInfo.getAddress_hd());
        videoInfo.setLookVideo(true);
        videoInfo.setName(lookInfo.getName());
        videoInfo.setThreeVideo(false);
        videoInfo.setSpare1(lookInfo.getSpare1());
        bundle.putSerializable("videoInfo", videoInfo);
        intent.putExtras(bundle);
        intent.putExtra("isLive", false);
        if (isResult) {
            Multi.Moon_LEVE = Multi.Moon_LEVE2;
            mContext.startActivityForResult(intent, Multi.Moon_LEVE);
        } else {
            mContext.startActivity(intent);
        }
    }

    Dialog dialog_pay_time;

    public void alertVipPay(Activity context) {
        ScreenTool.setLight(mContext, 250);
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_look_count, null);
        //对话框
        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
        if (dialog_pay_time == null) {
            dialog_pay_time = new Dialog(context, R.style.Dialog);
            dialog_pay_time.show();
            dialog_pay_time.setCancelable(true);
            Window window = dialog_pay_time.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            layout.getBackground().setAlpha(150);
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;//ScreenTool.getWidth(this) / 5 * 3;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setContentView(layout);

            initPlayTotalCount(layout);
        } else {
            initPlayTotalCount(layout);
            dialog_pay_time.show();
        }
    }

    private void initPlayTotalCount(LinearLayout layout) {
        LinearLayout left = (LinearLayout) layout.findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_pay_time.dismiss();
                doPlay(contextLookInfo, true);
                dialog_pay_time = null;
            }
        });
        TextView look_text = (TextView) layout.findViewById(R.id.look_text);
        TextView total = (TextView) layout.findViewById(R.id.total);
        total.setText(VipTool.get_ShiKan_Video_Count(mContext) + "次");
        if (VipTool.than_Shi_Kan_Six_Video(mContext)) {
            look_text.setText("您的试看次数已经看完,请成为会员观看!");
            total.setText("充值");
            left.setEnabled(false);
        } else {
            left.setEnabled(true);
        }
        LinearLayout right = (LinearLayout) layout.findViewById(R.id.right);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_pay_time.dismiss();
                dialog_pay_time = null;
                Multi.Moon_LEVE = Multi.Moon_LEVE1;
                Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
                mContext.startActivity(new Intent(mContext, PayActivity.class));
                dialog_pay_time = null;
            }
        });
    }

//    Dialog dialog_Gold;
//
//    public void alertGold() {
//        ScreenTool.setLight(mContext, 250);
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_gold, null);
//        //对话框
//        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
//        if (dialog_Gold == null) {
//            dialog_Gold = new Dialog(mContext, R.style.Dialog);
//            dialog_Gold.show();
//            dialog_Gold.setCancelable(true);
//            Window window = dialog_Gold.getWindow();
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
//                    dialog_Gold.dismiss();
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
//            dialog_Gold.show();
//        }
//    }
}
