package com.mbui.sdk.kits.pathview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

import com.mbui.sdk.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * PathView is an View that animate paths.
 */
public class PathView extends View {

    public static final String debug = "PathView";
    private static final float fillProgress = 1 / 3f;

    private Paint strokePaint, fillPaint;
    private SvgUtils svgUtils;
    private List<SvgUtils.SvgPath> mPaths;
    private Thread mLoader;
    private int svgResourceId;
    private AnimatorBuilder animatorBuilder;
    private float progress = 0f;
    private boolean naturalColors;

    public PathView(Context context) {
        this(context, null);
    }

    public PathView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initSelf();
        getFromAttributes(context, attrs);
    }

    private void initSelf() {
        svgUtils = new SvgUtils();

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setShader(null);
        fillPaint.setStyle(Paint.Style.FILL);

        mPaths = new ArrayList<>();
    }

    /**
     * Get all the fields from the attributes .
     *
     * @param context The Context of the application.
     * @param attrs   attributes provided from the resources.
     */
    private void getFromAttributes(Context context, AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PathView);
        try {
            if (a != null) {
                strokePaint.setColor(a.getColor(R.styleable.PathView_pathColor, 0xff000000));
                strokePaint.setStrokeWidth(a.getFloat(R.styleable.PathView_pathWidth, 1.5f));
                fillPaint.setColor(a.getColor(R.styleable.PathView_pathColor, 0xff000000));
                svgResourceId = a.getResourceId(R.styleable.PathView_svg, 0);
            }
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }


    public void setPercentage(float percentage) {
        if (percentage > 1.0f + fillProgress) {
            percentage = 1.0f + fillProgress;
        }
        if (percentage < 0.0f) {
            percentage = 0f;
        }
        this.progress = percentage;
        updatePathsPhaseLocked();
        invalidate();
    }

    private void updatePathsPhaseLocked() {
        final int count = mPaths.size();
        synchronized (PathView.class) {
            for (int i = 0; i < count; i++) {
                SvgUtils.SvgPath svgPath = mPaths.get(i);
                svgPath.path.reset();
                svgPath.measure.getSegment(0.0f, svgPath.length * (progress < 1f ? progress : 1f), svgPath.path, true);
                svgPath.fillPaint.setAlpha(progress <= 1f ? 0 : (int) (255 * (progress - 1f) / fillProgress));
                svgPath.path.rLineTo(0.0f, 0.0f);
            }
            fillPaint.setAlpha(progress <= 1f ? 0 : (int) (255 * (progress - 1f) / fillProgress));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (PathView.class) {
            canvas.save();
            canvas.translate(getPaddingLeft(), getPaddingTop());
            final int count = mPaths.size();
            for (int i = 0; i < count; i++) {
                final SvgUtils.SvgPath svgPath = mPaths.get(i);
                final Path path = svgPath.path;
                canvas.drawPath(path, naturalColors ? svgPath.paint : strokePaint);
                if (progress > 1f)
                    canvas.drawPath(path, naturalColors ? svgPath.fillPaint : fillPaint);
            }
            canvas.restore();
        }
    }

    @Override
    protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mLoader != null) {
            try {
                mLoader.join();
            } catch (InterruptedException e) {
                Log.e(debug, "Unexpected error", e);
            }
        }
        if (svgResourceId != 0) {
            mLoader = new Thread(new Runnable() {
                @Override
                public void run() {

                    svgUtils.load(getContext(), svgResourceId);

                    synchronized (PathView.class) {
                        mPaths = svgUtils.getPathsForViewport(w
                                        - getPaddingLeft() - getPaddingRight(),
                                h - getPaddingTop() - getPaddingBottom());
                        updatePathsPhaseLocked();
                    }
                }
            }, "SVG Loader");
            mLoader.start();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (svgResourceId != 0) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(widthSize, heightSize);
            return;
        }

        int desiredWidth = 0;
        int desiredHeight = 0;
        final float strokeWidth = strokePaint.getStrokeWidth() / 2;
        for (SvgUtils.SvgPath path : mPaths) {
            desiredWidth += path.bounds.left + path.bounds.width() + strokeWidth;
            desiredHeight += path.bounds.top + path.bounds.height() + strokeWidth;
        }
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(widthMeasureSpec);

        int measuredWidth, measuredHeight;

        if (widthMode == MeasureSpec.AT_MOST) {
            measuredWidth = desiredWidth;
        } else {
            measuredWidth = widthSize;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            measuredHeight = desiredHeight;
        } else {
            measuredHeight = heightSize;
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }


    public void useNaturalColors() {
        naturalColors = true;
    }

    /**
     * Animator for the paths of the view.
     *
     * @return The AnimatorBuilder to build the animation.
     */
    public AnimatorBuilder getPathAnimator() {
        if (animatorBuilder == null) {
            animatorBuilder = new AnimatorBuilder(this);
        }
        return animatorBuilder;
    }

    public void setPathColor(final int color) {
        strokePaint.setColor(color);
    }

    public void setPathWidth(final float width) {
        strokePaint.setStrokeWidth(width);
    }

    public void setSvgResource(int svgResource) {
        svgResourceId = svgResource;
    }

    /**
     * Object for building the animation of the path of this view.
     */
    public static class AnimatorBuilder {
        private int duration = 3000;
        private Interpolator interpolator = new AccelerateInterpolator();
        private int delay = 0;
        private final ObjectAnimator objectAnimator;
        private ListenEnd listenEnd;
        private ListenStart listenStart;


        public AnimatorBuilder(final PathView pathView) {
            objectAnimator = ObjectAnimator.ofFloat(pathView, "percentage", 0.0f, 1f + fillProgress);
        }

        public AnimatorBuilder duration(final int duration) {
            this.duration = duration;
            return this;
        }

        public AnimatorBuilder interpolator(final Interpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public AnimatorBuilder delay(final int delay) {
            this.delay = delay;
            return this;
        }

        public void start() {
            if (objectAnimator.isStarted())
                objectAnimator.cancel();
            objectAnimator.setDuration(duration);
            objectAnimator.setInterpolator(interpolator);
            objectAnimator.setStartDelay(delay);
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (listenStart != null)
                        listenStart.onAnimationStart();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (listenEnd != null)
                        listenEnd.onAnimationEnd();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            objectAnimator.start();
        }

        public void listenEnd(ListenEnd listenEnd) {
            this.listenEnd = listenEnd;
        }

        public void listenStart(ListenStart listenStart) {
            this.listenStart = listenStart;
        }
    }

    public static interface ListenStart {
        public void onAnimationStart();
    }

    public static interface ListenEnd {
        public void onAnimationEnd();
    }
}
