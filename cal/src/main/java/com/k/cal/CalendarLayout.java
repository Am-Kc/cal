package com.k.cal;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

public class CalendarLayout extends LinearLayout implements CompoundButton.OnCheckedChangeListener {

    private CalendarView calendarView;
    private CheckBox cbToggle;

    public CalendarLayout(Context context) {
        super(context);
    }

    public CalendarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CalendarLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViews();
    }

    private void findViews() {
        if (getChildCount() < 2) {
            throw new RuntimeException("CalendarLayout  layout error");
        }
        if (getChildAt(0) instanceof CalendarView) {
            calendarView = (CalendarView) getChildAt(0);
        } else {
            throw new RuntimeException("CalendarLayout   child 0 must be CalendarView");
        }
        if (getChildAt(1) instanceof CheckBox) {
            cbToggle = (CheckBox) getChildAt(1);
        } else {
            throw new RuntimeException("CalendarLayout   child 1 must be CheckBox");
        }

        cbToggle.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            openCalendar();
        } else {
            closeCalendar();
        }
    }

    private void openCalendar() {
        calendarView.openCalendar();
    }

    private void closeCalendar() {
        calendarView.closeCalendar();
    }
}
