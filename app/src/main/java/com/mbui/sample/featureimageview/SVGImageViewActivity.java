package com.mbui.sample.featureimageview;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.mbui.sample.R;
import com.mbui.sdk.kits.pathview.PathView;

/**
 * Created by chenwei on 15/1/28.
 */
public class SVGImageViewActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_imageview);

        final PathView imageView = (PathView) findViewById(R.id.image);
        imageView.setSvgResource(R.raw.map_ca);
        //imageView.useNaturalColors();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPathView(imageView);
            }
        });
        displayPathView(imageView);
    }

    private void displayPathView(final PathView pathView) {
        pathView.getPathAnimator().
                delay(400).
                duration(4000).
                interpolator(new AccelerateDecelerateInterpolator()).
                start();
    }
}