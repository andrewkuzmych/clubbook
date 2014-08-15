package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.PhotoPagerAdapter;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.JSONConverter;
import com.nl.clubbook.ui.view.ViewPagerBulletIndicatorView;
import com.nl.clubbook.utils.L;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by Volodymyr on 15.08.2014.
 */
public class ClubInfoFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private static final String ARG_JSON_CLUB = "ARG_JSON_CLUB";

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();

    private ViewPagerBulletIndicatorView mBulletIndicator;

    public static Fragment newInstance(String jsonClub) {
        Fragment fragment = new ClubInfoFragment();

        Bundle args = new Bundle();
        args.putString(ARG_JSON_CLUB, jsonClub);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_club_info, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initLoader();
        initView();
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public void onPageSelected(int position) {
        mBulletIndicator.setSelectedView(position);
    }

    private void initView() {
        View view = getView();
        if(view == null || isDetached() || getActivity() == null) {
            return;
        }

        Bundle args = getArguments();
        if(args == null) {
            L.i("args = null");
            return;
        }

        String jsonClub = args.getString(ARG_JSON_CLUB);
        ClubDto club = JSONConverter.newClub(jsonClub);
        if(club == null) {
            L.i("club = null");
            return;
        }

        fillView(view, club);
    }

    private void initLoader() {
        mImageLoader = ImageLoader.getInstance();
        mOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_list_image)
                .showImageOnFail(R.drawable.default_list_image)
                .cacheInMemory()
                .cacheOnDisc()
                .build();
    }

    private void fillView(View view, ClubDto club) {
        //TODO

        initViewPager(view, club.getPhotos());
    }

    private void initViewPager(View view, List<String> photos) {
        if(photos == null) {
            L.i("photos == null");
            return;
        }
        mBulletIndicator = (ViewPagerBulletIndicatorView)view.findViewById(R.id.indicatorPhotos);
        mBulletIndicator.setBulletViewCount(photos.size());

        ViewPager pagerImage = (ViewPager) view.findViewById(R.id.pagerPhoto);
        PhotoPagerAdapter adapter = new PhotoPagerAdapter(getChildFragmentManager(), photos, mImageLoader, mOptions, animateFirstListener);
        pagerImage.setAdapter(adapter);

//        for(int i = 0; i < userPhotoList.size(); i++) {
//            UserPhotoDto userPhoto = userPhotoList.get(i);
//            if(userPhoto.getIsAvatar()) {
//                pagerImage.setCurrentItem(i, false);
//                mBulletIndicator.setSelectedView(i);
//            }
//        }

        pagerImage.setOnPageChangeListener(this);
    }
}
