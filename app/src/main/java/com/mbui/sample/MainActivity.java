package com.mbui.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.mbui.sample.featureimageview.SVGImageViewActivity;
import com.mbui.sample.featurelistview.RefreshListViewActivity;
import com.mbui.sample.featurelistview.SecPullListViewActivity;
import com.mbui.sample.featurelistview.TipListViewActivity;
import com.mbui.sample.featurescrollview.RefreshScrollViewActivity;
import com.mbui.sample.featurescrollview.TipScrollViewActivity;
import com.mbui.sample.featureviewpager.PageAutoPlayActivity;
import com.mbui.sample.featureviewpager.PageControllerActivity;
import com.mbui.sample.featureviewpager.PageTransActivity;
import com.mbui.sample.featurewidget.SimpleWidget;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onFeatureListView(View view) {
        Intent intent = new Intent(this, FeatureListActivity.class);
        ArrayList<String> items = new ArrayList<>();
        items.add(RefreshListViewActivity.class.getName());
        items.add(TipListViewActivity.class.getName());
        items.add(SecPullListViewActivity.class.getName());
        intent.putStringArrayListExtra("classes", items);
        startActivity(intent);
    }

    public void onFeatureScrollView(View view) {
        Intent intent = new Intent(this, FeatureListActivity.class);
        ArrayList<String> items = new ArrayList<>();
        items.add(RefreshScrollViewActivity.class.getName());
        items.add(TipScrollViewActivity.class.getName());
        intent.putStringArrayListExtra("classes", items);
        startActivity(intent);
    }

    public void onFeatureViewPager(View view) {
        Intent intent = new Intent(this, FeatureListActivity.class);
        ArrayList<String> items = new ArrayList<>();
        items.add(PageTransActivity.class.getName());
        items.add(PageControllerActivity.class.getName());
        items.add(PageAutoPlayActivity.class.getName());
        intent.putStringArrayListExtra("classes", items);
        startActivity(intent);
    }

    public void onFeatureImageView(View view) {
        Intent intent = new Intent(this, FeatureListActivity.class);
        ArrayList<String> items = new ArrayList<>();
        items.add(SVGImageViewActivity.class.getName());
        intent.putStringArrayListExtra("classes", items);
        startActivity(intent);
    }

    public void onWidget(View view) {
        Intent intent = new Intent(this, FeatureListActivity.class);
        ArrayList<String> items = new ArrayList<>();
        items.add(SimpleWidget.class.getName());
        intent.putStringArrayListExtra("classes", items);
        startActivity(intent);
    }
}
