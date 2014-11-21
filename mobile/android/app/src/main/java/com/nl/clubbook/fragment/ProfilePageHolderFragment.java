package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.ProfilePagerAdapter;
import com.nl.clubbook.datasource.User;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.utils.UIUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Volodymyr on 14.11.2014.
 */
public class ProfilePageHolderFragment extends BaseInnerFragment implements ViewPager.OnPageChangeListener {

    public static final String ARG_POSITION = "ARG_POSITION";

    private List<User> mUsers;

    public static Fragment newInstance(@NotNull Fragment targetFragment, List<User> users, int currentPosition) {
        ProfilePageHolderFragment fragment = new ProfilePageHolderFragment();
        fragment.setTargetFragment(targetFragment, 0);
        fragment.setUser(users);

        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, currentPosition);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_profile_page_holder, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initTarget();

        initActionBarTitle(getString(R.string.user_profile));
        initView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            initActionBarTitle(getString(R.string.user_profile));
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onPageSelected(int position) {
        loadActionBarPhoto(position);
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        int currentPosition = getArguments().getInt(ARG_POSITION, 0);

        ProfilePagerAdapter adapter = new ProfilePagerAdapter(getChildFragmentManager(), mUsers);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition, false);
        viewPager.setOnPageChangeListener(this);

        loadActionBarPhoto(currentPosition);
    }

    private void setUser(List<User> newUsers) {
        mUsers = newUsers;
    }

    private void loadActionBarPhoto(int position) {
        User user = mUsers.get(position);
        if(user != null && !TextUtils.isEmpty(user.getAvatar())) {
            UIUtils.loadPhotoToActionBar((ActionBarActivity) getActivity(), ImageHelper.getUserListAvatar(user.getAvatar()), mTarget);
        }
    }
}
