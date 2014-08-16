package com.nl.clubbook.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nl.clubbook.fragment.ImageViewFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.util.List;

/**
 * Created by User on 15.08.2014.
 */
public class PhotoPagerAdapter extends FragmentPagerAdapter {

    private List<String> mUrls;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private ImageLoadingListener mAnimateFirstListener;

    public PhotoPagerAdapter(FragmentManager fm, List<String> urls, ImageLoader imageLoader, DisplayImageOptions displayOptions,
                             ImageLoadingListener animateFirstListener) {
        super(fm);

        mUrls = urls;
        mImageLoader = imageLoader;
        mOptions = displayOptions;
        mAnimateFirstListener = animateFirstListener;
    }

    @Override
    public Fragment getItem(int position) {
        return ImageViewFragment.newInstance(mImageLoader, mOptions, mAnimateFirstListener, mUrls.get(position));
    }

    @Override
    public int getCount() {
        return mUrls.size();
    }
}
