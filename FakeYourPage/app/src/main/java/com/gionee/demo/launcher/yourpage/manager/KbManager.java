package com.gionee.demo.launcher.yourpage.manager;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.gionee.yourspage.cardinterface.IGioneeCardViewInterface;
import com.tencent.readsdk.module.home.main.ReadCardImpl;


public class KbManager implements IGioneeCardViewInterface {
    private static final String TAG = "KbManager";
    private ReadCardImpl mReadCardImpl;

    public KbManager(Context launcherContext, Context context) {
        if (launcherContext == null) {
            Log.d(TAG,"launcherContext is null");
            return;
        }
        mReadCardImpl = new ReadCardImpl();
        init(launcherContext);
        setNetWorkAuthority(true);
        setAllowInvalidate(true);
        mReadCardImpl.setCardFilePath(null);
    }

    @Override
    public View getCardView(Context context) {
        return mReadCardImpl.getCardView(context);
    }

    @Override
    public void init(Context context) {
        mReadCardImpl.init(context);
    }

    @Override
    public void onAdd() {
        mReadCardImpl.onAdd();
    }

    @Override
    public void onRemove() {
        mReadCardImpl.onRemove();
    }

    @Override
    public void onResume() {
        mReadCardImpl.onResume();
    }

    @Override
    public void onPause() {
        mReadCardImpl.onPause();
    }

    @Override
    public String getCardViewName() {
        return null;
    }

    @Override
    public void setNetWorkAuthority(boolean b) {
        mReadCardImpl.setNetWorkAuthority(b);
    }

    @Override
    public void setCardFilePath(String s) {
        mReadCardImpl.setCardFilePath(s);

    }

    @Override
    public void setMaxSize(int i, int i1) {
        mReadCardImpl.setMaxSize(i, i1);
    }

    @Override
    public void setAllowInvalidate(boolean b) {
        mReadCardImpl.setAllowInvalidate(b);
    }
}
