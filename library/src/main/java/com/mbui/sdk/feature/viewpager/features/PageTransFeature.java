package com.mbui.sdk.feature.viewpager.features;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.mbui.sdk.afix.FixedViewPager;
import com.mbui.sdk.feature.abs.AbsFeature;
import com.mbui.sdk.feature.callback.AddFeatureCallBack;
import com.mbui.sdk.feature.callback.ComputeScrollCallBack;
import com.mbui.sdk.feature.view.features.ViewTouchFeature;
import com.mbui.sdk.feature.viewpager.callback.PageItemListener;
import com.mbui.sdk.feature.viewpager.callback.PageScrolledCallBack;
import com.mbui.sdk.reforms.Debug;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by chenwei on 15/1/20.
 */
public class PageTransFeature extends ViewTouchFeature<FixedViewPager> implements PageItemListener, PageScrolledCallBack,
        ComputeScrollCallBack, AddFeatureCallBack {

    private String debug = "PageTransFeature";
    private SparseArray<View> views;
    private Scroller mScroller;
    private FixedViewPager viewPager;
    private int transChildId;
    private boolean ON_POINT_UP;
    private TRANS_MODE transMode = TRANS_MODE.FADE_MIX;
    private int currPosition = 0;
    private float SCALE_MAX = 1.3f;
    private boolean viewChanged;

    public static enum TRANS_MODE {
        FADE_MIX, FADE_ORDER
    }

    public PageTransFeature(Context context) {
        super(context);
        initSelf();
    }

    private void initSelf() {
        views = new SparseArray<>();
    }

    public SparseArray<View> getViews() {
        return views;
    }

    public void setTransMode(TRANS_MODE mode) {
        this.transMode = mode;
    }

    @Override
    public void constructor(Context context, AttributeSet attrs, int defStyle) {

    }

    @Override
    public void afterInstantiateItem(ViewGroup container, View item, int position) {
        views.put(position, item);
        Debug.print(debug, "afterInstantiateItem  " + position);
    }

    @Override
    public void beforeDestroyItem(ViewGroup container, View item, int position) {
        int offset = 2 * viewPager.getOffscreenPageLimit() + 2;
        if (views.get(position + offset) != null)
            views.remove(position + offset);
        if (position >= offset && views.get(position - offset) != null)
            views.remove(position - offset);
        Debug.print(debug, "beforeDestroyItem  " + position);
    }

    @Override
    public void beforePageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        currPosition = position;
        transView(views.get(position), views.get(position + 1), positionOffset, positionOffsetPixels);
        Debug.print(debug, "beforePageScrolled  " + "   " + positionOffsetPixels);
    }

    @Override
    public void afterPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    //与 viewPager.getCurrentItem()相比，这个是完成过程的动态位置，viewPager.getCurrentItem()是最终结果
    public int getScrollPosition() {
        return currPosition;
    }

    public void setScaleMax(float scaleMax) {
        this.SCALE_MAX = scaleMax;
    }

    @Override
    public void beforeComputeScroll() {
        if (ON_POINT_UP && viewPager.getWidth() > 0 && mScroller.computeScrollOffset()) {
            int positionOffsetPixels = mScroller.getCurrX() % viewPager.getWidth();
            transView(views.get(currPosition), views.get(currPosition + 1), 1.0f * positionOffsetPixels / viewPager.getWidth(), positionOffsetPixels);
            Debug.print(debug, "beforeComputeScroll  " + 1.0f * positionOffsetPixels / viewPager.getWidth() + "   " + positionOffsetPixels);
        }
    }

    @Override
    public void afterComputeScroll() {

    }

    /**
     * 设置变换的View
     *
     * @param view
     * @return
     */
    private View getChildView(View view) {
        if (view == null || transChildId == 0 || view.findViewById(transChildId) == null)
            return view;
        return view.findViewById(transChildId);
    }

    //设置切换效果的View,默认是页面本身，可以设置他的子View
    public void setTransChildId(int resId) {
        this.transChildId = resId;
    }

    //选择切换方式
    private void transView(View left, View right, float positionOffset, int positionOffsetPixels) {
        if (positionOffset < 0 || positionOffsetPixels < 0) return;
        if (positionOffset < 1e-6) {
            if (viewChanged) {
                disableHardwareLayer();
                resetView();
                viewChanged = false;
            }
        } else {
            switch (transMode) {
                case FADE_MIX:
                case FADE_ORDER:
                    animateFade(left, right, positionOffset, positionOffsetPixels);
                    break;
            }
            viewChanged = true;
        }
    }

    private void animateFade(View left, View right, float positionOffset, int positionOffsetPixels) {
        Debug.print(debug, "animateFade  " + positionOffset + "   " + positionOffsetPixels);
        if (right != null) {
            manageLayer(right, true);
            int mTrans = -viewPager.getWidth() - viewPager.getPageMargin() + positionOffsetPixels;
            switch (transMode) {
                case FADE_MIX:
                    ViewHelper.setTranslationX(right, mTrans);
                    ViewHelper.setAlpha(getChildView(right), positionOffset);
                    break;
                case FADE_ORDER:
                    ViewHelper.setTranslationX(right, mTrans);
                    ViewHelper.setAlpha(getChildView(right), positionOffset < 0.5 ? 0 : 2f * positionOffset - 1f);
                    break;
            }
        }
        if (left != null) {
            manageLayer(left, true);
            float mScale = (SCALE_MAX - 1) * positionOffset + 1;
            ViewHelper.setTranslationX(left, positionOffsetPixels);
            ViewHelper.setScaleX(left, mScale);
            ViewHelper.setScaleY(left, mScale);
            switch (transMode) {
                case FADE_MIX:
                    ViewHelper.setAlpha(getChildView(left), 1 - positionOffset);
                    break;
                case FADE_ORDER:
                    ViewHelper.setAlpha(getChildView(left), positionOffset > 0.5 ? 0f : 1 - 2f * positionOffset);
                    break;
            }
        }
    }

    private void resetView() {
        for (int i = 0; i < viewPager.getChildCount(); i++) {
            View view = viewPager.getChildAt(i);
            if (view != null) {
                ViewHelper.setTranslationX(view, 0);
                ViewHelper.setScaleX(view, 1f);
                ViewHelper.setScaleY(view, 1f);
                ViewHelper.setAlpha(getChildView(view), 1f);
            }
        }
    }

    @Override
    public boolean beforeDispatchTouchEvent(MotionEvent event) {
        ON_POINT_UP = event.getAction() == MotionEvent.ACTION_UP;
        return super.beforeDispatchTouchEvent(event);
    }

    @Override
    public void afterDispatchTouchEvent(MotionEvent event) {
        super.afterDispatchTouchEvent(event);
    }

    @Override
    public void beforeAddFeature(AbsFeature feature) {
    }

    @Override
    public void afterAddFeature(AbsFeature feature) {
        mScroller = getHost().getScroller();
        viewPager = getHost();
        viewPager.setScrollDurationFactor(4f);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void disableHardwareLayer() {
        if (Build.VERSION.SDK_INT < 11) return;
        for (int i = 0; i < viewPager.getChildCount(); i++) {
            View v = viewPager.getChildAt(i);
            if (v.getLayerType() != View.LAYER_TYPE_NONE)
                v.setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void manageLayer(View v, boolean enableHardware) {
        if (Build.VERSION.SDK_INT < 11) return;
        int layerType = enableHardware ? View.LAYER_TYPE_HARDWARE : View.LAYER_TYPE_NONE;
        if (layerType != v.getLayerType())
            v.setLayerType(layerType, null);
    }
}
