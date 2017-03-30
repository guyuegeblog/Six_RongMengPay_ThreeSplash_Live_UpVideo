package com.app.Adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.app.Activity.MainActivity;
import com.app.Bean.Multi;
import com.app.Model.ComprehenSiveInfo;
import com.third.app.R;
import com.app.Tool.VipTool;
import com.app.View.T;

import java.util.List;

/**
 * Created by lin on 2016/11/24.
 */
public class PianKu_MultiAdapter extends BaseMultiItemQuickAdapter<Multi, BaseViewHolder> {

    private MainActivity mContext;

    public PianKu_MultiAdapter(List<Multi> data, MainActivity mContext) {
        super(data);
        addItemType(Multi.JAR_ITEM, R.layout.pianku_item);
        this.mContext = mContext;
    }


    @Override
    protected void convert(BaseViewHolder baseViewHolder, final Multi multi) {
        switch (baseViewHolder.getItemViewType()) {
            case Multi.JAR_HEADER:
                //....
                break;
            case Multi.JAR_ITEM:
                ImageView imageView_Pic = (ImageView) baseViewHolder.getView(R.id.images);
                Glide.with(mContext).load(multi.getComprehenSiveInfo().getPic()).placeholder(R.drawable.allloading).
                        error(R.drawable.allloading).into(imageView_Pic);
                imageView_Pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doPlay(multi.getComprehenSiveInfo());
                    }
                });
                break;
            case Multi.JAR_EMPTY:
                //....
                break;
        }
    }

    public void doPlay(ComprehenSiveInfo channeLnfo) {
        if (!VipTool.userIsLoginSucces(mContext)) {
            T.showTextToast(mContext, "请您重新启动App,完成自动登录后再播放视频!!");
            return;
        }
        int vipType = VipTool.getUserVipType(mContext);
//        if (vipType == Multi.VIP_SILVER_TYPE || vipType == Multi.VIP_GOLD_TYPE || vipType == Multi.VIP_PLAT_NIUM_TYPE || vipType == Multi.VIP_DIAMOND_TYPE) {
//            Intent intent = new Intent(mContext, SuperVideoDetailsActivity.class);
//            Bundle bundle = new Bundle();
//            VideoInfo videoInfo = new VideoInfo();
//            videoInfo.setId("0");
//            videoInfo.setAddress_hd(channeLnfo.getAddress_hd());
//            videoInfo.setAddress_sd(channeLnfo.getAddress_sd());
//            videoInfo.setName(channeLnfo.getName());
//            videoInfo.setThreeVideo(false);
//            Log.i("videoStr", "白银 " + channeLnfo.getAddress_hd());
//            videoInfo.setAddress_hd(videoInfo.getAddress_hd().replace("https", "http"));
//            videoInfo.setAddress_sd(videoInfo.getAddress_sd().replace("https", "http"));
//            bundle.putSerializable("videoInfo", videoInfo);
//            intent.putExtras(bundle);
//            intent.putExtra("isLive", false);
//            mContext.startActivity(intent);
//        } else {
//            alertVipPay();
//        }
        mContext.alertDialogPay();
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
