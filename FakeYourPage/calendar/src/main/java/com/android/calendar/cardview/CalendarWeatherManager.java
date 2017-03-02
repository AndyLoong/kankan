package com.android.calendar.cardview;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by xiaozhilong on 2/21/17.
 */

public class CalendarWeatherManager {
    private static final boolean DEBUG = true;
    private static final String TAG = "CalendarWeatherManager";

    private Context mLauncherContext;
    private TextView mWeatherContentView;

    public CalendarWeatherManager(Context launcherContext, TextView weatherContent) {
        initMember(launcherContext);
        initView(weatherContent);
        initListener();
    }

    private void initMember(Context launcherContext) {
        mLauncherContext = launcherContext;
    }

    private void initView(TextView weatherContent) {
        mWeatherContentView = weatherContent;
    }

    private void initListener() {
        mWeatherContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG) {
                    Log.d(TAG, "launch Weather");
                }
                CalendarUtils.launchWeather(mLauncherContext);
            }
        });
    }

    public void updateContent(String content) {
        mWeatherContentView.setText(content);
    }
}
