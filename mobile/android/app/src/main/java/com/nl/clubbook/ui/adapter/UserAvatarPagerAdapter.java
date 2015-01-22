package com.nl.clubbook.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.nl.clubbook.R;
import com.nl.clubbook.model.data.UserPhoto;
import com.nl.clubbook.ui.fragment.ImageViewFragment;

import java.util.List;

/**
 * Created by Volodymyr on 13.08.2014.
 */
public class UserAvatarPagerAdapter extends FragmentStatePagerAdapter {

    private List<UserPhoto> mUrls;

    public UserAvatarPagerAdapter(FragmentManager fm, List<UserPhoto> urls) {
        super(fm);

        mUrls = urls;
    }

    @Override
    public Fragment getItem(int position) {
        return ImageViewFragment.newInstance(mUrls.get(position).getUrl(), R.drawable.ic_avatar_unknown);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public int getCount() {
        return mUrls.size();
    }

    @Override
    public int getItemPosition(Object object){
        return POSITION_NONE;
    }

    public void updateData(List<UserPhoto> urls) {
        mUrls = urls;
        notifyDataSetChanged();
    }
}
