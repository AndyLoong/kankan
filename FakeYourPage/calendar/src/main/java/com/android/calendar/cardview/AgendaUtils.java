package com.android.calendar.cardview;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.text.format.Time;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by xiaozhilong on 2/22/17.
 */

public class AgendaUtils {
    private static final boolean DEBUG = true;
    private static final String TAG = "AgendaUtils";

    private static final String INSTANCES_WHERE = CalendarContract.Instances.VISIBLE + "=? AND " + CalendarContract.Instances.END + ">? AND " + CalendarContract.Instances.ALL_DAY + "=?";
    private static final String INSTANCES_ORDER = CalendarContract.Instances.BEGIN + " asc";
    private static final String[] INSTANCES_PROJECTION = new String[]{
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.ALL_DAY,
            CalendarContract.Instances.TITLE};

    private static final int INDEX_EVENT_ID = 0;
    private static final int INDEX_BEGIN = 1;
    private static final int INDEX_END = 2;
    private static final int INDEX_ALL_DAY = 3;
    private static final int INDEX_TITLE = 4;

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat DATE_YEAR_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat DATE_YEAR_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final Comparator<AgendaItem> sTimeComparator = new TimeComparator();


    /**
     * @param requestCount : request agenda count to show
     * @param items        : store agendas to show
     * @param refreshTime  : time to refresh agenda again, array size is 1    .
     * @return Returns agenda array and refresh time
     */
    public static void getTodayAgendasAndRefreshTime(Context context, int requestCount, ArrayList<AgendaItem> items, long[] refreshTime) {
        if (requestCount <= 0 || items == null || refreshTime == null) {
            Log.d(TAG, "params is invalid: requestCount = " + requestCount + ", items = " + items + ", refreshTime = " + refreshTime);
            return;
        }
        Cursor cursor = getAgendaCursor(context);
        ArrayList<AgendaItem> allItems = parseAgenda(cursor);
        getShowAgendaItems(allItems, items, requestCount, refreshTime);
        if (DEBUG) {
            Log.d(TAG, "show agenda size = " + items.size() + ", refreshTime = " + refreshTime[0]);
        }
    }

    private static Cursor getAgendaCursor(Context context) {
        if (!hasPermission(context, Manifest.permission.READ_CALENDAR)) {
            Log.d(TAG, "get agenda fail from providers, because permission read calendar deny.");
            return null;
        }

        Time baseTime = getBaseDate();
        normalizeTime(baseTime, true);
        Uri.Builder builder = CalendarContract.Instances.CONTENT_BY_DAY_URI.buildUpon();
        int rangeArgs = getRangeOfDay(baseTime);
        ContentUris.appendId(builder, rangeArgs);
        ContentUris.appendId(builder, rangeArgs);

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("(").append(INSTANCES_WHERE).append(") OR (").append(INSTANCES_WHERE).append(")");

        long localStart = System.currentTimeMillis();
        Time time = new Time();
        time.normalize(false);
        long localOffset = time.gmtoff * 1000;
        long utcStart = localStart - localOffset;
        String[] queryArgs = new String[]{
                // allday selection
                "1", /* visible = ? */
                String.valueOf(utcStart), /* end > ? */
                "1", /* allDay = ? */

                // non-allday selection
                "1", /* visible = ? */
                String.valueOf(localStart), /* end > ? */
                "0" /* allDay = ? */
        };

        return context.getContentResolver().query(builder.build(), INSTANCES_PROJECTION,
                queryBuilder.toString(), queryArgs, INSTANCES_ORDER);
    }

    private static ArrayList<AgendaItem> parseAgenda(Cursor cursor) {
        if (cursor == null) {
            return new ArrayList<>(0);
        }
        ArrayList<AgendaItem> items = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                int eventId = cursor.getInt(INDEX_EVENT_ID);
                long begin = cursor.getLong(INDEX_BEGIN);
                long end = cursor.getLong(INDEX_END);
                int allDay = cursor.getInt(INDEX_ALL_DAY);
                String title = cursor.getString(INDEX_TITLE);
                if (allDay == 1) {
                    begin = convertAlldayUtcToLocal(begin);
                    end = convertAlldayUtcToLocal(end);
                }
                AgendaItem item = new AgendaItem(eventId, allDay, begin, end, title);
                items.add(item);
            }

            return items;
        } finally {
            closeSilently(cursor);
        }
    }

    /**
     * @return Returns refresh time , when time reach, agenda should refresh.
     */
    private static void getShowAgendaItems(ArrayList<AgendaItem> allItems, ArrayList<AgendaItem> showItems, int requestCount, long[] refreshTime) {
        if (allItems == null || allItems.size() == 0) {
            if (DEBUG) {
                Log.d(TAG, "agenda count size is 0. ");
            }
            return;
        }
        Collections.sort(allItems, sTimeComparator);
        int allSize = allItems.size();
        if (DEBUG) {
            Log.d(TAG, "all agenda size is " + allSize);
        }

        //first : get requestCount agenda from agenda before now
        final long now = System.currentTimeMillis();
        final int nearestNowPositionAfterNow = getNearestNowPositionAfterNow(allItems, now);
        long refresh = nearestNowPositionAfterNow == -1 ? 0 : allItems.get(nearestNowPositionAfterNow).startTime;
        int begin = nearestNowPositionAfterNow == -1 ? allSize - 1 : nearestNowPositionAfterNow - 1;
        for (int i = begin; i >= 0; i--) {
            AgendaItem item = allItems.get(i);
            showItems.add(0, item);
            if (refresh == 0 || item.endTime < refresh) {
                refresh = item.endTime;
            }
            if (showItems.size() >= requestCount) {
                if (DEBUG) {
                    Log.d(TAG, "once agenda before now is enough.");
                }
                refreshTime[0] = refresh;
                return;
            }
        }

        //second : if not enough from first step, get from agenda after now
        if (nearestNowPositionAfterNow == -1) {
            refreshTime[0] = refresh;
            return;
        }
        for (int i = nearestNowPositionAfterNow; i < allSize; i++) {
            AgendaItem item = allItems.get(i);
            showItems.add(item);
            if (refresh == 0 || item.startTime < refresh) {
                refresh = item.startTime;
            }
            if (showItems.size() >= requestCount) {
                if (DEBUG) {
                    Log.d(TAG, "once agenda after now is enough.");
                }
                refreshTime[0] = refresh;
                return;
            }
        }
        refreshTime[0] = refresh;
    }

    private static int getNearestNowPositionAfterNow(ArrayList<AgendaItem> allItems, long now) {
        int size = allItems.size();
        if (allItems.get(size - 1).startTime <= now) {
            if (DEBUG) {
                Log.d(TAG, "getNearestNowPositionAfterNow fail 111 because no such agenda");
            }
            return -1;
        }
        for (int i = 0; i < size; i++) {
            AgendaItem item = allItems.get(i);
            if (item.startTime > now) {
                return i;
            }
        }
        if (DEBUG) {
            Log.d(TAG, "getNearestNowPositionAfterNow fail 222 because no such agenda");
        }
        return -1;
    }

    /**
     * @param times : store start time  and end time,  array size is 2.
     */
    public static void getAgendaTime(Context context, long start, long end, int allDay, String[] times) {
        if (times == null || times.length <= 1) {
            Log.d(TAG, "times is invalid , times = " + times);
            return;
        }
        Time time = new Time();
        time.set(System.currentTimeMillis());
        int thisYear = time.year;
        int thisYearDay = time.yearDay;

        time.set(start);
        int startYear = time.year;
        int startYearDay = time.yearDay;

        time.set(end);
        int endYear = time.year;
        int endYearDay = time.yearDay;
        boolean isStartInToday = (thisYear == startYear && thisYearDay == startYearDay);
        boolean isEndInToday = (thisYear == endYear && thisYearDay == endYearDay);

        String result = "";
        if (isStartInToday) {
            if (allDay == 1) {
                times[0] = context.getString(R.string.calendar_agenda_time_allday);
            } else {
                result = TIME_FORMAT.format(new Date(start));
                times[0] = String.format(context.getString(R.string.calendar_agenda_time_start), result);
                if (isEndInToday) {
                    result = TIME_FORMAT.format(new Date(end));
                    times[1] = String.format(context.getString(R.string.calendar_agenda_time_end), result);
                }
            }
            return;
        }

        if (allDay == 1) {
            result = DATE_YEAR_FORMAT.format(new Date(end));
            times[1] = String.format(context.getString(R.string.calendar_agenda_time_end), result);
            return;
        }

        result = DATE_YEAR_TIME_FORMAT.format(new Date(end));
        times[1] = String.format(context.getString(R.string.calendar_agenda_time_end), result);
    }

    private static Time getBaseDate() {
        Time time = new Time();
        time.setToNow();
        time.hour = 0;
        time.minute = 0;
        time.second = 0;
        return time;
    }

    private static int getRangeOfDay(Time time) {
        long start = normalizeTime(time, true);
        return Time.getJulianDay(start, time.gmtoff);
    }

    private static long normalizeTime(Time time, boolean ignoreDst) {
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

    private static long convertAlldayUtcToLocal(long utcTime) {
        Time recycle = new Time();
        recycle.timezone = Time.TIMEZONE_UTC;
        recycle.set(utcTime);
        recycle.timezone = Time.getCurrentTimezone();
        return normalizeTime(recycle, true);
    }

    private static int compare(long a, long b) {
        return a < b ? -1 : a == b ? 0 : 1;
    }

    private static void closeSilently(Cursor c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (Throwable t) {
            Log.w(TAG, "fail to close", t);
        }
    }

    private static boolean hasPermission(Context context, String permission) {
        int hasPermission = ContextCompat.checkSelfPermission(context, permission);
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }


    public static class AgendaItem {
        public int eventId;
        public int allDay;
        public long startTime;   //saved in local mode
        public long endTime;     //saved in local mode
        public String name;

        public AgendaItem(int eventId, int allDay, long startTime, long endTime, String name) {
            this.eventId = eventId;
            this.allDay = allDay;
            this.startTime = startTime;
            this.endTime = endTime;
            this.name = name;
        }
    }

    private static class TimeComparator implements Comparator<AgendaItem> {
        @Override
        public int compare(AgendaItem item1, AgendaItem item2) {
            return AgendaUtils.compare(item1.startTime, item2.startTime);
        }
    }
}
