package com.mbui.sample.featureviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mbui.sample.R;

/**
 * Created by chenwei on 15/1/20.
 */
public class SimplePagerAdapter extends PagerAdapter {

    private Context context;

    private int[] res = new int[]{
            R.drawable.a_item_image, R.drawable.image_bg, R.drawable.ic_launcher
    };

    public SimplePagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 100;
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_view, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        imageView.setImageResource(res[position % res.length]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object obj) {
        if (obj != null && obj instanceof View) {
            container.removeView((View) obj);
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }
}
