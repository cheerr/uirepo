package com.mbui.sample.featureviewpager;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.mbui.sample.R;
import com.mbui.sdk.absviews.FeatureViewPager;
import com.mbui.sdk.feature.viewpager.features.PageAutoPlayFeature;

/**
 * Created by chenwei on 15/1/21.
 */
public class PageAutoPlayActivity  extends ActionBarActivity {

    private FeatureViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_viewpager);
        viewPager = (FeatureViewPager) findViewById(R.id.viewpager);
        viewPager.addFeature(new PageAutoPlayFeature(this));
        viewPager.setAdapter(new SimplePagerAdapter(this));
    }

}
