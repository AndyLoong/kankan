package com.gionee.demo.launcher.yourpage.manager;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.gionee.yourspage.cardinterface.IGioneeCardViewInterface;

/**
 * Created by chengzhen on 16-8-16.
 */
public class TitleCardManager implements IGioneeCardViewInterface {
    private TextView mTitle;
    private Context mLauncherContext;
    private Context mContext;

    public TitleCardManager(Context launcherContext, Context context){
        mContext = context;
        mLauncherContext = launcherContext;

        mTitle = new TextView(context);
        mTitle.setText("This is title");
        mTitle.setTextColor(Color.RED);
    }

    @Override
    public View getCardView(Context context) {
        return mTitle;
    }

    @Override
    public void init(Context context) {

    }

    @Override
    public void onAdd() {

    }

    @Override
    public void onRemove() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public String getCardViewName() {
        return null;
    }

    @Override
    public void setNetWorkAuthority(boolean b) {

    }

    @Override
    public void setCardFilePath(String s) {

    }

    @Override
    public void setMaxSize(int i, int i1) {

    }

    @Override
    public void setAllowInvalidate(boolean b) {

    }
}
