package com.app.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.app.Activity.ChannelActivity;
import com.app.Bean.Multi;
import com.third.app.R;
import com.app.Tool.NetTool;
import com.app.Tool.RandomTool;
import com.app.View.T;

import java.util.List;

/**
 * Created by lin on 2016/11/24.
 */
public class Channel_MultiAdapter extends BaseMultiItemQuickAdapter<Multi, BaseViewHolder> {

    private Activity mContext;

    public Channel_MultiAdapter(List<Multi> data, Activity mContext) {
        super(data);
        addItemType(Multi.CHANNEL_ITEM, R.layout.channel_first_item);
        addItemType(Multi.JAR_HEADER, R.layout.jarpanse_header);
        this.mContext = mContext;
    }


    @Override
    protected void convert(BaseViewHolder baseViewHolder, final Multi multi) {
        switch (baseViewHolder.getItemViewType()) {
            case Multi.CHANNEL_ITEM:
                Glide.with(mContext).load(multi.getChanneLnfo().getPic()).placeholder(R.drawable.allloading).
                        error(R.drawable.allloading).into((ImageView) baseViewHolder.getView(R.id.channel_images));
                ((TextView) baseViewHolder.getView(R.id.fragment_tuku_text)).setText(multi.getChanneLnfo().getName());
                ((ImageView) baseViewHolder.getView(R.id.channel_images)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!NetTool.isConnected(mContext)) {
                            T.showTextToast(mContext, "您的网络没有连接,请检查您的网络");
                            return;
                        }
                        mContext.startActivity(new Intent(mContext, ChannelActivity.class).putExtra("channel_type", multi.getChanneLnfo().getType()));
                    }
                });
                ((TextView) baseViewHolder.getView(R.id.fragment_tuku_text)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!NetTool.isConnected(mContext)) {
                            T.showTextToast(mContext, "您的网络没有连接,请检查您的网络");
                            return;
                        }
                        mContext.startActivity(new Intent(mContext, ChannelActivity.class).putExtra("channel_type", multi.getChanneLnfo().getType()));
                    }
                });
                break;
            case Multi.JAR_HEADER:
                //....
                ((TextView) baseViewHolder.getView(R.id.video_count)).setText("当前片库约" + RandomTool.getRandomNumbers(4) + "部");
                break;
        }
    }
}
