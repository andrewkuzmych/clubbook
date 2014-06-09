package com.nl.clubbook.helper;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import com.nl.clubbook.R;

/**
 * Created by Andrew on 6/4/2014.
 */
public class UiHelper {
    public static void changeCheckinState(Context context, View view, boolean checkin) {
        Button button = (Button) view;
        if(checkin) {
            button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.checkin_button));
            button.setText(context.getResources().getString(R.string.checkin));
        } else{
            button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.checkout_button));
            button.setText(context.getResources().getString(R.string.checkout));
        }
    }
}
