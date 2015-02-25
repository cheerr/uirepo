package com.mbui.sdk.feature.viewpager.callback;

/**
 * Created by chenwei on 15/1/20.
 */
public interface PageScrolledCallBack {
    public void beforePageScrolled(int position, float positionOffset, int positionOffsetPixels);

    public void afterPageScrolled(int position, float positionOffset, int positionOffsetPixels);
}
