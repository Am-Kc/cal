package com.k.cal;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarView extends View {

    private final int UP = 0;
    private final int DOWN = 1;
    private String[][] values;
    private Rect[][] rects;
    private int height;
    private int width;
    private int weeks;
    private int cellSize;
    private int drawableWidth;
    private int todayX;
    private int todayY;
    private Paint blackPaint;
    private Paint.FontMetrics whiteFontMetrics;
    private Paint featurePaint;
    private Paint.FontMetrics featureFontMetrics;
    private List<String> done = new ArrayList<>();
    private Drawable sDrawable;
    private Drawable fDrawable;
    private ValueAnimator todayAnimator;
    private int todayRectOffset;
    private int continueType;
    private int featureDay;
    private int todayMarginTop;
    private int scrollerStartY;
    private int scrollerCurrY;
    private Scroller scroller;
    private int duration = 1000;
    private boolean isHeightOffsetInit = false;
    private boolean isMeasure;
    private int h;
    private int direction;
    private long startTime;
    private int hTmp;

    public CalendarView(Context context) {
        super(context);
        init();
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        scroller = new Scroller(getContext(), new LinearInterpolator());
        sDrawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_sign_calendar_s);
        fDrawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_sign_calendar_f);

        drawableWidth = fDrawable.getIntrinsicWidth();

        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int start = calendar.get(Calendar.DAY_OF_WEEK);

        int dayOfMonthMax = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonthMax);
        weeks = calendar.get(Calendar.WEEK_OF_MONTH);
        Log.d("API", String.format("  ====   weeks:%d ====%n ", weeks));
        Log.d("API", "   cal:" + calendar);

        values = new String[weeks][7];
        for (int i = 0; i < weeks; i++) {
            for (int j = 0; j < 7; j++) {
                int value = i * 7 + j + 2 - start;
                values[i][j] = value < 1 || value > dayOfMonthMax ? "" : String.valueOf(value);
                if (value == today) {
                    todayX = j;
                    todayY = i;
                    Log.d("API", String.format("today x:%d   y:%d       value:%d", j, i, value));
                }
                Log.d("API", String.format("cal   x:%d, y:%d   value:%d", j, i, value));
            }
        }

        blackPaint = new Paint();
        blackPaint.setStyle(Paint.Style.FILL);
        blackPaint.setColor(ContextCompat.getColor(getContext(), R.color.black));
        blackPaint.setTextAlign(Paint.Align.CENTER);
        blackPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.t_16));
        whiteFontMetrics = blackPaint.getFontMetrics();

        featurePaint = new Paint();
        featurePaint.setStyle(Paint.Style.FILL);
        featurePaint.setColor(ContextCompat.getColor(getContext(), R.color.red));
        featurePaint.setTextAlign(Paint.Align.CENTER);
        featurePaint.setTextSize(drawableWidth >> 2);
        featureFontMetrics = featurePaint.getFontMetrics();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!isMeasure) {
            isMeasure = true;
            width = MeasureSpec.getSize(widthMeasureSpec);
            cellSize = width / 7;
            if (drawableWidth > cellSize) {
                drawableWidth = cellSize - 2;
            }
            height = cellSize * weeks;
            initRect();
        }
        updateHeight();
        setMeasuredDimension(width, h);
    }

    private void initRect() {
        rects = new Rect[weeks][7];
        int offset = drawableWidth / 2;
        for (int i = 0; i < weeks; i++) {
            for (int j = 0; j < 7; j++) {
                int centerX = (int) ((j + .5) * cellSize);
                int centerY = (int) ((i + .5) * cellSize);
                rects[i][j] = new Rect(centerX - offset, centerY - offset, centerX + offset, centerY + offset);
            }
        }

        todayMarginTop = height / weeks * todayY;
        if (!isHeightOffsetInit) {
            scrollerCurrY = -todayMarginTop;
            scrollerStartY = -todayMarginTop;
            isHeightOffsetInit = true;
            h = cellSize;
        }
    }

    public void setData(int nextTargetDays, List<String> punchCardDateList) {
        if (punchCardDateList != null) {
            done.clear();
            done.addAll(punchCardDateList);
        }
        startTodayAnimate();
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, nextTargetDays);
        if (currentMonth != calendar.get(Calendar.MONTH)) return;
        featureDay = calendar.get(Calendar.DAY_OF_MONTH);
        continueType = nextTargetDays;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (values == null || rects == null) return;
        for (int i = 0; i < weeks; i++) {
            for (int j = 0; j < 7; j++) {
                String value = values[i][j];
                Rect r = rects[i][j];
                int top = r.top + scrollerCurrY;
                int bottom = r.bottom + scrollerCurrY;
                if (value == "") continue;
                if (done != null && done.contains(value)) {
                    if (todayX == j && todayY == i) {
                        sDrawable.setBounds(r.left - todayRectOffset, top - todayRectOffset, r.right + todayRectOffset, bottom + todayRectOffset);
                    } else {
                        sDrawable.setBounds(r.left, top, r.right, bottom);
                    }
                    sDrawable.draw(canvas);
                    continue;
                }

                if (value.equals(String.valueOf(featureDay))) {
                    fDrawable.setBounds(r.left, top, r.right, bottom);
                    fDrawable.draw(canvas);
                    int baseLine = (int) (bottom + top - featureFontMetrics.bottom - featureFontMetrics.top) >> 1;
                    canvas.drawText(continueType + "天", r.centerX(), baseLine, featurePaint);
                } else {
                    int baseLine = (int) (bottom + top - whiteFontMetrics.bottom - whiteFontMetrics.top) >> 1;
                    canvas.drawText(values[i][j], r.centerX(), baseLine, blackPaint);
                }
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollerCurrY = scroller.getCurrY();
            Log.d("API", "   scroller curry :" + scroller.getCurrY());
            invalidate();
            updateHeight();
            requestLayout();
        }
    }

    private void updateHeight() {
        long t = System.currentTimeMillis() - startTime;
        if (t > duration) t = duration;
        if (direction == DOWN) {
            h = (int) (hTmp + (height - hTmp) * t / (duration * 1f));
        } else {
            h = (int) (hTmp - (hTmp - cellSize) * t / (duration * 1f));
        }
        Log.d("API", String.format("   update height    h:%d     hTmp:%d", h, hTmp));
    }

    private void startTodayAnimate() {
        if (todayAnimator != null && todayAnimator.isRunning()) {
            todayAnimator.cancel();
        }
        todayAnimator = ValueAnimator.ofInt(drawableWidth, 0);
        todayAnimator.setDuration(2000);
        todayAnimator.setInterpolator(new BounceInterpolator());
        todayAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                todayRectOffset = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        todayAnimator.start();
    }

    public void openCalendar() {
        hTmp = h;
        direction = DOWN;
        scrollerStartY = scrollerCurrY;
        startTime = System.currentTimeMillis();
        scroller.startScroll(0, scrollerStartY, 0, -scrollerCurrY, duration);
        invalidate();
    }

    public void closeCalendar() {
        hTmp = h;
        direction = UP;
        scrollerStartY = scrollerCurrY;
        startTime = System.currentTimeMillis();
        scroller.startScroll(0, scrollerStartY, 0, -todayMarginTop - scrollerCurrY, duration);
        invalidate();
    }

}
