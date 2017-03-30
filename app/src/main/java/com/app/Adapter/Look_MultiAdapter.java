package com.app.Adapter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.app.Activity.LoadMoreActivity;
import com.app.Activity.MainActivity;
import com.app.Activity.MessageActivity;
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
import com.app.View.GlideImageLoader;
import com.app.View.T;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2016/11/24.
 */
public class Look_MultiAdapter extends BaseMultiItemQuickAdapter<Multi, BaseViewHolder> {

    private MainActivity mContext;

    public Look_MultiAdapter(List<Multi> data, MainActivity mContext) {
        super(data);
        addItemType(Multi.LOOK_BANNER, R.layout.activity_gallery);
        addItemType(Multi.LOOK_ITEM, R.layout.look_item);
        addItemType(Multi.LOOK_AREA, R.layout.activity_shikan_zhuanqu);
//        addItemType(Multi.LOOK_JARPANSE_JINGDIAN, R.layout.activity_daoguo_jingdian);
//        addItemType(Multi.LOOK_YOUNG_WOMAN, R.layout.activity_jiqing_shaofu);
//        addItemType(Multi.LOOK_BEAUTIFUAL_WOMAN, R.layout.activity_meinv_nenmo);
//        addItemType(Multi.LOOK_MEITUISIEA, R.layout.activity_meitui_siwa);
//        addItemType(Multi.LOOK_JARPANSE_SUREN, R.layout.activity_daoguo_suren);
        addItemType(Multi.LOOK_BOTTOM, R.layout.look_bottom);
        this.mContext = mContext;
    }


    @Override
    protected void convert(BaseViewHolder baseViewHolder, final Multi multi) {
        switch (baseViewHolder.getItemViewType()) {
            case Multi.LOOK_BANNER:
                //....
                List<String> titles = new ArrayList<>();
                List<String> imgUrls = new ArrayList<>();
                Banner banner = baseViewHolder.getView(R.id.banner);
                banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
                //设置标题列表
                for (int i = 0; i < multi.getLookInfoList().size(); i++) {
                    titles.add(multi.getLookInfoList().get(i).getName());
                    imgUrls.add(multi.getLookInfoList().get(i).getPic());
                }
                banner.setBannerTitles(titles);
                //设置轮播间隔时间 在布局文件中设置了5秒
                banner.setDelayTime(3000);
                //设置动画
                //holder.banner.setBannerAnimation(Transformer.CubeOut);//立体
                banner.setBannerAnimation(com.youth.banner.Transformer.Accordion);//延伸.
                banner.setImageLoader(new GlideImageLoader());
                //设置图片集合
                banner.setImages(imgUrls);
                //设置点击事件
                banner.setOnBannerClickListener(new OnBannerClickListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        NetCheck();
                        LookInfo lookInfo = multi.getLookInfoList().get(position - 1);
                        doPlay(lookInfo, true);
                    }
                });
                //banner设置方法全部调用完毕时最后调用
                banner.start();
                break;
            case Multi.LOOK_ITEM:
                ImageView imageView = (ImageView) baseViewHolder.getView(R.id.images);
                Glide.with(mContext).load(multi.getLookInfo().getPic()).placeholder(R.drawable.allloading).
                        error(R.drawable.allloading).into(imageView);
                ((TextView) baseViewHolder.getView(R.id.see_count)).setText(RandomTool.getRandomNumbers(5));
                ((TextView) baseViewHolder.getView(R.id.name)).setText(multi.getLookInfo().getName());
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("videoInfo", multi.getLookInfo());
                        mContext.startActivity(new Intent(mContext, MessageActivity.class).putExtras(bundle));
                    }
                });

                break;
            case Multi.LOOK_AREA:
                //.....
                break;
            case Multi.LOOK_JARPANSE_JINGDIAN:
                //.....
                ((TextView) baseViewHolder.getView(R.id.loadmore)).setText("今日更新" + RandomTool.getRandomNumbers(2)
                        + "部,总" + RandomTool.getRandomNumbers(4) + "部");
                ((TextView) baseViewHolder.getView(R.id.loadmore_text)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NetCheck();
                        mContext.startActivity(new Intent(mContext, LoadMoreActivity.class).putExtra("typeId", multi.getChanneType()));
                    }
                });
                break;
            case Multi.LOOK_YOUNG_WOMAN:
                //.....
                ((TextView) baseViewHolder.getView(R.id.loadmore)).setText("今日更新" + RandomTool.getRandomNumbers(2)
                        + "部,总" + RandomTool.getRandomNumbers(4) + "部");
                ((TextView) baseViewHolder.getView(R.id.loadmore_text)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NetCheck();
                        mContext.startActivity(new Intent(mContext, LoadMoreActivity.class).putExtra("typeId", multi.getChanneType()));
                    }
                });
                break;
            case Multi.LOOK_BEAUTIFUAL_WOMAN:
                //....
                ((TextView) baseViewHolder.getView(R.id.loadmore)).setText("今日更新" + RandomTool.getRandomNumbers(2)
                        + "部,总" + RandomTool.getRandomNumbers(4) + "部");
                ((TextView) baseViewHolder.getView(R.id.loadmore_text)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NetCheck();
                        mContext.startActivity(new Intent(mContext, LoadMoreActivity.class).putExtra("typeId", multi.getChanneType()));
                    }
                });
                break;
            case Multi.LOOK_MEITUISIEA:
                //.....
                ((TextView) baseViewHolder.getView(R.id.loadmore)).setText("今日更新" + RandomTool.getRandomNumbers(2)
                        + "部,总" + RandomTool.getRandomNumbers(4) + "部");
                ((TextView) baseViewHolder.getView(R.id.loadmore_text)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NetCheck();
                        mContext.startActivity(new Intent(mContext, LoadMoreActivity.class).putExtra("typeId", multi.getChanneType()));
                    }
                });
                break;
            case Multi.LOOK_JARPANSE_SUREN:
                //.....
                ((TextView) baseViewHolder.getView(R.id.loadmore)).setText("今日更新" + RandomTool.getRandomNumbers(2)
                        + "部,总" + RandomTool.getRandomNumbers(4) + "部");
                ((TextView) baseViewHolder.getView(R.id.loadmore_text)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NetCheck();
                        mContext.startActivity(new Intent(mContext, LoadMoreActivity.class).putExtra("typeId", multi.getChanneType()));
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

    private void doPlay(LookInfo lookInfo, boolean isResult) {
        if (!VipTool.userIsLoginSucces(mContext)) {
            T.showTextToast(mContext, "请您重新启动App,完成自动登录后再播放视频!!");
            return;
        }
//        lookInfoDialog = lookInfo;
//        isResult = isResult;
        if (VipTool.getUserVipType(mContext) == Multi.VIP_NOT_VIP_TYPE) {
            if (VipTool.than_Shi_Kan_Six_Video(mContext)) {
                mContext.alertDialogPay();
            } else {
                startPlay(lookInfo, isResult);
            }
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
        videoInfo.setName(lookInfo.getName());
        videoInfo.setLookVideo(true);
        videoInfo.setThreeVideo(false);
        videoInfo.setSpare1(lookInfo.getSpare1());
        Log.i("videoStrsd",videoInfo.getAddress_hd());
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

    private void six_Total() {
        VipTool.Six_Total(mContext);
        if (VipTool.canVip1(mContext)) return;
        if (!TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.TV_USER_LIVE_UNBIND)))
            return;//表示已经解绑
        if (VipTool.than_Six_Total(mContext)) {
            mContext.alertIntentLive();
            return;
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
}
