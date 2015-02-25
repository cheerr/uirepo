package com.mbui.sdk.feature.callback;

import android.view.MotionEvent;

/**
 * Created by chenwei on 15/1/14.
 */
public interface DispatchTouchEventCallBack {
    /**
     * @param event
     * @return false 表示拦截dispatchTouchEvent;
     */
    public boolean beforeDispatchTouchEvent(MotionEvent event);

    public void afterDispatchTouchEvent(MotionEvent event);
}
