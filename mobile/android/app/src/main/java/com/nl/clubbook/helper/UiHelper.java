package com.nl.clubbook.helper;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.nl.clubbook.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 6/4/2014.
 */
public class UiHelper {
    public static void changeCheckinState(Context context, View view, boolean checkin) {
        TextView button = (TextView) view;
        if (checkin) {
            view.setBackgroundResource(R.drawable.checkin_button);
            button.setText(context.getResources().getString(R.string.checkin));
        } else {
            button.setBackgroundResource(R.drawable.checkout_button);
            button.setText(context.getResources().getString(R.string.checkout));
        }
    }

    public static Spinner createGenderSpinner(Spinner spinGender, Context context, String activeItem) {
        final TextValuePair items[] = new TextValuePair[2];
        items[0] = new TextValuePair("Male", "male");
        items[1] = new TextValuePair("Female", "female");
        ArrayAdapter<TextValuePair> adapter = new ArrayAdapter<TextValuePair>(
                        context,
                        R.layout.simple_spinner_item,
                        items);

        adapter.setDropDownViewResource(R.layout.simple_spinner_item);

        spinGender.setAdapter(adapter);

        if(activeItem.equalsIgnoreCase("male")) {
            spinGender.setSelection(adapter.getPosition(items[0]));
        } else {
            spinGender.setSelection(adapter.getPosition(items[1]));
        }

        return spinGender;
    }

    public static Spinner createCountrySpinner(Spinner spinCountry, Context context, String activeItem) {
        Resources res = context.getResources();
        String[] countries = res.getStringArray(R.array.countries_array);
        final List<TextValuePair> items =  new ArrayList<TextValuePair>();
        for(String country : countries) {
            items.add(new TextValuePair(country, country.toLowerCase()));
        }

        ArrayAdapter<TextValuePair> adapter = new ArrayAdapter<TextValuePair>(
                        context,
                        R.layout.simple_spinner_item,
                        items);

        adapter.setDropDownViewResource(R.layout.simple_spinner_item);

        spinCountry.setAdapter(adapter);

        for(TextValuePair country : items){
            if(country.getValue().equalsIgnoreCase(activeItem)) {
                spinCountry.setSelection(adapter.getPosition(country));
            }
        }

        return spinCountry;
    }

    public static class TextValuePair {
        String text;
        String value;

        public TextValuePair(String text, String value) {
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
