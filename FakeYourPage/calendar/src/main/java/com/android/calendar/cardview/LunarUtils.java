package com.android.calendar.cardview;

import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

/**
 * Created by xiaozhilong on 2/16/17.
 */

public class LunarUtils {
    private static final boolean DEBUG = true;
    private static final String TAG = "LunarUtils";

    public static String getLunarMonthDayOrFes(Time time) {
        Time t = new Time(time);
        CalendarUtils.normalizeTime(t, true);
        int flag = Lunar.FLAG_SHOW_DAY | Lunar.FLAG_SHOW_FESTIVAL | Lunar.FLAG_SHOW_DAY_FIRSTDAY_BY_MONTH;
        return getLunarMonthDayOrFes(t.year, t.month, t.monthDay, flag);
    }

    public static String getLunarMonthDayOrFes(int year, int month, int day) {
        int flag = Lunar.FLAG_SHOW_DAY | Lunar.FLAG_SHOW_FESTIVAL | Lunar.FLAG_SHOW_DAY_FIRSTDAY_BY_MONTH;
        return getLunarMonthDayOrFes(year, month, day, flag);
    }

    private static String getLunarMonthDayOrFes(int year, int month, int day, int flag) {
        String result = null;
        try {
            String lunar[] = Lunar.getLunarString(year, month, day, flag);
            if (lunar != null) {
                if (!TextUtils.isEmpty(lunar[1])) {
                    result = lunar[1];
                } else if (!TextUtils.isEmpty(lunar[0])) {
                    result = lunar[0];
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "getLunarMonthDayOrFes error");
        }
        return result;
    }
}
