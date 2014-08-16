package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nl.clubbook.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * Created by Volodymyr on 13.08.2014.
 */
public class ImageViewFragment extends Fragment {

    public static final String ARG_IMAGE_URL = "ARG_IMAGE_URL";

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private ImageLoadingListener animateFirstListener;

    public static Fragment newInstance(ImageLoader imageLoader, DisplayImageOptions displayOptions,
                                       ImageLoadingListener animateFirstListener, String url) {
        ImageViewFragment fragment = new ImageViewFragment();

        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, url);
        fragment.setArguments(args);

        fragment.setImageLoader(imageLoader);
        fragment.setDisplayOptions(displayOptions);
        fragment.setAnimateFirstListener(animateFirstListener);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_image, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        if(view == null) {
            return;
        }

        ImageView imgAvatar = (ImageView) view.findViewById(R.id.imgView);

        String url = getArguments().getString(ARG_IMAGE_URL);
        if(url != null) {
            mImageLoader.displayImage(url, imgAvatar, mOptions, animateFirstListener);
        }
    }

    public void setImageLoader(ImageLoader mImageLoader) {
        this.mImageLoader = mImageLoader;
    }

    public void setDisplayOptions(DisplayImageOptions mOptions) {
        this.mOptions = mOptions;
    }

    public void setAnimateFirstListener(ImageLoadingListener animateFirstListener) {
        this.animateFirstListener = animateFirstListener;
    }
}
