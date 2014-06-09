package com.nl.clubbook.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.helper.SessionManager;
import com.sromku.simple.fb.SimpleFacebook;

public class HomeFragment extends BaseFragment {

    private FragmentTabHost tabHost;
    private SeekBar distance;
    private String TAB_CLUBS = "Clubs";
    private String TAB_PROFILES = "Profiles";
    //private TabHost tabHost;

    public HomeFragment()
    {

    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        tabHost = (FragmentTabHost)rootView.findViewById(R.id.tabHost);

        //tabHost
        //tabHost = new FragmentTabHost(getActivity());
        tabHost.setup(getActivity(), getChildFragmentManager(), R.id.tabcontent);
        //tabHost.setup();
        //ClubsFragment cf = (ClubsFragment) getChildFragmentManager().findFragmentByTag("Clubs");

        Bundle arg1 = new Bundle();
        arg1.putInt("Arg for Frag1", 1);
        View clubView=inflater.inflate(R.layout.tab_item,null);
        ((TextView)clubView.findViewById(R.id.text)).setText("Clubs (34)");
        tabHost.addTab(tabHost.newTabSpec(TAB_CLUBS).setIndicator(clubView), ClubsFragment.class, arg1);
        //tabHost.addTab(clubs_ts, ClubsFragment.class, arg1);

        Bundle arg2 = new Bundle();
        arg2.putInt("Arg for Frag2", 2);
        View profileView=inflater.inflate(R.layout.tab_item,null);
        ((TextView)profileView.findViewById(R.id.text)).setText("Profiles (456)");
        tabHost.addTab(tabHost.newTabSpec(TAB_PROFILES).setIndicator(profileView), ProfilesFragmant.class, arg1);

        distance = (SeekBar) rootView.findViewById(R.id.distance);
        distance.setMax(9);
        distance.incrementProgressBy(1);
        distance.setProgress(SessionManager.DEFOULT_DISTANCE);

/*        ClubsFragment cf = (ClubsFragment) getChildFragmentManager().findFragmentByTag("Clubs");
        cf.loadData(String.valueOf(convertToKm(defoult_distance)));*/

        final TextView distance_text = (TextView) rootView.findViewById(R.id.distance_text);
        distance_text.setText(convertToKm(SessionManager.DEFOULT_DISTANCE) + " " + getString(R.string.km));

        distance.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;
                    int km = 0;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                        km = convertToKm(progresValue);
                        progress = progresValue;
                        distance_text.setText(String.valueOf(km) + " " + getString(R.string.km));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Do something here,
                        //if you want to do anything at the start of
                        // touching the seekbar
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        int tab  = tabHost.getCurrentTab();
                        if (tab == 0) {
                            ClubsFragment cf = (ClubsFragment) getChildFragmentManager().findFragmentByTag("Clubs");
                            cf.loadData(String.valueOf(km));
                        }
                        // Display the value in textview
                        //distance_text.setText(progress + "/" + seekBar.getMax());
                        //distance_text.setText(String.valueOf(progress));
                    }
                });


       /* Button test = (Button) rootView.findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
               //View th =  tabHost.getCurrentTabView();
               View clubView =  tabHost.getTabWidget().getChildTabViewAt(0);
               ((TextView)clubView.findViewById(R.id.text)).setText("Clubs (34) changed");
               View profileView =  tabHost.getTabWidget().getChildTabViewAt(1) ;
               ((TextView)profileView.findViewById(R.id.text)).setText("Profiles (456) changed");

               //tabHost.forceLayout();
               // clubs_ts.setIndicator("CHANGED!");

               ClubsFragment cf = (ClubsFragment) getChildFragmentManager().findFragmentByTag("Clubs");
               cf.loadData();

            }
        });*/

        //return tabHost;
        return rootView;
    }

    public String getSelectedDistance()
    {
        return String.valueOf(convertToKm(distance.getProgress()));
    }

    public void setClubCount(Integer count)
    {
        View clubView =  tabHost.getTabWidget().getChildTabViewAt(0);
        ((TextView)clubView.findViewById(R.id.text)).setText(String.format("Clubs (%s)", count));

    }

    //View clubView =  tabHost.getTabWidget().getChildTabViewAt(0);
    //((TextView)clubView.findViewById(R.id.text)).setText("Clubs (34) changed");

    private int convertToKm(int value)
    {
        int result = 0;
        switch (value) {
            case 0:
                result = 1;
                break;
            case 1:
                result = 2;
                break;
            case 2:
                result = 3;
                break;
            case 3:
                result = 4;
                break;
            case 4:
                result = 5;
                break;
            case 5:
                result = 10;
                break;
            case 6:
                result = 20;
                break;
            case 7:
                result = 30;
                break;
            case 8:
                result = 50;
                break;
            case 9:
                result = 100;
                break;
            default:
                break;
        }
        return result;
    }
}
