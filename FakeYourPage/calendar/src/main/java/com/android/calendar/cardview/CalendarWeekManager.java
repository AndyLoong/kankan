package com.android.calendar.cardview;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by xiaozhilong on 2/16/17.
 */

public class CalendarWeekManager {
    private static final boolean DEBUG = true;
    private static final String TAG = "CalendarWeekManager";

    private Context mLauncherContext;
    private View mWeekContainerView;
    private ViewGroup mWeekHeaderView;
    private CalendarWeekView mWeekNumberView;

    //save week header string; from Calendar.SUNDAY to Calendar.SATURDAY
    private String[] mHeaderLabels;

    public CalendarWeekManager(Context launcherContext, View weekContainer) {
        initView(launcherContext, weekContainer);
        initHeader();
        initListener();
    }

    private void initView(Context launcherContext, View weekContainer) {
        mLauncherContext = launcherContext;
        Resources res = launcherContext.getResources();
        String pn = launcherContext.getPackageName();

        mWeekContainerView = weekContainer;
        mWeekHeaderView = (ViewGroup) weekContainer.findViewById(res.getIdentifier("calendar_week_header", "id", pn));
        mWeekNumberView = (CalendarWeekView) weekContainer.findViewById(res.getIdentifier("calendar_week_numbers", "id", pn));
    }

    private void initHeader() {
        mHeaderLabels = new String[7];
        int dayOfWeekType = DateUtils.LENGTH_MEDIUM;
        if (CalendarUtils.isCNLanguage()) {
            dayOfWeekType = DateUtils.LENGTH_SHORTEST;
        }
        for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
            mHeaderLabels[i - Calendar.SUNDAY] = DateUtils.getDayOfWeekString(i, dayOfWeekType).toUpperCase();
        }
    }

    private void initListener() {
        mWeekContainerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG) {
                    Log.d(TAG, "launch calendar month page");
                }
                CalendarUtils.launchCalendarMonthPage(mLauncherContext);
            }
        });
    }

    private void updateWeekHeader() {
        int firstDayOfWeekInTime = CalendarUtils.getFirstDayOfWeek();
        if (DEBUG) {
            Log.d(TAG, "update Week : firstDayOfWeekInTime = " + firstDayOfWeekInTime);
        }
        //translate to Calendar type
        int firstDayOfWeekInCalendar = firstDayOfWeekInTime + 1;
        TextView headerDay = null;
        for (int i = 0; i < 7; i++) {
            headerDay = (TextView) mWeekHeaderView.getChildAt(i);
            int position = (firstDayOfWeekInCalendar - Calendar.SUNDAY + i) % 7;
            if (DEBUG) {
                Log.d(TAG, "updateWeekHeader : position = " + position);
            }
            headerDay.setText(mHeaderLabels[position]);
        }
    }

    public void updateContent(String[] headerDayNumbers, String[] headerDayLunars, int todayIndex) {
        updateWeekHeader();
        updateWeekNumbers(headerDayNumbers, headerDayLunars, todayIndex);
    }

    private void updateWeekNumbers(String[] headerDayNumbers, String[] headerDayLunars, int todayIndex) {
        if (headerDayNumbers == null || headerDayLunars == null || todayIndex == -1) {
            Log.d(TAG, "update week numbers fail, data is null.");
            return;
        }
        ViewGroup view = null;
        for (int i = 0; i < 7; i++) {
            view = (ViewGroup) mWeekNumberView.getChildAt(i);
            TextView number = (TextView) view.getChildAt(0);
            TextView lunar = (TextView) view.getChildAt(1);
            number.setText(headerDayNumbers[i]);
            lunar.setText(headerDayLunars[i]);
        }
        mWeekNumberView.setTodayIndex(todayIndex);
    }

}
