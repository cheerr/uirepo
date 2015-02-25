package com.mbui.sdk.feature.pullrefresh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.mbui.sdk.R;
import com.mbui.sdk.feature.abs.AbsViewFeature;
import com.mbui.sdk.feature.callback.ComputeScrollCallBack;
import com.mbui.sdk.feature.callback.DispatchTouchEventCallBack;
import com.mbui.sdk.feature.callback.ScrollCallBack;
import com.mbui.sdk.feature.callback.TouchEventCallBack;
import com.mbui.sdk.feature.pullrefresh.builders.HeaderFooterBuilder;
import com.mbui.sdk.feature.pullrefresh.builders.PullModeBuilder;
import com.mbui.sdk.feature.pullrefresh.callback.ControllerCallBack;
import com.mbui.sdk.feature.pullrefresh.callback.OnLoadCallBack;
import com.mbui.sdk.reforms.Debug;
import com.mbui.sdk.util.UIViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenwei on 15/1/14.
 * <p/>
 * 注意：
 * 支持一个刷新的View的不同刷新Feature共用同一个HeaderView和FooterView的容器
 * 多个分支上的HeaderView和FooterView以覆盖形式存在。
 * 但是不支持两个HeaderView和FooterView的PullMode不同，否则可能出现混乱
 */
public class RefreshController implements GestureDetector.OnGestureListener, TouchEventCallBack, DispatchTouchEventCallBack,
        ComputeScrollCallBack, PullModeBuilder, ControllerCallBack, ScrollCallBack {

    private final String debug = "RefreshController";

    private AbsViewFeature<? extends HeaderFooterBuilder> viewFeature;
    private Scroller mScroller;
    private Context context;
    private View controllerView;
    private View headerView, footerView;
    private List<View> innerHeaderList, innerFooterList;
    private int headerHeight, footerHeight;
    private boolean ITEM_FLAG_RETURN = false;
    private GestureDetector mGestureDetector;
    private ControllerCallBack controllerCallBack;
    private boolean viewFeatureControllerCallBackEnable = true;
    private boolean upPullToRefreshEnable = true, downPullToRefreshEnable = false;
    private PullMode upMode = PullMode.PULL_SMOOTH, downMode = PullMode.PULL_SMOOTH;
    private OnLoadCallBack loadCallBack;
    private float touchBuffer = 2.5f;

    public float upThreshold = 0.5f, downThreshold = 0.5f;
    public float upTouchBuffer = 2.5f, downTouchBuffer = 2.5f;

    public RefreshController(@NonNull AbsViewFeature<? extends HeaderFooterBuilder> viewFeature, @NonNull Scroller scroller) {
        this.context = viewFeature.getContext();
        this.viewFeature = viewFeature;
        this.mScroller = scroller;
        this.initSelf();
    }

    private void initSelf() {
        mGestureDetector = new GestureDetector(context, this);
        headerView = LayoutInflater.from(context).inflate(R.layout.ui_header_footer_container, null);
        footerView = LayoutInflater.from(context).inflate(R.layout.ui_header_footer_container, null);
        innerHeaderList = new ArrayList<>();
        innerFooterList = new ArrayList<>();
    }

    /**
     * 将RefreshController控制的View变量传入
     *
     * @param view
     */
    public void setControllerView(View view) {
        this.controllerView = view;
    }

    /**
     * 如果没有传进来，尝试判断viewFeature是否继承自View
     *
     * @return
     */
    public View getControllerView() {
        if (controllerView == null && viewFeature.getHost() instanceof View) {
            controllerView = (View) (viewFeature.getHost());
        }
        return controllerView;
    }

    public int getHeaderHeight() {
        return headerHeight;
    }

    public int getFooterHeight() {
        return footerHeight;
    }

    public View getHeaderView() {
        return headerView;
    }

    public View getFooterView() {
        return footerView;
    }

    public Scroller getScroller() {
        return mScroller;
    }

    /**
     * 监听controller返回的所有回调事件
     *
     * @param controllerCallBack
     */
    public void setControllerCallBack(ControllerCallBack controllerCallBack) {
        this.controllerCallBack = controllerCallBack;
    }

    /**
     * 当ViewFeature继承ControllerCallBack时会自动回调ControllerCallBack的方法
     * 此参数设置是否自动回调，默认为true
     *
     * @param enable
     */
    public void setViewFeatureControllerCallBackEnable(boolean enable) {
        viewFeatureControllerCallBackEnable = enable;
    }

    /**
     * 是否支持下拉到阈值后执行刷新操作
     * 默认为true
     *
     * @param enable
     */
    public void setUpPullToRefreshEnable(boolean enable) {
        this.upPullToRefreshEnable = enable;
    }

    /**
     * 是否支持上拉到阈值后执行刷新操作
     * 默认为false
     *
     * @param enable
     */
    public void setDownPullToRefreshEnable(boolean enable) {
        this.downPullToRefreshEnable = enable;
    }

    /**
     * 监听上拉刷新和加载更多松手后刷新的事件
     *
     * @param callBack
     */
    public void setLoadCallBack(OnLoadCallBack callBack) {
        this.loadCallBack = callBack;
    }

    @Override
    public boolean beforeDispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onStopScroll();
                ITEM_FLAG_RETURN = false;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                ITEM_FLAG_RETURN = false;
                onResetLayout();
                break;
        }
        return true;
    }

    @Override
    public void afterDispatchTouchEvent(MotionEvent ev) {

    }

    @Override
    public boolean beforeOnTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        if (ITEM_FLAG_RETURN && ev.getAction() != MotionEvent.ACTION_DOWN) {
            ev.setAction(MotionEvent.ACTION_POINTER_DOWN);
        }
        return true;
    }

    @Override
    public void afterOnTouchEvent(MotionEvent event) {

    }

    @Override
    public void beforeComputeScroll() {
        if (mScroller.computeScrollOffset() && viewFeature.getHost() != null) {
            if (viewFeature.getHost().arrivedTop()) {
                switch (upMode) {
                    case PULL_SMOOTH:
                        headerView.setPadding(0, mScroller.getCurrY(), 0, 0);
                        break;
                    case PULL_STATE:
                        onPull(getControllerView(), mScroller.getCurrY());
                        pullHeight = mScroller.getCurrY();
                        break;
                }
            }
            if (viewFeature.getHost().arrivedBottom()) {
                switch (downMode) {
                    case PULL_SMOOTH:
                        footerView.setPadding(0, 0, 0, mScroller.getCurrY());
                        break;
                }
            }
            if (getControllerView() != null)
                getControllerView().postInvalidate();
        }
    }

    @Override
    public void afterComputeScroll() {

    }

    private void toScrollByY(int fromY, int toY, int duration) {
        mScroller.startScroll(0, fromY, 0, toY - fromY, duration);
        if (getControllerView() != null)
            getControllerView().postInvalidate();
    }

    private void toScrollByY(int fromY, int toY) {
        if (fromY == toY) return;
        toScrollByY(fromY, toY, 300 + 2 * Math.abs(fromY - toY));
    }

    @Override
    public void onStopScroll() {
        if (mScroller.computeScrollOffset()) {
            mScroller.setFinalY(mScroller.getCurrY());
            mScroller.forceFinished(true);
        }
        if (controllerCallBack != null)
            controllerCallBack.onStopScroll();
        if (viewFeatureControllerCallBackEnable && controllerCallBack != viewFeature
                && viewFeature instanceof ControllerCallBack)
            ((ControllerCallBack) viewFeature).onStopScroll();
    }

    @Override
    public void onResetLayout() {
        if (viewFeature.getHost() == null) return;
        if (viewFeature.getHost().arrivedTop() || pullHeight > 0) {
            switch (upMode) {
                case PULL_SMOOTH:
                    if (!upPullToRefreshEnable || headerView.getPaddingTop() <= upThreshold * headerHeight) {
                        onUpBack();
                    } else if (headerView.getPaddingTop() > upThreshold * headerHeight) {
                        onUpRefresh();
                    }
                    toScrollByY(headerView.getPaddingTop(), -headerHeight);
                    break;
                case PULL_STATE:
                    toScrollByY((int) pullHeight, 0);
                    break;
            }
        }
        if (viewFeature.getHost().arrivedBottom()) {
            switch (downMode) {
                case PULL_AUTO:
                    toScrollByY(footerView.getPaddingBottom(), 0);
                    break;
                case PULL_SMOOTH:
                    if (!downPullToRefreshEnable || footerView.getPaddingBottom() <= downThreshold * footerHeight) {
                        onDownBack();
                    } else if (footerView.getPaddingBottom() > downThreshold * footerHeight) {
                        onDownRefresh();
                    }
                    toScrollByY(footerView.getPaddingBottom(), -footerHeight);
                    break;
                default:
                    break;
            }
        }
        if (controllerCallBack != null)
            controllerCallBack.onResetLayout();
        if (viewFeatureControllerCallBackEnable && controllerCallBack != viewFeature
                && viewFeature instanceof ControllerCallBack) {
            ((ControllerCallBack) viewFeature).onResetLayout();
        }
    }

    public void scrollTo(int toY) {
        toScrollByY(mScroller.getCurrY(), toY);
    }

    @Override
    public void onUpRefresh() {
        if (controllerCallBack != null)
            controllerCallBack.onUpRefresh();
        if (viewFeatureControllerCallBackEnable && controllerCallBack != viewFeature
                && viewFeature instanceof ControllerCallBack) {
            ((ControllerCallBack) viewFeature).onUpRefresh();
        }
        if (loadCallBack != null) {
            loadCallBack.loadAll();
        }
    }

    @Override
    public void onDownRefresh() {
        if (controllerCallBack != null)
            controllerCallBack.onDownRefresh();
        if (viewFeatureControllerCallBackEnable && controllerCallBack != viewFeature
                && viewFeature instanceof ControllerCallBack) {
            ((ControllerCallBack) viewFeature).onDownRefresh();
        }
        if (loadCallBack != null) {
            loadCallBack.loadMore();
        }
    }

    @Override
    public void onUpBack() {
        if (controllerCallBack != null)
            controllerCallBack.onUpBack();
        if (viewFeatureControllerCallBackEnable && controllerCallBack != viewFeature
                && viewFeature instanceof ControllerCallBack) {
            ((ControllerCallBack) viewFeature).onUpBack();
        }
    }

    @Override
    public void onDownBack() {
        if (controllerCallBack != null)
            controllerCallBack.onDownBack();
        if (viewFeatureControllerCallBackEnable && controllerCallBack != viewFeature
                && viewFeature instanceof ControllerCallBack) {
            ((ControllerCallBack) viewFeature).onDownBack();
        }
    }

    @Override
    public void onUpMove(View view, int disY, float percent) {
        if (controllerCallBack != null)
            controllerCallBack.onUpMove(view, disY, percent);
        if (viewFeatureControllerCallBackEnable && controllerCallBack != viewFeature
                && viewFeature instanceof ControllerCallBack) {
            ((ControllerCallBack) viewFeature).onUpMove(view, disY, percent);
        }
    }

    @Override
    public void onDownMove(View view, int disY, float percent) {
        if (controllerCallBack != null)
            controllerCallBack.onDownMove(view, disY, percent);
        if (viewFeatureControllerCallBackEnable && controllerCallBack != viewFeature
                && viewFeature instanceof ControllerCallBack) {
            ((ControllerCallBack) viewFeature).onDownMove(view, disY, percent);
        }
    }

    @Override
    public void onPull(View view, int disY) {
        if (controllerCallBack != null)
            controllerCallBack.onPull(view, disY);
        if (viewFeatureControllerCallBackEnable && controllerCallBack != viewFeature
                && viewFeature instanceof ControllerCallBack) {
            ((ControllerCallBack) viewFeature).onPull(view, disY);
        }
    }

    public void addInnerHeader(@NonNull View view) {
        innerHeaderList.add(view);
        ((FrameLayout) headerView.findViewById(R.id.frame_container)).addView(view);
    }

    public List<View> getInnerHeaderList() {
        return innerHeaderList;
    }

    public void addInnerFooter(@NonNull View view) {
        innerFooterList.add(view);
        ((FrameLayout) footerView.findViewById(R.id.frame_container)).addView(view);
    }

    public List<View> getInnerFooterList() {
        return innerFooterList;
    }

    @Override
    public void removeInnerHeader(View view) {
        if (innerHeaderList.contains(view)) {
            ((FrameLayout) headerView.findViewById(R.id.frame_container)).removeView(view);
            this.innerHeaderList.remove(view);
        }
    }

    @Override
    public void removeInnerFooter(View view) {
        if (innerFooterList.contains(view)) {
            ((FrameLayout) footerView.findViewById(R.id.frame_container)).removeView(view);
            this.innerFooterList.remove(view);
        }
    }

    @Override
    public void setUpMode(PullMode mode) {
        headerView.setPadding(0, 0, 0, 0);
        UIViewUtil.measureView(headerView);
        headerHeight = headerView.getMeasuredHeight();
        switch (mode) {
            case PULL_SMOOTH:
                headerView.setPadding(0, -headerHeight, 0, 0);
                break;
            case PULL_STATE:
                headerView.setPadding(0, -headerHeight, 0, 0);
                break;
            default:
                Debug.print(debug, "UpMode 暂不支持" + mode);
                return;
        }
        upMode = mode;
    }

    @Override
    public PullMode getUpMode() {
        return upMode;
    }

    @Override
    public void setDownMode(PullMode mode) {
        UIViewUtil.measureView(footerView);
        footerHeight = footerView.getMeasuredHeight();
        switch (mode) {
            case PULL_SMOOTH:
                footerView.setPadding(0, 0, 0, -footerHeight);
                break;
            case PULL_AUTO:
                footerView.setPadding(0, 0, 0, 0);
                break;
            default:
                Debug.print(debug, "DownMode 暂不支持" + mode);
                return;
        }
        downMode = mode;
    }

    @Override
    public PullMode getDownMode() {
        return downMode;
    }

    /**
     * 方法必须在setHost之后调用
     * <p/>
     * 将header和footer注入Controller
     * <p/>
     * 由于ListView 的setAdapter必须在setHeaderView/setFooterView之后调用的关系
     * 如果是ListView 这个函数需要在setAdapter之前调用
     */
    public void loadController() {
        if (viewFeature.getHost() == null) {
            throw new RuntimeException("loadController 方法必须在setHost 之后调用");
        }
        if (viewFeature.getHost() instanceof AdapterView &&
                ((AdapterView) viewFeature.getHost()).getAdapter() != null) {
            throw new RuntimeException("loadController 方法必须在setAdapter 之前调用");
        }
        View firstHeader = viewFeature.getHost().getFirstHeader();
        //如果header容器已存在,就用存在的header
        if (firstHeader != null && firstHeader.getId() == R.id.top_header_footer_container) {
            headerView = firstHeader;
            for (View view : innerHeaderList) {
                ((FrameLayout) headerView.findViewById(R.id.frame_container)).addView(view);
            }
        }

        View lastFooter = viewFeature.getHost().getLastFooter();
        //如果footer容器已存在,就用存在的footer
        if (lastFooter != null && lastFooter.getId() == R.id.top_header_footer_container) {
            footerView = lastFooter;
            for (View view : innerFooterList)
                ((FrameLayout) footerView.findViewById(R.id.frame_container)).addView(view);
        }
        viewFeature.getHost().addHeaderView(headerView);
        viewFeature.getHost().addFooterView(footerView);
        this.setUpMode(upMode);
        this.setDownMode(downMode);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    private float pullHeight, maxPull = -1;

    /**
     * PullMode.PULL_STATE下，设置最大下拉距离，<0表示无限下拉
     *
     * @param maxPull
     */
    public void setMaxPullDown(int maxPull) {
        this.maxPull = maxPull;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (Math.abs(distanceY) > 144 || viewFeature.getHost() == null) return true;
        Debug.print(debug, "onScroll " + distanceY);
        if ((viewFeature.getHost().arrivedTop() && distanceY > 0) || (viewFeature.getHost().arrivedBottom() && distanceY < 0)) {
            distanceY = touchBuffer * distanceY;
        }
        if ((viewFeature.getHost().arrivedTop() || pullHeight > 0) && upMode == PullMode.PULL_STATE) {
            if (pullHeight - distanceY / upTouchBuffer > 0 && (maxPull < 0 || pullHeight < maxPull || pullHeight - distanceY / upTouchBuffer <= maxPull)) {
                pullHeight -= distanceY / upTouchBuffer;
                if (pullHeight > maxPull && maxPull >= 0) pullHeight = maxPull;
                //headerView.setPadding(0, -headerHeight, 0, 0);
                onPull(getControllerView(), (int) pullHeight);
                ITEM_FLAG_RETURN = distanceY >= 0;
            } else {
                if (pullHeight - distanceY / touchBuffer < 1) {
                    pullHeight = 0;
                    ITEM_FLAG_RETURN = false;
                } else {
                    ITEM_FLAG_RETURN = true;
                }
            }
        } else if (viewFeature.getHost().arrivedTop() && headerHeight > 0 && upMode == PullMode.PULL_SMOOTH && !(headerView.getPaddingTop() == -headerHeight && distanceY > 0)) {
            int upPadding = (int) (headerView.getPaddingTop() - distanceY / touchBuffer);
            if (upPadding < -headerHeight) upPadding = -headerHeight;
            onUpMove(headerView, upPadding + headerHeight, 1.0f * (upPadding + headerHeight) / ((1 + upThreshold) * headerHeight));
            headerView.setPadding(0, upPadding, 0, 0);
            ITEM_FLAG_RETURN = distanceY > 0;
            touchBuffer = upPadding < headerHeight ? upTouchBuffer : (upTouchBuffer + 1.0f * upPadding / headerHeight);
        } else if (viewFeature.getHost().arrivedBottom() && footerHeight > 0 && downMode == PullMode.PULL_SMOOTH
                && !(footerView.getPaddingBottom() == -footerHeight && distanceY < 0)) {
            int downPadding = (int) (footerView.getPaddingBottom() + distanceY / touchBuffer);
            if (downPadding < -footerHeight) downPadding = -footerHeight;
            onDownMove(footerView, downPadding + footerHeight, 1.0f * (downPadding + footerHeight) / (footerHeight * (1 + downThreshold)));
            footerView.setPadding(0, 0, 0, downPadding);
            ITEM_FLAG_RETURN = distanceY < 0;
            touchBuffer = downPadding < footerHeight ? downTouchBuffer : (downTouchBuffer + 1.0f * downPadding / footerHeight);
        } else {
            ITEM_FLAG_RETURN = false;
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    //防止底部多次刷新
    private boolean autoDownRefreshLock;

    @Override
    public void onScrollStateChanged(View view, boolean isScrolling) {
        if (viewFeature.getHost() == null) return;
        if (!viewFeature.getHost().arrivedBottom()) {
            autoDownRefreshLock = false;
        } else if (!isScrolling) {
            if (downMode == PullMode.PULL_AUTO) {
                if (!autoDownRefreshLock) {
                    autoDownRefreshLock = true;
                    onDownRefresh();
                }
            }
        }
    }

    public void loseDownRefreshLock() {
        autoDownRefreshLock = false;
    }

    @Override
    public void onScroll(View view, int scrollX, int scrollY) {

    }
}
