package com.mbui.sdk.absviews;

import android.content.Context;
import android.util.AttributeSet;

import com.mbui.sdk.feature.widget.features.EditTextRightClear;

/**
 * Created by chenwei on 15/2/10.
 */
public class FeatureEditText extends AbsEditText {
    public FeatureEditText(Context context) {
        super(context);
    }

    public FeatureEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void showClearIcon() {
        addFeature(new EditTextRightClear(getContext()));
    }
}
