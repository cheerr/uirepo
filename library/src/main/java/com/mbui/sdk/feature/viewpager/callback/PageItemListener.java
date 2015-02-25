package com.mbui.sdk.feature.viewpager.callback;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by chenwei on 15/1/20.
 */
public interface PageItemListener {
    public void afterInstantiateItem(ViewGroup container, View item, final int position);

    public void beforeDestroyItem(ViewGroup container, View item, final int position);
}
