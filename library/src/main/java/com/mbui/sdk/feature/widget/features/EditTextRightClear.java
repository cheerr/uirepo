package com.mbui.sdk.feature.widget.features;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.mbui.sdk.feature.abs.AbsFeature;
import com.mbui.sdk.feature.abs.AbsViewFeature;
import com.mbui.sdk.feature.callback.AddFeatureCallBack;
import com.mbui.sdk.feature.callback.TouchEventCallBack;
import com.mbui.sdk.widget.TEditText;

/**
 * Created by chenwei on 15/2/10.
 */
public class EditTextRightClear extends AbsViewFeature<TEditText> implements TouchEventCallBack, AddFeatureCallBack {

    private Drawable clearDrawable;

    public EditTextRightClear(Context context) {
        super(context);
    }

    @Override
    public void constructor(Context context, AttributeSet attrs, int defStyle) {

    }

    @Override
    public void beforeAddFeature(AbsFeature feature) {

    }

    @Override
    public void afterAddFeature(AbsFeature feature) {
        clearDrawable = getHost().getCompoundDrawables()[2];
        getHost().addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (clearDrawable != null) {
                    if (editable.toString().length() == 0) {
                        getHost().setCompoundDrawables(getHost().getCompoundDrawables()[0], getHost().getCompoundDrawables()[1], null, getHost().getCompoundDrawables()[3]);
                    } else {
                        getHost().setCompoundDrawables(getHost().getCompoundDrawables()[0], getHost().getCompoundDrawables()[1], clearDrawable, getHost().getCompoundDrawables()[3]);
                    }
                }
            }
        });
    }

    @Override
    public boolean beforeOnTouchEvent(MotionEvent event) {
        if ((clearDrawable != null) && getHost() != null && (event.getAction() == MotionEvent.ACTION_DOWN)) {
            Rect rBounds = clearDrawable.getBounds();
            int dis = (int) event.getRawX();// 距离屏幕的距离
            if (dis > getHost().getRight() - 3 * rBounds.width()) {
                getHost().setText("");
                event.setAction(MotionEvent.ACTION_CANCEL);
            }
        }
        return true;
    }

    @Override
    public void afterOnTouchEvent(MotionEvent event) {

    }
}
