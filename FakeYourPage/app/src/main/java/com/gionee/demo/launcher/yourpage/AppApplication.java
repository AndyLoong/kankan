package com.gionee.demo.launcher.yourpage;

import android.app.Application;
import android.content.Context;

/**
 * Created by chengzhen on 16-8-18.
 */
public class AppApplication extends Application {
    private static AppApplication mInstance;

    public AppApplication() {
        mInstance = this;
    }

    public static Context getContext() {
        return mInstance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
