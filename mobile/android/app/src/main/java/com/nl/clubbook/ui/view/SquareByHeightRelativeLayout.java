package com.nl.clubbook.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by User on 19.08.2014.
 */
public class SquareByHeightRelativeLayout extends RelativeLayout {

    public SquareByHeightRelativeLayout(Context context) {
        super(context);
    }

    public SquareByHeightRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareByHeightRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}
