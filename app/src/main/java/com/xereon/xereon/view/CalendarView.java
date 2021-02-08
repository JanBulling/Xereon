package com.xereon.xereon.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.xereon.xereon.R;
import com.xereon.xereon.databinding.ViewCalendarBinding;

import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CalendarView extends FrameLayout {

    private final Calendar calendar = Calendar.getInstance();

    private OnDayClickedListener mOnDayClickedListener;
    private final long[] times = new long[7];
    private final int[] dates = new int[7];

    public CalendarView(Context context) {
        super(context);
        init(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_calendar, this);

        ConstraintLayout container = (ConstraintLayout)getChildAt(0);
        for (int i = 0; i < 7; i++) {
            LinearLayout child = (LinearLayout) container.getChildAt(i);
            if (i == 0)
                child.setActivated(true);

            child.setOnClickListener(view -> {
                int childIndex = 6;
                for (int j = 0; j < container.getChildCount(); j++) {
                    View itChild = container.getChildAt(j);
                    if (itChild.getId() != child.getId())
                        itChild.setActivated(false);
                    else
                        childIndex = j;
                }

                child.setActivated(true);

                Toast.makeText(context, "Clicked on: " + dates[childIndex], Toast.LENGTH_SHORT).show();

                if (mOnDayClickedListener != null)
                    mOnDayClickedListener.onDayClicked(times[childIndex], dates[childIndex]);
            });

            String dayOfMonth = String.format(Locale.US, "%02d",
                    calendar.get(Calendar.DAY_OF_MONTH));
            String month = getResources().getStringArray(R.array.month_short)
                    [calendar.get(Calendar.MONTH)];
            int dayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;
            String dayOfWeekString = getResources().getStringArray(R.array.day_of_week_short)
                    [dayOfWeek];

            ((TextView)child.getChildAt(0)).setText(dayOfWeekString);
            ((TextView)child.getChildAt(1)).setText(dayOfMonth);
            ((TextView)child.getChildAt(2)).setText(month);

            times[i] = calendar.getTimeInMillis();
            dates[i] = dayOfWeek;

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    public void setOnDayClickedListener(OnDayClickedListener listener) {
        this.mOnDayClickedListener = listener;
    }

    public interface OnDayClickedListener {
        public void onDayClicked(long time, int dayOfWeek);
    }

}
