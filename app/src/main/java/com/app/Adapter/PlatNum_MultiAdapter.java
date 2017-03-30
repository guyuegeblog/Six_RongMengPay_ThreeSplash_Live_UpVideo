package com.app.Adapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.app.Activity.MainActivity;
import com.app.Activity.SuperVideoDetailsActivity;
import com.app.Bean.Multi;
import com.app.Model.ComprehenSiveInfo;
import com.app.Model.VideoInfo;
import com.third.app.R;
import com.app.Tool.RandomTool;
import com.app.Tool.VipTool;
import com.app.View.T;

import java.util.List;

/**
 * Created by lin on 2016/11/24.
 */
public class PlatNum_MultiAdapter extends BaseMultiItemQuickAdapter<Multi, BaseViewHolder> {

    private MainActivity mContext;

    public PlatNum_MultiAdapter(List<Multi> data, MainActivity mContext) {
        super(data);
        addItemType(Multi.JAR_HEADER, R.layout.jarpanse_header);
        addItemType(Multi.JAR_ITEM, R.layout.jarpanse_item);
        addItemType(Multi.JAR_EMPTY, R.layout.jarpanse_empty);
        this.mContext = mContext;
    }


    @Override
    protected void convert(BaseViewHolder baseViewHolder, final Multi multi) {
        switch (baseViewHolder.getItemViewType()) {
            case Multi.JAR_HEADER:
                //....
                ((TextView) baseViewHolder.getView(R.id.video_count)).setText("当前片库约" + RandomTool.getRandomNumbers(4) + "部");
                break;
            case Multi.JAR_ITEM:
                ImageView imageView_Pic = (ImageView) baseViewHolder.getView(R.id.images);
                TextView textView_name = ((TextView) baseViewHolder.getView(R.id.name));
                TextView textView_seecount = ((TextView) baseViewHolder.getView(R.id.see_count));
                Glide.with(mContext).load(multi.getComprehenSiveInfo().getPic_heng()).placeholder(R.drawable.allloading).
                        error(R.drawable.allloading).into(imageView_Pic);
                textView_seecount.setText(RandomTool.getRandomNumbers(5));
                textView_name.setText(multi.getComprehenSiveInfo().getName());
                imageView_Pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doPlay(multi.getComprehenSiveInfo());
                    }
                });
                textView_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doPlay(multi.getComprehenSiveInfo());
                    }
                });
                textView_seecount.setOnClickListener(new View.OnClickListener() {
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
        if (vipType == Multi.VIP_PLAT_NIUM_TYPE
                || vipType== Multi.VIP_DIAMOND_TYPE) {
            Intent intent = new Intent(mContext, SuperVideoDetailsActivity.class);
            Bundle bundle = new Bundle();
            VideoInfo videoInfo = new VideoInfo();
            videoInfo.setId("0");
            videoInfo.setAddress_hd(channeLnfo.getAddress_hd());
            videoInfo.setAddress_sd(channeLnfo.getAddress_sd());
            videoInfo.setName(channeLnfo.getName());
            videoInfo.setThreeVideo(false);
            Log.i("videoStr", "白金 " + channeLnfo.getAddress_hd());
            videoInfo.setAddress_hd(videoInfo.getAddress_hd().replace("https","http"));
            videoInfo.setAddress_sd(videoInfo.getAddress_sd().replace("https","http"));
            bundle.putSerializable("videoInfo", videoInfo);
            intent.putExtras(bundle);
            intent.putExtra("isLive", false);
            intent.putExtra("isVipArea", true);
            mContext.startActivity(intent);
        } else {
            mContext.alertDialogPay();
        }
    }
}
