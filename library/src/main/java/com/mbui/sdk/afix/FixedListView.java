package com.mbui.sdk.afix;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.mbui.sdk.feature.pullrefresh.builders.HeaderFooterBuilder;
import com.mbui.sdk.reforms.Debug;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenwei on 15/1/15.
 * 作用：
 * 1、修复低版本兼容性bug
 * 2、继承接口HeaderFooterBuilder，提高扩展性
 */
public class FixedListView extends ListView implements HeaderFooterBuilder {

    private final String debug = "FixedListView";
    private List<View> headerList, footerList;

    public FixedListView(Context context) {
        this(context, null);
    }

    public FixedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSelf();
    }

    public FixedListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSelf();
    }

    private void initSelf() {
        headerList = new ArrayList<>();
        footerList = new ArrayList<>();
    }

    @Override
    public void addHeaderView(View view) {
        if (!headerList.contains(view)) {
            super.addHeaderView(view);
            headerList.add(view);
        } else {
            Debug.print(debug, "headerView 已存在！！！不能重复添加");
        }
    }

    @Override
    public void addFooterView(View view) {
        if (!footerList.contains(view)) {
            super.addFooterView(view);
            footerList.add(view);
        } else {
            Debug.print(debug, "footerView 已存在！！！不能重复添加");
        }
    }

    @Override
    public boolean removeHeaderView(View view) {
        if (headerList.contains(view)) {
            headerList.remove(view);
            //当API<11时如果getAdapter==null的时候removeHeaderView会报空指针错误
            if (getAdapter() != null || Build.VERSION.SDK_INT > 10) {
                return super.removeHeaderView(view);
            } else {
                return removeFixedView(view, "mHeaderViewInfos");
            }
        } else {
            Debug.print(debug, "删除失败，headerView 不存在！");
            return false;
        }
    }

    @Override
    public boolean removeFooterView(View view) {
        if (footerList.contains(view)) {
            footerList.remove(view);
            //当API<11时如果getAdapter==null的时候removeFooterView会报空指针错误
            if (getAdapter() != null || Build.VERSION.SDK_INT > 10) {
                return super.removeFooterView(view);
            } else {
                return removeFixedView(view, "mFooterViewInfos");
            }
        } else {
            Debug.print(debug, "删除失败，footerView 不存在！");
            return false;
        }
    }


    @Override
    public View getFirstHeader() {
        return headerList.size() == 0 ? null : headerList.get(0);
    }

    @Override
    public View getLastFooter() {
        return footerList.size() == 0 ? null : footerList.get(footerList.size() - 1);
    }

    //修复当API<11时当adapter为null,removeHeaderView和removeFooterView的空指针Bug
    private boolean removeFixedView(View view, String fieldName) {
        try {
            Field field = ListView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            ArrayList<FixedViewInfo> where = (ArrayList<FixedViewInfo>) field.get(this);
            if (where == null || where.size() < 1) return false;
            for (int i = 0, len = where.size(); i < len; ++i) {
                FixedViewInfo info = where.get(i);
                if (info.view == view) {
                    where.remove(i);
                    break;
                }
            }
            field.set(this, where);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean arrivedTop() {
        return getFirstVisiblePosition() <= 0;
    }

    @Override
    public boolean arrivedBottom() {
        return getLastVisiblePosition() == getCount() - 1 && !arrivedTop();
    }
}
