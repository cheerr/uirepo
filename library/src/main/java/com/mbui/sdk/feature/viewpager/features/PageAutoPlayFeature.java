package com.mbui.sdk.feature.viewpager.features;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;

import com.mbui.sdk.afix.FixedViewPager;
import com.mbui.sdk.feature.abs.AbsFeature;
import com.mbui.sdk.feature.callback.AddFeatureCallBack;
import com.mbui.sdk.feature.view.features.ViewTouchFeature;
import com.mbui.sdk.feature.viewpager.callback.SetPageAdapterCallBack;

/**
 * Created by chenwei on 15/1/21.
 */
public class PageAutoPlayFeature extends ViewTouchFeature<FixedViewPager> implements
        AddFeatureCallBack, SetPageAdapterCallBack{
    private FixedViewPager viewPager;
    private int moveStep = 1;//方向，1表示向右，-1向左
    private long delay = 4000;

    public PageAutoPlayFeature(Activity context) {
        super(context);
    }

    @Override
    public void constructor(Context context, AttributeSet attrs, int defStyle) {

    }

    @Override
    public void beforeAddFeature(AbsFeature feature) {

    }

    @Override
    public void afterAddFeature(AbsFeature feature) {
        viewPager = getHost();
        getHost().setScrollDurationFactor(5f);
    }

    private void loop() {
        if (viewPager != null && viewPager.getAdapter() != null && viewPager.getAdapter().getCount() > 1) {
            if (moveStep == 1 && viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1) {
                moveStep = -1;
            } else if (moveStep == -1 && viewPager.getCurrentItem() == 0) {
                moveStep = 1;
            }
            viewPager.setCurrentItem(viewPager.getCurrentItem() + moveStep, true);
        }
    }

    public void startPlay() {
        stopPlay();
        mHandler.postDelayed(runnable, delay);
    }

    public void stopPlay() {
        mHandler.removeCallbacks(runnable);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager != null && !isOnTouching() && ((Activity) getContext()).hasWindowFocus()) {
                loop();
            }
            mHandler.sendEmptyMessage(0x22);
        }
    };

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == 0x22) {
                mHandler.postDelayed(runnable, delay);
            }
        }
    };

    @Override
    public void beforeSetAdapter(PagerAdapter adapter) {

    }

    @Override
    public void afterSetAdapter(PagerAdapter adapter) {
        startPlay();
    }
}
