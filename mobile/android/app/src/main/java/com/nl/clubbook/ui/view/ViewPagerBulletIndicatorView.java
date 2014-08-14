package com.nl.clubbook.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.nl.clubbook.R;

/**
 * Created by Volodymyr on 14.08.2014.
 */
public class ViewPagerBulletIndicatorView extends LinearLayout {

    public ViewPagerBulletIndicatorView(Context context) {
        super(context);
    }

    public ViewPagerBulletIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewPagerBulletIndicatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setBulletViewCount(int count) {
        for(int i = 0; i < count; i ++) {
            addView(getBulletView());
        }

        setSelectedView(0);
    }

    private View getBulletView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.view_bullet, null);
    }

    public void setSelectedView(int position) {
        if(position > getChildCount() || position < 0) {
            return;
        }

        for(int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            View bulletIcon = view.findViewById(R.id.imgIndicatorBullet);

            if(bulletIcon == null) {
                return;
            }

            if(i != position) {
                bulletIcon.setBackgroundResource(R.drawable.bg_bullet_unselected);
            } else {
                bulletIcon.setBackgroundResource(R.drawable.bg_bullet_selected);
            }
        }
    }
}