package com.mbui.sdk.feature.viewpager.features;

import android.content.Context;
import android.view.MotionEvent;

import com.mbui.sdk.afix.FixedViewPager;
import com.mbui.sdk.feature.abs.AbsFeature;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by chenwei on 15/1/21.
 */
public class PageControllerFeature extends PageTransFeature {

    private FixedViewPager viewPager;
    private boolean isMoving;
    private float downX = -1f;
    private String debug = "PageControllerFeature";

    public PageControllerFeature(Context context) {
        super(context);
    }

    @Override
    public void afterAddFeature(AbsFeature feature) {
        super.afterAddFeature(feature);
        viewPager = getHost();
        setTransMode(TRANS_MODE.FADE_ORDER);
        setScaleMax(1f);
    }

    public void moveNext() {
        //发现如果使用viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);情况下会出现同时3个View的Bug，待解决
        //这里利用手动动画弥补
        if (viewPager != null && viewPager.getAdapter() != null && viewPager.getAdapter().getCount() > 1) {
            ValueAnimator animator = ValueAnimator.ofFloat(1e-5f, 1f);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (Float) animation.getAnimatedValue();
                    if (viewPager == null) return;
                    if (value < 1f) {
                        beforePageScrolled(viewPager.getCurrentItem(), value, 0);
                    } else {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, false);
                    }
                }
            });
            animator.setDuration(320).start();
        }
    }

    public void moveLast() {
        if (viewPager != null && viewPager.getCurrentItem() > 0)
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
    }

    @Override
    public void afterPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        super.afterPageScrolled(position, positionOffset, positionOffsetPixels);
        isMoving = positionOffsetPixels != 0;
    }


    @Override
    public boolean beforeDispatchTouchEvent(MotionEvent event) {
        if (isMoving) return false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = event.getX();
        }
        if (isMoving || event.getAction() == MotionEvent.ACTION_MOVE) {
            event.setLocation(downX, event.getY());  //防止左右滑动
        }
        return super.beforeDispatchTouchEvent(event);
    }
}
