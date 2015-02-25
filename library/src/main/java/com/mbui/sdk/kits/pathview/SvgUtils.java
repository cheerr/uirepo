package com.mbui.sdk.kits.pathview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;

import com.caverock.androidsvg.PreserveAspectRatio;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Util class to init and get paths from svg.
 */
public class SvgUtils {
    private static final String debug = "SVGUtils";
    private final List<SvgPath> mPaths = new ArrayList<SvgPath>();
    private SVG mSvg;

    public SvgUtils() {
    }

    public void load(Context context, int svgResource) {
        if (mSvg != null) return;
        try {
            mSvg = SVG.getFromResource(context, svgResource);
            mSvg.setDocumentPreserveAspectRatio(PreserveAspectRatio.UNSCALED);

        } catch (SVGParseException e) {
            Log.e(debug, "Could not load specified SVG resource", e);
        }
    }

    public List<SvgPath> getPathsForViewport(final int width, final int height) {

        Canvas canvas = new Canvas() {
            private final Matrix mMatrix = new Matrix();

            @Override
            public int getWidth() {
                return width;
            }

            @Override
            public int getHeight() {
                return height;
            }

            @Override
            public void drawPath(Path path, Paint paint) {
                Path dst = new Path();
                //noinspection deprecation
                getMatrix(mMatrix);
                path.transform(mMatrix, dst);
                final Paint paintOut = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintOut.setStyle(Paint.Style.STROKE);
                paintOut.setColor(paint.getColor());
                paintOut.setStrokeWidth(1.5f);
                final Paint paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintFill.setStyle(Paint.Style.FILL);
                paintFill.setShader(null);
                paintFill.setColor(paint.getColor());
                mPaths.add(new SvgPath(dst, paintOut, paintFill));
            }
        };

        final RectF viewBox = mSvg.getDocumentViewBox();
        final float scale = Math.min(width / viewBox.width(), height / viewBox.height());

        canvas.translate((width - viewBox.width() * scale) / 2.0f,
                (height - viewBox.height() * scale) / 2.0f);
        canvas.scale(scale, scale);

        mSvg.renderToCanvas(canvas);

        return mPaths;
    }

    public static class SvgPath {

        private static final Region REGION = new Region();
        private static final Region MAX_CLIP =
                new Region(Integer.MIN_VALUE, Integer.MIN_VALUE,
                        Integer.MAX_VALUE, Integer.MAX_VALUE);
        final Path path;
        final Paint paint, fillPaint;
        final float length;
        final Rect bounds;
        final PathMeasure measure;

        SvgPath(Path path, Paint paint, Paint fillPaint) {
            this.path = path;
            this.paint = paint;
            this.fillPaint = fillPaint;
            this.measure = new PathMeasure(path, false);
            this.length = measure.getLength();

            REGION.setPath(path, MAX_CLIP);
            bounds = REGION.getBounds();
        }
    }
}