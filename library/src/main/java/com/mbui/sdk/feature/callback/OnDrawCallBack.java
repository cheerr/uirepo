package com.mbui.sdk.feature.callback;

import android.graphics.Canvas;

/**
 * Created by chenwei on 15/1/20.
 */
public interface OnDrawCallBack {
    /**
     * @param canvas
     * @return false表示不执行super.onDraw, true表示执行super.onDraw
     */
    public boolean beforeOnDraw(Canvas canvas);

    public void afterOnDraw(Canvas canvas);
}
