package com.nl.clubbook.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nl.clubbook.R;
import com.nl.clubbook.fragment.ImageViewFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * Created by Volodymyr on 15.08.2014.
 */
public class ClubPhotoPagerAdapter extends FragmentPagerAdapter {

    private String[] mUrls;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private ImageLoadingListener mAnimateFirstListener = new SimpleImageLoadingListener();

    public ClubPhotoPagerAdapter(FragmentManager fm, String[] urls) {
        super(fm);

        mUrls = urls;
        mImageLoader = ImageLoader.getInstance();
        mOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_club_avatar_default)
                .showImageOnFail(R.drawable.ic_club_avatar_default)
                .cacheInMemory()
                .cacheOnDisc()
                .build();
    }

    @Override
    public Fragment getItem(int position) {
        return ImageViewFragment.newInstance(mImageLoader, mOptions, mAnimateFirstListener, mUrls[position]);
    }

    @Override
    public int getCount() {
        return mUrls.length;
    }
}
