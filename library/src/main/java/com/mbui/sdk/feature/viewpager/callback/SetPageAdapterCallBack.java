package com.mbui.sdk.feature.viewpager.callback;

import android.support.v4.view.PagerAdapter;

/**
 * Created by chenwei on 15/1/20.
 */
public interface SetPageAdapterCallBack {
    public void beforeSetAdapter(PagerAdapter adapter);

    public void afterSetAdapter(PagerAdapter adapter);
}
