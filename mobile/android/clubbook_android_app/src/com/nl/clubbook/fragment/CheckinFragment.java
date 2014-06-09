package com.nl.clubbook.fragment;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.adapter.CheckinAdapter;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.helper.LocationCheckinHelper;

import java.util.List;

public class CheckinFragment extends BaseFragment {
    ListView club_list;

    public CheckinFragment()
    {

    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_checkin, container, false);
        club_list = (ListView) rootView.findViewById(R.id.club_list_checkin);
         
        return rootView;
    }

    public void onStart() {
        super.onStart();
        //String distance =  ((HomeFragment)getParentFragment()).getSelectedDistance();
        loadData("0.5");
    }

    protected void loadData(String distanceKm) {
        //showProgress(getString(R.string.loading));
        DataStore.setContext(getActivity());

        final Context contextThis = getActivity();

        Location currentLocation  = LocationCheckinHelper.getBestLocation(getActivity());

        ((BaseActivity)getActivity()).showProgress("Loading...");

        DataStore.retrievePlaces(distanceKm, String.valueOf(currentLocation.getLatitude()), String.valueOf(currentLocation.getLongitude()), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed)
                {
                    // hideProgress(false);
                    return;
                }
                //hideProgress(true);
                ((BaseActivity)getActivity()).hideProgress(true);

                List<ClubDto> places = (List<ClubDto>) result;

                DataStore.setCheckinAdapter(new CheckinAdapter(contextThis, R.layout.checkin_list_item, places.toArray(new ClubDto[places.size()])));
                //prizes_list = (ListView) findViewById(R.id.events_list);

              /*  DataStore.getPlaceAdapter().sort(new Comparator<ClubDto>() {
                    @Override
                    public int compare(ClubDto lhs, ClubDto rhs) {
                        if (lhs.getDistance() > rhs.getDistance()){
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                });*/

                club_list.setAdapter(DataStore.getCheckinAdapter());


            }
        });
    }

}
