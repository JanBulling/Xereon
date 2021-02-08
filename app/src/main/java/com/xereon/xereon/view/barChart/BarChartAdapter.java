package com.xereon.xereon.view.barChart;

import android.database.DataSetObservable;
import android.database.DataSetObserver;

public abstract class BarChartAdapter {
    private final DataSetObservable observable = new DataSetObservable();

    public abstract int getPopularity(int index);
    public abstract int getCurrentHour();

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
