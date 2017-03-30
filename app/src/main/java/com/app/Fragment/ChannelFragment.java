package com.app.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.app.Activity.MainActivity;
import com.app.Adapter.Channel_MultiAdapter;
import com.app.Bean.Multi;
import com.app.Model.ChanneLnfo;
import com.app.Net.JsonUtils;
import com.app.Net.NetInterface;
import com.app.Net.OkHttp;
import com.third.app.R;
import com.app.Tool.AesTool;
import com.app.Tool.NetTool;
import com.app.View.Look_MarginDecoration;
import com.app.View.T;
import com.shizhefei.fragment.LazyFragment;

import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChannelFragment extends LazyFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.channel_recyclerview)
    RecyclerView channelRecyclerview;
    @Bind(R.id.channel_swipe_fresh)
    SwipeRefreshLayout channelSwipeFresh;
    @Bind(R.id.progressbar)
    RelativeLayout progressbar;
    private int delayMillis = 10;
    private boolean isFresh = false;
    private List<Multi> channel_Multi = new ArrayList<>();
    private Channel_MultiAdapter channel_multiAdapter;
    private List<ChanneLnfo> aesLookInfoList;
    private MainActivity mContext;
    private View rootView;

    private final int GO_LOOK_REQUEST_FAILED = 1000;
    private final int GO_JSON_DATA_FAILED = 1020;
    private final int GO_CLOSE_DIAOLOG = 1030;
    private final int GO_FREFRESH_DATA = 1010;
    private final int GO_ONLOADMORE_DATA = 1034;
    private final int GO_NOTIFY_DATA = 1042;
    private final int GO_RECEIVE_EMPTY = 1044;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_LOOK_REQUEST_FAILED:
                    T.showTextToast(mContext, "请求失败!!!");
                    mContext.getProgressBar().dismiss();
                    channelSwipeFresh.setRefreshing(false);
                    break;
                case GO_JSON_DATA_FAILED:
                    T.showTextToast(mContext, "系统繁忙!!请稍候下拉刷新试试!!");
                    mContext.getProgressBar().dismiss();
                    channelSwipeFresh.setRefreshing(false);
                    break;
                case GO_CLOSE_DIAOLOG:
                    mContext.getProgressBar().dismiss();
                    break;
                case GO_FREFRESH_DATA:
                    refreshData();
                    break;
                case GO_NOTIFY_DATA:
                    adapterData();
                    break;
            }
        }
    };

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        mContext = (MainActivity) getActivity();
        rootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_channel, null);
        setContentView(rootView);
        ButterKnife.bind(this, rootView);
        initView();
        initData();
    }

    private void initView() {
        mContext.getProgressBar().show();
        channelSwipeFresh.setOnRefreshListener(this);
        channelSwipeFresh.setRefreshing(false);
        channelRecyclerview.addItemDecoration(new Look_MarginDecoration(mContext));
        channelRecyclerview.setHasFixedSize(true);
        channelRecyclerview.setLayoutManager(new GridLayoutManager(mContext, 2));
        channel_multiAdapter = new Channel_MultiAdapter(channel_Multi, mContext);
        channel_multiAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int i) {
                return channel_Multi.get(i).getSpanSize();
            }
        });
//      channel_multiAdapter.setOnLoadMoreListener(this);
        channel_multiAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        channelRecyclerview.setAdapter(channel_multiAdapter);
    }

    private void initData() {
        getNetLookData();
    }


    private void getNetLookData() {
        mContext.getProgressBar().show();
        if (!NetTool.isConnected(mContext)) {
            T.showTextToast(mContext, "您的网络没有连接，请检查您的网络,然后下拉刷新试试!!");
            mContext.getProgressBar().dismiss();
            channelSwipeFresh.setRefreshing(false);
            progressbar.setVisibility(View.VISIBLE);
            return;
        }
        progressbar.setVisibility(View.GONE);
        OkHttpClient mOkHttpClient = OkHttp.getInstance();
//      RequestBody formBody = new FormBody.Builder()
//                .add(NetInterface.REQUEST_HEADER, aesJson)
//                .build();
        Request request = new Request.Builder()
                .url(NetInterface.USER_FIRST_LEVEL_TYPE_DATA)
//              .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(GO_LOOK_REQUEST_FAILED);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String json = response.body().string();
                    if (json.equals("[]")) {
                        mHandler.sendEmptyMessage(GO_RECEIVE_EMPTY);
                        return;
                    }
                    List<JSONObject> jsonObjectList = JsonUtils.parseJsonArray(json);
                    aesLookInfoList = new ArrayList<ChanneLnfo>();
                    for (int i = 0; i < jsonObjectList.size(); i++) {
                        ChanneLnfo channeLnfo = new ChanneLnfo();
                        channeLnfo.setName(AesTool.decrypt(jsonObjectList.get(i).getString("name")));
                        channeLnfo.setPic(AesTool.decrypt(jsonObjectList.get(i).getString("pic")));
                        channeLnfo.setType(AesTool.decrypt(jsonObjectList.get(i).getString("type")));
                        channeLnfo.setPic_heng(AesTool.decrypt(jsonObjectList.get(i).getString("pic_heng")));
                        channeLnfo.setIsvip(AesTool.decrypt(jsonObjectList.get(i).getString("isvip")));
                        aesLookInfoList.add(channeLnfo);
                    }
                    if (aesLookInfoList == null || aesLookInfoList.size() == 0) {
                        mHandler.sendEmptyMessage(GO_JSON_DATA_FAILED);
                        return;
                    }
                    if (isFresh) {
                        isFresh = false;
                        mHandler.sendEmptyMessage(GO_FREFRESH_DATA);
                    } else {
                        channel_Multi.addAll(initChannelData(aesLookInfoList));
                        mHandler.sendEmptyMessage(GO_NOTIFY_DATA);
                    }
                    mHandler.sendEmptyMessage(GO_CLOSE_DIAOLOG);
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(GO_JSON_DATA_FAILED);
                }
            }
        });
    }

    private List<Multi> initChannelData(List<ChanneLnfo> channeLnfoList) {
        List<Multi> chanelMutils_Data = new ArrayList<>();
        if (channeLnfoList == null || channeLnfoList.size() == 0) {
            return chanelMutils_Data;
        }
        chanelMutils_Data.add(new Multi(new Object(), Multi.JAR_HEADER, Multi.LOOK_NORMAL_SPAN_SIZE));
        for (ChanneLnfo comprehenSiveInfo1 : channeLnfoList) {
            chanelMutils_Data.add(new Multi(comprehenSiveInfo1, Multi.CHANNEL_ITEM, Multi.ITEM_SPAN_SIZE));
        }
        return chanelMutils_Data;
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onResumeLazy() {
        super.onResumeLazy();
        Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
    }

    @Override
    protected void onPauseLazy() {
        super.onPauseLazy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        channel_multiAdapter.setEnableLoadMore(false);
        isFresh = true;
        getNetLookData();
    }

    private void refreshData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                channel_Multi.clear();
                channel_Multi.addAll(initChannelData(aesLookInfoList));
//              three_Position = 0;
//              three_Id = 0;
                channelSwipeFresh.setRefreshing(false);
                channel_multiAdapter.setEnableLoadMore(true);
                channel_multiAdapter.setNewData(channel_Multi);
            }
        }, delayMillis);
    }

    private void adapterData() {
        channel_multiAdapter.notifyDataSetChanged();
        mContext.getProgressBar().dismiss();
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // TODO: inflate a fragment view
//        View rootView = super.onCreateView(inflater, container, savedInstanceState);
//        ButterKnife.bind(this, rootView);
//        return rootView;
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        ButterKnife.unbind(this);
//    }
}
