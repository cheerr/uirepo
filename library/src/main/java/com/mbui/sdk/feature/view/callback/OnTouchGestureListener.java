package com.mbui.sdk.feature.view.callback;

/**
 * Created by chenwei on 14/12/1.
 */
public interface OnTouchGestureListener {
    public boolean intercept();

    public boolean onDown(float downX, float downY, long downTime);

    public boolean onTouch(float dX, float dY, long dTime);

    public boolean onUp(float upX, float upY, long upTime);

    public void onCancel(float upX, float upY, long upTime);
}
