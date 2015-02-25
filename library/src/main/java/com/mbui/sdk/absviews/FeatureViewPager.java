package com.mbui.sdk.absviews;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by chenwei on 15/1/20.
 */
public class FeatureViewPager extends AbsFeatureViewPager {

    private String debug = "FeatureViewPager";

    public FeatureViewPager(Context context) {
        this(context, null);
    }

    public FeatureViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
