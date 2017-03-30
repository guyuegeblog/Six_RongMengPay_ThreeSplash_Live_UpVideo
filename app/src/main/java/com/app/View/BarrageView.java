package com.app.View;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by lixueyong on 16/2/19.
 */
public class BarrageView extends RelativeLayout {
    private Context mContext;
    private BarrageHandler mHandler = new BarrageHandler();
    private Random random = new Random(System.currentTimeMillis());
    private static final long BARRAGE_GAP_MIN_DURATION = 1000;//两个弹幕的最小间隔时间
    private static final long BARRAGE_GAP_MAX_DURATION = 5000;//两个弹幕的最大间隔时间
    private int maxSpeed = 30000;//速度，ms
    private int minSpeed = 10000;//速度，ms
    private int maxSize = 20;//文字大小，dp
    private int minSize = 15;//文字大小，dp

    private int totalHeight = 0;
    private int lineHeight = 0;//每一行弹幕的高度
    private int totalLine = 0;//弹幕的行数
    private static List<String> itemText = new ArrayList<>();
    private static List<String> showList = new ArrayList<>();
    private boolean isReturn = false;
    public static int count = 0;
    public static int currentCount = 0;
    //    public static String[] itemText = {"谁有星野明的全集资源啊、、求", "我会告诉你我就是冲着这个女猪脚才开的会员吗.资源太全了",
//            "没想到还有这个私家影院。", "抢占沙发。。。。。。",
//            "太精彩了,看来是充值对了,在其它的地方都看不了,还是这个给力!!!", "这个是素人吧,身材真不错,叫的也浪 不错不错",
//            "开了会员,效果还好呀，很棒呢", "哇`` 不错 缓冲的很快 播放的也不卡 值得一看",
//            "这是我见过的最长长长长长长长长长长长的评论"
//            , "我日,怎么最长只能开一年 有没有永久会员啊,我要开通永久的", "这片好刺激呀",
//            "希望可以支持下载到手机，家里没有wifi。想看的时候看不了。"};
    public static int textCount;
//    private List<BarrageItem> itemList = new ArrayList<BarrageItem>();

    public void initData() {
//        itemText.add("谁有星野明的全集资源啊、、求");
//        itemText.add("我会告诉你我就是冲着这个女猪脚才开的会员吗.资源太全了");
//        itemText.add("没想到还有这个私家影院。");
//        itemText.add("抢占沙发。。。。。。");
//        itemText.add("太精彩了,看来是充值对了,在其它的地方都看不了,还是这个给力!!!");
//        itemText.add("这个是素人吧,身材真不错,叫的也浪 不错不错");
//        itemText.add("开了会员,效果还好呀，很棒呢");
//        itemText.add("哇`` 不错 缓冲的很快 播放的也不卡 值得一看");
//        itemText.add("这是我见过的最长长长长长长长长长长长的评论");
//        itemText.add("我日,怎么最长只能开一年 有没有永久会员啊,我要开通永久的\", \"这片好刺激呀");
//        itemText.add("希望可以支持下载到手机，家里没有wifi。想看的时候看不了。");
    }

    public static List<String> getItemText() {
        return itemText;
    }

    public static void setItemText(List<String> itemText) {
        BarrageView.itemText = itemText;
    }

    public BarrageView(Context context) {
        this(context, null);
    }

    public BarrageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BarrageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public static List<String> getShowList() {
        return showList;
    }

    private void init() {
        try {
            initData();
            textCount = itemText.size();
            count = 0;
            int duration_time = createLiveRandom(5, 5);
            long duration = BARRAGE_GAP_MIN_DURATION * duration_time;
            mHandler.sendEmptyMessageDelayed(0, duration);
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        try {
            totalHeight = getMeasuredHeight();
            lineHeight = getLineHeight();
            totalLine = totalHeight / lineHeight;
        }catch (Exception e){
            return;
        }
    }


    private void generateItem() {
        try {
            count++;
            if (count > textCount) {
                mHandler.removeMessages(0);
                count = 0;
                return;
            }
            BarrageItem item = new BarrageItem();
            String tx;
            try {
                tx = itemText.get(count);
                currentCount = count;
            } catch (Exception e) {
                return;
            }
            if (showList.size() >= itemText.size()) {
                mHandler.removeMessages(0);
                return;
            }
            for (int j = 0; j < showList.size(); j++) {
                if (showList.get(j).equals(tx)) {
                    isReturn = true;
                    break;
                } else {
                    isReturn = false;
                }
            }
            if (isReturn) return;

            initBarrageItem(item, tx);
        } catch (Exception e) {
            return;
        }
    }

    public void initBarrageItem(BarrageItem item, String tx) {
        try {
            showList.add(tx);
            int sz = (int) (minSize + (maxSize - minSize) * Math.random());
            item.textView = new TextView(mContext);
            item.textView.setText(tx);
            item.textView.setTextSize(sz);
            item.textView.setTextColor(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            item.textMeasuredWidth = (int) getTextWidth(item, tx, sz);
            item.moveSpeed = (int) (minSpeed + (maxSpeed - minSpeed) * Math.random());
            if (totalLine == 0) {
                totalHeight = getMeasuredHeight();
                lineHeight = getLineHeight();
                totalLine = totalHeight / lineHeight;
            }
            item.verticalPos = random.nextInt(totalLine) * lineHeight;
        } catch (Exception e) {
            return;
        }
//        itemList.add(item);
        showBarrageItem(item);
    }


    Random itemRandom = null;

    private int createLiveRandom(int min, int jia) {
        if (itemRandom == null) {
            itemRandom = new Random();
        }
        int randNum = itemRandom.nextInt(jia) + min;
        return randNum;
    }

    private void showBarrageItem(final BarrageItem item) {

        int leftMargin = this.getRight() - this.getLeft() - this.getPaddingLeft();

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.topMargin = item.verticalPos;
        this.addView(item.textView, params);
        Animation anim = generateTranslateAnim(item, leftMargin);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                item.textView.clearAnimation();
                BarrageView.this.removeView(item.textView);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        item.textView.startAnimation(anim);
    }

    private TranslateAnimation generateTranslateAnim(BarrageItem item, int leftMargin) {
        TranslateAnimation anim = new TranslateAnimation(leftMargin, -item.textMeasuredWidth, 0, 0);
        anim.setDuration(item.moveSpeed);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setFillAfter(true);
        return anim;
    }

    /**
     * 计算TextView中字符串的长度
     *
     * @param text 要计算的字符串
     * @param Size 字体大小
     * @return TextView中字符串的长度
     */
    public float getTextWidth(BarrageItem item, String text, float Size) {
        Rect bounds = new Rect();
        TextPaint paint;
        paint = item.textView.getPaint();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    /**
     * 获得每一行弹幕的最大高度
     *
     * @return
     */
    private int getLineHeight() {
        Rect bounds = new Rect();
        try {
            BarrageItem item = new BarrageItem();
            String tx = itemText.get(0);
            item.textView = new TextView(mContext);
            item.textView.setText(tx);
            item.textView.setTextSize(maxSize);

            TextPaint paint;
            paint = item.textView.getPaint();
            paint.getTextBounds(tx, 0, tx.length(), bounds);
        } catch (Exception e) {
            return 0;
        }
        return bounds.height();
    }

//    public static boolean isShow = true;

    class BarrageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            generateItem();
            //每个弹幕产生的间隔时间随机
            int duration_time = createLiveRandom(5, 5);
            long duration = BARRAGE_GAP_MIN_DURATION * duration_time;
            this.sendEmptyMessageDelayed(0, duration);
//            if(!isShow){
//                mHandler.removeMessages(0);
//            }
        }
    }

    public void startBarrage() {
        int duration_time = createLiveRandom(5, 5);
        long duration = BARRAGE_GAP_MIN_DURATION * duration_time;
        mHandler.sendEmptyMessageDelayed(0, duration);
    }

    public void stopBarrage() {
        mHandler.removeMessages(0);
    }


}
