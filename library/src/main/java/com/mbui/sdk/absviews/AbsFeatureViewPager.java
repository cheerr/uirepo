package com.mbui.sdk.absviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.mbui.sdk.afix.FixedViewPager;
import com.mbui.sdk.feature.abs.AbsFeatureBuilder;
import com.mbui.sdk.feature.abs.AbsViewFeature;
import com.mbui.sdk.feature.callback.AddFeatureCallBack;
import com.mbui.sdk.feature.callback.ComputeScrollCallBack;
import com.mbui.sdk.feature.callback.DispatchTouchEventCallBack;
import com.mbui.sdk.feature.callback.InterceptTouchEventCallBack;
import com.mbui.sdk.feature.callback.IsFocusedCallBack;
import com.mbui.sdk.feature.callback.OnDrawCallBack;
import com.mbui.sdk.feature.callback.OnFocusChangedCallBack;
import com.mbui.sdk.feature.callback.OnWindowFocusChangedCallBack;
import com.mbui.sdk.feature.callback.TouchEventCallBack;
import com.mbui.sdk.feature.viewpager.callback.PageItemListener;
import com.mbui.sdk.feature.viewpager.callback.PageScrolledCallBack;
import com.mbui.sdk.feature.viewpager.callback.SetPageAdapterCallBack;
import com.mbui.sdk.reforms.Debug;

import java.util.ArrayList;

/**
 * Created by chenwei on 15/1/20.
 */
public class AbsFeatureViewPager extends FixedViewPager implements AbsFeatureBuilder<AbsViewFeature<FixedViewPager>> {

    private static final String debug = "AbsFeatureViewPager";
    private ArrayList<AbsViewFeature<FixedViewPager>> mFeatureList = new ArrayList<>();

    public AbsFeatureViewPager(Context context) {
        this(context, null);
    }

    public AbsFeatureViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        constructor(context, attrs);
    }

    private void initSelf() {

    }

    private void constructor(Context context, AttributeSet attrs) {
        this.initSelf();
    }

    @Override
    public void addFeature(@NonNull AbsViewFeature<FixedViewPager> feature) {
        if (!mFeatureList.contains(feature)) {
            if (feature instanceof AddFeatureCallBack) {
                ((AddFeatureCallBack) feature).beforeAddFeature(feature);
            }
            mFeatureList.add(feature);
            feature.setHost(this);
            if (feature instanceof AddFeatureCallBack) {
                ((AddFeatureCallBack) feature).afterAddFeature(feature);
            }
        } else {
            Debug.print(debug, "添加失败，" + feature.getClass().getSimpleName() + " 已存在！！！");
        }
    }

    @Override
    public void removeFeature(@NonNull AbsViewFeature<FixedViewPager> feature) {
        if (mFeatureList.contains(feature)) {
            mFeatureList.remove(feature);
        } else {
            Debug.print(debug, "删除失败，" + feature.getClass().getSimpleName() + " 不存在！！！");
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        boolean drawSuper = true;
        for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
            if (feature instanceof OnDrawCallBack) {
                drawSuper = drawSuper && ((OnDrawCallBack) feature).beforeOnDraw(canvas);
            }
        }
        if (drawSuper) {
            super.onDraw(canvas);
            for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
                if (feature instanceof OnDrawCallBack) {
                    ((OnDrawCallBack) feature).afterOnDraw(canvas);
                }
            }
        }
    }

    @Override
    public void computeScroll() {
        for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
            if (feature instanceof ComputeScrollCallBack) {
                ((ComputeScrollCallBack) feature).beforeComputeScroll();
            }
        }
        super.computeScroll();
        for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
            if (feature instanceof ComputeScrollCallBack) {
                ((ComputeScrollCallBack) feature).afterComputeScroll();
            }
        }
    }

    //监控TouchEvent事件分发
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            boolean dispatch = true;
            for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
                if (feature instanceof DispatchTouchEventCallBack) {
                    dispatch = dispatch && ((DispatchTouchEventCallBack) feature).beforeDispatchTouchEvent(ev);
                }
            }
            if (!dispatch) return true;//如果dispatch为false,拦截事件传递
            dispatch = super.dispatchTouchEvent(ev);
            for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
                if (feature instanceof DispatchTouchEventCallBack) {
                    ((DispatchTouchEventCallBack) feature).afterDispatchTouchEvent(ev);
                }
            }
            return dispatch;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
                if (feature instanceof InterceptTouchEventCallBack) {
                    ((InterceptTouchEventCallBack) feature).beforeInterceptTouchEvent(ev);
                }
            }
            boolean dispatch = super.onInterceptTouchEvent(ev);
            for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
                if (feature instanceof InterceptTouchEventCallBack) {
                    ((InterceptTouchEventCallBack) feature).afterInterceptTouchEvent(ev);
                }
            }
            return dispatch;
        } catch (IllegalArgumentException ex) {
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            boolean dispatch = true;
            for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
                if (feature instanceof TouchEventCallBack) {
                    dispatch = dispatch && ((TouchEventCallBack) feature).beforeOnTouchEvent(ev);
                }
            }
            if (!dispatch) return true;//如果dispatch为false,拦截事件传递
            dispatch = super.onTouchEvent(ev);
            for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
                if (feature instanceof TouchEventCallBack) {
                    ((TouchEventCallBack) feature).afterOnTouchEvent(ev);
                }
            }
            return dispatch;
        } catch (IllegalArgumentException ex) {
            return true;
        }
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
            if (feature instanceof SetPageAdapterCallBack) {
                ((SetPageAdapterCallBack) feature).beforeSetAdapter(adapter);
            }
        }
        super.setAdapter(adapter);
        for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
            if (feature instanceof SetPageAdapterCallBack) {
                ((SetPageAdapterCallBack) feature).afterSetAdapter(adapter);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
            if (feature instanceof PageScrolledCallBack) {
                ((PageScrolledCallBack) feature).beforePageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
            if (feature instanceof PageScrolledCallBack) {
                ((PageScrolledCallBack) feature).afterPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }
    }

    @Override
    public void afterInstantiateItem(ViewGroup container, View item, int position) {
        for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
            if (feature instanceof PageItemListener) {
                ((PageItemListener) feature).afterInstantiateItem(container, item, position);
            }
        }
    }

    @Override
    public void beforeDestroyItem(ViewGroup container, View item, int position) {
        for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
            if (feature instanceof PageItemListener) {
                ((PageItemListener) feature).beforeDestroyItem(container, item, position);
            }
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        boolean dispatch = true;
        for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
            if (feature instanceof OnFocusChangedCallBack) {
                dispatch = dispatch && ((OnFocusChangedCallBack) feature).beforeFocusChanged(focused, direction, previouslyFocusedRect);
            }
        }
        if (!dispatch) return;
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
            if (feature instanceof OnFocusChangedCallBack) {
                ((OnFocusChangedCallBack) feature).afterFocusChanged(focused, direction, previouslyFocusedRect);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        boolean dispatch = true;
        for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
            if (feature instanceof OnWindowFocusChangedCallBack) {
                dispatch = dispatch && ((OnWindowFocusChangedCallBack) feature).beforeWindowFocusChanged(hasWindowFocus);
            }
        }
        if (!dispatch) return;
        super.onWindowFocusChanged(hasWindowFocus);
        for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
            if (feature instanceof OnWindowFocusChangedCallBack) {
                ((OnWindowFocusChangedCallBack) feature).afterWindowFocusChanged(hasWindowFocus);
            }
        }
    }

    @Override
    public boolean isFocused() {
        int count = 0;
        if (mFeatureList != null && mFeatureList.size() > 0) {
            for (AbsViewFeature<FixedViewPager> feature : mFeatureList) {
                if (feature instanceof IsFocusedCallBack) {
                    count += ((IsFocusedCallBack) feature).isFocused();
                }
            }
        }
        if (count == 0) return super.isFocused();
        return count > 0;
    }
}
