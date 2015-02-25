package com.mbui.sdk.feature.callback;

/**
 * Created by chenwei on 15/2/10.
 */
public interface OnWindowFocusChangedCallBack {
    public boolean beforeWindowFocusChanged(boolean hasWindowFocus);

    public void afterWindowFocusChanged(boolean hasWindowFocus);
}
