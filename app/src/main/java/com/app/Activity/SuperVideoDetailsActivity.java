package com.app.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.Bean.Multi;
import com.app.Constant.Constant;
import com.app.DBManager.DBManager;
import com.app.Model.CommentInfo;
import com.app.Model.VideoInfo;
import com.app.Net.NetInterface;
import com.app.Save.KeyFile;
import com.app.Save.KeyUser;
import com.app.Tool.AesTool;
import com.app.Tool.DateTool;
import com.app.Tool.FileTool;
import com.app.Tool.NetTool;
import com.app.Tool.ParamsPutterTool;
import com.app.Tool.RandomTool;
import com.app.Tool.ScreenTool;
import com.app.Tool.VipTool;
import com.app.VideoView.DensityUtil;
import com.app.VideoView.FullScreenVideoView;
import com.app.VideoView.LightnessController;
import com.app.VideoView.VolumnController;
import com.app.View.BarrageItem;
import com.app.View.BarrageView;
import com.app.View.T;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.third.app.R;
import com.umeng.analytics.MobclickAgent;
import com.wjdz.rmgljtsc.wxapi.PayActivity;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 类描述：视频详情页
 *
 * @author Super冬子
 * @time 2016-9-19
 */
public class SuperVideoDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.center_images)
    ImageView centerImages;
    @Bind(R.id.diamond_text)
    TextView diamondText;
    @Bind(R.id.progressbar)
    public ProgressBar progressbar;
    @Bind(R.id.tv_speed)
    public TextView tv_speed;
    @Bind(R.id.tv_time)
    TextView tv_time;
    @Bind(R.id.textview_live)
    TextView textviewLive;
    @Bind(R.id.video_name)
    TextView videoName;
    @Bind(R.id.full)
    ImageView full;
    @Bind(R.id.barra_send)
    ImageView barraSend;
    @Bind(R.id.barrage_switch)
    ImageView barrageSwitch;
    @Bind(R.id.video_back)
    ImageView videoBack;
    private boolean isLive;
    private VideoInfo videoInfo;
    public String video_title;
    private boolean isThree;
    private boolean isLookArea;
    private boolean isVipArea;//vip
    private boolean liveAreaDemand = false;
    private SuperVideoDetailsActivity mContext;
    private final int MESSAGE_UPDATE_BYTES = 602;
    private final int LOOK_TIME_END = 603;
    private final int REFRESH_LOOK_TIME = 604;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            /***
             *
             * 更新网速显示*/
            switch (msg.what) {
                case MESSAGE_UPDATE_BYTES:
                    if (tv_speed != null) {
                        TextPaint tp1 = tv_speed.getPaint();
                        tp1.setFakeBoldText(true);
                        tv_speed.setText(msg.obj + "Kb/S");
                    }
                    break;
                case 1:
                    if (isLive) {
                        mPlayTime.setText("00:00");
                        mSeekBar.setProgress(0);
                        mDurationTime.setText("00:00");
                    } else if (player.getCurrentPosition() > 0 && player.getDuration() > 0) {
                        mPlayTime.setText(formatTime(player.getCurrentPosition()));
                        //假进度
                        int progress;
                        if ((isLookArea && !isThree) || isVipArea) {
                            progress = player.getCurrentPosition() * 2 / player.getDuration();
                        } else {
                            progress = player.getCurrentPosition() * 100 / player.getDuration();
                        }
                        mSeekBar.setProgress(progress);
                        if (player.getCurrentPosition() > player.getDuration() - 100) {
                            mPlayTime.setText("00:00");
                            mSeekBar.setProgress(0);
                        }
                        mSeekBar.setSecondaryProgress(player.getBufferPercentage());
                    } else {
                        mPlayTime.setText("00:00");
                        mSeekBar.setProgress(0);
                    }
                    break;
                case 2:
                    showOrHide();
                    break;
                case LOOK_TIME_END:
                    if (player != null) {
                        player.pause();
                    }
                    if (isLive == true && !isThree) {
                        //直播结束
                        FileTool.writeFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL, AesTool.encrypt(Constant.LIVE_DATE + ""));
                    }
                    if (isThree) {
                        //三级结束
                        FileTool.writeFileToSDFile(Constant.TV_SHIYONG_MP4_ALL, AesTool.encrypt(Constant.Three_DATE + ""));
                        initShowDialog();
                    }
                    if (isLookArea) {
                        if (tv_time != null) {
                            tv_time.setText("试用已过期,立即成为VIP会员。");
                        }
                        initShowDialog();
                    }
                    break;
                case REFRESH_LOOK_TIME:
                    if (tv_time != null) {
                        if (isThree) {
                            tv_time.setText(" 试用倒计时 " + formatLongToTimeStr((long) player.lookAreaTotalSecond));
                        } else {
                            if (!isLive) {
                                tv_time.setText(" 试用倒计时 " + formatLongToTimeStr((long) player.lookAreaTotalSecond) + ",当前试看第" + player.lookCurrentTotal + "部视频");
                            }
                        }
                    }
                    if (player.lookAreaTotalSecond > 0) {
                        mHandler.postDelayed(runnable, 1000);
                    }
                    break;
            }
        }
    };
    //player
    // 自定义VideoView
    private FullScreenVideoView player;

    // 头部View
    private View mTopView;

    // 底部View
    private View mBottomView;
    // 视频播放拖动条
    private SeekBar mSeekBar;
    private ImageView mPlay;
    private TextView mPlayTime;
    private TextView mDurationTime;
    // 音频管理器
    private AudioManager mAudioManager;
    // 屏幕宽高
    private float width;
    private float height;
    // 视频播放时间
    private int playTime;
    // 自动隐藏顶部和底部View的时间
    private static final int HIDE_TIME = 5000;
    // 声音调节Toast
    private VolumnController volumnController;
    // 原始屏幕亮度
    private int orginalLight;
    /**
     * 测试地址
     */
    private String url;
    private BarrageView barrageView;
    private ImageView submit;
    private EditText et_barrage;
    private DBManager dbManager;
    private RelativeLayout barrage_submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 禁止屏幕休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        setContentView(R.layout.video_player);
        ButterKnife.bind(this);
        mContext = this;
        initData();
        initView();
        initPlayer();
        registerBoradcastReceiver();
    }


    public void registerBoradcastReceiver() {
//        IntentFilter myIntentFilter = new IntentFilter();
//        myIntentFilter.addAction("play_video_succes");
//        //注册广播
//        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

//    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, final Intent intent) {
//            //接受广播做逻辑处理
//            String action = intent.getAction();
//            if (action.equals("play_video_succes")) {
//                String playContent = intent.getStringExtra("playContent");
//                if (playContent.equals("hd")) {
//                    if (isLive == true) {
//                        //不执行任何代码
//                        if (!VipTool.canVip1(mContext)) {
//                            player.stop();
//                            initShowDialog();
//                            return;
//                        }
//                    } else {
//                        if (url.equals(videoInfo.getAddress_hd())) {
//                            return;
//                        }
//                        url = videoInfo.getAddress_hd();
//                        video_title = videoInfo.getName();
//                        player.setTitle(video_title)//设置视频的titleName
//                                .play(url, player.getCurrentPosition());//开始播放视频
//                        player.setScaleType(SuperPlayer.SCALETYPE_16_9);
//                        player.setPlayerWH(0, player.getMeasuredHeight());//设置竖屏的时候屏幕的高度，如果不设置会切换后按照16:9的高度重置
//                        choiceVip(mContext, false);
//                    }
//
//                } else if (playContent.equals("sd")) {
//                    if (isLive == true) {
//                        //不执行任何代码
//                        if (!VipTool.canVip1(mContext)) {
//                            player.stop();
//                            initShowDialog();
//                            return;
//                        }
//                    } else {
//                        if (!VipTool.canVip1(mContext)) {
//                            player.stop();
//                            initShowDialog();
//                            return;
//                        }
//                        if (url.equals(videoInfo.getAddress_sd())) {
//                            return;
//                        }
//                        if (VipTool.canVip1(mContext)) {
//                            url = videoInfo.getAddress_sd();
//                            video_title = videoInfo.getName();
//                            player.setTitle(video_title)//设置视频的titleName
//                                    .play(url, player.getCurrentPosition());//开始播放视频
//                            player.setScaleType(SuperPlayer.SCALETYPE_16_9);
//                            player.setPlayerWH(0, player.getMeasuredHeight());//设置竖屏的时候屏幕的高度，如果不设置会切换后按照16:9的高度重置
//                            choiceVip(mContext, false);
//                        } else {
//                            player.stop();
//                            initShowDialog();
//                        }
//                    }
//
//                } else if (playContent.equals("dialog_show")) {
//                    if (player != null) {
//                        player.stop();
//                    }
//                    if (isLive == true && !isThree) {
//                        //直播结束
//                        FileTool.writeFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL, AesTool.encrypt(Constant.LIVE_DATE + ""));
////                      alerPayFailTiShi();
//                    }
//                    if (isThree) {
//                        //三级结束
//                        FileTool.writeFileToSDFile(Constant.TV_SHIYONG_MP4_ALL, AesTool.encrypt(Constant.Three_DATE + ""));
//                        initShowDialog();
//                    }
//                    if (isLookArea) {
//                        initShowDialog();
//                    }
//                } else if (playContent.equals("activity_finish")) {
//                    //doPlayData();
//                    finish();
//                }
//
//                //短视频流程一(停用)
//                else if (playContent.equals("playing")) {
//                    if (startPlayDate == null) {
////                        sendUserDoData("3", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//                    }
//                    startPlayDate = new Date();
//                } else if (playContent.equals("stop")) {
//                    doPlayData();
//                } else if (playContent.equals("loading")) {
//                    doPlayData();
//                } else if (playContent.equals("play_finish")) {
//                    doPlayData();
//                } else if (playContent.equals("resume_timer")) {
//                    resumeTimer();
//                }
//                //短视频流程二(启用)
//                else if (playContent.equals("shot_video_stop_play")) {
//                    initShowDialog();
//                    if (player != null) player.stop();
//                }
//
//                //直播处理
//                else if (playContent.equals("playing_live")) {
//                    if (startPlayDate_live == null) {
////                        sendUserDoData("3", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//                        if (VipTool.getUserVipType(mContext) == Multi.VIP_NOT_VIP_TYPE) {
//                            String oldTime = AesTool.decrypt(FileTool.readFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL));
//                            if (TextUtils.isEmpty(oldTime)) {
//                                FileTool.writeFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL, AesTool.encrypt("100"));
//                            } else {
//                                if (Integer.parseInt(oldTime) > Constant.LIVE_DATE) {
//                                    //试用过期
////                                    alerPayFailTiShi();
//                                    if (player != null) {
//                                        player.stop();
//                                    }
//                                    player.isStartTime = true;
//                                    player.startTime();
//                                } else {
//                                    //没有过期
//                                    player.isStartTime = true;
//                                    if (player != null) {
//                                        player.timeSecond = Constant.LIVE_DATE - Integer.parseInt(oldTime);
//                                    }
//                                    player.startTime();
//                                }
//                            }
//                        }
//                    }
//                    startPlayDate_live = new Date();
//                } else if (playContent.equals("stop_live")) {
//                    doPlayData_Live();
//                } else if (playContent.equals("loading_live")) {
//                    doPlayData_Live();
//                } else if (playContent.equals("play_finish_live")) {
//                    doPlayData_Live();
//                } else if (playContent.equals("resume_timer_live")) {
//                    resumeTimer_Live();
//                }
//                //弹幕
//                else if (playContent.equals("barrage_close")) {
//                    barrageView.setVisibility(View.GONE);
//                    Constant.barra_show = false;
//                    barrageView.stopBarrage();
//                } else if (playContent.equals("barrage_show")) {
//                    barrageView.setVisibility(View.VISIBLE);
//                    Constant.barra_show = true;
//                    initBarrage();
//                    barrageView.startBarrage();
//
//                } else if (playContent.equals("send_barra")) {
//                    if (barrage_submit.getVisibility() == View.VISIBLE) {
//                        barrage_submit.setVisibility(View.GONE);
//                    } else {
//                        barrage_submit.setVisibility(View.VISIBLE);
//                    }
//                } else if (playContent.equals("hide_input")) {
//                    barrage_submit.setVisibility(View.GONE);
//                }
//            }
//        }
//    };

    private Date startPlayDate_live;
    private Date loadAndStopDate_live;

    private void resumeTimer_Live() {
        String vipStatus2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIPS_TWOS_KEY));
        if (!TextUtils.isEmpty(vipStatus2)) {
            if (vipStatus2.equals(VipTool.YES_VIP + "")) {
                return;
            }
        }
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_DIAMOND_TYPE ||
                vipType == Multi.VIP_SILVER_TYPE ||
                vipType == Multi.VIP_PLAT_NIUM_TYPE ||
                vipType == Multi.VIP_GOLD_TYPE ||
                vipType == Multi.VIP_RED_DIAMOND_TYPE ||
                vipType == Multi.VIP_CROWN_TYPE) {
            return;
        }
        try {
            String oldTime = AesTool.decrypt(FileTool.readFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL));
            if (TextUtils.isEmpty(oldTime)) {
            } else {
                if (Integer.parseInt(oldTime) > Constant.LIVE_DATE) {
                    //试用过期
                    initShowDialog();
                    if (player != null) {
                        player.pause();
                    }
//                    player.isStartTime = true;
                } else {
                    //没有过期
                    if (player != null) {
                        //player.timeSecond = Constant.doDate - Integer.parseInt(oldTime);
                    }
//                    player.isStartTime = true;
//                    player.startTime();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resumeTimer() {
        String vipStatus1 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.USER_VIPS_KEY));
        if (!TextUtils.isEmpty(vipStatus1)) {
            if (vipStatus1.equals(VipTool.YES_VIP + "")) {
                return;
            }
        }
        try {
            String oldTime = AesTool.decrypt(FileTool.readFileToSDFile(Constant.TV_SHIYONG_MP4_ALL));
            if (TextUtils.isEmpty(oldTime)) {
            } else {
                if (Integer.parseInt(oldTime) > Constant.Three_DATE) {
                    //试用过期
                    initShowDialog();
                    if (player != null) {
                        player.pause();
                    }
//                    player.isStartTime = true;
                } else {
                    //没有过期
                    if (player != null) {
                        //player.timeSecond = Constant.doDate - Integer.parseInt(oldTime);
                    }
                    //如果是体验区，则不启动倒计时（开始播启动）
//                    if (!isLookArea) {
//                        player.isStartTime = true;
//                        player.startTime();
//                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doPlayData_Live() {
        //直播用v2
//        String vipStatus2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIPS_TWOS_KEY));
//        if (!TextUtils.isEmpty(vipStatus2)) {
//            if (vipStatus2.equals(VipTool.YES_VIP + "")) {
//                return;
//            }
//        }
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_DIAMOND_TYPE ||
                vipType == Multi.VIP_SILVER_TYPE ||
                vipType == Multi.VIP_PLAT_NIUM_TYPE ||
                vipType == Multi.VIP_GOLD_TYPE ||
                vipType == Multi.VIP_RED_DIAMOND_TYPE ||
                vipType == Multi.VIP_CROWN_TYPE) {
        } else if (isLive == true && !isThree && vipType == Multi.VIP_NOT_VIP_TYPE) {
            loadAndStopDate_live = new Date();
            File file = new File(Constant.TV_SHIYONG_M3U8_ALL);
            if (!file.exists()) {
                FileTool.createFile(Constant.TV_SHIYONG_M3U8_ALL);
            }
            String old = FileTool.readFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL);
            String oldTime = AesTool.decrypt(old);
            if (TextUtils.isEmpty(oldTime)) {
                //第一次写入(给1个小时)
                if (player != null) {
//                    player.isStartTime = true;
//                    player.timeSecond = Constant.LIVE_DATE;
                }
                FileTool.writeFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL, AesTool.encrypt("100"));
            } else {
                if (startPlayDate_live != null) {
                    long[] time = DateTool.getTime(loadAndStopDate_live, startPlayDate_live);
                    int second = Integer.parseInt(oldTime) + Integer.parseInt(time[3] + "");
                    FileTool.writeFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL, AesTool.encrypt(second + ""));
                }
            }
        }
    }

    private void doPlayData() {
        //短视频用v1
        if (isThree && !VipTool.canVip1(mContext)) {
            loadAndStopDate = new Date();
            File file = new File(Constant.TV_SHIYONG_MP4_ALL);
            if (!file.exists()) {
                FileTool.createFile(Constant.TV_SHIYONG_MP4_ALL);
            }
            String old = FileTool.readFileToSDFile(Constant.TV_SHIYONG_MP4_ALL);
            String oldTime = AesTool.decrypt(old);
            if (TextUtils.isEmpty(oldTime)) {
                //第一次写入(给1个小时)
                if (player != null) {
                    player.lookAreaTotalSecond = Constant.Three_DATE;
                }
                FileTool.writeFileToSDFile(Constant.TV_SHIYONG_MP4_ALL, AesTool.encrypt("100"));
                mHandler.post(runnable);
            } else {
                if (startPlayDate != null) {
                    long[] time = DateTool.getTime(loadAndStopDate, startPlayDate);
                    int minutes = Integer.parseInt(oldTime) + Integer.parseInt(time[3] + "");
                    FileTool.writeFileToSDFile(Constant.TV_SHIYONG_MP4_ALL, AesTool.encrypt(minutes + ""));
                }
            }
        }
    }

    private Date startPlayDate;
    private Date loadAndStopDate;

    /**
     * 初始化相关的信息
     */
    private void initData() {
        dbManager = DBManager.getDBManager(this);
        isLive = getIntent().getBooleanExtra("isLive", false);
        isVipArea = getIntent().getBooleanExtra("isVipArea", false);
        liveAreaDemand = getIntent().getBooleanExtra("liveAreaDemand", false);
        if (isLive == true) {
            url = getIntent().getStringExtra("url");
            video_title = getIntent().getStringExtra("title");
            isThree = false;
        } else {
            videoInfo = (VideoInfo) getIntent().getSerializableExtra("videoInfo");
            url = videoInfo.getAddress_hd();
            video_title = videoInfo.getName();
            isThree = videoInfo.isThreeVideo();
            isLookArea = videoInfo.isLookVideo();
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {

        barrageSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.barra_show) {
                    Constant.barra_show = false;
                    barrageSwitch.setImageResource(R.drawable.switch_off);
                } else {
                    Constant.barra_show = true;
                    barrageSwitch.setImageResource(R.drawable.switch_on);
                }
                barrageView.setVisibility(Constant.barra_show ? View.VISIBLE : View.GONE);
            }
        });
        barraSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (barrage_submit.getVisibility() == View.VISIBLE) {
                    barrage_submit.setVisibility(View.GONE);
                } else if (barrage_submit.getVisibility() == View.GONE) {
                    barrage_submit.setVisibility(View.VISIBLE);
                }
            }
        });
        barrage_submit = (RelativeLayout) findViewById(R.id.barrage_submit);
        et_barrage = (EditText) findViewById(R.id.et_barrage);
        barrageView = (BarrageView) findViewById(R.id.containerView);
        submit = (ImageView) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String text = et_barrage.getText().toString();
                    barrage_submit.setVisibility(View.GONE);
                    if (TextUtils.isEmpty(text)) {
                        T.showTextToast(mContext, "请输入弹幕内容");
                        return;
                    }
                    barrageView.initBarrageItem(new BarrageItem(), text);
                    T.showTextToast(mContext, "评论成功!!!");
                } catch (Exception e) {
                }
            }
        });
        initBarrage();
    }

    private List<String> barTexts = new ArrayList<>();

    private void initBarrage() {
        barrageView.getShowList().clear();
        List<CommentInfo> commentInfoList = dbManager.queryCommentAll();
        Collections.shuffle(commentInfoList);
        int listSize = 0;
        if (commentInfoList.size() != 0) {
            barrageView.getItemText().clear();
            if (commentInfoList.size() < 10) {
                listSize = commentInfoList.size();
            } else {
                listSize = 10;
            }
            barrageView.textCount = listSize;
            for (int i = 0; i < listSize; i++) {
                barTexts.add(commentInfoList.get(i).getInfo());
            }
        }
        barrageView.setItemText(barTexts);
    }


    /**
     * 初始化播放器
     */
    private void initPlayer() {
        volumnController = new VolumnController(this);
        mPlayTime = (TextView) findViewById(R.id.play_time);
        mDurationTime = (TextView) findViewById(R.id.total_time);
        mPlay = (ImageView) findViewById(R.id.play_btn);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        mTopView = findViewById(R.id.top_layout);
        mBottomView = findViewById(R.id.bottom_layout);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        width = DensityUtil.getWidthInPx(this);
        height = DensityUtil.getHeightInPx(this);
        threshold = DensityUtil.dip2px(this, 18);

        orginalLight = LightnessController.getLightness(this);

        mPlay.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        if (player == null) {
            player = (FullScreenVideoView) findViewById(R.id.view_super_player);
        }
        startNetSpeed();
        playVideo();
        if (isLive && !isThree) {
            textviewLive.setVisibility(View.VISIBLE);
        } else {
            textviewLive.setVisibility(View.GONE);
        }
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_NOT_VIP_TYPE) {
            VipTool.shi_Kan_Six_Video(mContext);
            player.lookCurrentTotal = Integer.parseInt(VipTool.get_ShiKan_Video_Count(mContext)) - 1;
        }
        try {
            if (!isLive && videoInfo != null) {
//                if (TextUtils.isEmpty(videoInfo.getId()) || videoInfo.getId().equals("0")) {
//                    player.videoTime = 90;
//                } else if (!TextUtils.isEmpty(videoInfo.getId())) {
//                    int time = Integer.parseInt(dbManager.queryVideoTimeById(videoInfo.getId()).getVideo_Time());
//                    player.videoTime = time;
//                } else {
//                    player.videoTime = 90;
//                }
                player.videoTime = Integer.parseInt(videoInfo.getSpare1());
            }
        } catch (Exception e) {
            player.videoTime = 80;
        }
        centerImages.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                //v2
                                                Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
                                                Intent intent = new Intent(mContext, PayActivity.class);
                                                startActivity(intent);
                                                mContext.finish();
                                            }
                                        }

        );
    }

    /**
     * 下面的这几个Activity的生命状态很重要
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
        if (isLive == false && isThree) {
            doPlayData();
        }
        if (isLive == true && !isThree) {
            doPlayData_Live();
        }
        LightnessController.setLightness(this, orginalLight);
        MobclickAgent.onPageEnd("午夜啪啪播放器"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.resume();
        }
        mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        MobclickAgent.onPageStart("午夜啪啪播放器"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyPlayer();
        //注销广播
//        this.unregisterReceiver(mBroadcastReceiver);
    }

    private void destroyPlayer() {
        if (mHandler != null) {
            mHandler.removeMessages(0);
            mHandler.removeCallbacksAndMessages(null);
        }
        if (updateKbTask != null) {
            updateKbTask.cancel();
            updateKbTask = null;
        }
        if (playVideoTask != null) {
            playVideoTask.cancel();
            playVideoTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

//    @Override()
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == 1) {//1竖屏 2横屏
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
//        }
//    }


//    @Override
//    public void setRequestedOrientation(int requestedOrientation) {
////        super.setRequestedOrientation(requestedOrientation);
//    }

    @Override
    public void onBackPressed() {
        if (player.isPlaying() && isLive == false && isThree == true) {
            doPlayData();
        }
        if (player.isPlaying() && isLive == true && !isThree) {
            doPlayData_Live();
        }
        Multi.isShowDialog = true;
        destroyPlayer();
        finish();
    }

    private void choiceVip(Activity context, boolean isLive) {
        //直播用v2,
        if (isLive == true) {
            int vipType = VipTool.getUserVipType(mContext);
            if (vipType == Multi.VIP_NOT_VIP_TYPE) {
                File file = new File(Constant.TV_SHIYONG_M3U8_ALL);
                if (!file.exists()) {
                    //没有控制试用vpn的文件(試用)
                    FileTool.createFile(Constant.TV_SHIYONG_M3U8_ALL);
//                    player.isStartTime = true;
//                    player.timeSecond = Constant.LIVE_DATE;
                    //继续执行
                } else {
                    //有文件
                    try {
                        String oldTime = AesTool.decrypt(FileTool.readFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL));
                        if (TextUtils.isEmpty(oldTime)) {
                            FileTool.writeFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL, AesTool.encrypt("100"));
                        } else {
                            if (Integer.parseInt(oldTime) > Constant.LIVE_DATE) {
                                //试用过期
                                initShowDialog();
                                if (player != null) {
                                    player.pause();
                                }
//                                player.isStartTime = true;
                            } else {
                                //没有过期
//                                player.isStartTime = true;
                                if (player != null) {
//                                    player.timeSecond = Constant.LIVE_DATE - Integer.parseInt(oldTime);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (vipType == Multi.VIP_SILVER_TYPE ||
                    vipType == Multi.VIP_DIAMOND_TYPE ||
                    vipType == Multi.VIP_PLAT_NIUM_TYPE ||
                    vipType == Multi.VIP_GOLD_TYPE ||
                    vipType == Multi.VIP_RED_DIAMOND_TYPE ||
                    vipType == Multi.VIP_CROWN_TYPE) {
                String vipLastTime2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIP_TIME_TWO_KEY));
                String vipStatus2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIPS_TWOS_KEY));
                String result2 = DateTool.compareTime2(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), vipLastTime2);
                //result  1过期 2未过期
                if (result2.equals("2")) {
                    //继续执行
//                    player.isStartTime = false;
                } else if (result2.equals("1")) {
                    if (!context.isFinishing()) {
//                        alertVipPay();
//                        if (player != null) {
//                            player.stop();
//                        }
                        //initBan();
                    } else {
//                        alertVipPay();
//                        if (player != null) {
//                            player.stop();
//                        }
                        //initBan();
                    }
                }
            }
        } else {
            //不是直播,不是直播用v1,而且三级试看半小时开启
            if (isThree) {
                //三级(必须钻石会员观看完整,钻石充值的v2)
                String vipLastTime2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIP_TIME_TWO_KEY));
                String vipStatus2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIPS_TWOS_KEY));
                String result2 = DateTool.compareTime2(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), vipLastTime2);

//                //是会员
//                if (vipStatus2.equals(VipTool.YES_VIP + "")) {
//                    //查看vip会员时间是否过期
//                    //result  1过期 2未过期
//                    if (result2.equals("2")) {
//                        //继续播放。。。
//                    } else if (result2.equals("1")) {
//                        if (!context.isFinishing()) {
//                            initShowDialog();
//                            if (player != null) {
//                                player.pause();
//                            }
//                        }
//                    }
//                    //不是会员
//                } else if (vipStatus2.equals(VipTool.NO_VIP + "")) {
//                    //查看普通用户试用时间是否过期(本地控制)
//                    //result  1过期 2未过期
//                    File file = new File(Constant.TV_SHIYONG_MP4_ALL);
//                    if (!file.exists()) {
//                        //mp4(試用)
//                        FileTool.createFile(Constant.TV_SHIYONG_MP4_ALL);
//                        //试用流程三(启用)
//                        player.lookAreaTotalSecond = Constant.Three_DATE;
//                        mHandler.post(runnable);
//                    } else {
//                        //有文件
//                        try {
//                            //试用流程三(启用)
//                            String oldTime = AesTool.decrypt(FileTool.readFileToSDFile(Constant.TV_SHIYONG_MP4_ALL));
//                            if (TextUtils.isEmpty(oldTime)) {
//                            } else {
//                                if (Integer.parseInt(oldTime) > Constant.Three_DATE) {
//                                    //试用过期
//                                    initShowDialog();
//                                    if (player != null) {
//                                        player.pause();
//                                    }
//                                } else {
//                                    //没有过期
//                                    if (player != null) {
//                                        player.lookAreaTotalSecond = Constant.Three_DATE - Integer.parseInt(oldTime);
//                                    }
//                                    mHandler.post(runnable);
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
                //流程二
                int vipType = VipTool.getUserVipType(mContext);
                if (vipType == Multi.VIP_NOT_VIP_TYPE) {
                    //result  1过期 2未过期
                    File file = new File(Constant.TV_SHIYONG_MP4_ALL);
                    if (!file.exists()) {
                        //mp4(試用)
                        FileTool.createFile(Constant.TV_SHIYONG_MP4_ALL);
                        //试用流程三(启用)
                        player.lookAreaTotalSecond = Constant.Three_DATE;
                        mHandler.post(runnable);
                    } else {
                        //有文件
                        try {
                            //试用流程三(启用)
                            String oldTime = AesTool.decrypt(FileTool.readFileToSDFile(Constant.TV_SHIYONG_MP4_ALL));
                            if (TextUtils.isEmpty(oldTime)) {
                            } else {
                                if (Integer.parseInt(oldTime) > Constant.Three_DATE) {
                                    //试用过期
                                    initShowDialog();
                                    if (player != null) {
                                        player.pause();
                                    }
                                } else {
                                    //没有过期
                                    if (player != null) {
                                        player.lookAreaTotalSecond = Constant.Three_DATE - Integer.parseInt(oldTime);
                                    }
                                    mHandler.post(runnable);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                //不是三级
                String vipLastTime1 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIP_TIMES_KEY));
                String vipStatus1 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.USER_VIPS_KEY));
                String result1 = DateTool.compareTime2(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), vipLastTime1);

                //是会员
                if (vipStatus1.equals(VipTool.YES_VIP + "")) {
                    //查看vip会员时间是否过期
                    //result  1过期 2未过期
//                    if (result1.equals("2")) {
//                        //继续执行
//                        player.isStartTime = false;
//                        player.userIsVip = true;
//                    } else if (result1.equals("1")) {
//                        if (!context.isFinishing()) {
//                            initShowDialog();
//                            if (player != null) {
//                                player.stop();
//                            }
//                        }
//                    }
                    int vipType = VipTool.getUserVipType(mContext);
                    if (isLookArea && vipType == Multi.VIP_NOT_VIP_TYPE) {
                        player.lookAreaTotalSecond = 20;
                        mHandler.post(runnable);
                    }
                    //不是会员
                } else if (vipStatus1.equals(VipTool.NO_VIP + "")) {
                    //查看普通用户试用时间是否过期(本地控制)
                    //result  1过期 2未过期
//                    File file = new File(Constant.TV_SHIYONG_MP4_ALL);
//                    if (!file.exists()) {
//                        //mp4(試用)
//                        player.isStartTime = false;
//                        FileTool.createFile(Constant.TV_SHIYONG_MP4_ALL);
//                        //试用流程一(停用)
////                    player.isStartTime = true;
////                    player.timeSecond = Constant.doDate;
//                        /***
//                         * 试用流程二(启用)
//                         */
//                        player.userIsVip = false;
//                        videoHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                tvLook.setVisibility(View.VISIBLE);
//                            }
//                        }, 2000);
//                        videoHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                tvLook.setVisibility(View.GONE);
//                            }
//                        }, 5000);
//                    } else {
//                        //有文件
//                        try {
//                            //试用流程一(停用)
////                        String oldTime = AesTool.decrypt(FileTool.readFileToSDFile(Constant.TV_SHIYONG_MP4_ALL));
////                        if (TextUtils.isEmpty(oldTime)) {
////                        } else {
////                            if (Integer.parseInt(oldTime) > Constant.doDate) {
////                                //试用过期
////                                alerPayFailTiShi();
////                                if (player != null) {
////                                    player.stop();
////                                }
////                                player.isStartTime = true;
////                            } else {
////                                //没有过期
////                                if (player != null) {
////                                    player.timeSecond = Constant.doDate - Integer.parseInt(oldTime);
////                                }
////                                player.isStartTime = true;
////                            }
////                        }
////                            /***
////                             * 试用流程二(启用)
////                             */
////
////                            player.userIsVip = false;
////                            player.isStartTime = true;
////                            player.timeSecond = 20;
////                            player.startTime();
////                            videoHandler.postDelayed(new Runnable() {
////                                @Override
////                                public void run() {
////                                    tvLook.setVisibility(View.VISIBLE);
////                                }
////                            }, 5000);
////                            videoHandler.postDelayed(new Runnable() {
////                                @Override
////                                public void run() {
////                                    tvLook.setVisibility(View.GONE);
////                                }
////                            }, 10000);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
                    /***
                     * 试用流程二(启用)
                     */
                    player.lookAreaTotalSecond = 20;
                    mHandler.post(runnable);
                }
            }
        }
    }

    Dialog dialog_pay_time;

//    public void alertVipPay() {
//        ScreenTool.setLight(mContext, 250);
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_shikan, null);
//        //对话框
//        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
//        if (dialog_pay_time == null) {
//            dialog_pay_time = new Dialog(mContext, R.style.Dialog);
//            dialog_pay_time.show();
//            dialog_pay_time.setCancelable(false);
//            Window window = dialog_pay_time.getWindow();
//            window.getDecorView().setPadding(0, 0, 0, 0);
//            WindowManager.LayoutParams lp = window.getAttributes();
//            layout.getBackground().setAlpha(150);
//            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;//ScreenTool.getWidth(this) / 5 * 3;
//            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            window.setAttributes(lp);
//            window.setContentView(layout);
//
//            ImageButton pay = (ImageButton) layout.findViewById(R.id.pay);
//            pay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog_pay_time.dismiss();
//                    if (!isLive) {
//                        //v1
//                        Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
//                    }
//                    if (isThree) {
//                        Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
//                    }
//                    if (isLive) {
//                        //v2
//                        Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
//                    }
//                    Intent intent = new Intent(mContext, WXPayEntryActivity.class);
//                    startActivity(intent);
//                    mContext.finish();
//                }
//            });
//            ImageButton cancel = (ImageButton) layout.findViewById(R.id.cancel);
//            cancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog_pay_time.dismiss();
//                    Multi.Moon_LEVE = Multi.Moon_LEVE1;
//                    mContext.finish();
//                }
//            });
//        } else {
//            dialog_pay_time.show();
//        }
//    }

//    private void sendUserDoData(String type, String oprationtime) {
//        //发起请求
//        DoInfo data = new DoInfo();
//        data.setUserName(util.getAndroidId(this));
//        data.setType(type);
//        data.setOperationTime(oprationtime);
//        String json = com.alibaba.fastjson.JSONObject.toJSONString(data);
//        String aesJson = aesUtils.encrypt(json);
//        RequestParams params = new RequestParams(Constant.USER_DO_INTERFACE);
//        params.setCacheMaxAge(0);//最大数据缓存时间
//        params.setConnectTimeout(5000);//连接超时时间
//        params.setCharset("UTF-8");
//        params.addQueryStringParameter("data", aesJson);
//
//        x.http().post(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });
//    }

//    Dialog dialog_level;

//    public void alertVipLevel() {
//        ScreenTool.setLight(mContext, 250);
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_gold, null);
//        //对话框
//        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
//        if (dialog_level == null) {
//            dialog_level = new Dialog(mContext, R.style.Dialog);
//            dialog_level.show();
//            dialog_level.setCancelable(false);
//            Window window = dialog_level.getWindow();
//            window.getDecorView().setPadding(0, 0, 0, 0);
//            WindowManager.LayoutParams lp = window.getAttributes();
////            layout.getBackground().setAlpha(150);
//            lp.width = ScreenTool.getWidth(this) / 7 * 4;
//            lp.height = ScreenTool.getHeight(this);
//            window.setAttributes(lp);
//            window.setContentView(layout);
//            ImageView pay_gold = (ImageView) layout.findViewById(R.id.pay_gold);
//            pay_gold.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog_level.dismiss();
//                    Multi.Moon_LEVE = Multi.Moon_LEVE1;
//                    Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
//                    mContext.startActivity(new Intent(mContext, WXPayEntryActivity.class));
//                    mContext.finish();
//                }
//            });
//            TextView vip_level_decription = (TextView) layout.findViewById(R.id.vip_level_decription);
//            if (VipTool.getUserVipType(mContext) == Multi.VIP_NOT_VIP_TYPE) {
//                vip_level_decription.setText("升级白银会员观看完整视频");
//            } else if (VipTool.getUserVipType(mContext) == Multi.VIP_SILVER_TYPE) {
//                vip_level_decription.setText("升级黄金会员观看完整视频");
//            } else if (VipTool.getUserVipType(mContext) == Multi.VIP_GOLD_TYPE) {
//                vip_level_decription.setText("升级白金会员观看完整视频");
//            } else if (VipTool.getUserVipType(mContext) == Multi.VIP_PLAT_NIUM_TYPE) {
//                vip_level_decription.setText("升级钻石会员观看完整视频");
//            } else {
//                vip_level_decription.setText("升级钻石会员观看完整视频");
//            }
//        } else {
//            dialog_level.show();
//        }
//    }

    private void initShowDialog() {
        Multi.isShowDialog = true;
        mContext.finish();
    }

    private void initBan() {
        Glide.with(mContext)
                .load(NetInterface.TV_LIVE_WINDOW)
                .crossFade()
                .placeholder(R.drawable.allloading)
                .error(R.drawable.allloading)
                .into(new GlideDrawableImageViewTarget(centerImages) {
                    @Override
                    public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                        super.onResourceReady(drawable, anim);
                        int vipType = VipTool.getUserVipType(mContext);
                        if (vipType == Multi.VIP_NOT_VIP_TYPE) {
                            if (isLive && !isThree)
                                centerImages.setVisibility(View.VISIBLE);
                            centerImages.setMinimumWidth(ScreenTool.getWidth(mContext) / 10 * 7);
                            centerImages.setMinimumHeight(ScreenTool.getHeight(mContext) / 10 * 8);
                        } else if (vipType == Multi.VIP_SILVER_TYPE) {
                            //白银
                            if (isLive && !isThree) {
                                centerImages.setMinimumWidth(ScreenTool.getWidth(mContext) / 10 * 7);
                                centerImages.setMinimumHeight(ScreenTool.getHeight(mContext) / 10 * 7);
                                centerImages.setVisibility(View.VISIBLE);
                            }
                        } else if (vipType == Multi.VIP_GOLD_TYPE) {
                            //黄金
                            if (isLive && !isThree) {
                                centerImages.setMinimumWidth(ScreenTool.getWidth(mContext) / 10 * 7);
                                centerImages.setMinimumHeight(ScreenTool.getHeight(mContext) / 10 * 6);
                                centerImages.setVisibility(View.VISIBLE);
                            }
                        } else if (vipType == Multi.VIP_PLAT_NIUM_TYPE) {
                            //白金
                            if (isLive && !isThree) {
                                centerImages.setMinimumWidth(ScreenTool.getWidth(mContext) / 10 * 6);
                                centerImages.setMinimumHeight(ScreenTool.getHeight(mContext) / 10 * 5);
//                diamondText.setVisibility(View.VISIBLE);
                                centerImages.setVisibility(View.VISIBLE);
                            }
                        } else if (vipType == Multi.VIP_DIAMOND_TYPE) {
                            //钻石
                            if (isLive && !isThree) {
                                centerImages.setMinimumWidth(ScreenTool.getWidth(mContext) / 10 * 5);
                                centerImages.setMinimumHeight(ScreenTool.getHeight(mContext) / 10 * 5);
//              diamondText.setVisibility(View.VISIBLE);
                                centerImages.setVisibility(View.VISIBLE);
                            }
                        } else if (vipType == Multi.VIP_RED_DIAMOND_TYPE) {
                            //红钻
                            if (isLive && !isThree) {
                                centerImages.setMinimumWidth(ScreenTool.getWidth(mContext) / 10 * 4);
                                centerImages.setMinimumHeight(ScreenTool.getHeight(mContext) / 10 * 4);
                                diamondText.setVisibility(View.VISIBLE);
                                centerImages.setVisibility(View.VISIBLE);
                            }
                        } else if (vipType == Multi.VIP_CROWN_TYPE) {
                            //皇冠
                            if (isLive && !isThree) {
                                centerImages.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    /**
     * ==========================================网速=============================
     */
    public long getUidRxBytes(Context mContext) { //获取总的接受字节数，包含Mobile和WiFi等
        PackageManager pm = mContext.getPackageManager();
        ApplicationInfo ai = null;
        try {
            ai = pm.getApplicationInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return TrafficStats.getUidRxBytes(ai.uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);
    }

    long new_KB, old_KB;
    Timer timer;
    TimerTask updateKbTask = new TimerTask() {
        @Override
        public void run() {
            if (player != null && player.isPlaying()) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressbar.setVisibility(View.GONE);
                        tv_speed.setVisibility(View.GONE);
                    }
                }, 3000);
            }
            new_KB = getUidRxBytes(mContext) - old_KB;
            old_KB = getUidRxBytes(mContext);
            Message msg = new Message();
            msg.what = MESSAGE_UPDATE_BYTES;
            msg.obj = new_KB;
            mHandler.sendMessage(msg);
        }
    };

    private void startNetSpeed() {
        /**开始加载视频显示网速*/
        if (tv_speed != null) {
            //网速显示
            if (timer == null) {
                timer = new Timer();
                timer.schedule(updateKbTask, 1000, 1000);
            }
        }
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == 1) {
//            //竖屏
//            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        } else {
//            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        }
//    }

    private void playVideo() {
        videoName.setText(video_title);
        videoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initShowDialog();
            }
        });
        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                //如果是横排,则改为竖排
                else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        });
        player.setVideoPath(url);
        player.requestFocus();
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
//                Toast.makeText(mContext,"播放错误!请重新再试!",Toast.LENGTH_SHORT).show();
                /**
                 * 监听视频播放失败的回调
                 */
                if (!NetTool.isConnected(mContext)) {
                    T.showTextCenterToast(mContext, "您的网络没有连接!!");
                } else {
                    T.showTextCenterToast(mContext, "视频出了点小问题!请重新再试！！");
                }
                progressbar.setVisibility(View.GONE);
                tv_speed.setVisibility(View.GONE);
                return false;
            }
        });
        player.setPlayPauseListener(new FullScreenVideoView.PlayPauseListener() {
            @Override
            public void onPlay() {
                progressbar.setVisibility(View.GONE);
                tv_speed.setVisibility(View.GONE);
                //...
                int vipType = VipTool.getUserVipType(mContext);
                if (isLookArea && vipType == Multi.VIP_NOT_VIP_TYPE) {
//                    player.isStartTime = true;n
//                    player.startTime();
                }
                //....
                if (isLive && !isThree) {
                    if (vipType == Multi.VIP_SILVER_TYPE || vipType == Multi.VIP_DIAMOND_TYPE || vipType == Multi.VIP_PLAT_NIUM_TYPE ||
                            vipType == Multi.VIP_GOLD_TYPE || vipType == Multi.VIP_RED_DIAMOND_TYPE || vipType == Multi.VIP_CROWN_TYPE) {
                        String vipLastTime2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIP_TIME_TWO_KEY));
                        String vipStatus2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIPS_TWOS_KEY));
                        String result2 = DateTool.compareTime2(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), vipLastTime2);
                        //result  1过期 2未过期
                        if (result2.equals("2")) {
                            //继续执行
//                            player.isStartTime = false;
                        } else if (result2.equals("1")) {
                            if (!mContext.isFinishing()) {
                                initBan();
                            } else {
                                initBan();
                            }
                        }
                    }
                }
                //........
                initBan();
            }

            @Override
            public void onPause() {

            }
        });
        player.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        progressbar.setVisibility(View.VISIBLE);
                        tv_speed.setVisibility(View.VISIBLE);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        progressbar.setVisibility(View.GONE);
                        tv_speed.setVisibility(View.GONE);
                        break;
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        progressbar.setVisibility(View.GONE);
                        tv_speed.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                player.setVideoWidth(mp.getVideoWidth());
//                player.setVideoHeight(mp.getVideoHeight());
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    Log.i("videostrfull", "视频是横屏");
                } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    Log.i("videostrfull", "视频是竖屏");
                }
                mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                progressbar.setVisibility(View.GONE);//
                tv_speed.setVisibility(View.GONE);//
                player.start();
                //随机直播区域点播视频画面
                if (liveAreaDemand && isLive) {
                    playTime = RandomTool.getRandom(player.getDuration() / 5, player.getDuration() / 2);
                }
                if (playTime != 0) {
                    player.seekTo(playTime);
                }
                mHandler.removeCallbacks(hideRunnable);
                mHandler.postDelayed(hideRunnable, HIDE_TIME);
                //加长了时间的时长
                if ((isLookArea && !isThree) || isVipArea) {
                    if (isLookArea && !isThree) {
                        mDurationTime.setText(formatLongToTimeStr((long) (player.videoTime)));
                    } else if (isVipArea) {
                        mDurationTime.setText(formatLongToTimeStr((long) (RandomTool.getRandom(3600, 7200))));
                    }
                } else {
                    //默认正确时长
                    mDurationTime.setText(formatTime(player.getDuration()));
                }
                playerPosistion();
                //视频业务处理
                choiceVip(mContext, isLive);
            }
        });
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlay.setImageResource(R.drawable.video_btn_on);
                mPlayTime.setText("00:00");
                mSeekBar.setProgress(0);
                player.seekTo(0);
                player.start();
                //完成后处理
                int vipType = VipTool.getUserVipType(mContext);
                if (vipType == Multi.VIP_SILVER_TYPE ||
                        vipType == Multi.VIP_GOLD_TYPE ||
                        vipType == Multi.VIP_DIAMOND_TYPE ||
                        vipType == Multi.VIP_PLAT_NIUM_TYPE ||
                        vipType == Multi.VIP_RED_DIAMOND_TYPE ||
                        vipType == Multi.VIP_CROWN_TYPE) {
                    if (isFromUser) {
                        player.seekTo(0);
                        player.start();
                    } else {
                        initShowDialog();
                    }
                } else {
//                    mp.stop();
//                    initShowDialog();
                    player.seekTo(0);
                    player.start();
                }
            }
        });
        player.setOnTouchListener(mTouchListener);
    }

    private void playerPosistion() {
        if (timer != null) {
            timer.schedule(playVideoTask, 0, 1000);
        }
    }

    TimerTask playVideoTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(1);
        }
    };


    private Runnable hideRunnable = new Runnable() {

        @Override
        public void run() {
            showOrHide();
        }
    };

    @SuppressLint("SimpleDateFormat")
    private String formatTime(long time) {
        DateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(new Date(time));
    }

    private float mLastMotionX;
    private float mLastMotionY;
    private int startX;
    private int startY;
    private int threshold;
    private boolean isClick = true;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final float x = event.getX();
            final float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastMotionX = x;
                    mLastMotionY = y;
                    startX = (int) x;
                    startY = (int) y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = x - mLastMotionX;
                    float deltaY = y - mLastMotionY;
                    float absDeltaX = Math.abs(deltaX);
                    float absDeltaY = Math.abs(deltaY);
                    // 声音调节标识
                    boolean isAdjustAudio = false;
                    if (absDeltaX > threshold && absDeltaY > threshold) {
                        if (absDeltaX < absDeltaY) {
                            isAdjustAudio = true;
                        } else {
                            isAdjustAudio = false;
                        }
                    } else if (absDeltaX < threshold && absDeltaY > threshold) {
                        isAdjustAudio = true;
                    } else if (absDeltaX > threshold && absDeltaY < threshold) {
                        isAdjustAudio = false;
                    } else {
                        return true;
                    }
                    if (isAdjustAudio) {
                        if (x < width / 2) {
                            if (deltaY > 0) {
                                lightDown(absDeltaY);
                            } else if (deltaY < 0) {
                                lightUp(absDeltaY);
                            }
                        } else {
                            if (deltaY > 0) {
                                volumeDown(absDeltaY);
                            } else if (deltaY < 0) {
                                volumeUp(absDeltaY);
                            }
                        }

                    } else {
                        if (deltaX > 0) {
                            forward(absDeltaX);
                        } else if (deltaX < 0) {
                            backward(absDeltaX);
                        }
                    }
                    mLastMotionX = x;
                    mLastMotionY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(x - startX) > threshold
                            || Math.abs(y - startY) > threshold) {
                        isClick = false;
                    }
                    mLastMotionX = 0;
                    mLastMotionY = 0;
                    startX = (int) 0;
                    if (isClick) {
                        showOrHide();
                    }
                    isClick = true;
                    break;

                default:
                    break;
            }
            return true;
        }
    };

    private void showOrHide() {
        if (mTopView.getVisibility() == View.VISIBLE) {
            mTopView.clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(this,
                    R.anim.option_leave_from_top);
            animation.setAnimationListener(new AnimationImp() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                    mTopView.setVisibility(View.GONE);
                }
            });
            mTopView.startAnimation(animation);

            mBottomView.clearAnimation();
            Animation animation1 = AnimationUtils.loadAnimation(this,
                    R.anim.option_leave_from_bottom);
            animation1.setAnimationListener(new AnimationImp() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                    mBottomView.setVisibility(View.GONE);
                }
            });
            mBottomView.startAnimation(animation1);
        } else {
            mTopView.setVisibility(View.VISIBLE);
            mTopView.clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(this,
                    R.anim.option_entry_from_top);
            mTopView.startAnimation(animation);

            mBottomView.setVisibility(View.VISIBLE);
            mBottomView.clearAnimation();
            Animation animation1 = AnimationUtils.loadAnimation(this,
                    R.anim.option_entry_from_bottom);
            mBottomView.startAnimation(animation1);
            mHandler.removeCallbacks(hideRunnable);
            mHandler.postDelayed(hideRunnable, HIDE_TIME);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_btn:
                if (player.isPlaying()) {
                    player.pause();
                    mPlay.setImageResource(R.drawable.video_btn_down);
                } else {
                    player.start();
                    mPlay.setImageResource(R.drawable.video_btn_on);
                }
                break;
            default:
                break;
        }
    }

    private class AnimationImp implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }
    }

    int currentProgress;
    int currentPos;
    boolean isFromUser = false;
    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            progressbar.setVisibility(View.VISIBLE);
            tv_speed.setVisibility(View.VISIBLE);
            if (isLive && !isThree) {
                seekBar.setProgress(0);
            }
            if ((isLookArea && !isThree) || isVipArea) {
                seekBar.setProgress(currentProgress);
                if (currentPos >= player.getDuration()) {
                    player.seekTo(0);
                    player.start();
                } else {
                    player.seekTo(currentPos);
                    player.start();
                }
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isFromUser = false;
                }
            }, 6000);
            mHandler.postDelayed(hideRunnable, HIDE_TIME);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isFromUser = true;
            currentPos = player.getCurrentPosition();
            currentProgress = seekBar.getProgress();
            mHandler.removeCallbacks(hideRunnable);
        }

        @Override
        public void onProgressChanged(final SeekBar seekBar, final int progress,
                                      boolean fromUser) {
//            if ((isLookArea && !isThree) || isVipArea) {
//                //试看区假进度
//                long pos = 1L * player.getCurrentPosition() / player.getDuration();
//                seekBar.setProgress((int) pos);
//            } else
            if (fromUser) {
                int time = progress * player.getDuration() / 100;
                player.seekTo(time);
            }
        }
    };

    private void backward(float delataX) {
//        int current = player.getCurrentPosition();
//        int backwardTime = (int) (delataX / width * player.getDuration());
//        int currentTime = current - backwardTime;
//        player.seekTo(currentTime);
//        mSeekBar.setProgress(currentTime * 100 / player.getDuration());
//        mPlayTime.setText(formatTime(currentTime));
    }

    private void forward(float delataX) {
//        int current = player.getCurrentPosition();
//        int forwardTime = (int) (delataX / width * player.getDuration());
//        int currentTime = current + forwardTime;
//        player.seekTo(currentTime);
//        mSeekBar.setProgress(currentTime * 100 / player.getDuration());
//        mPlayTime.setText(formatTime(currentTime));
    }

    private void volumeDown(float delatY) {
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int down = (int) (delatY / height * max * 3);
        int volume = Math.max(current - down, 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        int transformatVolume = volume * 100 / max;
        volumnController.show(transformatVolume);
    }

    private void volumeUp(float delatY) {
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int up = (int) ((delatY / height) * max * 3);
        int volume = Math.min(current + up, max);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        int transformatVolume = volume * 100 / max;
        volumnController.show(transformatVolume);
    }

    private void lightDown(float delatY) {
        int down = (int) (delatY / height * 255 * 3);
        int transformatLight = LightnessController.getLightness(this) - down;
        LightnessController.setLightness(this, transformatLight);
    }

    private void lightUp(float delatY) {
        int up = (int) (delatY / height * 255 * 3);
        int transformatLight = LightnessController.getLightness(this) + up;
        LightnessController.setLightness(this, transformatLight);
    }

    //非会员倒计时
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            player.lookAreaTotalSecond--;
            if (player.lookAreaTotalSecond <= 0) {
                mHandler.sendEmptyMessage(LOOK_TIME_END);
            } else {
                mHandler.sendEmptyMessage(REFRESH_LOOK_TIME);
            }
        }
    };

    public String formatLongToTimeStr(Long l) {
        int hour = 0;
        int minute = 0;
        int second = 0;
        second = l.intValue();
        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        String strtime = hour + ":" + minute + ":" + second + "";
        return strtime;
    }

}
