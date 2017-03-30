package com.app.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.app.Activity.MainActivity;
import com.app.Adapter.Code_MultiAdapter;
import com.app.Bean.LookType;
import com.app.Bean.Multi;
import com.app.DBManager.DBManager;
import com.app.Model.LookInfo;
import com.third.app.R;
import com.app.Tool.NetTool;
import com.app.View.Look_MarginDecoration;
import com.shizhefei.fragment.LazyFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CodeFragment extends LazyFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.code_recyclerview)
    RecyclerView codeRecyclerview;
    @Bind(R.id.code_swipe_fresh)
    SwipeRefreshLayout codeSwipeFresh;
    @Bind(R.id.progressbar)
    RelativeLayout progressbar;
    private MainActivity mContext;
    private View rootView;
    private List<Multi> cood_Multi = new ArrayList<>();
    private Code_MultiAdapter code_multiAdapter;
    private boolean isFresh = false;
    private int delayMillis = 10;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        mContext = (MainActivity) getActivity();
        rootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_code, null);
        setContentView(rootView);
        ButterKnife.bind(this, rootView);
        initView();
        initData();
    }

    private void initView() {
        mContext.getProgressBar().show();
        codeSwipeFresh.setOnRefreshListener(this);
        codeSwipeFresh.setRefreshing(false);
        codeRecyclerview.setHasFixedSize(true);
        codeRecyclerview.addItemDecoration(new Look_MarginDecoration(mContext));
        codeRecyclerview.setLayoutManager(new GridLayoutManager(mContext, 2));
        code_multiAdapter = new Code_MultiAdapter(cood_Multi, mContext);
        code_multiAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int i) {
                return cood_Multi.get(i).getSpanSize();
            }
        });
//      look_multiAdapter.setOnLoadMoreListener(this);
        code_multiAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        codeRecyclerview.setAdapter(code_multiAdapter);
    }

    private void initData() {
        if (DBManager.getDBManager(mContext).queryLookCodeAll().size() != 0) {
            cood_Multi.addAll(initCodeMutilData());
            adapterData();
        } else {
            if (!NetTool.isConnected(mContext)) {
                mContext.getProgressBar().dismiss();
                progressbar.setVisibility(View.VISIBLE);
            } else {
                progressbar.setVisibility(View.GONE);
            }
        }
    }

    private void adapterData() {
        code_multiAdapter.notifyDataSetChanged();
        mContext.getProgressBar().dismiss();
    }

    private List<Multi> initCodeMutilData() {
        List<LookInfo> aesLookInfoList = DBManager.getDBManager(mContext).queryLookCodeAll();
        List<LookInfo> mingxingList = new ArrayList<>();
        List<LookInfo> zhifuList = new ArrayList<>();
        List<LookInfo> zhainanList = new ArrayList<>();
        List<LookInfo> zuijinList = new ArrayList<>();
        List<LookInfo> gifList = new ArrayList<>();
        List<Multi> codeMutils_Data = new ArrayList<>();
        try {
            for (LookInfo lookInfo : aesLookInfoList) {
                if (lookInfo.getType().equals("" + LookType.LOOK1)) {
                } else if (lookInfo.getType().equals("" + LookType.LOOK2)) {
                } else if (lookInfo.getType().equals("" + LookType.LOOK3)) {
                } else if (lookInfo.getType().equals("" + LookType.LOOK4)) {
                } else if (lookInfo.getType().equals("" + LookType.LOOK5)) {
                } else if (lookInfo.getType().equals("" + LookType.LOOK6)) {
                } else if (lookInfo.getType().equals("" + LookType.LOOK7)) {
                } else if (lookInfo.getType().equals("" + LookType.LOOK8)) {
                } else if (lookInfo.getType().equals("" + LookType.LOOK9)) {
                    // 9.gif图片10.明星女优11.制服诱惑12.宅男福利13.最近更新)
                    if (gifList.size() >= 4) continue;
                    gifList.add(lookInfo);
                } else if (lookInfo.getType().equals("" + LookType.LOOK10)) {
                    if (mingxingList.size() >= 4) continue;
                    mingxingList.add(lookInfo);
                } else if (lookInfo.getType().equals("" + LookType.LOOK11)) {
                    if (zhifuList.size() >= 4) continue;
                    zhifuList.add(lookInfo);
                } else if (lookInfo.getType().equals("" + LookType.LOOK12)) {
                    if (zhainanList.size() >= 4) continue;
                    zhainanList.add(lookInfo);
                } else if (lookInfo.getType().equals("" + LookType.LOOK13)) {
                    if (zuijinList.size() >= 4) continue;
                    zuijinList.add(lookInfo);
                }
            }
//          int[] randomPosotion = RandomTool.randomCommon(0, 3, 4);
            codeMutils_Data.add(new Multi("" + LookType.LOOK2, Multi.CODE_MINGXINGNVYOU, Multi.LOOK_NORMAL_SPAN_SIZE));
            codeMutils_Data.add(new Multi(gifList.get(0), Multi.CODE_GIF, Multi.LOOK_NORMAL_SPAN_SIZE));
            for (LookInfo lookInfo : mingxingList) {
                codeMutils_Data.add(new Multi(lookInfo, Multi.CODE_ITEM, Multi.ITEM_SPAN_SIZE));
            }

            codeMutils_Data.add(new Multi("" + LookType.LOOK6, Multi.CODE_ZHIFUYOUHUO, Multi.LOOK_NORMAL_SPAN_SIZE));
            codeMutils_Data.add(new Multi(gifList.get(1), Multi.CODE_GIF, Multi.LOOK_NORMAL_SPAN_SIZE));
            for (LookInfo lookInfo : zhifuList) {
                codeMutils_Data.add(new Multi(lookInfo, Multi.CODE_ITEM, Multi.ITEM_SPAN_SIZE));
            }

            codeMutils_Data.add(new Multi("" + LookType.LOOK5, Multi.CODE_ZHAINANFULI, Multi.LOOK_NORMAL_SPAN_SIZE));
            codeMutils_Data.add(new Multi(gifList.get(2), Multi.CODE_GIF, Multi.LOOK_NORMAL_SPAN_SIZE));
            for (LookInfo lookInfo : zhainanList) {
                codeMutils_Data.add(new Multi(lookInfo, Multi.CODE_ITEM, Multi.ITEM_SPAN_SIZE));
            }

            codeMutils_Data.add(new Multi("" + LookType.LOOK7, Multi.CODE_ZUIJINGENGXIN, Multi.LOOK_NORMAL_SPAN_SIZE));
            codeMutils_Data.add(new Multi(gifList.get(3), Multi.CODE_GIF, Multi.LOOK_NORMAL_SPAN_SIZE));
            for (LookInfo lookInfo : zuijinList) {
                codeMutils_Data.add(new Multi(lookInfo, Multi.CODE_ITEM, Multi.ITEM_SPAN_SIZE));
            }
            codeMutils_Data.add(new Multi(new Object(), Multi.LOOK_BOTTOM, Multi.LOOK_NORMAL_SPAN_SIZE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codeMutils_Data;
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
        codeSwipeFresh.setRefreshing(true);
        code_multiAdapter.setEnableLoadMore(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (NetTool.isConnected(mContext)) progressbar.setVisibility(View.GONE);
                cood_Multi.clear();
                cood_Multi.addAll(initCodeMutilData());
                codeSwipeFresh.setRefreshing(false);
                code_multiAdapter.setEnableLoadMore(true);
                code_multiAdapter.setNewData(cood_Multi);
            }
        }, delayMillis);
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
