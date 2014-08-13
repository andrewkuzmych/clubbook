package com.nl.clubbook.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.nl.clubbook.R;
import com.nl.clubbook.fragment.ProfileFragment;

/**
 * Created by Volodymyr on 11.08.2014.
 */
public class TestActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_test);

        FragmentTransaction fTransaction = getSupportFragmentManager().beginTransaction();
        fTransaction.replace(R.id.fragmentContainer, new ProfileFragment());
        fTransaction.commitAllowingStateLoss();
    }
}
