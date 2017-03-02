package com.android.calendar.cardview;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.calendar.cardview.AgendaUtils.AgendaItem;

import java.util.ArrayList;

/**
 * Created by xiaozhilong on 2/21/17.
 */

public class CalendarAgendaManager {
    private static final boolean DEBUG = true;
    private static final String TAG = "CalendarAgendaManager";

    public static final String ACTION_AGENDA_END = "action_agenda_end";

    private Context mLauncherContext;
    private Context mPackageContext;
    private AlarmManager mAlarmManager;

    private View mAgendaOneContainerView;
    private View mAgendaOneContentView;
    private TextView mAgendaOneStartTimeView;
    private TextView mAgendaOneEndTimeView;
    private TextView mAgendaOneNameView;
    private View mAgendaTwoContainerView;
    private View mAgendaTwoContentView;
    private TextView mAgendaTwoStartTimeView;
    private TextView mAgendaTwoEndTimeView;
    private TextView mAgendaTwoNameView;

    private View mAgendaAlmanacContainerView;
    private TextView mAgendaAlmanacFitView;

    private View mAgendaCreateView;

    private String[] mTempTimes = new String[2];


    public CalendarAgendaManager(Context launcherContext, Context packageContext, View agendaContainer) {
        initMember(launcherContext, packageContext);
        initView(packageContext, agendaContainer);
        initListener();
    }

    private void initMember(Context launcherContext, Context packageContext) {
        mLauncherContext = launcherContext;
        mPackageContext = packageContext;
        mAlarmManager = (AlarmManager) launcherContext.getSystemService(Context.ALARM_SERVICE);
    }

    private void initView(Context context, View agendaContainer) {
        Resources res = context.getResources();
        String pn = context.getPackageName();

        mAgendaOneContainerView = agendaContainer.findViewById(res.getIdentifier("calendar_agenda_one_container", "id", pn));
        mAgendaOneContentView = agendaContainer.findViewById(res.getIdentifier("calendar_agenda_one_content", "id", pn));
        mAgendaOneStartTimeView = (TextView) agendaContainer.findViewById(res.getIdentifier("calendar_agenda_one_start_time", "id", pn));
        mAgendaOneEndTimeView = (TextView) agendaContainer.findViewById(res.getIdentifier("calendar_agenda_one_end_time", "id", pn));
        mAgendaOneNameView = (TextView) agendaContainer.findViewById(res.getIdentifier("calendar_agenda_one_name", "id", pn));

        mAgendaTwoContainerView = agendaContainer.findViewById(res.getIdentifier("calendar_agenda_two_container", "id", pn));
        mAgendaTwoContentView = agendaContainer.findViewById(res.getIdentifier("calendar_agenda_two_content", "id", pn));
        mAgendaTwoStartTimeView = (TextView) agendaContainer.findViewById(res.getIdentifier("calendar_agenda_two_start_time", "id", pn));
        mAgendaTwoEndTimeView = (TextView) agendaContainer.findViewById(res.getIdentifier("calendar_agenda_two_end_time", "id", pn));
        mAgendaTwoNameView = (TextView) agendaContainer.findViewById(res.getIdentifier("calendar_agenda_two_name", "id", pn));

        mAgendaAlmanacContainerView = agendaContainer.findViewById(res.getIdentifier("calendar_agenda_almanac", "id", pn));
        mAgendaAlmanacFitView = (TextView) agendaContainer.findViewById(res.getIdentifier("calendar_agenda_almanac_content_text", "id", pn));

        mAgendaCreateView = agendaContainer.findViewById(res.getIdentifier("calendar_agenda_create", "id", pn));
    }

    private void initListener() {
        mAgendaOneContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAgendaDatails(v);
            }
        });
        mAgendaTwoContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAgendaDatails(v);
            }
        });
        mAgendaAlmanacContainerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtils.launchAlmanacPage(mLauncherContext);
            }
        });
        mAgendaCreateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtils.launchCreateAgenda(mLauncherContext);
            }
        });
    }

    private void launchAgendaDatails(View view) {
        AgendaItem item = (AgendaItem) view.getTag();
        if (item == null) {
            return;
        }
        long start = item.startTime;
        long end = item.endTime;
        int eventId = item.eventId;
        if (item.allDay == 1) {
            start = CalendarUtils.convertAlldayLocalToUtc(start);
            end = CalendarUtils.convertAlldayLocalToUtc(end);
        }
        CalendarUtils.launchAgendaDetails(mLauncherContext, eventId, start, end);
    }

    public void updateContent(ArrayList<AgendaItem> items, long agendaRefreshTime, String almanacFit) {
        int size = items.size();
        boolean isCNLanguage = CalendarUtils.isCNLanguage();
        if (DEBUG) {
            Log.d(TAG, "size = " + size + ", isCNLanguage = " + isCNLanguage);
        }
        setViewVisibility(size, isCNLanguage);
        setAgendaContent(size, isCNLanguage, items, agendaRefreshTime, almanacFit);
    }

    private void setViewVisibility(int size, boolean isCNLanguage) {
        if (size == 0) {
            mAgendaOneContainerView.setVisibility(View.GONE);
            mAgendaTwoContainerView.setVisibility(View.GONE);
            mAgendaAlmanacContainerView.setVisibility(View.VISIBLE);
        } else if (size == 1) {
            mAgendaOneContainerView.setVisibility(View.VISIBLE);
            mAgendaTwoContainerView.setVisibility(View.GONE);
            mAgendaAlmanacContainerView.setVisibility(View.GONE);
        } else {
            mAgendaOneContainerView.setVisibility(View.VISIBLE);
            mAgendaTwoContainerView.setVisibility(View.VISIBLE);
            mAgendaAlmanacContainerView.setVisibility(View.GONE);
        }

        if (!isCNLanguage && mAgendaAlmanacContainerView.getVisibility() == View.VISIBLE) {
            mAgendaAlmanacContainerView.setVisibility(View.GONE);
        }
    }

    private void setAgendaContent(int size, boolean isCNLanguage, ArrayList<AgendaItem> items, long agendaRefreshTime, String almanacFit) {
        cancelAgendaEndAlarm();
        if (size == 0) {
            if (isCNLanguage) {
                setAlmanacFit(almanacFit);
            }
        } else if (size == 1) {
            AgendaItem item = items.get(0);
            setAgendaItem(item, mAgendaOneContentView, mAgendaOneStartTimeView, mAgendaOneEndTimeView, mAgendaOneNameView);
            setAgendaRefreshAlarm(agendaRefreshTime);
        } else if (size == 2) {
            AgendaItem item1 = items.get(0);
            AgendaItem item2 = items.get(1);
            setAgendaItem(item1, mAgendaOneContentView, mAgendaOneStartTimeView, mAgendaOneEndTimeView, mAgendaOneNameView);
            setAgendaItem(item2, mAgendaTwoContentView, mAgendaTwoStartTimeView, mAgendaTwoEndTimeView, mAgendaTwoNameView);

            setAgendaRefreshAlarm(agendaRefreshTime);
        }
    }

    private void setAlmanacFit(String almanacFit) {
        if (TextUtils.isEmpty(almanacFit)) {
            mAgendaAlmanacContainerView.setVisibility(View.GONE);
            return;
        }
        mAgendaAlmanacFitView.setText(almanacFit);
    }

    private void setAgendaItem(AgendaItem item, View agenda, TextView timeStart, TextView timeEnd, TextView agendaName) {
        agenda.setTag(item);
        agendaName.setText(item.name);

        String[] times = mTempTimes;
        times[0] = "";
        times[1] = "";
        AgendaUtils.getAgendaTime(mPackageContext, item.startTime, item.endTime, item.allDay, times);
        if (TextUtils.isEmpty(times[0])) {
            timeStart.setText(times[1]);
            timeEnd.setText("");
            return;
        }
        timeStart.setText(times[0]);
        timeEnd.setText(times[1]);
    }

    private void setAgendaRefreshAlarm(long agendaRefreshTime) {
        if (DEBUG) {
            Log.d(TAG, "setAgendaRefreshAlarm : agendaRefreshTime = " + agendaRefreshTime);
        }
        Intent intent = new Intent(ACTION_AGENDA_END);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mLauncherContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(pendingIntent);

        if (agendaRefreshTime <= 0) {
            return;
        }

        if (CalendarUtils.isKeyLimePieOrLater()) {
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, agendaRefreshTime, pendingIntent);
        } else {
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, agendaRefreshTime, pendingIntent);
        }
    }

    public void cancelAgendaEndAlarm() {
        Intent intent = new Intent(ACTION_AGENDA_END);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mLauncherContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(pendingIntent);
    }

}
