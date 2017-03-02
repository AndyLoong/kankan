package com.android.calendar.cardview;

import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

import com.gionee.amiweather.library.QueryConstant;
import com.gionee.amiweather.library.WeatherData;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by xiaozhilong on 9/14/16.
 */
public class CalendarUtils {
    private static final boolean DEBUG = true;
    private static final String TAG = "CalendarUtils";

    private static String GN_WEATHER_PACKAGE = "com.coolwind.weather";
    private static String GN_CALENDAR_PACKAGE = "com.android.calendar";

    private static final int MONDAY_BEFORE_JULIAN_EPOCH = Time.EPOCH_JULIAN_DAY - 3;

    private static final Uri URI_ALMANAC = Uri.parse("content://com.android.calendar.gnprovider/almanac");
    private static final String TODAY_FITTED = "today_fitted";
    private static final String ALMANAC_EXTRA = "almanac_extra";


    public static String getWeatherContent(Context context) {
        Cursor cursor = context.getContentResolver().query(QueryConstant.QUERY_WEATHER_UNCASE_LANGUAGE, null, null, null, null);
        if (cursor == null) {
            return "";
        }
        try {
            if (cursor.moveToFirst()) {
                WeatherData data = WeatherData.obtain(cursor);
                if (data == null) {
                    return "";
                }

                String city = data.getCityName();
                String state = data.getForecastState();
                String temperature = data.getForecastTemperatureWithUnit();
                String air = data.getPM25();
                if (isInvalidValue(city) || isInvalidValue(temperature)) {
                    return "";
                }
                StringBuilder content = new StringBuilder();
                content.append(city);
                if (!isInvalidValue(state)) {
                    content.append(" ").append(state);
                }
                content.append(" ").append(temperature);
                if (!isInvalidValue(air)) {
                    content.append("  ").append(air);
                }
                return content.toString();
            }
        } finally {
            closeSilently(cursor);
        }
        return "";
    }

    private static boolean isInvalidValue(String value) {
        return TextUtils.isEmpty(value) || "null".equals(value);
    }

    public static void getCurrentWeekNumberData(CalendarData data, String timeZone) {
        if (data == null || TextUtils.isEmpty(timeZone)) {
            Log.d(TAG, "terrible, data or timezone is null");
            return;
        }
        String[] headerDayNumbers = new String[7];
        String[] headerDayLunars = new String[7];
        int todayIndex = -1;

        int firstDayOfWeekInTime = getFirstDayOfWeek();
        Time today = new Time(timeZone);
        today.setToNow();
        normalizeTime(today, true);

        int weekNum = getWeekNum(today, firstDayOfWeekInTime);
        int julianMonday = CalendarUtils.getJulianMondayFromWeeksSinceEpoch(weekNum);
        Time time = new Time(timeZone);
        time.setJulianDay(julianMonday);
        normalizeTime(time, true);

        // Now adjust our starting day based on the start day of the week
        // If the week is set to start on a Saturday the first week will be
        // Dec 27th 1969 -Jan 2nd, 1970
        if (time.weekDay != firstDayOfWeekInTime) {
            int diff = time.weekDay - firstDayOfWeekInTime;
            if (diff < 0) {
                diff += 7;
            }
            time.monthDay -= diff;
            normalizeTime(time, true);
        }

        for (int i = 0; i < 7; i++) {
            if (time.year == today.year && time.yearDay == today.yearDay) {
                todayIndex = i;
            }
            headerDayLunars[i] = LunarUtils.getLunarMonthDayOrFes(time.year, time.month, time.monthDay);
            headerDayNumbers[i] = Integer.toString(time.monthDay++);
            normalizeTime(time, true);
            if (DEBUG) {
                Log.d(TAG, "year = " + time.year + ", month = " + time.month + ", day = " + time.monthDay +
                        ", day number " + i + " = " + headerDayNumbers[i]
                        + ", day lunar " + i + " = " + headerDayLunars[i]);
            }
        }
        data.setTodayIndex(todayIndex);
        data.setHeaderDayNumbers(headerDayNumbers);
        data.setHeaderDayLunars(headerDayLunars);
    }

    public static int getFirstDayOfWeek() {
        Calendar cal = Calendar.getInstance(Locale.getDefault());

        //-1: translate to Time type
        return cal.getFirstDayOfWeek() - 1;
    }

    public static String getAlmanacFit(Context context) {
        String[] selectionArgs = new String[]{String.valueOf(System.currentTimeMillis())};
        Cursor cursor = context.getContentResolver().query(URI_ALMANAC, null, null, selectionArgs, null);
        if (cursor == null) {
            return "";
        }
        try {
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(TODAY_FITTED));
            }
        } finally {
            closeSilently(cursor);
        }
        return "";
    }

    private static int getWeekNum(Time time, int firstDayOfWeekInTime) {
        Time temp = new Time(time);
        long millis = normalizeTime(temp, true);

        int julianDay = Time.getJulianDay(millis, temp.gmtoff);
        int weekNum = getWeeksSinceEpochFromJulianDay(julianDay, firstDayOfWeekInTime);

        return weekNum;
    }

    private static int getWeeksSinceEpochFromJulianDay(int julianDay, int firstDayOfWeekInTime) {
        int diff = Time.THURSDAY - firstDayOfWeekInTime;
        if (diff < 0) {
            diff += 7;
        }
        int refDay = Time.EPOCH_JULIAN_DAY - diff;
        return (julianDay - refDay) / 7;
    }

    private static int getJulianMondayFromWeeksSinceEpoch(int week) {
        return MONDAY_BEFORE_JULIAN_EPOCH + week * 7;
    }

    public static boolean isCNLanguage() {
        String locale = Locale.getDefault().getLanguage();
        if (locale.equals(Locale.CHINESE.getLanguage())) {
            return true;
        }

        return false;
    }

    public static boolean isKeyLimePieOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static int compare(boolean lhs, boolean rhs) {
        return lhs == rhs ? 0 : lhs ? 1 : -1;
    }

    public static long normalizeTime(Time time, boolean ignoreDst) {
        long timeMills = -1;
        if (time != null) {
            timeMills = time.normalize(ignoreDst);
            if (timeMills < 0) {
                time.isDst = 0;
                timeMills = time.normalize(false);
                time.allDay = false;
            }
        }
        return timeMills;
    }

    public static long convertAlldayLocalToUtc(long utcTime) {
        Time recycle = new Time();
        recycle.timezone = Time.getCurrentTimezone();
        recycle.set(utcTime);
        recycle.timezone = Time.TIMEZONE_UTC;
        return normalizeTime(recycle, true);
    }

    public static void closeSilently(Cursor c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (Throwable t) {
            Log.w(TAG, "fail to close", t);
        }
    }

    public static void launchWeather(Context context) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(
                    GN_WEATHER_PACKAGE);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, "start weather fail : " + e.toString());
        }
    }

    public static void launchCalendarMonthPage(Context context) {
        try {
            Intent intent = new Intent();
            intent.setAction("push.action.calendar.month.activity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, "start calendar month page fail : " + e.toString());
        }
    }

    public static void launchAgendaDetails(Context context, int eventId, long start, long end) {
        try {
            if (DEBUG) {
                Log.d(TAG, "launch agenda details: eventId = " + eventId + ", start = " + start + ", end = " + end);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(ContentUris.withAppendedId(Uri.parse("content://com.android.calendar/events"), eventId));
            intent.putExtra("beginTime", start);
            intent.putExtra("endTime", end);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, "launchAgendaDetails fail : " + e);
        }

    }

    public static void launchAlmanacPage(Context context) {
        try {
            Intent intent = new Intent();
            intent.setAction("gionee.action.calendar.almanac");
            intent.putExtra(ALMANAC_EXTRA, System.currentTimeMillis());
            intent.setPackage("com.android.calendar");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, "launchAlmanacPage fail : " + e.toString());
        }
    }

    public static void launchCreateAgenda(Context context) {
        try {
            Intent intent = new Intent("gionee.pressure.sensor.action.calendar.event.create");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, "launchCreateAgenda fail : " + e.toString());
        }
    }

}
