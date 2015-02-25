package com.mbui.sdk.feature.pullrefresh.builders;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.mbui.sdk.feature.abs.AbsFeature;
import com.mbui.sdk.feature.callback.AddFeatureCallBack;
import com.mbui.sdk.feature.callback.ComputeScrollCallBack;
import com.mbui.sdk.feature.callback.ScrollCallBack;
import com.mbui.sdk.feature.callback.TouchEventCallBack;
import com.mbui.sdk.feature.pullrefresh.RefreshController;
import com.mbui.sdk.feature.pullrefresh.callback.SetListAdapterCallBack;
import com.mbui.sdk.feature.view.features.ViewTouchFeature;

/**
 * Created by chenwei on 15/1/15.
 * <p/>
 * 简单封装了一下内含RefreshController的FixedListView的Feature，统一控制RefreshController必须实现的几个方法
 */
public abstract class RefreshFeatureBuilder<T extends HeaderFooterBuilder> extends ViewTouchFeature<T> implements ComputeScrollCallBack,
        TouchEventCallBack, ScrollCallBack, AddFeatureCallBack, SetListAdapterCallBack {

    private String debug = "RefreshFeatureBuilder";
    private RefreshController mRefreshController;
    private TYPE type = TYPE.UNKNOWN;

    private enum TYPE {
        LISTVIEW, SCROLLVIEW, UNKNOWN;
    }

    public RefreshFeatureBuilder(Context context) {
        super(context);
        mRefreshController = new RefreshController(this, new Scroller(context));
        onCreateRefreshController(mRefreshController);
    }

    public RefreshController getRefreshController() {
        return mRefreshController;
    }

    @Override
    public void constructor(Context context, AttributeSet attrs, int defStyle) {

    }

    @Override
    public void setHost(T host) {
        super.setHost(host);
        if (host instanceof ListView) {
            type = TYPE.LISTVIEW;
            // 将控制的View变量传入RefreshController，
            mRefreshController.setControllerView((View) host);
        } else if (host instanceof ScrollView) {
            type = TYPE.SCROLLVIEW;
            mRefreshController.setControllerView((View) host);
        } else {
            type = TYPE.UNKNOWN;
        }
    }

    protected abstract void onCreateRefreshController(RefreshController refreshController);

    @Override
    public void beforeComputeScroll() {
        mRefreshController.beforeComputeScroll();
    }

    @Override
    public void afterComputeScroll() {
    }


    @Override
    public boolean beforeDispatchTouchEvent(MotionEvent event) {
        return super.beforeDispatchTouchEvent(event) && mRefreshController.beforeDispatchTouchEvent(event);
    }

    @Override
    public void afterDispatchTouchEvent(MotionEvent event) {

    }

    @Override
    public boolean beforeOnTouchEvent(MotionEvent event) {
        return mRefreshController.beforeOnTouchEvent(event);
    }

    @Override
    public void afterOnTouchEvent(MotionEvent event) {

    }


    @Override
    public void onScrollStateChanged(View view, boolean isScrolling) {
        mRefreshController.onScrollStateChanged(view, isScrolling);
    }

    @Override
    public void onScroll(View view, int scrollX, int scrollY) {

    }

    @Override
    public void beforeAddFeature(AbsFeature feature) {

    }

    @Override
    public void afterAddFeature(AbsFeature feature) {
        if (type == TYPE.SCROLLVIEW)
            mRefreshController.loadController();
    }

    @Override
    public void beforeSetAdapter(ListAdapter adapter) {
        if (type == TYPE.LISTVIEW)
            mRefreshController.loadController();
    }

    @Override
    public void afterSetAdapter(ListAdapter adapter) {

    }
}
