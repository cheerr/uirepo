package com.mbui.sdk.feature.view.features;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.mbui.sdk.feature.abs.AbsViewFeature;
import com.mbui.sdk.feature.callback.DispatchTouchEventCallBack;

/**
 * Created by chenwei on 15/1/22.
 */
public class ViewTouchFeature<T> extends AbsViewFeature<T> implements DispatchTouchEventCallBack {

    private boolean isOnTouching;

    public ViewTouchFeature(Context context) {
        super(context);
    }

    public boolean isOnTouching() {
        return isOnTouching;
    }

    @Override
    public void constructor(Context context, AttributeSet attrs, int defStyle) {

    }

    @Override
    public boolean beforeDispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
                isOnTouching = false;
                break;
            default:
                isOnTouching = true;
                break;
        }
        return true;
    }

    @Override
    public void afterDispatchTouchEvent(MotionEvent event) {

    }
}
