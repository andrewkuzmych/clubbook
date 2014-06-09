package com.nl.clubbook.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import com.nl.clubbook.activity.MainActivity;

/**
 * Created by Andrew on 6/8/2014.
 */
public class BaseFragment extends Fragment {
    BaseFragment provoiusFregment;

    public BaseFragment()
    {}

    public BaseFragment( BaseFragment provoiusFregment)
    {
        this.provoiusFregment = provoiusFregment;
    }
    @Override
    public void onDestroyView () {
        super.onDestroyView();

        try {
            //FragmentManager fm =  getActivity().getSupportFragmentManager();
            //SelectedClubFragment fragment = (SelectedClubFragment) fm.findFragmentById(R.id.frame_container);

            //FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            //ft.remove(this).commit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void backButtonWasPressed() {
        ((MainActivity)getActivity()).setCurrentFragment(provoiusFregment);
    }


    @Override
    public void onStart()
    {
        super.onStart();
        ((MainActivity)getActivity()).setCurrentFragment(this);
    }
}
