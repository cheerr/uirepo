package com.mbui.sdk.feature.pullrefresh.callback;

import android.widget.ListAdapter;

/**
 * Created by chenwei on 15/1/14.
 */
public interface SetListAdapterCallBack {
    public void beforeSetAdapter(ListAdapter adapter);

    public void afterSetAdapter(ListAdapter adapter);
}
