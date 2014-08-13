package com.nl.clubbook.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by User on 13.08.2014.
 */
public class CustomViewPager extends ViewPager {


    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View view, boolean checkV, int dx, int x, int y) {
        if(view != this || !(view instanceof ViewPager)) {
            return true;
        }
        return super.canScroll(view, checkV, dx, x, y);
    }
}
