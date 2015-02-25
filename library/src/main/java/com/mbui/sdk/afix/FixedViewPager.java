package com.mbui.sdk.afix;

import android.content.Context;
import android.database.DataSetObservable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.mbui.sdk.feature.viewpager.callback.PageItemListener;

import java.lang.reflect.Field;

/**
 * Created by chenwei on 15/1/20.
 */
public class FixedViewPager extends ViewPager implements PageItemListener {
    private InnerAdapter innerAdapter;
    private OnPageUpdateListener pageListener;
    private float scrollFactor = 1.0f;
    private Scroller scroller;


    public FixedViewPager(Context context) {
        this(context, null);
    }

    public FixedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        fillInScroller();
    }

    public void setAdapter(PagerAdapter adapter) {
        fillInDataSetObservable(adapter);
        innerAdapter = new InnerAdapter(adapter);
        super.setAdapter(innerAdapter);
    }

    public Scroller getScroller() {
        return scroller;
    }

    @Override
    public void afterInstantiateItem(ViewGroup container, View item, int position) {

    }

    @Override
    public void beforeDestroyItem(ViewGroup container, View item, int position) {

    }

    private class InnerAdapter extends PagerAdapter {

        public PagerAdapter mAdapter;

        public InnerAdapter(PagerAdapter adapter) {
            this.mAdapter = adapter;
        }

        @Override
        public int getCount() {
            return mAdapter == null ? 0 : mAdapter.getCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            if (mAdapter != null)
                return mAdapter.isViewFromObject(view, object);
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Object item = null;
            if (mAdapter != null)
                item = mAdapter.instantiateItem(container, position);
            afterInstantiateItem(container, (View) item, position);
            return item;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object item) {
            beforeDestroyItem(container, (View) item, position);
            if (mAdapter != null)
                mAdapter.destroyItem(container, position, item);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        if (positionOffset == 0 && pageListener != null) {
            pageListener.onUpdate(position);
        }
    }


    public static interface OnPageUpdateListener {
        public void onUpdate(int i);
    }

    public void setOnPageUpdateListener(OnPageUpdateListener pageListener) {
        this.pageListener = pageListener;
    }

    public void setScrollDurationFactor(float scrollFactor) {
        this.scrollFactor = scrollFactor;
    }

    private boolean fillInDataSetObservable(PagerAdapter adapter) {
        try {
            Field field = PagerAdapter.class.getDeclaredField("mObservable");
            field.setAccessible(true);
            field.set(adapter, new DataSetObservable() {
                public void notifyChanged() {
                    if (innerAdapter != null)
                        innerAdapter.notifyDataSetChanged();
                    super.notifyChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void fillInScroller() {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            scrollerField.set(this, scroller = new Scroller(getContext(), sInterpolator) {
                @Override
                public void startScroll(int startX, int startY, int dx, int dy, int duration) {
                    super.startScroll(startX, startY, dx, dy, (int) (duration * scrollFactor));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };
}
