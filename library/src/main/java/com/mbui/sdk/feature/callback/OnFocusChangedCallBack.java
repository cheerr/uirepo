package com.mbui.sdk.feature.callback;

import android.graphics.Rect;

/**
 * Created by chenwei on 15/2/10.
 */
public interface OnFocusChangedCallBack {
    public boolean beforeFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect);

    public void afterFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect);
}
