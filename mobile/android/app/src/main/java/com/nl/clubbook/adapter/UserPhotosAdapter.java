package com.nl.clubbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nl.clubbook.R;
import com.nl.clubbook.datasource.UserPhotoDto;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by User on 12.09.2014.
 */
public class UserPhotosAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<UserPhotoDto> mUserPhotos;

    public UserPhotosAdapter(Context context, List<UserPhotoDto> userPhotos) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mUserPhotos = userPhotos;
    }

    @Override
    public int getCount() {
        return mUserPhotos.size();
    }

    @Override
    public UserPhotoDto getItem(int position) {
        return mUserPhotos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if(row == null) {
            row = mInflater.inflate(R.layout.item_list_profile_image, null);
            holder = new ViewHolder();

            holder.imgPhoto = (ImageView) row.findViewById(R.id.imgAvatar);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Picasso.with(mContext).load(mUserPhotos.get(position).getUrl()).error(R.drawable.ic_avatar_unknown).into(holder.imgPhoto);

        return row;
    }

    public void addNewImage(UserPhotoDto photo, int position) {
        if(photo != null && position >= 0) {
            mUserPhotos.add(position, photo);
            notifyDataSetChanged();
        }
    }

    public void removePhoto(UserPhotoDto photo) {
        if(photo != null) {
            mUserPhotos.remove(photo);
            notifyDataSetChanged();
        }
    }

    private class ViewHolder {
        ImageView imgPhoto;
    }
}
