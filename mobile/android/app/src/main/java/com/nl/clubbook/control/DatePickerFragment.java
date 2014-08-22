package com.nl.clubbook.control;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Andrew on 5/26/2014.
 */
public class DatePickerFragment extends DialogFragment {

    public static final String TAG = "DatePickerFragment";

    public static final String ARG_YEAR = "ARG_YEAR";
    public static final String ARG_MONTH = "ARG_MONTH";
    public static final String ARG_DAY = "ARG_DAY";

    private DatePickerDialog.OnDateSetListener mOnDateSet;

    public static DialogFragment newInstance(int year, int month, int day, DatePickerDialog.OnDateSetListener onDateSet) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setDateSetCallBack(onDateSet);

        Bundle args = new Bundle();
        args.putInt(ARG_YEAR, year);
        args.putInt(ARG_MONTH, month);
        args.putInt(ARG_DAY, day);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();

        int year = args.getInt(ARG_YEAR);
        int month = args.getInt(ARG_MONTH);
        int day = args.getInt(ARG_DAY);

        return new DatePickerDialog(getActivity(), mOnDateSet, year, month, day);
    }

    private void setDateSetCallBack(DatePickerDialog.OnDateSetListener onDate) {
        mOnDateSet = onDate;
    }
}
