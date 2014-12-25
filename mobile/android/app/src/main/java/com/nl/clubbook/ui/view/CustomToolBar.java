package com.nl.clubbook.ui.view;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

/**
 * Created by Volodymyr on 23.12.2014.
 */
public class CustomToolBar extends Toolbar {

    private boolean mIsInBackMode = false;

    public CustomToolBar(Context context) {
        super(context);
    }

    public CustomToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isInBackMode() {
        return mIsInBackMode;
    }

    public void setIsInBackMode(boolean mIsInBackMode) {
        this.mIsInBackMode = mIsInBackMode;
    }
}
