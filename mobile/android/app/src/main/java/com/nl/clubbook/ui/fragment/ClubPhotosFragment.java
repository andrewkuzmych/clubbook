package com.nl.clubbook.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nl.clubbook.R;
import com.nl.clubbook.model.data.Place;
import com.nl.clubbook.ui.activity.ImagesGalleryActivity;
import com.nl.clubbook.ui.adapter.PhotoGridAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Volodymyr on 27.01.2015.
 */
public class ClubPhotosFragment extends BaseFragment implements AdapterView.OnItemClickListener{

    private List<String> mPhotos;

    public static Fragment newInstance(List<String> photos) {
        ClubPhotosFragment fragment = new ClubPhotosFragment();

        fragment.setPhotos(photos);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_club_photos, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), ImagesGalleryActivity.class);
        intent.putStringArrayListExtra(ImagesGalleryActivity.EXTRA_PHOTOS_URLS, (ArrayList) mPhotos);
        intent.putExtra(ImagesGalleryActivity.EXTRA_SELECTED_PHOTO, position);
        startActivity(intent);
    }

    private void setPhotos(List<String> photos) {
        mPhotos = photos;
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        if(mPhotos == null || mPhotos.isEmpty()) {
            view.findViewById(R.id.txtNoPhotos).setVisibility(View.VISIBLE);
            return;
        }

        GridView gridPhotos = (GridView) view.findViewById(R.id.gridPhotos);
        PhotoGridAdapter adapter = new PhotoGridAdapter(getActivity(), mPhotos);
        gridPhotos.setAdapter(adapter);
        gridPhotos.setOnItemClickListener(this);
    }
}
