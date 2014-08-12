package com.nl.clubbook.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.nl.clubbook.helper.FontHelper;

/**
 * Created by Volodymyr on 12.08.2014.
 */
public class TitilliumBoldButton extends Button {

    public TitilliumBoldButton(Context context) {
        super(context);
        setTypeface(getFont(context));
    }

    public TitilliumBoldButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(getFont(context));
    }

    public TitilliumBoldButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(getFont(context));
    }

    private Typeface getFont(Context context) {
        return FontHelper.getInstance(context).getTitilliumWebBold();
    }
}
