package com.mbui.sdk.absviews;

import android.content.Context;
import android.util.AttributeSet;

import com.mbui.sdk.feature.widget.features.TextViewMarquee;

/**
 * Created by chenwei on 15/2/10.
 */
public class FeatureTextView extends AbsTextView {

    public FeatureTextView(Context context) {
        super(context);
    }

    public FeatureTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void showMarquee() {
        addFeature(new TextViewMarquee(getContext()));
    }
}
