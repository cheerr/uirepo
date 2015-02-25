package com.mbui.sdk.feature.widget.features;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.mbui.sdk.feature.abs.AbsFeature;
import com.mbui.sdk.feature.abs.AbsViewFeature;
import com.mbui.sdk.feature.callback.AddFeatureCallBack;
import com.mbui.sdk.feature.callback.IsFocusedCallBack;
import com.mbui.sdk.feature.callback.OnFocusChangedCallBack;
import com.mbui.sdk.feature.callback.OnWindowFocusChangedCallBack;
import com.mbui.sdk.widget.TTextView;

/**
 * Created by chenwei on 15/2/10.
 */
public class TextViewMarquee extends AbsViewFeature<TTextView> implements AddFeatureCallBack, OnFocusChangedCallBack, OnWindowFocusChangedCallBack, IsFocusedCallBack {

    private boolean marquee = true;

    public TextViewMarquee(Context context) {
        super(context);
    }

    @Override
    public void constructor(Context context, AttributeSet attrs, int defStyle) {

    }

    public void setMarquee(boolean marquee) {
        this.marquee = marquee;
    }

    @Override
    public void beforeAddFeature(AbsFeature feature) {

    }

    @Override
    public void afterAddFeature(AbsFeature feature) {
        setMarquee(getHost());
    }

    public void setMarquee(final TextView view) {
        if (marquee) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    view.setSingleLine();
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.width = view.getMeasuredWidth();
                    view.setLayoutParams(params);
                    view.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    view.setFocusableInTouchMode(true);
                    view.setFocusable(true);
                    view.setMarqueeRepeatLimit(100);
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
            view.invalidate();
        } else {
            view.setEllipsize(TextUtils.TruncateAt.END);
            view.setMarqueeRepeatLimit(0);
            view.invalidate();
        }
    }

    @Override
    public boolean beforeFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        return (!marquee || focused);
    }

    @Override
    public void afterFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {

    }

    @Override
    public boolean beforeWindowFocusChanged(boolean hasWindowFocus) {
        return (!marquee || hasWindowFocus);
    }

    @Override
    public void afterWindowFocusChanged(boolean hasWindowFocus) {

    }

    @Override
    public int isFocused() {
        return marquee ? 1 : 0;
    }
}
