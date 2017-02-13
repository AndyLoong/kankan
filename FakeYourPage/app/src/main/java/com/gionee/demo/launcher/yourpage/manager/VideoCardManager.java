package com.gionee.demo.launcher.yourpage.manager;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.android.music.cardview.CardViewLayout;
import com.gionee.yourspage.cardinterface.IGioneeCardViewInterface;

/**
 * Created by chengzhen on 16-8-16.
 */
public class VideoCardManager implements IGioneeCardViewInterface {
    private CardViewLayout mCard;
    private Context mLauncherContext;
    private Context mContext;

    public VideoCardManager(Context launcherContext, Context context){
        mContext = context;
        mLauncherContext = launcherContext;

        mCard = new CardViewLayout(context);
        init(launcherContext);
        setNetWorkAuthority(true);
        setAllowInvalidate(true);
        mCard.setCardFilePath(null);
    }

    @Override
    public View getCardView(Context context) {
        return mCard.getCardView(context);
    }

    @Override
    public void init(Context context) {
        mCard.init(context);
    }

    @Override
    public void onAdd() {
        mCard.onAdd();
    }

    @Override
    public void onRemove() {
        mCard.onRemove();
    }

    @Override
    public void onResume() {
        mCard.onResume();
    }

    @Override
    public void onPause() {
        mCard.onPause();
    }

    @Override
    public String getCardViewName() {
        return mCard.getCardViewName();
    }

    @Override
    public void setNetWorkAuthority(boolean b) {
        mCard.setNetWorkAuthority(b);
    }

    @Override
    public void setCardFilePath(String s) {
        mCard.setCardFilePath(s);
    }

    @Override
    public void setMaxSize(int i, int i1) {
        mCard.setMaxSize(i, i1);
    }

    @Override
    public void setAllowInvalidate(boolean b) {
        mCard.setAllowInvalidate(b);
    }
}
