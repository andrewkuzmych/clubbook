package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nl.clubbook.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Volodymyr on 13.08.2014.
 */
public class ImageViewFragment extends Fragment {

    public static final String ARG_IMAGE_URL = "ARG_IMAGE_URL";
    public static final String ARG_DEFAULT_IMAGE_RES = "ARG_DEFAULT_IMAGE_RES";

    public static Fragment newInstance(String url, int defaultPhotoRes) {
        ImageViewFragment fragment = new ImageViewFragment();

        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, url);
        args.putInt(ARG_DEFAULT_IMAGE_RES, defaultPhotoRes);
        fragment.setArguments(args);

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

        Bundle args = getArguments();
        String url = args.getString(ARG_IMAGE_URL);
        int defaultPhotoRes = args.getInt(ARG_DEFAULT_IMAGE_RES);
        if(url != null) {
            Picasso.with(getActivity()).load(url).error(defaultPhotoRes).into(imgAvatar);
        }
    }
}
