package com.nl.clubbook.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nl.clubbook.R;
import com.nl.clubbook.model.ClubbookPreferences;
import com.nl.clubbook.ui.adapter.YesterdayPlacesAdapter;
import com.nl.clubbook.model.httpclient.HttpClientManager;
import com.nl.clubbook.model.data.Place;
import com.nl.clubbook.utils.L;
import com.nl.clubbook.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Volodymyr on 06.01.2015.
 */
public class YesterdayFragment extends BaseRefreshFragment implements AdapterView.OnItemClickListener {

    private YesterdayPlacesAdapter mPlacesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_yesterday, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sendScreenStatistic(R.string.yesterday_screen);

        initActionBarTitle(getString(R.string.yesterday));
        initView();
        doRefresh(false, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
            actionBar.setTitle(R.string.yesterday);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment = ClubFragment.newInstance(YesterdayFragment.this, mPlacesAdapter.getItem(position));
        openFragment(fragment, ClubFragment.class);
    }

    @Override
    protected void loadData() {
        mSkipNumber = DEFAULT_CLUBS_SKIP;

        doRefresh(true, false);
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        mPlacesAdapter = new YesterdayPlacesAdapter(getActivity(), new ArrayList<Place>());

        final ListView clubList = (ListView) view.findViewById(R.id.listPlaces);
        clubList.addFooterView(mFooterProgress);

        clubList.setAdapter(mPlacesAdapter);
        clubList.setOnItemClickListener(this);
        clubList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE && clubList.getLastVisiblePosition() >= clubList.getCount() - 1) {
                    mSkipNumber += DEFAULT_CLUBS_COUNT;

                    doRefresh(false, true);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    private void doRefresh(boolean isSwipeLayoutRefreshing, boolean isFooterVisible) {
        if(!NetworkUtils.isOn(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("Location Updates", "Google Play services is available.");

        if(isSwipeLayoutRefreshing) {
            setProgressViewsState(true, View.GONE, View.GONE);
        } else if(isFooterVisible) {
            setProgressViewsState(false, View.VISIBLE, View.GONE);
        } else {
            setProgressViewsState(false, View.GONE, View.VISIBLE);
        }

        ClubbookPreferences preferences = ClubbookPreferences.getInstance(getActivity());
        String accessToken = preferences.getAccessToken();
        if(accessToken.isEmpty()) {
            L.i("accessToken is empty");

            setProgressViewsState(false, View.GONE, View.GONE);

            return;
        }

        // retrieve places from server and set distance
        HttpClientManager.getInstance().retrieveYesterdayCheckedInPlaces(accessToken, new HttpClientManager.OnResultReady() {

            @Override
            public void onReady(Object result, boolean failed) {
                if (isDetached() || getActivity() == null) {
                    L.i("fragment_is_detached");
                    return;
                }

                setProgressViewsState(false, View.GONE, View.GONE);

                if (failed) {
                    showToast(R.string.something_went_wrong_please_try_again);
                    return;
                }

                List<Place> places = (List<Place>) result;

                if (mSkipNumber == DEFAULT_CLUBS_SKIP) {
                    mPlacesAdapter.updateData(places);
                } else {
                    mPlacesAdapter.addData(places);
                }
            }
        });
    }
}
