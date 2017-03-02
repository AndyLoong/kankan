package com.android.calendar.cardview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by xiaozhilong on 2/16/17.
 */

public class CalendarWeekView extends LinearLayout {
    private Paint mPaint;
    private int mTodayIndex = -1;

    public CalendarWeekView(Context context) {
        super(context);
        init();
    }

    public CalendarWeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalendarWeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3.0f);
        mPaint.setColor(0xfffe8f01);
    }

    public void setTodayIndex(int todayIndex) {
        mTodayIndex = todayIndex;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
    }

    private void drawCircle(Canvas canvas) {
        int todayIndex = mTodayIndex;
        if (todayIndex == -1) {
            return;
        }

        int separatePart = 7 * 2;
        int parts = (todayIndex + 1) * 2 - 1;
        int cx = getWidth() / separatePart * parts;
        int cy = (getPaddingTop() + getHeight()) / 2 - 9;
        int radius = (getHeight() - getPaddingTop()) / 2;
        canvas.drawCircle(cx, cy, radius, mPaint);
    }
}
