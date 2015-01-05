package com.nl.clubbook.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by User on 31.12.2014.
 */
public class DisableSwipingViewPager extends ViewPager {

    private boolean isSwipeEnabled = true;

    public DisableSwipingViewPager(Context context) {
        super(context);
    }

    public DisableSwipingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isSwipeEnabled) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }

    public boolean isSwipeEnabled() {
        return isSwipeEnabled;
    }

    public void setSwipeEnabled(boolean isSwipeEnabled) {
        this.isSwipeEnabled = isSwipeEnabled;
    }
}
