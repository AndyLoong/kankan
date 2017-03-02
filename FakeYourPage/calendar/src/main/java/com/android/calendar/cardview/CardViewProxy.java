package com.android.calendar.cardview;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.calendar.cardview.AgendaUtils.AgendaItem;
import com.gionee.yourspage.cardinterface.IGioneeCardViewInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xiaozhilong on 9/14/16.
 */
public class CardViewProxy implements IGioneeCardViewInterface {
    private static final boolean DEBUG = true;
    private static final String TAG = "CardViewProxy";

    private static final int MSG_UPDATE_VIEW = 10;

    private static final String ACTION_DATE_CHENGED = "action_date_changed";

    private Context mPackageContext;
    private Context mLauncherContext;
    private boolean mActive;
    private AlarmManager mAlarmManager;
    private Handler mMainHander;
    private BroadcastReceiver mReceiver;
    private NotifyBroker mNotify;
    private CalendarWeatherManager mWeatherManager;
    private CalendarWeekManager mWeekManager;
    private CalendarAgendaManager mAgendaManager;
    private boolean mCNLanguage;
    private AtomicBoolean mContentDirty = new AtomicBoolean(true);

    private View mCardView;

    public CardViewProxy(Context launcherContext, Context packageContext) {
        initHandler(packageContext);
        initView(packageContext);
        initMember(launcherContext, packageContext);
    }

    private void initHandler(Context packageContext) {
        mMainHander = new Handler(packageContext.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_UPDATE_VIEW: {
                        CalendarData data = (CalendarData) msg.obj;
                        if (data == null) {
                            Log.d(TAG, "terrible, calendar data missed");
                            return;
                        }
                        updateContent(data);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        };
    }

    private void initView(Context packageContext) {
        Resources res = packageContext.getResources();
        LayoutInflater inflater = LayoutInflater.from(packageContext);
        String pn = packageContext.getPackageName();

        View view = inflater.inflate(res.getIdentifier("calendar_cardview_layout", "layout", pn), null, false);
        mCardView = view;
    }

    private void initMember(Context launcherContext, Context packageContext) {
        mPackageContext = packageContext;
        mLauncherContext = launcherContext;
        mAlarmManager = (AlarmManager) mLauncherContext.getSystemService(Context.ALARM_SERVICE);
        mCNLanguage = CalendarUtils.isCNLanguage();

        Resources res = packageContext.getResources();
        String pn = packageContext.getPackageName();
        View rootView = mCardView;
        TextView weather = (TextView) rootView.findViewById(res.getIdentifier("calendar_weather_content", "id", pn));
        View week = rootView.findViewById(res.getIdentifier("calendar_week", "id", pn));
        View agenda = rootView.findViewById(res.getIdentifier("calendar_agenda", "id", pn));
        mWeatherManager = new CalendarWeatherManager(packageContext, weather);
        mWeekManager = new CalendarWeekManager(packageContext, week);
        mAgendaManager = new CalendarAgendaManager(launcherContext, packageContext, agenda);
    }

    private void checkLanguageChanged() {
        boolean oldValue = mCNLanguage;
        boolean isChinese = CalendarUtils.isCNLanguage();
        if (CalendarUtils.compare(oldValue, isChinese) == 0) {
            return;
        }

        mCNLanguage = isChinese;
        mContentDirty.compareAndSet(false, true);
    }

    private void checkContentChanged() {
        if (mContentDirty.compareAndSet(true, false)) {
            refreshContent();
        }
    }

    private void notifyDataChanged() {
        if (mActive) {
            refreshContent();
        } else {
            mContentDirty.compareAndSet(false, true);
        }
    }

    private void refreshContent() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMainHander.removeCallbacksAndMessages(null);

                CalendarData data = new CalendarData();
                String weatherContent = CalendarUtils.getWeatherContent(mLauncherContext);
                data.setWeatherContent(weatherContent);

                CalendarUtils.getCurrentWeekNumberData(data, Time.getCurrentTimezone());

                ArrayList<AgendaItem> items = new ArrayList<>(2);
                long[] agendaRefreshTime = new long[1];
                AgendaUtils.getTodayAgendasAndRefreshTime(mLauncherContext, 2, items, agendaRefreshTime);
                if (items.size() == 0) {
                    String almanacFit = CalendarUtils.getAlmanacFit(mLauncherContext);
                    data.setAlmanacFit(almanacFit);
                }
                data.setAgendaData(items);
                data.setAgendaRefreshTime(agendaRefreshTime[0]);

                Message msg = mMainHander.obtainMessage();
                msg.what = MSG_UPDATE_VIEW;
                msg.obj = data;
                mMainHander.sendMessage(msg);
            }
        }).start();
    }

    private void updateContent(CalendarData data) {
        mWeatherManager.updateContent(data.getWeatherContent());
        mWeekManager.updateContent(data.getHeadDayNumbers(), data.getHeaderDayLunars(), data.getTodayIndex());
        mAgendaManager.updateContent(data.getAgendaData(), data.getAgendaRefreshTime(), data.getAlmanacFit());
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(ACTION_DATE_CHENGED);
        filter.addAction(CalendarAgendaManager.ACTION_AGENDA_END);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (DEBUG) {
                    Log.d(TAG, "action = " + intent.getAction());
                }
                notifyDataChanged();
                setDateChangedAlarm();
            }
        };
        mLauncherContext.registerReceiver(mReceiver, filter);
    }

    private void unregisterReceiver() {
        mLauncherContext.unregisterReceiver(mReceiver);
    }

    private void registerAgendaChange() {
        NotifyBroker notify = new NotifyBroker(mMainHander);
        mNotify = notify;
        mLauncherContext.getContentResolver().registerContentObserver(CalendarContract.Instances.CONTENT_URI, true, notify);
    }

    private void unregisterAgendaChange() {
        mLauncherContext.getContentResolver().unregisterContentObserver(mNotify);
    }

    private void setDateChangedAlarm() {
        Intent intent = new Intent(ACTION_DATE_CHENGED);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mLauncherContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(pendingIntent);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long sendTime = calendar.getTimeInMillis() + DateUtils.DAY_IN_MILLIS;
        if (CalendarUtils.isKeyLimePieOrLater()) {
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, sendTime, pendingIntent);
        } else {
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, sendTime, pendingIntent);
        }
    }

    private void cancelDateChangedAlarm() {
        Intent intent = new Intent(ACTION_DATE_CHENGED);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mLauncherContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(pendingIntent);
    }

    private void cancelAgendaEndAlarm() {
        mAgendaManager.cancelAgendaEndAlarm();
    }

    @Override
    public View getCardView(Context context) {
        Log.d(TAG, "getCardView");
        return mCardView;
    }

    @Override
    public void init(Context context) {
        Log.d(TAG, "init");
    }

    @Override
    public void onAdd() {
        Log.d(TAG, "onAdd");
        registerReceiver();
        registerAgendaChange();
        setDateChangedAlarm();
    }

    @Override
    public void onRemove() {
        Log.d(TAG, "onRemove");
        unregisterReceiver();
        unregisterAgendaChange();
        cancelDateChangedAlarm();
        cancelAgendaEndAlarm();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        mActive = true;
        checkLanguageChanged();
        checkContentChanged();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        mActive = false;
    }

    @Override
    public String getCardViewName() {
        return "Calendar";
    }

    @Override
    public void setNetWorkAuthority(boolean b) {
        Log.d(TAG, "setNetWorkAuthority : b = " + b);
    }

    @Override
    public void setCardFilePath(String s) {
        Log.d(TAG, "setCardFilePath : s = " + s);
    }

    @Override
    public void setMaxSize(int i, int i1) {
        Log.d(TAG, "setMaxSize : i = " + i + ", i1 = " + i1);
    }

    @Override
    public void setAllowInvalidate(boolean b) {
        Log.d(TAG, "setAllowInvalidate : b = " + b);
    }


    private class NotifyBroker extends ContentObserver {

        public NotifyBroker(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG, "event onChange");
            notifyDataChanged();
        }
    }

}
