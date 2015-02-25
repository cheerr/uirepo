package com.mbui.sdk.absviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListAdapter;

import com.mbui.sdk.R;
import com.mbui.sdk.afix.FixedListView;
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
import com.mbui.sdk.feature.callback.ScrollCallBack;
import com.mbui.sdk.feature.callback.TouchEventCallBack;
import com.mbui.sdk.feature.enums.PullRefreshEnum;
import com.mbui.sdk.feature.pullrefresh.callback.SetListAdapterCallBack;
import com.mbui.sdk.feature.pullrefresh.features.common.PullTipFeature;
import com.mbui.sdk.feature.pullrefresh.features.common.PullToRefreshFeature;
import com.mbui.sdk.feature.pullrefresh.features.listview.SecPullFeature;
import com.mbui.sdk.feature.pullrefresh.features.listview.SmoothListFeature;
import com.mbui.sdk.reforms.Debug;

import java.util.ArrayList;

/**
 * Created by chenwei on 15/1/14.
 * <p/>
 * 注意：
 * 如果需要对AbsFeatureListView内方法进行重载，需要注意
 * 方法中feature的beforeXXX方法只保证在基类AbsFeatureListView调用该函数之前执行
 */
public class AbsFeatureListView extends FixedListView implements AbsFeatureBuilder<AbsViewFeature<FixedListView>>, AbsListView.OnScrollListener {

    private static final String debug = "AbsFeatureListView";
    private ArrayList<AbsViewFeature<FixedListView>> mFeatureList = new ArrayList<>();
    private AbsListView.OnScrollListener mOnScrollListener;

    public AbsFeatureListView(Context context) {
        this(context, null);
    }

    public AbsFeatureListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        constructor(context, attrs, 0);
    }

    public AbsFeatureListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        constructor(context, attrs, defStyleAttr);
    }

    private void constructor(Context context, AttributeSet attrs, int defStyleAttr) {
        this.initSelf();
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.feature);
            String value = a.getString(R.styleable.feature_listview);
            if (value == null || value.length() == 0) {
                a.recycle();
                return;
            }
            AbsViewFeature<FixedListView> innerFeature;
            switch (Integer.parseInt(value)) {
                case PullRefreshEnum.SmoothListFeature:
                    innerFeature = new SmoothListFeature(context);
                    mFeatureList.add(innerFeature);
                    innerFeature.setHost(this);
                    innerFeature.constructor(context, attrs, defStyleAttr);
                    break;
                case PullRefreshEnum.PullToRefreshFeature:
                    innerFeature = new PullToRefreshFeature<>(context);
                    mFeatureList.add(innerFeature);
                    innerFeature.setHost(this);
                    innerFeature.constructor(context, attrs, defStyleAttr);
                    break;
                case PullRefreshEnum.PullTipFeature:
                    innerFeature = new PullTipFeature<>(context);
                    mFeatureList.add(innerFeature);
                    innerFeature.setHost(this);
                    innerFeature.constructor(context, attrs, defStyleAttr);
                    break;
                case PullRefreshEnum.SecPullFeature:
                    innerFeature = new SecPullFeature(context);
                    mFeatureList.add(innerFeature);
                    innerFeature.setHost(this);
                    innerFeature.constructor(context, attrs, defStyleAttr);
                    break;
            }
            a.recycle();
        }
    }

    /**
     * 初始化
     */
    private void initSelf() {
        super.setOnScrollListener(this);
    }

    @Override
    public void addFeature(@NonNull AbsViewFeature<FixedListView> feature) {
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
    public void removeFeature(@NonNull AbsViewFeature<FixedListView> feature) {
        if (mFeatureList.contains(feature)) {
            mFeatureList.remove(feature);
        } else {
            Debug.print(debug, "删除失败，" + feature.getClass().getSimpleName() + " 不存在！！！");
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        for (AbsViewFeature<FixedListView> feature : mFeatureList) {
            if (feature instanceof SetListAdapterCallBack) {
                ((SetListAdapterCallBack) feature).beforeSetAdapter(adapter);
            }
        }
        super.setAdapter(adapter);
        for (AbsViewFeature<FixedListView> feature : mFeatureList) {
            if (feature instanceof SetListAdapterCallBack) {
                ((SetListAdapterCallBack) feature).afterSetAdapter(adapter);
            }
        }
    }

    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener listener) {
        this.mOnScrollListener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (this.mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
        for (AbsViewFeature<FixedListView> feature : mFeatureList) {
            if (feature instanceof ScrollCallBack) {
                ((ScrollCallBack) feature).onScrollStateChanged(view, scrollState != SCROLL_STATE_IDLE);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (this.mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        for (AbsViewFeature<FixedListView> feature : mFeatureList) {
            if (feature instanceof ScrollCallBack) {
                ((ScrollCallBack) feature).onScroll(this, getScrollY(), getScrollX());
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        boolean drawSuper = true;
        for (AbsViewFeature<FixedListView> feature : mFeatureList) {
            if (feature instanceof OnDrawCallBack) {
                drawSuper = drawSuper && ((OnDrawCallBack) feature).beforeOnDraw(canvas);
            }
        }
        if (drawSuper) {
            super.onDraw(canvas);
            for (AbsViewFeature<FixedListView> feature : mFeatureList) {
                if (feature instanceof OnDrawCallBack) {
                    ((OnDrawCallBack) feature).afterOnDraw(canvas);
                }
            }
        }
    }

    @Override
    public void computeScroll() {
        for (AbsViewFeature<FixedListView> feature : mFeatureList) {
            if (feature instanceof ComputeScrollCallBack) {
                ((ComputeScrollCallBack) feature).beforeComputeScroll();
            }
        }
        super.computeScroll();
        for (AbsViewFeature<FixedListView> feature : mFeatureList) {
            if (feature instanceof ComputeScrollCallBack) {
                ((ComputeScrollCallBack) feature).afterComputeScroll();
            }
        }
    }

    //监控TouchEvent事件分发
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean dispatch = true;
        for (AbsViewFeature<FixedListView> feature : mFeatureList) {
            if (feature instanceof DispatchTouchEventCallBack) {
                dispatch = dispatch && ((DispatchTouchEventCallBack) feature).beforeDispatchTouchEvent(ev);
            }
        }
        if (!dispatch) return true;//如果dispatch为false,拦截事件传递
        dispatch = super.dispatchTouchEvent(ev);
        for (AbsViewFeature<FixedListView> feature : mFeatureList) {
            if (feature instanceof DispatchTouchEventCallBack) {
                ((DispatchTouchEventCallBack) feature).afterDispatchTouchEvent(ev);
            }
        }
        return dispatch;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        for (AbsViewFeature<FixedListView> feature : mFeatureList) {
            if (feature instanceof InterceptTouchEventCallBack) {
                ((InterceptTouchEventCallBack) feature).beforeInterceptTouchEvent(ev);
            }
        }
        boolean dispatch = super.onInterceptTouchEvent(ev);
        for (AbsViewFeature<FixedListView> feature : mFeatureList) {
            if (feature instanceof InterceptTouchEventCallBack) {
                ((InterceptTouchEventCallBack) feature).afterInterceptTouchEvent(ev);
            }
        }
        return dispatch;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean dispatch = true;
        for (AbsViewFeature<FixedListView> feature : mFeatureList) {
            if (feature instanceof TouchEventCallBack) {
                dispatch = dispatch && ((TouchEventCallBack) feature).beforeOnTouchEvent(ev);
            }
        }
        if (!dispatch) return true;//如果dispatch为false,拦截事件传递
        dispatch = super.onTouchEvent(ev);
        for (AbsViewFeature<FixedListView> feature : mFeatureList) {
            if (feature instanceof TouchEventCallBack) {
                ((TouchEventCallBack) feature).afterOnTouchEvent(ev);
            }
        }
        return dispatch;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        boolean dispatch = true;
        for (AbsViewFeature<FixedListView> feature : mFeatureList) {
            if (feature instanceof OnFocusChangedCallBack) {
                dispatch = dispatch && ((OnFocusChangedCallBack) feature).beforeFocusChanged(focused, direction, previouslyFocusedRect);
            }
        }
        if (!dispatch) return;
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        for (AbsViewFeature<FixedListView> feature : mFeatureList) {
            if (feature instanceof OnFocusChangedCallBack) {
                ((OnFocusChangedCallBack) feature).afterFocusChanged(focused, direction, previouslyFocusedRect);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        boolean dispatch = true;
        for (AbsViewFeature<FixedListView> feature : mFeatureList) {
            if (feature instanceof OnWindowFocusChangedCallBack) {
                dispatch = dispatch && ((OnWindowFocusChangedCallBack) feature).beforeWindowFocusChanged(hasWindowFocus);
            }
        }
        if (!dispatch) return;
        super.onWindowFocusChanged(hasWindowFocus);
        for (AbsViewFeature<FixedListView> feature : mFeatureList) {
            if (feature instanceof OnWindowFocusChangedCallBack) {
                ((OnWindowFocusChangedCallBack) feature).afterWindowFocusChanged(hasWindowFocus);
            }
        }
    }

    @Override
    public boolean isFocused() {
        int count = 0;
        if (mFeatureList != null && mFeatureList.size() > 0) {
            for (AbsViewFeature<FixedListView> feature : mFeatureList) {
                if (feature instanceof IsFocusedCallBack) {
                    count += ((IsFocusedCallBack) feature).isFocused();
                }
            }
        }
        if (count == 0) return super.isFocused();
        return count > 0;
    }
}
