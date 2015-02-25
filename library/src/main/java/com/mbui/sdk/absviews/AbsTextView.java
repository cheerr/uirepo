package com.mbui.sdk.absviews;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.mbui.sdk.feature.abs.AbsFeatureBuilder;
import com.mbui.sdk.feature.abs.AbsViewFeature;
import com.mbui.sdk.feature.callback.AddFeatureCallBack;
import com.mbui.sdk.feature.callback.DispatchTouchEventCallBack;
import com.mbui.sdk.feature.callback.IsFocusedCallBack;
import com.mbui.sdk.feature.callback.OnFocusChangedCallBack;
import com.mbui.sdk.feature.callback.OnWindowFocusChangedCallBack;
import com.mbui.sdk.feature.callback.TouchEventCallBack;
import com.mbui.sdk.reforms.Debug;
import com.mbui.sdk.widget.TTextView;

import java.util.ArrayList;

/**
 * Created by chenwei on 15/2/10.
 */
public class AbsTextView extends TTextView implements AbsFeatureBuilder<AbsViewFeature<TTextView>> {

    private static final String debug = "AbsTextView";
    private ArrayList<AbsViewFeature<TTextView>> mFeatureList = new ArrayList<>();

    public AbsTextView(Context context) {
        this(context, null);
    }

    public AbsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        constructor(context, attrs);
    }

    /**
     * 初始化
     */
    private void initSelf() {
    }

    private void constructor(Context context, AttributeSet attrs) {
        this.initSelf();
    }

    @Override
    public void addFeature(@NonNull AbsViewFeature<TTextView> feature) {
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
    public void removeFeature(@NonNull AbsViewFeature<TTextView> feature) {
        if (mFeatureList.contains(feature)) {
            mFeatureList.remove(feature);
        } else {
            Debug.print(debug, "删除失败，" + feature.getClass().getSimpleName() + " 不存在！！！");
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean dispatch = true;
        for (AbsViewFeature<TTextView> feature : mFeatureList) {
            if (feature instanceof DispatchTouchEventCallBack) {
                dispatch = dispatch && ((DispatchTouchEventCallBack) feature).beforeDispatchTouchEvent(ev);
            }
        }
        if (!dispatch) return true;//如果dispatch为false,拦截事件传递
        dispatch = super.dispatchTouchEvent(ev);
        for (AbsViewFeature<TTextView> feature : mFeatureList) {
            if (feature instanceof DispatchTouchEventCallBack) {
                ((DispatchTouchEventCallBack) feature).afterDispatchTouchEvent(ev);
            }
        }
        return dispatch;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean dispatch = true;
        for (AbsViewFeature<TTextView> feature : mFeatureList) {
            if (feature instanceof TouchEventCallBack) {
                dispatch = dispatch && ((TouchEventCallBack) feature).beforeOnTouchEvent(ev);
            }
        }
        if (!dispatch) return true;//如果dispatch为false,拦截事件传递
        dispatch = super.onTouchEvent(ev);
        for (AbsViewFeature<TTextView> feature : mFeatureList) {
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
        for (AbsViewFeature<TTextView> feature : mFeatureList) {
            if (feature instanceof OnFocusChangedCallBack) {
                dispatch = dispatch && ((OnFocusChangedCallBack) feature).beforeFocusChanged(focused, direction, previouslyFocusedRect);
            }
        }
        if (!dispatch) return;
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        for (AbsViewFeature<TTextView> feature : mFeatureList) {
            if (feature instanceof OnFocusChangedCallBack) {
                ((OnFocusChangedCallBack) feature).afterFocusChanged(focused, direction, previouslyFocusedRect);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        boolean dispatch = true;
        for (AbsViewFeature<TTextView> feature : mFeatureList) {
            if (feature instanceof OnWindowFocusChangedCallBack) {
                dispatch = dispatch && ((OnWindowFocusChangedCallBack) feature).beforeWindowFocusChanged(hasWindowFocus);
            }
        }
        if (!dispatch) return;
        super.onWindowFocusChanged(hasWindowFocus);
        for (AbsViewFeature<TTextView> feature : mFeatureList) {
            if (feature instanceof OnWindowFocusChangedCallBack) {
                ((OnWindowFocusChangedCallBack) feature).afterWindowFocusChanged(hasWindowFocus);
            }
        }
    }

    @Override
    public boolean isFocused() {
        int count = 0;
        if (mFeatureList != null && mFeatureList.size() > 0) {
            for (AbsViewFeature<TTextView> feature : mFeatureList) {
                if (feature instanceof IsFocusedCallBack) {
                    count += ((IsFocusedCallBack) feature).isFocused();
                }
            }
        }
        if (count == 0) return super.isFocused();
        return count > 0;
    }
}
