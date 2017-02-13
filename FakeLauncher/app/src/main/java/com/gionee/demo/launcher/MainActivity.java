package com.gionee.demo.launcher;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import dalvik.system.PathClassLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private View mLoadView;

    /** 伪桌面看看的包名 **/
    private static final String YOURPAGE_PACKAGE_NAME = "com.gionee.demo.launcher.yourpage";

    /** 伪桌面看看卡片容器 **/
    private static final String YOURPAGE_VIEW_NAME = "com.gionee.demo.launcher.yourpage.YourPageView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoadView = getView();
        if(mLoadView!=null) {
            setContentView(mLoadView);
        }else{
            Toast.makeText(this, "启动伪桌面看看失败", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        callMethod(mLoadView, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        callMethod(mLoadView, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        callMethod(mLoadView, "onDestroy");
    }

    private View getView() {
        try {
            Context packContext = createPackageContext(YOURPAGE_PACKAGE_NAME, CONTEXT_IGNORE_SECURITY | CONTEXT_INCLUDE_CODE);
            String apkPath = packContext.getApplicationInfo().sourceDir;
            PathClassLoader loader = new PathClassLoader(apkPath, packContext.getClassLoader());
            Class<?> clazz = loader.loadClass(YOURPAGE_VIEW_NAME);
            Constructor constructor = clazz.getConstructor(Context.class, Context.class);
            return (View) constructor.newInstance(this, packContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void callMethod(Object object, String methodName) {
        if (object == null) {
            return;
        }
        try {
            Method method = object.getClass().getMethod(methodName);
            method.invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
