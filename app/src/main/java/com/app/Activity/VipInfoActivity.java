package com.app.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.Bean.Multi;
import com.app.Constant.Constant;
import com.third.app.R;
import com.app.Save.KeyFile;
import com.app.Save.KeyUser;
import com.app.Tool.AesTool;
import com.app.Tool.ParamsPutterTool;
import com.app.Tool.PhoneTool;
import com.app.Tool.ScreenTool;
import com.app.Tool.VipTool;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VipInfoActivity extends AppCompatActivity {

    @Bind(R.id.titletext)
    TextView titletext;
    @Bind(R.id.tv_username)
    TextView tvUsername;
    @Bind(R.id.tv_vip_type)
    TextView tvVipType;
    @Bind(R.id.tv_vip1_lasttime)
    TextView tvVip1Lasttime;
    @Bind(R.id.return_ok)
    ImageView returnOk;
    private Activity mContext;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_info);
        ButterKnife.bind(this);
        mContext = this;
        initView();
        initData();
    }

    private void initView() {
        ScreenTool.setLight(mContext, 255);
    }

    private void initData() {
        if (TextUtils.isEmpty(VipTool.getUserRandom(mContext))) {
            //防止本地数据库被删除
            userName = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.USER_NAME_KEY));
        } else {
            userName = VipTool.getUserRandom(mContext) + "" + PhoneTool.getPhoneTool(mContext).getAndroidId(mContext);
        }
        tvUsername.setText(PhoneTool.getPhoneTool(mContext).getAndroidId(mContext));
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_NOT_VIP_TYPE) {
            tvVipType.setText("普通试用用户");
        } else if (vipType == Multi.VIP_SILVER_TYPE) {
            tvVipType.setText("高级白银会员");
        } else if (vipType == Multi.VIP_GOLD_TYPE) {
            tvVipType.setText("高级黄金会员");
        } else if (vipType == Multi.VIP_PLAT_NIUM_TYPE) {
            tvVipType.setText("高级白金会员");
        } else if (vipType == Multi.VIP_DIAMOND_TYPE) {
            tvVipType.setText("高级钻石会员");
        } else if (vipType == Multi.VIP_RED_DIAMOND_TYPE) {
            tvVipType.setText("高级粉钻会员");
        }else if (vipType == Multi.VIP_CROWN_TYPE) {
            tvVipType.setText("高级皇冠会员");
        }

        String vip1 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIP_TIMES_KEY));
        if (TextUtils.isEmpty(vip1)) {
            tvVip1Lasttime.setText("所有(除直播)试看" + Constant.doDate + "秒");
        } else {
            tvVip1Lasttime.setText(vip1);
        }
    }

    @OnClick(R.id.return_ok)
    public void onClick() {
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("午夜啪啪会员信息界面"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("午夜啪啪会员信息界面"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }
}
