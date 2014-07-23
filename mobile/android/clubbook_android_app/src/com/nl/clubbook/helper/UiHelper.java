package com.nl.clubbook.helper;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.nl.clubbook.R;

/**
 * Created by Andrew on 6/4/2014.
 */
public class UiHelper {
    public static void changeCheckinState(Context context, View view, boolean checkin) {
        Button button = (Button) view;
        if (checkin) {
            button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.checkin_button));
            button.setText(context.getResources().getString(R.string.checkin));
        } else {
            button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.checkout_button));
            button.setText(context.getResources().getString(R.string.checkout));
        }
    }

    public static Spinner createGenderSpinner(Spinner gender_spinner, Context context, String activeItem) {
        final GenderPair items[] = new GenderPair[2];
        items[0] = new GenderPair("Male", "male");
        items[1] = new GenderPair("Female", "female");
        ArrayAdapter<GenderPair> adapter = new ArrayAdapter<GenderPair>(
                        context,
                        android.R.layout.simple_spinner_item,
                        items);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        gender_spinner.setAdapter(adapter);

        if(activeItem.equalsIgnoreCase("male")) {
            gender_spinner.setSelection(adapter.getPosition(items[0]));
        } else {
            gender_spinner.setSelection(adapter.getPosition(items[1]));
        }

        return gender_spinner;
    }

    public static class GenderPair {
        String text;
        String value;

        public GenderPair(String text, String value) {
            this.text = text;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String toString() {
            return text;
        }
    }
}
