package com.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.app.Activity.MainActivity;
import com.app.Adapter.Three_MultiAdapter;
import com.app.Bean.Multi;
import com.app.Model.CompreHenSiveMode;
import com.app.Model.ComprehenSiveInfo;
import com.app.Net.JsonUtils;
import com.app.Net.NetInterface;
import com.app.Net.OkHttp;
import com.third.app.R;
import com.app.Tool.AesTool;
import com.app.Tool.NetTool;
import com.app.Tool.ScreenTool;
import com.app.Tool.VipTool;
import com.app.View.Look_MarginDecoration;
import com.app.View.T;
import com.shizhefei.fragment.LazyFragment;
import com.wjdz.rmgljtsc.wxapi.PayActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ThreeFragment extends LazyFragment implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.jarpanse_recyclerview)
    RecyclerView jarpanseRecyclerview;
    @Bind(R.id.jarpanse_swipe_fresh)
    SwipeRefreshLayout jarpanseSwipeFresh;
    @Bind(R.id.progressbar)
    RelativeLayout progressbar;
    private MainActivity mContext;
    private View rootView;
    private List<Multi> jarpanse_Multi = new ArrayList<>();
    private Three_MultiAdapter jar_multiAdapter;
    private boolean isFresh = false;
    private boolean onLoadMore = false;
    private int delayMillis = 10;
    private int page = 0;
    private int mCurrentCounter = 0;
    private int mTOTAL_COUNTER = 10000;
    private boolean isErr;
    private static final int PAGE_SIZE = 18;
    private List<ComprehenSiveInfo> aesLookInfoList;
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
                    isErr = true;
                    T.showTextToast(mContext, "请求失败!!!");
                    mContext.getProgressBar().dismiss();
                    jarpanseSwipeFresh.setRefreshing(false);
                    break;
                case GO_JSON_DATA_FAILED:
                    T.showTextToast(mContext, "系统繁忙!!请稍候下拉刷新试试!!");
                    mContext.getProgressBar().dismiss();
                    jarpanseSwipeFresh.setRefreshing(false);
                    break;
                case GO_CLOSE_DIAOLOG:
                    mContext.getProgressBar().dismiss();
                    jarpanseSwipeFresh.setRefreshing(false);
                    break;
                case GO_FREFRESH_DATA:
                    refreshData();
                    break;
                case GO_ONLOADMORE_DATA:
                    onLoadMore();
                    break;
                case GO_NOTIFY_DATA:
                    adapterData();
                    break;
                case GO_RECEIVE_EMPTY:
                    receiveEmptyMessage();
                    break;
            }
        }
    };

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        mContext = (MainActivity) getActivity();
        rootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_three, null);
        setContentView(rootView);
        ButterKnife.bind(this, rootView);
        initView();
        initData();
    }

    private void initView() {
        mContext.getProgressBar().show();
        jarpanseSwipeFresh.setOnRefreshListener(this);
        jarpanseSwipeFresh.setRefreshing(false);
        jarpanseRecyclerview.addItemDecoration(new Look_MarginDecoration(mContext));
        jarpanseRecyclerview.setHasFixedSize(true);
        jarpanseRecyclerview.setLayoutManager(new GridLayoutManager(mContext, 2));
        jar_multiAdapter = new Three_MultiAdapter(jarpanse_Multi, mContext);
        jar_multiAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int i) {
                return jarpanse_Multi.get(i).getSpanSize();
            }
        });
        jar_multiAdapter.setOnLoadMoreListener(this);
        jar_multiAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        jarpanseRecyclerview.setAdapter(jar_multiAdapter);
    }

    private void initData() {
        getNetLookData(Multi.COMPREHENSIVE_DIANYING / 1000 + "");
    }


    private void getNetLookData(String type) {
        if (!NetTool.isConnected(mContext)) {
            mContext.getProgressBar().dismiss();
            T.showTextToast(mContext, "您的网络没有连接，请检查您的网络,然后下拉刷新试试!!");
            jarpanseSwipeFresh.setRefreshing(false);
            mContext.getProgressBar().dismiss();
            isErr = true;
            progressbar.setVisibility(View.VISIBLE);
            return;
        }
        progressbar.setVisibility(View.GONE);
        page++;
        String ip = AesTool.encrypt(NetInterface.REQUEST_IP);
        CompreHenSiveMode compreHenSiveMode = new CompreHenSiveMode();
        compreHenSiveMode.setIp(AesTool.encrypt(ip));
        compreHenSiveMode.setPagesize(AesTool.encrypt(PAGE_SIZE + ""));
        compreHenSiveMode.setType(AesTool.encrypt(type));
        compreHenSiveMode.setPageindex(AesTool.encrypt(String.valueOf(page)));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ip", compreHenSiveMode.getIp());
            jsonObject.put("type", compreHenSiveMode.getType());
            jsonObject.put("pageindex", compreHenSiveMode.getPageindex());
            jsonObject.put("pagesize", compreHenSiveMode.getPagesize());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String aesJson = jsonObject.toString();
        OkHttpClient mOkHttpClient = OkHttp.getInstance();
        RequestBody formBody = new FormBody.Builder()
                .add(NetInterface.REQUEST_HEADER, aesJson)
                .build();
        Request request = new Request.Builder()
                .url(NetInterface.USER_COMPREHENSIVE_DATA)
                .post(formBody)
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
                    List<org.json.JSONObject> jsonObjectList = JsonUtils.parseJsonArray(json);
                    aesLookInfoList = new ArrayList<ComprehenSiveInfo>();
                    for (int i = 0; i < jsonObjectList.size(); i++) {
                        ComprehenSiveInfo comprehenSiveInfo = new ComprehenSiveInfo();
                        comprehenSiveInfo.setName(AesTool.decrypt(jsonObjectList.get(i).getString("name")));
                        comprehenSiveInfo.setPic(AesTool.decrypt(jsonObjectList.get(i).getString("pic")));
                        comprehenSiveInfo.setAddress_sd(AesTool.decrypt(jsonObjectList.get(i).getString("address_sd")));
                        comprehenSiveInfo.setAddress_hd(AesTool.decrypt(jsonObjectList.get(i).getString("address_hd")));
                        comprehenSiveInfo.setPic_heng(AesTool.decrypt(jsonObjectList.get(i).getString("pic_heng")));
                        comprehenSiveInfo.setRandomstr(AesTool.decrypt(jsonObjectList.get(i).getString("randomstr")));
                        aesLookInfoList.add(comprehenSiveInfo);
                    }
                    if (aesLookInfoList == null || aesLookInfoList.size() == 0) {
                        mHandler.sendEmptyMessage(GO_JSON_DATA_FAILED);
                        return;
                    }
                    isErr = false;
                    if (isFresh) {
                        isFresh = false;
                        mHandler.sendEmptyMessage(GO_FREFRESH_DATA);
                    } else if (onLoadMore) {
                        mHandler.sendEmptyMessage(GO_ONLOADMORE_DATA);
                    } else {
                        jarpanse_Multi.addAll(initJarpanseData(Integer.parseInt(String.valueOf(page)), aesLookInfoList));
                        mHandler.sendEmptyMessage(GO_NOTIFY_DATA);
                    }
                    mHandler.sendEmptyMessage(GO_CLOSE_DIAOLOG);
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(GO_JSON_DATA_FAILED);
                }
            }
        });
    }

    private List<Multi> initJarpanseData(int page, List<ComprehenSiveInfo> comprehenSiveInfoList) {
        List<Multi> jarMutils_Data = new ArrayList<>();
        if (comprehenSiveInfoList == null || comprehenSiveInfoList.size() == 0) {
            return jarMutils_Data;
        }
        jarMutils_Data.add(page == 1 && Multi.COMPREHENSIVE_CURRENT == Multi.COMPREHENSIVE_DUANSHIPIN ? new Multi(new Object(), Multi.JAR_HEADER, Multi.LOOK_NORMAL_SPAN_SIZE) :
                new Multi(new Object(), Multi.JAR_EMPTY, Multi.LOOK_NORMAL_SPAN_SIZE));
//        if (page == 1) {
//            jarMutils_Data.add(new Multi(new Object(), Multi.JAR_HEADER, Multi.JARPAN_NORMAL_SPAN_SIZE));
//        }
        for (ComprehenSiveInfo comprehenSiveInfo1 : comprehenSiveInfoList) {
            jarMutils_Data.add(new Multi(comprehenSiveInfo1, Multi.THREE_ITEM, Multi.ITEM_SPAN_SIZE));
        }
        mCurrentCounter += comprehenSiveInfoList.size();
        return jarMutils_Data;
    }

    private void adapterData() {
        jar_multiAdapter.notifyDataSetChanged();
        mContext.getProgressBar().dismiss();
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onResumeLazy() {
        super.onResumeLazy();
        initPayLevel();
    }

    @Override
    protected void onPauseLazy() {
        super.onPauseLazy();
    }

    public void initPayLevel() {
        Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
    }

    @Override
    public void onRefresh() {
        jar_multiAdapter.setEnableLoadMore(true);
        isFresh = true;
        onLoadMore = false;
        page = 0;
        getNetLookData(Multi.COMPREHENSIVE_DIANYING / 1000 + "");
    }

    @Override
    public void onLoadMoreRequested() {
        jarpanseSwipeFresh.setEnabled(false);
        isFresh = false;
        onLoadMore = true;
        getNetLookData(Multi.COMPREHENSIVE_DIANYING / 1000 + "");
    }

    private void onLoadMore() {
        jarpanseRecyclerview.postDelayed(new Runnable() {
            @Override
            public void run() {
//              Log.i("MainLog", "three_mCurrentCounter==" + three_mCurrentCounter + " three_TOTAL_COUNTER" + three_TOTAL_COUNTER);
                if (mCurrentCounter >= mTOTAL_COUNTER) {
                    //jar_multiAdapter.loadMoreEnd();
                } else {
                    if (!isErr) {
                        page++;
                        jar_multiAdapter.addData(initJarpanseData(Integer.parseInt(String.valueOf(page)), aesLookInfoList));
                        jar_multiAdapter.loadMoreComplete();
                    } else {
                        Toast.makeText(mContext, "加载失败，请重新再试!", Toast.LENGTH_LONG).show();
                        jar_multiAdapter.loadMoreFail();
                    }
                }
                jarpanseSwipeFresh.setEnabled(true);
            }
        }, delayMillis);
    }

    private void refreshData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                page = 1;
                jarpanse_Multi.clear();
                jarpanse_Multi.addAll(initJarpanseData(Integer.parseInt(String.valueOf(page)), aesLookInfoList));
//              three_Position = 0;
//              three_Id = 0;
                mCurrentCounter = PAGE_SIZE;
                jarpanseSwipeFresh.setRefreshing(false);
                isErr = false;
                jar_multiAdapter.setEnableLoadMore(true);
                jar_multiAdapter.setNewData(jarpanse_Multi);
            }
        }, delayMillis);
    }

    private void receiveEmptyMessage() {
//        if (Multi.COMPREHENSIVE_CURRENT == Multi.COMPREHENSIVE_DUANSHIPIN) {
//            Multi.COMPREHENSIVE_CURRENT = Multi.COMPREHENSIVE_DIANYING;
//            isFresh = false;
//            onLoadMore = true;
//            page = 0;
//            getNetLookData(Multi.COMPREHENSIVE_CURRENT / 1000 + "");
//        } else if (Multi.COMPREHENSIVE_CURRENT == Multi.COMPREHENSIVE_DIANYING) {
//            jar_multiAdapter.loadMoreEnd();
//        }
        jar_multiAdapter.loadMoreEnd();
        jarpanseSwipeFresh.setEnabled(true);
        mContext.getProgressBar().dismiss();
        if (!VipTool.canVip1(mContext)) {
            alertVipPay();
        }
    }

    Dialog dialog_pay_time;

    public void alertVipPay() {
        ScreenTool.setLight(mContext,250);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_loremore, null);
        //对话框
        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
        if (dialog_pay_time == null) {
            dialog_pay_time = new Dialog(mContext, R.style.Dialog);
            dialog_pay_time.show();
            dialog_pay_time.setCancelable(false);
            Window window = dialog_pay_time.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
//            layout.getBackground().setAlpha(150);
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;//ScreenTool.getWidth(this) / 5 * 3;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setContentView(layout);

            ImageButton pay = (ImageButton) layout.findViewById(R.id.pay);
            pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_pay_time.dismiss();
                    //v1
                    Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
                    Intent intent = new Intent(mContext, PayActivity.class);
                    mContext.startActivity(intent);
                }
            });
            ImageButton cancel = (ImageButton) layout.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_pay_time.dismiss();
                }
            });
        } else {
            dialog_pay_time.show();
        }
    }
}
