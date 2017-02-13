package com.gionee.demo.launcher.yourpage;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.gionee.demo.launcher.yourpage.callbacks.IYourPageInterface;
import com.gionee.demo.launcher.yourpage.manager.KbManager;
import com.gionee.yourspage.cardinterface.IGioneeCardViewInterface;

/**
 * Created by chengzhen on 16-8-16.
 */
public class YourPageView extends ViewGroup implements IYourPageInterface {
    private static final String TAG = YourPageView.class.getSimpleName();

    private IGioneeCardViewInterface mCardView;

    /**
     *
     * @param launcherContext   伪桌面Context
     * @param context           伪桌面看看Context
     */
    public YourPageView(Context launcherContext, Context context) {
        super(context);
        init(launcherContext, context);
    }

    private void init(Context launcherContext, Context context) {
        // 初始化YourPageView布局
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);
        setVerticalScrollBarEnabled(false);

        //=========卡片开始加载时的生命周期=========
        /**
         * 实例化卡片
         * init
         * setXXX
         * onAdd
         */
        Context applicationContext = launcherContext.getApplicationContext();

        // TODO 第三方卡片--影影
//        mCardView = new VideoCardManager(applicationContext, context);

        // TODO 代理第三方卡片--高德地图
//        mCardView = new AmapCardManager(applicationContext, context);

        // TODO 代理第三方卡片--天天快报
        mCardView = new KbManager(applicationContext, context);

        // getCardView
        View card = mCardView.getCardView(context);

        LayoutParams subLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(mCardView.getCardView(context), subLayoutParams);

        // onResume
        mCardView.onAdd();
        mCardView.onResume();
        //=========卡片开始加载时的生命周期=========
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        // TODO 假设只有一个卡片
        int height = 0;
        if(count > 0){
            Log.d(TAG, "count = " + count);
            getChildAt(0).measure(widthMeasureSpec, heightMeasureSpec);
            height = getChildAt(0).getMeasuredHeight();
        }
        setMeasuredDimension(getContext().getResources().getDisplayMetrics().widthPixels, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        // TODO 假设只有一个卡片
        if(count > 0){
            Log.d(TAG, "count = " + count);
            View child = getChildAt(0);
            Log.d(TAG, "width = " + child.getMeasuredWidth());
            Log.d(TAG, "height = " + child.getMeasuredHeight());

            int height = child.getMeasuredHeight();
            int width = child.getMeasuredWidth();
            int childLeft = 0;
            int childRight = childLeft + width;
            int childTop = 0;
            int childBottom = height + childTop;
            child.layout(childLeft, childTop, childRight, childBottom);
            Log.d(TAG, childLeft + " " + childTop + " " + childRight + " " + childBottom);
        }
    }

    @Override
    public void onResume() {
        mCardView.onResume();
    }

    @Override
    public void onPause() {
        mCardView.onPause();
    }

    @Override
    public void onDestroy() {
        mCardView.onRemove();
    }
}
