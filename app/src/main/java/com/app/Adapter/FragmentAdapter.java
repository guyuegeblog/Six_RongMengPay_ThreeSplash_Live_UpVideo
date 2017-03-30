package com.app.Adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.Bean.Multi;
import com.app.Fragment.ChannelFragment;
import com.app.Fragment.CodeFragment;
import com.app.Fragment.CrownFragment;
import com.app.Fragment.DiamondFragment;
import com.app.Fragment.LiveFragment;
import com.app.Fragment.JarpanFragment;
import com.app.Fragment.LookAtFragment;
import com.app.Fragment.PianKuFragment;
import com.app.Fragment.PlatNumFragment;
import com.app.Fragment.RedDiamondFragment;
import com.app.Fragment.SilverFragment;
import com.app.Fragment.ThreeFragment;
import com.third.app.R;
import com.app.Tool.VipTool;
import com.shizhefei.view.indicator.IndicatorViewPager;

/**
 * Created by ASUS on 2016/12/7.
 */
public class FragmentAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

    //无码和白银
    private int[] tabIcons1 = {R.drawable.tab_1_selector,
            R.drawable.tab_2_selector, R.drawable.tab_14_selector, R.drawable.tab_4_selector,
            R.drawable.tab_6_selector, R.drawable.tab_5_selector
    };

    //白银和黄金
    private int[] tabIcons2 = {R.drawable.tab_5_selector,
            R.drawable.tab_14_selector, R.drawable.tab_3_selector, R.drawable.tab_4_selector,
            R.drawable.tab_6_selector, R.drawable.tab_15_selector
    };
    //白金和钻石
//    private int[] tabIcons3 = {R.drawable.tab_5_selector,
//            R.drawable.tab_3_selector, R.drawable.tab_10_selector, R.drawable.tab_12_selector,
//            R.drawable.tab_4_selector, R.drawable.tab_6_selector
//    };
    //黄金和白金
    private int[] tabIcons3 = {R.drawable.tab_5_selector,
            R.drawable.tab_3_selector, R.drawable.tab_10_selector, R.drawable.tab_4_selector,
            R.drawable.tab_6_selector
    };
    //白金和钻石
    private int[] tabIcons4 = {R.drawable.tab_5_selector, R.drawable.tab_10_selector, R.drawable.tab_12_selector,
            R.drawable.tab_4_selector, R.drawable.tab_6_selector
    };
    //钻石和红钻
    private int[] tabIcons5 = {R.drawable.tab_5_selector, R.drawable.tab_12_selector, R.drawable.tab_16_selector,
            R.drawable.tab_4_selector, R.drawable.tab_6_selector
    };
    //红钻和皇冠
    private int[] tabIcons6 = {R.drawable.tab_5_selector, R.drawable.tab_16_selector, R.drawable.tab_17_selector,
            R.drawable.tab_4_selector, R.drawable.tab_6_selector
    };


    private LayoutInflater inflater;
    private Activity mContext;

    public FragmentAdapter(FragmentManager fragmentManager, Activity activity) {
        super(fragmentManager);
        inflater = LayoutInflater.from(activity);
        this.mContext = activity;
    }

    @Override
    public int getCount() {
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_NOT_VIP_TYPE) {
            return tabIcons1.length;
        } else if (vipType == Multi.VIP_SILVER_TYPE) {
            return tabIcons2.length;
        } else if (vipType == Multi.VIP_GOLD_TYPE) {
            return tabIcons3.length;
        } else if (vipType == Multi.VIP_PLAT_NIUM_TYPE) {
            return tabIcons4.length;
        } else if (vipType == Multi.VIP_DIAMOND_TYPE) {
            return tabIcons5.length;
        } else if (vipType == Multi.VIP_RED_DIAMOND_TYPE) {
            return tabIcons6.length;
        } else {
            return tabIcons6.length;
        }
    }

    @Override
    public View getViewForTab(final int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = (ImageView) inflater.inflate(R.layout.tab_menu, container, false);
        }
        ImageView imageView = (ImageView) convertView;
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_NOT_VIP_TYPE) {
            imageView.setBackgroundResource(tabIcons1[position]);
        } else if (vipType == Multi.VIP_SILVER_TYPE) {
            imageView.setBackgroundResource(tabIcons2[position]);
        } else if (vipType == Multi.VIP_GOLD_TYPE) {
            imageView.setBackgroundResource(tabIcons3[position]);
        } else if (vipType == Multi.VIP_PLAT_NIUM_TYPE) {
            imageView.setBackgroundResource(tabIcons4[position]);
        } else if (vipType == Multi.VIP_DIAMOND_TYPE) {
            imageView.setBackgroundResource(tabIcons5[position]);
        } else if (vipType == Multi.VIP_RED_DIAMOND_TYPE) {
            imageView.setBackgroundResource(tabIcons6[position]);
        } else {
            imageView.setBackgroundResource(tabIcons6[position]);
        }
//      imageView.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[position], 0, 0);
        return imageView;
    }

    private LookAtFragment lookAtFragment;
    private CodeFragment codeFragment;
    private PlatNumFragment platNumFragment;
    private DiamondFragment diamondFragment;
    private JarpanFragment jarpanFragment;
    private ChannelFragment channelFragment;
    private LiveFragment liveFragment;
    private ThreeFragment threeFragment;
    private SilverFragment silverFragment;
    private PianKuFragment pianKuFragment;
    private RedDiamondFragment redDiamondFragment;
    private CrownFragment crownFragment;

    @Override
    public Fragment getFragmentForPage(int position) {
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_NOT_VIP_TYPE) {
            //无码和白银
            if (position == 0) {
                return lookAtFragment == null ? lookAtFragment = new LookAtFragment() : lookAtFragment;
            } else if (position == 1) {
                return codeFragment == null ? codeFragment = new CodeFragment() : codeFragment;
            } else if (position == 2) {
                return silverFragment == null ? silverFragment = new SilverFragment() : silverFragment;
            } else if (position == 3) {
                return channelFragment == null ? channelFragment = new ChannelFragment() : channelFragment;
            } else if (position == 4) {
                return threeFragment == null ? threeFragment = new ThreeFragment() : threeFragment;
            } else {
                return liveFragment == null ? liveFragment = new LiveFragment() : liveFragment;
            }
        } else if (vipType == Multi.VIP_SILVER_TYPE) {
            //白银和黄金
            if (position == 0) {
                return liveFragment == null ? liveFragment = new LiveFragment() : liveFragment;
            } else if (position == 1) {
                return silverFragment == null ? silverFragment = new SilverFragment() : silverFragment;
            } else if (position == 2) {
                return jarpanFragment == null ? jarpanFragment = new JarpanFragment() : jarpanFragment;
            } else if (position == 3) {
                return channelFragment == null ? channelFragment = new ChannelFragment() : channelFragment;
            } else if (position == 4) {
                return threeFragment == null ? threeFragment = new ThreeFragment() : threeFragment;
            } else {
                return pianKuFragment == null ? pianKuFragment = new PianKuFragment() : pianKuFragment;
            }
        } else if (vipType == Multi.VIP_GOLD_TYPE) {
            //黄金和白金
            if (position == 0) {
                return liveFragment == null ? liveFragment = new LiveFragment() : liveFragment;
            } else if (position == 1) {
                return jarpanFragment == null ? jarpanFragment = new JarpanFragment() : jarpanFragment;
            } else if (position == 2) {
                return platNumFragment == null ? platNumFragment = new PlatNumFragment() : platNumFragment;
            } else if (position == 3) {
                return channelFragment == null ? channelFragment = new ChannelFragment() : channelFragment;
            } else {
                return threeFragment == null ? threeFragment = new ThreeFragment() : threeFragment;
            }
        } else if (vipType == Multi.VIP_PLAT_NIUM_TYPE) {
            //白金和钻石
            if (position == 0) {
                return liveFragment == null ? liveFragment = new LiveFragment() : liveFragment;
            } else if (position == 1) {
                return platNumFragment == null ? platNumFragment = new PlatNumFragment() : platNumFragment;
            } else if (position == 2) {
                return diamondFragment == null ? diamondFragment = new DiamondFragment() : diamondFragment;
            } else if (position == 3) {
                return channelFragment == null ? channelFragment = new ChannelFragment() : channelFragment;
            } else {
                return threeFragment == null ? threeFragment = new ThreeFragment() : threeFragment;
            }
        } else if (vipType == Multi.VIP_DIAMOND_TYPE) {
            //粉钻和钻石
            if (position == 0) {
                return liveFragment == null ? liveFragment = new LiveFragment() : liveFragment;
            } else if (position == 1) {
                return diamondFragment == null ? diamondFragment = new DiamondFragment() : diamondFragment;
            } else if (position == 2) {
                return redDiamondFragment == null ? redDiamondFragment = new RedDiamondFragment() : redDiamondFragment;
            } else if (position == 3) {
                return channelFragment == null ? channelFragment = new ChannelFragment() : channelFragment;
            } else {
                return threeFragment == null ? threeFragment = new ThreeFragment() : threeFragment;
            }
        } else if (vipType == Multi.VIP_RED_DIAMOND_TYPE) {
            //粉钻和皇冠
            if (position == 0) {
                return liveFragment == null ? liveFragment = new LiveFragment() : liveFragment;
            } else if (position == 1) {
                return redDiamondFragment == null ? redDiamondFragment = new RedDiamondFragment() : redDiamondFragment;
            } else if (position == 2) {
                return crownFragment == null ? crownFragment = new CrownFragment() : crownFragment;
            } else if (position == 3) {
                return channelFragment == null ? channelFragment = new ChannelFragment() : channelFragment;
            } else {
                return threeFragment == null ? threeFragment = new ThreeFragment() : threeFragment;
            }
        } else {
            if (position == 0) {
                return liveFragment == null ? liveFragment = new LiveFragment() : liveFragment;
            } else if (position == 1) {
                return redDiamondFragment == null ? redDiamondFragment = new RedDiamondFragment() : redDiamondFragment;
            } else if (position == 2) {
                return crownFragment == null ? crownFragment = new CrownFragment() : crownFragment;
            } else if (position == 3) {
                return channelFragment == null ? channelFragment = new ChannelFragment() : channelFragment;
            } else {
                return threeFragment == null ? threeFragment = new ThreeFragment() : threeFragment;
            }
        }
    }
}