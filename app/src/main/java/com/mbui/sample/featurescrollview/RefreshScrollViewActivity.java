package com.mbui.sample.featurescrollview;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mbui.sample.R;
import com.mbui.sdk.absviews.FeatureScrollView;
import com.mbui.sdk.afix.FixedScrollView;
import com.mbui.sdk.feature.pullrefresh.callback.OnLoadCallBack;
import com.mbui.sdk.feature.pullrefresh.features.common.PullToRefreshFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenwei on 15/1/12.
 */
public class RefreshScrollViewActivity extends ActionBarActivity {

    private FeatureScrollView mScrollView;
    private PullToRefreshFeature<FixedScrollView> mFeature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scrollview);
        mScrollView = (FeatureScrollView) findViewById(R.id.scrollview);
        mFeature = new PullToRefreshFeature<>(this);
        mScrollView.initViewItems(this);
        //使用下拉到阈值后刷新的方法时需要setUpPullToRefreshEnable(true),默认是true
        //controller.setUpPullToRefreshEnable(true);
        //使用上拉到阈值后刷新的方法时需要setDownPullToRefreshEnable(true),默认是false
        //controller.setDownPullToRefreshEnable(false);

        mFeature.getRefreshController().setLoadCallBack(new OnLoadCallBack() {

            private int num = 1;

            @Override
            public void loadMore() {
                Toast.makeText(RefreshScrollViewActivity.this, "loadMore", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mScrollView.getFooterViewsCount() < 4) {
                            mScrollView.addFooterView(getItem("New Item" + num++));
                            mFeature.getRefreshController().loseDownRefreshLock();
                        } else {
                            mFeature.setFooterMode(PullToRefreshFeature.FooterMode.SHOW_NO_MORE);
                        }
                    }
                }, 1000);
            }

            @Override
            public void loadAll() {
                Toast.makeText(RefreshScrollViewActivity.this, "loadAll", Toast.LENGTH_SHORT).show();
            }
        });

        mScrollView.addFeature(mFeature);
        mScrollView.addHeaderView(getItem("header 1"));
        mScrollView.addFooterView(getItem("footer 1"));
        mScrollView.findViewById(R.id.item1).setBackgroundColor(Color.BLUE);
    }

    @FeatureScrollView.ViewItem
    public View addView1() {
        View view = LayoutInflater.from(this).inflate(R.layout.test_list_item, null);
        TextView tv = (TextView) view.findViewById(R.id.text);
        tv.setText("VVVVVV1");
        return view;
    }


    @FeatureScrollView.ViewItem
    public List<View> addView2() {
        List<View> views = new ArrayList<>();
        View view = LayoutInflater.from(this).inflate(R.layout.test_list_item, null);
        TextView tv = (TextView) view.findViewById(R.id.text);
        tv.setText("VVVVVV2");
        views.add(view);
        return views;
    }

    public View getItem(String text) {
        View view = LayoutInflater.from(this).inflate(R.layout.test_list_item, null);
        TextView tv = (TextView) view.findViewById(R.id.text);
        tv.setText(text);
        return view;
    }
}
