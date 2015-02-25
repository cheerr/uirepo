package com.mbui.sample.featurewidget;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.mbui.sample.R;
import com.mbui.sdk.absviews.FeatureEditText;
import com.mbui.sdk.absviews.FeatureTextView;

/**
 * Created by chenwei on 15/2/10.
 */
public class SimpleWidget extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);
        ((FeatureTextView) findViewById(R.id.paoma)).showMarquee();
        ((FeatureEditText) findViewById(R.id.clearbtn)).showClearIcon();
    }
}
