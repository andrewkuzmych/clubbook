package com.nl.clubbook.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.nl.clubbook.datasource.UserPhotoDto;
import com.nl.clubbook.fragment.ImageViewFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.util.List;

/**
 * Created by Volodymyr on 13.08.2014.
 */
public class UserAvatarPagerAdapter extends FragmentStatePagerAdapter {

    private List<UserPhotoDto> mUrls;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private ImageLoadingListener mAnimateFirstListener;

    public UserAvatarPagerAdapter(FragmentManager fm, List<UserPhotoDto> urls, ImageLoader imageLoader, DisplayImageOptions displayOptions,
                                  ImageLoadingListener animateFirstListener) {
        super(fm);

        mUrls = urls;
        mImageLoader = imageLoader;
        mOptions = displayOptions;
        mAnimateFirstListener = animateFirstListener;
    }

    @Override
    public Fragment getItem(int position) {
        return ImageViewFragment.newInstance(mImageLoader, mOptions, mAnimateFirstListener, mUrls.get(position).getUrl());
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

    public void updateData(List<UserPhotoDto> urls) {
        mUrls = urls;
        notifyDataSetChanged();
    }
}
