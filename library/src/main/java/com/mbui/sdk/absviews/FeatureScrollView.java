package com.mbui.sdk.absviews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.mbui.sdk.R;
import com.mbui.sdk.reforms.Debug;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenwei on 15/1/17.
 */
public class FeatureScrollView extends AbsFeatureScrollView {

    private final String debug = "FeatureScrollView";
    private ArrayDeque<View> headerList, footerList;
    private List<View> annViews;


    @Retention(RetentionPolicy.RUNTIME)
    public @interface ViewItem {
    }

    public FeatureScrollView(Context context) {
        this(context, null);
    }

    public FeatureScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initSelf();
    }

    public FeatureScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initSelf();
    }

    private void initSelf() {
        headerList = new ArrayDeque<>();
        footerList = new ArrayDeque<>();
        annViews = new ArrayList<>();
    }


    /**
     * 自动识别刷新头，将其置于顶端
     *
     * @param v
     */
    @Override
    public void addHeaderView(@NonNull View v) {
        Debug.print(debug, "addHeaderView " + headerList.size());
        if (headerList.contains(v)) {
            Debug.print(debug, "headerView 已存在");
            return;
        }
        if (v.getId() == R.id.top_header_footer_container) {
            //只取一个Header的容器，重复添加则忽略
            if (headerList.size() > 0 && headerList.getFirst().getId() == R.id.top_header_footer_container) {
                return;
            }
            for (View view : headerList) {
                super.removeHeaderView(view);
            }
            headerList.addFirst(v);
            for (View view : headerList) {
                super.addHeaderView(view);
            }
        } else {
            headerList.addLast(v);
            super.addHeaderView(v);
        }
    }

    @Override
    public boolean removeHeaderView(View view) {
        if (headerList.contains(view)) {
            headerList.remove(view);
        }
        return super.removeHeaderView(view);
    }

    /**
     * 自动识别刷新尾，将其置于末尾
     *
     * @param v
     */
    @Override
    public void addFooterView(View v) {
        if (footerList.contains(v)) {
            Debug.print(debug, "footerView 已存在");
            return;
        }
        if (v.getId() == R.id.top_header_footer_container) {
            //只取一个Footer的容器，重复添加则忽略
            if (footerList.size() > 0 && footerList.getLast().getId() == R.id.top_header_footer_container) {
                return;
            }
        }
        if (footerList.size() > 0 && footerList.getLast().getId() == R.id.top_header_footer_container) {
            View temp = footerList.getLast();
            footerList.removeLast();
            super.removeFooterView(temp);
            footerList.addLast(v);
            super.addFooterView(v);
            footerList.addLast(temp);
            super.addFooterView(temp);
        } else {
            footerList.addLast(v);
            super.addFooterView(v);
        }
    }

    @Override
    public boolean removeFooterView(View view) {
        if (footerList.contains(view)) {
            footerList.remove(view);
        }
        return super.removeFooterView(view);
    }

    @Override
    public View getFirstHeader() {
        return headerList.isEmpty() ? null : headerList.getFirst();
    }

    @Override
    public View getLastFooter() {
        return footerList.isEmpty() ? null : footerList.getLast();
    }


    public void removeAnnViews() {
        for (View child : annViews) {
            super.removeContentView(child);
        }
        annViews.clear();
    }


    /**
     * 利用注解更加方便的添加View
     *
     * @param currentClass
     */
    public void initViewItems(Object currentClass) {
        Method[] methods = currentClass.getClass().getDeclaredMethods();
        if (methods != null && methods.length > 0) {
            for (View child : annViews) {
                super.removeContentView(child);
            }
            annViews.clear();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(ViewItem.class)) continue;
                try {
                    Object object = method.invoke(currentClass);
                    if (object instanceof View) {
                        super.addContentView((View) object);
                        annViews.add((View) object);
                    }
                    if (object instanceof View[]) {
                        for (Object v : (View[]) object) {
                            if (v instanceof View) {
                                super.addContentView((View) v);
                                annViews.add((View) v);
                            }
                        }
                    }
                    if (object instanceof List) {
                        for (Object v : (List) object) {
                            if (v instanceof View) {
                                super.addContentView((View) v);
                                annViews.add((View) v);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
