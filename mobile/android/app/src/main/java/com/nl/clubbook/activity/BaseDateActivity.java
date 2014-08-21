package com.nl.clubbook.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.DatePicker;
import android.widget.EditText;

import com.nl.clubbook.R;
import com.nl.clubbook.control.DatePickerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Volodymyr on 21.08.2014.
 */
public class BaseDateActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    private final String DEFAULT_DATE = "1990-01-01";

    protected final SimpleDateFormat mDisplayFormat = new SimpleDateFormat("dd MMM. yyyy", Locale.getDefault());
    protected final SimpleDateFormat mServerFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    protected final SimpleDateFormat mParseFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    protected Date mBirthDate;
    protected Calendar mCalendar = Calendar.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parseBirthDate(DEFAULT_DATE);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        mBirthDate.setTime(mCalendar.getTimeInMillis());

        EditText editBirthDate = (EditText) findViewById(R.id.editBirthDate);
        if(editBirthDate == null) {
            throw new IllegalArgumentException("Your Activity should contain editText (id = R.id.editBirthDate)");
        }

        editBirthDate.setText(mDisplayFormat.format(mBirthDate));
    }

    protected void parseBirthDate(String dateToParse) {
        try {
            mBirthDate = mParseFormat.parse(dateToParse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    protected void showDatePicker() {
        mCalendar.setTimeInMillis(mBirthDate.getTime());

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        DialogFragment date = DatePickerFragment.newInstance(year, month, day, BaseDateActivity.this);
        FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.add(date, DatePickerFragment.TAG).commitAllowingStateLoss();
    }
}
