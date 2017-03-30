package com.app.Adapter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.app.Activity.LoadMoreActivity;
import com.app.Activity.MainActivity;
import com.app.Activity.SuperVideoDetailsActivity;
import com.app.Bean.Multi;
import com.app.Constant.Constant;
import com.app.Model.LookInfo;
import com.app.Model.VideoInfo;
import com.third.app.R;
import com.app.Tool.FileTool;
import com.app.Tool.NetTool;
import com.app.Tool.RandomTool;
import com.app.Tool.VipTool;
import com.app.View.T;

import java.util.List;

/**
 * Created by lin on 2016/11/24.
 */
public class Code_MultiAdapter extends BaseMultiItemQuickAdapter<Multi, BaseViewHolder> {

    private MainActivity mContext;

    public Code_MultiAdapter(List<Multi> data, MainActivity mContext) {
        super(data);
        addItemType(Multi.CODE_MINGXINGNVYOU, R.layout.activity_mingxing_nvyou);
        addItemType(Multi.CODE_ZHIFUYOUHUO, R.layout.activity_zhifu_youhuo);
        addItemType(Multi.CODE_ZHAINANFULI, R.layout.activity_zhainan_fuli);
        addItemType(Multi.CODE_ZUIJINGENGXIN, R.layout.activity_zuijin_gengxin);
        addItemType(Multi.CODE_GIF, R.layout.gif_item);
        addItemType(Multi.CODE_ITEM, R.layout.code_item);
        addItemType(Multi.LOOK_BOTTOM, R.layout.look_bottom);
        this.mContext = mContext;
    }


    @Override
    protected void convert(BaseViewHolder baseViewHolder, final Multi multi) {
        switch (baseViewHolder.getItemViewType()) {
            case Multi.CODE_MINGXINGNVYOU:
                //....
                ((TextView) baseViewHolder.getView(R.id.loadmore)).setText("今日更新" + RandomTool.getRandomNumbers(2)
                        + "部,总" + RandomTool.getRandomNumbers(4) + "部");

                ((TextView) baseViewHolder.getView(R.id.loadmore_text)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mContext.startActivity(new Intent(mContext, LoadMoreActivity.class).putExtra("typeId", multi.getChanneType()));
                    }
                });
                break;
            case Multi.CODE_ZHIFUYOUHUO:
                //....
                ((TextView) baseViewHolder.getView(R.id.loadmore)).setText("今日更新" + RandomTool.getRandomNumbers(2)
                        + "部,总" + RandomTool.getRandomNumbers(4) + "部");
                ((TextView) baseViewHolder.getView(R.id.loadmore_text)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mContext.startActivity(new Intent(mContext, LoadMoreActivity.class).putExtra("typeId", multi.getChanneType()));
                    }
                });
                break;
            case Multi.CODE_ZHAINANFULI:
                //....
                ((TextView) baseViewHolder.getView(R.id.loadmore)).setText("今日更新" + RandomTool.getRandomNumbers(2)
                        + "部,总" + RandomTool.getRandomNumbers(4) + "部");
                ((TextView) baseViewHolder.getView(R.id.loadmore_text)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mContext.startActivity(new Intent(mContext, LoadMoreActivity.class).putExtra("typeId", multi.getChanneType()));
                    }
                });
                break;
            case Multi.CODE_ZUIJINGENGXIN:
                //....
                ((TextView) baseViewHolder.getView(R.id.loadmore)).setText("今日更新" + RandomTool.getRandomNumbers(2)
                        + "部,总" + RandomTool.getRandomNumbers(4) + "部");
                ((TextView) baseViewHolder.getView(R.id.loadmore_text)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mContext.startActivity(new Intent(mContext, LoadMoreActivity.class).putExtra("typeId", multi.getChanneType()));
                    }
                });
                break;
            case Multi.CODE_GIF:
                //....
                ImageView imageView_gif = (ImageView) baseViewHolder.getView(R.id.gif_images);
                Glide.with(mContext).load(multi.getLookInfo().getPic_heng()).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(R.drawable.allloading).error(R.drawable.allloading).into(imageView_gif);
                imageView_gif.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NetCheck();
                        doPlay(multi.getLookInfo(), true);
                    }
                });
                break;
            case Multi.CODE_ITEM:
                //....
                ImageView imageView = (ImageView) baseViewHolder.getView(R.id.images);
                TextView textView_name = ((TextView) baseViewHolder.getView(R.id.name));
                TextView textView_seecount = ((TextView) baseViewHolder.getView(R.id.see_count));
                Glide.with(mContext).load(multi.getLookInfo().getPic()).placeholder(R.drawable.allloading).
                        error(R.drawable.allloading).into(imageView);
                textView_seecount.setText(RandomTool.getRandomNumbers(5));
                textView_name.setText(multi.getLookInfo().getName());
                textView_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NetCheck();
                        doPlay(multi.getLookInfo(), true);
                    }
                });
                textView_seecount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NetCheck();
                        doPlay(multi.getLookInfo(), true);
                    }
                });
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NetCheck();
                        doPlay(multi.getLookInfo(), true);
                    }
                });
                break;

            case Multi.LOOK_BOTTOM:
                //....
                break;
        }
    }

    private void NetCheck() {
        if (!NetTool.isConnected(mContext)) {
            T.showTextToast(mContext, "您的网络没有连接,请检查您的网络");
            return;
        }
    }

//    private LookInfo lookInfoDialog;
//    private boolean isResult;

    public void doPlay(LookInfo channeLnfo, boolean isResult) {
        if (!VipTool.userIsLoginSucces(mContext)) {
            T.showTextToast(mContext, "请您重新启动App,完成自动登录后再播放视频!!");
            return;
        }
//        lookInfoDialog = channeLnfo;
//        isResult = isResult;
        if (VipTool.getUserVipType(mContext) == Multi.VIP_NOT_VIP_TYPE) {
            //...,非试看专区
            if (VipTool.than_Shi_Kan_Six_Video(mContext)) {
                mContext.alertDialogPay();
            } else {
                startPlay(channeLnfo, isResult);
            }
        }else{
            startPlay(channeLnfo, isResult);
        }
//        if (VipTool.getUserVipType() == Multi.VIP_DIAMOND_TYPE) {
//            startPlay(channeLnfo, isResult);
//        } else {
//            alertVipPay(mContext);
//        }
    }

    private void six_Total() {
        if (VipTool.canVip1(mContext)) return;
        if (!TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.TV_USER_LIVE_UNBIND)))
            return;//表示已经解绑
        if (VipTool.than_Six_Total(mContext)) {
            mContext.alertIntentLive();
            return;
        }
    }

    private void startPlay(LookInfo channeLnfo, boolean isResult) {
        six_Total();
        Intent intent = new Intent(mContext, SuperVideoDetailsActivity.class);
        Bundle bundle = new Bundle();
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setAddress_hd(channeLnfo.getAddress_hd());
        videoInfo.setAddress_sd(channeLnfo.getAddress_sd());
        videoInfo.setId(channeLnfo.getId());
        videoInfo.setName(channeLnfo.getName());
        videoInfo.setThreeVideo(false);
        videoInfo.setSpare1(channeLnfo.getSpare1());;
        videoInfo.setLookVideo(true);
        bundle.putSerializable("videoInfo", videoInfo);
        intent.putExtras(bundle);
        intent.putExtra("isLive", false);
        if (isResult) {
            Multi.Moon_LEVE = Multi.Moon_LEVE1;
            mContext.startActivityForResult(intent, Multi.Moon_LEVE);
        } else {
            mContext.startActivity(intent);
        }
    }

//    Dialog dialog_pay_time;
//
//    public void alertVipPay() {
//        ScreenTool.setLight(mContext, 250);
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_look_count, null);
//        //对话框
//        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
//        if (dialog_pay_time == null) {
//            dialog_pay_time = new Dialog(mContext, R.style.Dialog);
//            dialog_pay_time.show();
//            dialog_pay_time.setCancelable(true);
//            Window window = dialog_pay_time.getWindow();
//            window.getDecorView().setPadding(0, 0, 0, 0);
//            WindowManager.LayoutParams lp = window.getAttributes();
//            layout.getBackground().setAlpha(150);
//            lp.width = WindowManager.LayoutParams.MATCH_PARENT;//ScreenTool.getWidth(this) / 5 * 3;
//            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            window.setAttributes(lp);
//            window.setContentView(layout);
//            LinearLayout left = (LinearLayout) layout.findViewById(R.id.left);
//            left.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog_pay_time.dismiss();
//                    startPlay(lookInfoDialog, isResult);
//                    dialog_pay_time = null;
//                }
//            });
//            TextView look_text = (TextView) layout.findViewById(R.id.look_text);
//            TextView total = (TextView) layout.findViewById(R.id.total);
//            total.setText(VipTool.get_ShiKan_Video_Count() + "次");
//            if (VipTool.than_Shi_Kan_Six_Video()) {
//                look_text.setText("您的试看次数已经看完,请成为会员观看!");
//                total.setText("充值");
//                left.setEnabled(false);
//            } else {
//                left.setEnabled(true);
//            }
//            LinearLayout right = (LinearLayout) layout.findViewById(R.id.right);
//            right.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog_pay_time.dismiss();
//                    Multi.Moon_LEVE = Multi.Moon_LEVE1;
//                    Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
//                    mContext.startActivity(new Intent(mContext, WXPayEntryActivity.class));
//                    dialog_pay_time = null;
//                }
//            });
//        } else {
//            dialog_pay_time.show();
//        }
//    }

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
//            vip_level_decription.setText("充值成为黄金会员观看");
//        } else {
//            dialog_pay_time.show();
//        }
//    }
}
