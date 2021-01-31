package com.xereon.xereon.linechart;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

public abstract class LineChartAdapter {
    private final DataSetObservable observable = new DataSetObservable();
    public abstract int getCount();
    public abstract Object getItem(int index);

    public float getX(int index) { return index; }

    public abstract float getY(int index);

    @NonNull
    RectF getDataBounds() {
        final int count = getCount();

        float minX = 0;
        float maxX = count-1;
        return createRectF(minX, 0, maxX, 100);
    }

    @VisibleForTesting
    RectF createRectF(float left, float top, float right, float bottom) {
        return new RectF(left, top, right, bottom);
    }

    public boolean hasBaseLine() { return false; }

    public float getBaseLine() { return 0; }

    public final void notifyDataSetChanged() {
        observable.notifyChanged();
    }

    public final void notifyDataSetInvalidated() {
        observable.notifyInvalidated();
    }

    public final void registerDataSetObserver(DataSetObserver observer) {
        observable.registerObserver(observer);
    }

    public final void unregisterDataSetObserver(DataSetObserver observer) {
        observable.unregisterObserver(observer);
    }
}
