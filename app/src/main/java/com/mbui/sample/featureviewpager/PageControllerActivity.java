package com.mbui.sample.featureviewpager;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.mbui.sample.R;
import com.mbui.sdk.absviews.FeatureViewPager;
import com.mbui.sdk.feature.viewpager.features.PageControllerFeature;

/**
 * Created by chenwei on 15/1/21.
 */
public class PageControllerActivity extends ActionBarActivity {

    private FeatureViewPager viewPager;
    private PageControllerFeature feature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller_viewpager);
        viewPager = (FeatureViewPager) findViewById(R.id.viewpager);
        feature = new PageControllerFeature(this);
        viewPager.setScrollDurationFactor(4f);
        viewPager.addFeature(feature);
        viewPager.setAdapter(new SimplePagerAdapter(this));
    }

    public void onLast(View view) {
        feature.moveLast();
    }

    public void onNext(View view) {
        feature.moveNext();
    }
}
