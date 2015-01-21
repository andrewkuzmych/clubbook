package com.nl.clubbook.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Volodymyr on 21.01.2015.
 */
public class SquareByWidthImageView extends ImageView {
    public SquareByWidthImageView(Context context) {
        super(context);
    }

    public SquareByWidthImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareByWidthImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SquareByWidthImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
