package com.xereon.xereon.linechart;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xereon.xereon.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class LineChartView extends View implements ScrubGestureDetector.ScrubListener {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            FillType.NONE,
            FillType.UP,
            FillType.DOWN,
            FillType.TOWARD_ZERO,
    })
    public @interface FillType {
        /**
         * Fill type constant for having no fill on the graph
         */
        int NONE = 0;

        /**
         * Fill type constant for always filling the area above the char.
         */
        int UP = 1;

        /**
         * Fill type constant for always filling the area below the chart
         */
        int DOWN = 2;

        /**
         * Fill type constant for filling toward zero. This will fill downward if your chart is
         * positive, or upward if your chart is negative. If your chart intersects zero,
         * each segment will still color toward zero.
         */
        int TOWARD_ZERO = 3;
    }

    @ColorInt
    private int lineColor;
    @ColorInt
    private int fillColor;
    private float lineWidth;
    private float cornerRadius;
    @FillType
    private int fillType = FillType.NONE;
    @ColorInt
    private int baseLineColor;
    private float baseLineWidth;
    @ColorInt
    private int scrubLineColor;
    private float scrubLineWidth;
    private boolean scrubEnabled;

    // the onDraw data
    private final Path renderPath = new Path();
    private final Path chartPath = new Path();
    private final Path baseLinePath = new Path();
    private final Path scrubLinePath = new Path();

    // adapter
    private @Nullable
    LineChartAdapter adapter;

    private ScaleHelper scaleHelper;
    private Paint chartLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint chartFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint baseLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint scrubLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private @Nullable OnScrubListener scrubListener;
    private @NonNull ScrubGestureDetector scrubGestureDetector;
    private final RectF contentRect = new RectF();

    private List<Float> xPoints;
    private List<Float> yPoints;

    public LineChartView(Context context) {
        super(context);
        init(context, null, R.attr.chart_LineChartViewStyle, R.style.chart_LineChart);
    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.chart_LineChartViewStyle, R.style.chart_LineChart);
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, R.style.chart_LineChart);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LineChartView,
                defStyleAttr, defStyleRes);

        lineColor = a.getColor(R.styleable.LineChartView_chart_lineColor, 0);
        fillColor = a.getColor(R.styleable.LineChartView_chart_fillColor, 0);
        lineWidth = a.getDimension(R.styleable.LineChartView_chart_lineWidth, 0);
        cornerRadius = a.getDimension(R.styleable.LineChartView_chart_cornerRadius, 0);

        int legacyFill = a.getBoolean(R.styleable.LineChartView_chart_fill, false)
                ? FillType.DOWN
                : FillType.NONE;
        int fillType = a.getInt(R.styleable.LineChartView_chart_fillType, legacyFill);
        setFillType(fillType);

        baseLineColor = a.getColor(R.styleable.LineChartView_chart_baseLineColor, 0);
        baseLineWidth = a.getDimension(R.styleable.LineChartView_chart_baseLineWidth, 0);
        scrubEnabled = a.getBoolean(R.styleable.LineChartView_chart_scrubEnabled, true);
        scrubLineColor = a.getColor(R.styleable.LineChartView_chart_scrubLineColor, baseLineColor);
        scrubLineWidth = a.getDimension(R.styleable.LineChartView_chart_scrubLineWidth, lineWidth);
        a.recycle();

        chartLinePaint.setStyle(Paint.Style.STROKE);
        chartLinePaint.setColor(lineColor);
        chartLinePaint.setStrokeWidth(lineWidth);
        chartLinePaint.setStrokeCap(Paint.Cap.ROUND);
        if (cornerRadius != 0) {
            chartLinePaint.setPathEffect(new CornerPathEffect(cornerRadius));
        }

        chartFillPaint.set(chartLinePaint);
        chartFillPaint.setColor(fillColor);
        chartFillPaint.setStyle(Paint.Style.FILL);
        chartFillPaint.setStrokeWidth(0);

        baseLinePaint.setStyle(Paint.Style.STROKE);
        baseLinePaint.setColor(baseLineColor);
        baseLinePaint.setStrokeWidth(baseLineWidth);

        scrubLinePaint.setStyle(Paint.Style.STROKE);
        scrubLinePaint.setStrokeWidth(scrubLineWidth);
        scrubLinePaint.setColor(scrubLineColor);
        scrubLinePaint.setStrokeCap(Paint.Cap.ROUND);

        final Handler handler = new Handler();
        final float touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        scrubGestureDetector = new ScrubGestureDetector(this, handler, touchSlop);
        scrubGestureDetector.setEnabled(scrubEnabled);
        setOnTouchListener(scrubGestureDetector);

        xPoints = new ArrayList<>();
        yPoints = new ArrayList<>();

        if (isInEditMode()) {
            this.setAdapter(new LineChartAdapter() {
                private final float[] yData = new float[] {68,22,31,57,35,79,86,47,34,55,80,72,99,66,47,42,56,64,66,80,97,10,43,12,25,71,47,73,49,36};
                @Override public int getCount() { return yData.length; }
                @NonNull @Override public Object getItem(int index) { return yData[index]; }
                @Override public float getY(int index) { return yData[index]; }
            });
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        updateContentRect();
        populatePath();
    }

    private void populatePath() {
        if (adapter == null) return;
        if (getWidth() == 0 || getHeight() == 0) return;

        final int adapterCount = adapter.getCount();

        // to draw anything, we need 2 or more points
        if (adapterCount < 2) {
            clearData();
            return;
        }

        scaleHelper = new ScaleHelper(adapter, contentRect, lineWidth, isFillInternal());

        xPoints.clear();
        yPoints.clear();

        // make our main graph path
        chartPath.reset();
        for (int i = 0; i < adapterCount; i++) {
            final float x = scaleHelper.getX(adapter.getX(i));
            final float y = scaleHelper.getY(adapter.getY(i));

            // points to render graphic
            xPoints.add(x);
            yPoints.add(y);

            if (i == 0) {
                chartPath.moveTo(x, y);
            } else {
                chartPath.lineTo(x, y);
            }

        }

        // if we're filling the graph in, close the path's circuit
        final Float fillEdge = getFillEdge();
        if (fillEdge != null) {
            final float lastX = scaleHelper.getX(adapter.getX(adapter.getCount() - 1));
            // line up or down to the fill edge
            chartPath.lineTo(lastX, fillEdge);
            // line straight left to far edge of the view
            chartPath.lineTo(getPaddingStart(), fillEdge);
            // closes line back on the first point
            chartPath.close();
        }

        // make our base line path
        baseLinePath.reset();
        if (adapter.hasBaseLine()) {
            float scaledBaseLine = scaleHelper.getY(adapter.getBaseLine());
            baseLinePath.moveTo(0, scaledBaseLine);
            baseLinePath.lineTo(getWidth(), scaledBaseLine);
        }

        renderPath.reset();
        renderPath.addPath(chartPath);

        invalidate();
    }

    @Nullable
    private Float getFillEdge() {
        switch (fillType) {
            case FillType.NONE:
                return null;
            case FillType.UP:
                return (float) getPaddingTop();
            case FillType.DOWN:
                return (float) getHeight() - getPaddingBottom();
            case FillType.TOWARD_ZERO:
                float zero = scaleHelper.getY(0F);
                float bottom = (float) getHeight() - getPaddingBottom();
                return Math.min(zero, bottom);
            default:
                throw new IllegalStateException(
                        String.format(Locale.US, "Unknown fill-type: %d", fillType)
                );
        }
    }

    public float getScaledX(float x) {
        if (scaleHelper == null) {
            return x;
        }
        return scaleHelper.getX(x);
    }

    public float getScaledY(float y) {
        if (scaleHelper == null) {
            return y;
        }
        return scaleHelper.getY(y);
    }

    @NonNull
    public Path getChartLinePath() {
        return new Path(chartPath);
    }

    private void setScrubLine(float x) {
        x = resolveBoundedScrubLine(x);
        scrubLinePath.reset();
        scrubLinePath.moveTo(x, getPaddingTop());
        scrubLinePath.lineTo(x, getHeight() - getPaddingBottom());
        invalidate();
    }

    private float resolveBoundedScrubLine(float x) {
        float scrubLineOffset = scrubLineWidth / 2;

        float leftBound = getPaddingStart() + scrubLineOffset;
        if (x < leftBound) {
            return leftBound;
        }

        float rightBound = getWidth() - getPaddingEnd() - scrubLineOffset;
        if (x > rightBound) {
            return rightBound;
        }

        return x;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        updateContentRect();
        populatePath();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(baseLinePath, baseLinePaint);

        if(fillType != FillType.NONE){
            canvas.drawPath(renderPath, chartFillPaint);
        }

        canvas.drawPath(renderPath, chartLinePaint);
        canvas.drawPath(scrubLinePath, scrubLinePaint);
    }

    @ColorInt public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(@ColorInt int lineColor) {
        this.lineColor = lineColor;
        chartLinePaint.setColor(lineColor);
        invalidate();
    }

    @ColorInt public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(@ColorInt int fillColor) {
        this.fillColor = fillColor;
        chartFillPaint.setColor(fillColor);
        invalidate();
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        chartLinePaint.setStrokeWidth(lineWidth);
        invalidate();
    }

    public float getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        if (cornerRadius != 0) {
            chartLinePaint.setPathEffect(new CornerPathEffect(cornerRadius));
            chartFillPaint.setPathEffect(new CornerPathEffect(cornerRadius));
        } else {
            chartLinePaint.setPathEffect(null);
            chartFillPaint.setPathEffect(null);
        }
        invalidate();
    }

    @NonNull
    public Paint getScrubLinePaint() {
        return scrubLinePaint;
    }

    public void setScrubLinePaint(@NonNull Paint scrubLinePaint) {
        this.scrubLinePaint = scrubLinePaint;
        invalidate();
    }

    public boolean isFill() {
        switch (fillType) {
            case FillType.NONE:
                return false;
            case FillType.UP:
            case FillType.DOWN:
            case FillType.TOWARD_ZERO:
                return true;
            default:
                throw new IllegalStateException(
                        String.format(Locale.US, "Unknown fill-type: %d", fillType)
                );
        }
    }

    @SuppressWarnings("deprecation")
    private boolean isFillInternal() {
        return isFill();
    }

    @FillType
    public int getFillType() {
        return fillType;
    }

    @Deprecated
    public void setFill(boolean fill) {
        setFillType(fill ? FillType.DOWN : FillType.NONE);
    }

    public void setFillType(@FillType int fillType) {
        if (this.fillType != fillType) {
            this.fillType = fillType;
            populatePath();
        }
    }

    @NonNull
    public Paint getChartLinePaint() {
        return chartLinePaint;
    }

    public void setChartLinePaint(@NonNull Paint pathPaint) {
        this.chartLinePaint = pathPaint;
        invalidate();
    }

    public void setChartFillPaint(@NonNull Paint pathPaint) {
        this.chartFillPaint = pathPaint;
        invalidate();
    }

    @NonNull
    public Paint getChartFillPaint() {
        return chartFillPaint;
    }

    @ColorInt public int getBaseLineColor() {
        return baseLineColor;
    }

    public void setBaseLineColor(@ColorInt int baseLineColor) {
        this.baseLineColor = baseLineColor;
        baseLinePaint.setColor(baseLineColor);
        invalidate();
    }

    public float getBaseLineWidth() {
        return baseLineWidth;
    }

    public void setBaseLineWidth(float baseLineWidth) {
        this.baseLineWidth = baseLineWidth;
        baseLinePaint.setStrokeWidth(baseLineWidth);
        invalidate();
    }

    @NonNull
    public Paint getBaseLinePaint() {
        return baseLinePaint;
    }

    public void setBaseLinePaint(@NonNull Paint baseLinePaint) {
        this.baseLinePaint = baseLinePaint;
        invalidate();
    }

    @ColorInt public int getScrubLineColor() {
        return scrubLineColor;
    }

    public void setScrubLineColor(@ColorInt int scrubLineColor) {
        this.scrubLineColor = scrubLineColor;
        scrubLinePaint.setColor(scrubLineColor);
        invalidate();
    }

    public float getScrubLineWidth() {
        return scrubLineWidth;
    }

    public void setScrubLineWidth(float scrubLineWidth) {
        this.scrubLineWidth = scrubLineWidth;
        scrubLinePaint.setStrokeWidth(scrubLineWidth);
        invalidate();
    }

    public boolean isScrubEnabled() {
        return scrubEnabled;
    }

    public void setScrubEnabled(boolean scrubbingEnabled) {
        this.scrubEnabled = scrubbingEnabled;
        scrubGestureDetector.setEnabled(scrubbingEnabled);
        invalidate();
    }

    @Nullable
    public OnScrubListener getScrubListener() {
        return scrubListener;
    }

    public void setScrubListener(@Nullable OnScrubListener scrubListener) {
        this.scrubListener = scrubListener;
    }

    @Nullable
    public LineChartAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(@Nullable LineChartAdapter adapter) {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(dataSetObserver);
        }
        this.adapter = adapter;
        if (this.adapter != null) {
            this.adapter.registerDataSetObserver(dataSetObserver);
        }
        populatePath();
    }

    @NonNull
    public List<Float> getXPoints() {
        return new ArrayList<>(xPoints);
    }

    @NonNull
    public List<Float> getYPoints() {
        return new ArrayList<>(yPoints);
    }

    private void clearData() {
        scaleHelper = null;
        renderPath.reset();
        chartPath.reset();
        baseLinePath.reset();
        invalidate();
    }

    static class ScaleHelper {
        final float width, height;
        final int size;
        final float xScale, yScale;
        final float xTranslation, yTranslation;

        public ScaleHelper(LineChartAdapter adapter, RectF contentRect, float lineWidth, boolean fill) {
            final float leftPadding = contentRect.left;
            final float topPadding = contentRect.top;

            final float lineWidthOffset = fill ? 0 : lineWidth;
            this.width = contentRect.width() - lineWidthOffset;
            this.height = contentRect.height() - lineWidthOffset;

            this.size = adapter.getCount();

            RectF bounds = adapter.getDataBounds();

            bounds.inset(bounds.width() == 0 ? -1 : 0, bounds.height() == 0 ? -1 : 0);

            final float minX = bounds.left;
            final float maxX = bounds.right;
            final float minY = bounds.top;
            final float maxY = bounds.bottom;

            this.xScale = width / (maxX - minX);
            this.xTranslation = leftPadding - (minX * xScale) + (lineWidthOffset / 2);
            this.yScale = height / (maxY - minY);
            this.yTranslation = minY * yScale + topPadding + (lineWidthOffset / 2);
        }

        public float getX(float rawX) {
            return rawX * xScale + xTranslation;
        }

        public float getY(float rawY) {
            return height - (rawY * yScale) + yTranslation;
        }
    }

    @Override
    public int getPaddingStart() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1
                ? super.getPaddingStart()
                : getPaddingLeft();
    }

    @Override
    public int getPaddingEnd() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1
                ? super.getPaddingEnd()
                : getPaddingRight();
    }

    private void updateContentRect() {
        if (contentRect == null) return;

        contentRect.set(
                getPaddingStart(),
                getPaddingTop(),
                getWidth() - getPaddingEnd(),
                getHeight() - getPaddingBottom()
        );
    }

    static int getNearestIndex(List<Float> points, float x) {
        int index = Collections.binarySearch(points, x);

        if (index >= 0) return index;

        index = - 1 - index;

        if (index == 0) return index;

        if (index == points.size()) return --index;

        final float deltaUp = points.get(index) - x;
        final float deltaDown = x - points.get(index - 1);
        if (deltaUp > deltaDown) {
            index--;
        }

        return index;
    }

    @Override
    public void onScrubbed(float x, float y) {
        if (adapter == null || adapter.getCount() == 0) return;
        if (scrubListener != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
            int index = getNearestIndex(xPoints, x);
            if (scrubListener != null) {
                scrubListener.onScrubbed(adapter.getItem(index));
            }
        }

        setScrubLine(x);
    }

    @Override
    public void onScrubEnded() {
        scrubLinePath.reset();
        if (scrubListener != null) scrubListener.onScrubbed(null);
        invalidate();
    }

    public interface OnScrubListener {
        void onScrubbed(@Nullable Object value);
    }

    private final DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            populatePath();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            clearData();
        }
    };
}
