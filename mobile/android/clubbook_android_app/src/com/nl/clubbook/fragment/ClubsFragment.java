package com.nl.clubbook.fragment;

import android.content.Context;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.adapter.ClubsAdapter;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.helper.LocationCheckinHelper;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Andrew on 5/27/2014.
 */
public class ClubsFragment extends BaseFragment {
    ListView club_list;
    private ProgressDialog progressDialog;

    public ClubsFragment()
    {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_clubs, container, false);
        club_list = (ListView) v.findViewById(R.id.club_listview);
        String distance =  ((HomeFragment)getParentFragment()).getSelectedDistance();
        loadData(distance);
        return v;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    public void onActivityCreated (Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    protected void loadData(String distanceKm) {
        //showProgress(getString(R.string.loading));
        DataStore.setContext(getActivity());

        final Context contextThis = getActivity();
        final BaseFragment thisInstance = this;

        Location currentLocation  = LocationCheckinHelper.getBestLocation(getActivity());

        ((BaseActivity)getActivity()).showProgress("Loading...");

        DataStore.retrievePlaces(distanceKm, String.valueOf(currentLocation.getLatitude()), String.valueOf(currentLocation.getLongitude()), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
               /* if (failed)
                {
                   // hideProgress(false);
                    return;
                }*/
                //hideProgress(true);
                ((BaseActivity)getActivity()).hideProgress(true);

                List<ClubDto> places = (List<ClubDto>) result;
                ((HomeFragment)getParentFragment()).setClubCount(places.size());

                DataStore.setPlaceAdapter(new ClubsAdapter(contextThis, R.layout.club_list_item, places.toArray(new ClubDto[places.size()])));
                //prizes_list = (ListView) findViewById(R.id.events_list);

                DataStore.getPlaceAdapter().sort(new Comparator<ClubDto>() {
                    @Override
                    public int compare(ClubDto lhs, ClubDto rhs) {
                        if (lhs.getDistance() > rhs.getDistance()){
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                });

                club_list.setAdapter(DataStore.getPlaceAdapter());

                club_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        String club_id = ((TextView) view.findViewById(R.id.club_id)).getText().toString();
                        SelectedClubFragment fragment = new SelectedClubFragment(thisInstance, club_id);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction mFragmentTransaction  = fragmentManager.beginTransaction();

                        mFragmentTransaction.add(R.id.frame_container, fragment);
                        mFragmentTransaction.addToBackStack(null);
                        mFragmentTransaction.hide(getParentFragment());
                        mFragmentTransaction.commit();
                        //mFragmentTransaction.replace(R.id.frame_container, fragment).commit();

                    }
                });
            }
        });
    }


}