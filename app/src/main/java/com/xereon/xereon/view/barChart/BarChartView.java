package com.xereon.xereon.view.barChart;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.sax.StartElementListener;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xereon.xereon.R;
import com.xereon.xereon.view.barChart.BarChartAdapter;

import java.util.ArrayList;
import java.util.List;

public class BarChartView extends View {

    private static final int START_HOUR  =  5;   /* 6 Uhr */
    private static final int NUMBER_BARS = 17; /* 6 Uhr bis 22 Uhr */

    @ColorInt private int barColor;
    @ColorInt private int currentPopularityBarColor;

    private final List<RectF> mData = new ArrayList<>(NUMBER_BARS);

    private @Nullable
    BarChartAdapter adapter;
    private boolean showCurrentPopularity = true;

    private ScaleHelper scaleHelper;

    private Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint barCurrentPopularityPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF contentRect = new RectF();

    public BarChartView(Context context) {
        super(context);
        init(context, null, R.attr.chart_BarChartViewStyle, R.style.chart_BarChart);
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.chart_BarChartViewStyle, R.style.chart_BarChart);
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, R.style.chart_BarChart);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BarChartView,
                defStyleAttr, defStyleRes);

        currentPopularityBarColor = a.getColor(R.styleable.BarChartView_currentBarColor, 0);
        barColor = a.getColor(R.styleable.BarChartView_barColor, 0);

        showCurrentPopularity  = a.getBoolean(R.styleable.BarChartView_showCurrentPopularity, true);
        a.recycle();

        barPaint.setColor(barColor);
        barPaint.setStyle(Paint.Style.FILL);

        if (showCurrentPopularity) {
            barCurrentPopularityPaint.setColor(currentPopularityBarColor);
            barCurrentPopularityPaint.setStyle(Paint.Style.FILL);
        }

        if (isInEditMode()) {
            this.setAdapter(new BarChartAdapter() {
                private final int[] popularity = new int[] {0,0,0,0,0,0, 15, 32, 57, 80, 95, 98, 95, 90, 93, 98, 95, 78, 52, 26, 12, 0,0,0};
                private int currentHour = 10;
                private int currentPopularity = 92;
                @Override public int getPopularity(int hour) {
                    if (hour == currentHour)
                        return currentPopularity;
                    return popularity[hour];
                }
                @Override public int getCurrentHour() { return currentHour; }
            });
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateContentRect();
        populateChart();
    }

    private void populateChart() {
        if (adapter == null) return;
        if (getWidth() == 0 || getHeight() == 0) return;

        scaleHelper = new ScaleHelper(contentRect);

        mData.clear();

        for (int i = START_HOUR; i < START_HOUR + NUMBER_BARS; i++) {
            final float x = scaleHelper.getX(i);
            final float width = scaleHelper.getBarWidth();
            final float y = scaleHelper.getY(adapter.getPopularity(i) + 2);

            // Rects to render graphic
            RectF rect = new RectF(x, y, x + width, getHeight());
            mData.add(rect);
        }

        invalidate();
    }

    private void clearData() {
        scaleHelper = null;
        mData.clear();
        invalidate();
    }

    //O(24)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int currentHour = -1;
        try {
            currentHour = adapter.getCurrentHour() - START_HOUR;
        } catch (NullPointerException e) { /*NO-OP*/ }

        for (int i = 0; i < mData.size(); i++) {
            if (showCurrentPopularity && i == currentHour) {
                canvas.drawRect(mData.get(i), barCurrentPopularityPaint);
            } else
                canvas.drawRect(mData.get(i), barPaint);
        }
    }

    @Nullable
    public BarChartAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(@Nullable BarChartAdapter adapter) {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(dataSetObserver);
        }
        this.adapter = adapter;
        if (this.adapter != null) {
            this.adapter.registerDataSetObserver(dataSetObserver);
        }
        populateChart();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        updateContentRect();
        populateChart();
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

    @ColorInt public int getBarColor() {
        return barColor;
    }

    public void setBarColor(@ColorInt int barColor) {
        this.barColor = barColor;
        barPaint.setColor(barColor);
        invalidate();
    }

    @ColorInt public int getCurrentPopularityBarColor() {
        return currentPopularityBarColor;
    }

    public void setCurrentPopularityBarColor(@ColorInt int barColor) {
        this.currentPopularityBarColor = barColor;
        barCurrentPopularityPaint.setColor(currentPopularityBarColor);
        invalidate();
    }

    @NonNull
    public Paint getBarPaint() {
        return barPaint;
    }

    public void setBarPaint(@NonNull Paint barPaint) {
        this.barPaint = barPaint;
        invalidate();
    }

    @NonNull
    public Paint getBarCurrentPopularityPaint() {
        return barCurrentPopularityPaint;
    }

    public void setBarCurrentPopularityPaint(@NonNull Paint barPaint) {
        this.barCurrentPopularityPaint = barPaint;
        invalidate();
    }

    static class ScaleHelper {
        final float width, height;
        final float xScale, yScale;
        final float xTranslation, yTranslation;

        public ScaleHelper(RectF contentRect) {
            this.xTranslation = contentRect.left;
            this.yTranslation = contentRect.top;

            this.width = contentRect.width();
            this.height = contentRect.height();

            this.xScale = width / NUMBER_BARS;
            this.yScale = height / 102;
        }

        public float getX(float rawX) {
            return (rawX - START_HOUR) * xScale + xTranslation;
        }

        public float getY(float rawY) {
            return height - (rawY * yScale) + yTranslation;
        }

        public float getBarWidth() { return (width / NUMBER_BARS) - 2; }
    }

    private final DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            populateChart();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            clearData();
        }
    };
}
