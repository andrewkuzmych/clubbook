package com.nl.clubbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.datasource.CheckInUserDto;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.utils.L;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 6/2/2014.
 */
public class ProfileAdapter extends BaseAdapter {
    public static final int MODE_GRID = 7777;
    public static final int MODE_LIST = 8888;

    private List<CheckInUserDto> mUsers;
    private String mCurrentUserId;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();
    private LayoutInflater mInflater;
    private int mMode = MODE_GRID;


    public ProfileAdapter(Context context, List<CheckInUserDto> users, String currentUserId, int mode) {
        mInflater = LayoutInflater.from(context);
        mCurrentUserId = currentUserId;

        mUsers = users;
        mMode = mode;

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_avatar_missing)
                .showImageForEmptyUri(R.drawable.ic_avatar_missing)
                .showImageOnFail(R.drawable.ic_avatar_unknown)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            if(mMode == MODE_GRID) {
                row = mInflater.inflate(R.layout.item_grid_profile, parent, false);
            } else {
                row = mInflater.inflate(R.layout.item_list_checked_in_users, parent, false);
            }

            holder = new ViewHolder();
            holder.imgAvatar = (ImageView) row.findViewById(R.id.imgAvatar);
            holder.txtFriendIndicator = (TextView) row.findViewById(R.id.txtFriendIndicator);
            holder.userId = row.findViewById(R.id.userId);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        fillView(holder, mUsers.get(position));

        return row;
    }

    private void fillView(ViewHolder holder, CheckInUserDto item) {
        String imageUrl = ImageHelper.getUserListAvatar(item.getAvatarUrl());
        holder.imgAvatar.setTag(imageUrl);
        imageLoader.displayImage(imageUrl, holder.imgAvatar, options, animateFirstListener);

        holder.userId.setTag(item.getId());

        if(item.isFriend()) {
            holder.txtFriendIndicator.setVisibility(View.GONE);
        } else if(mCurrentUserId.equals(item.getId())) {
            holder.txtFriendIndicator.setVisibility(View.GONE);
        } else {
            holder.txtFriendIndicator.setVisibility(View.VISIBLE);
        }
    }

    static class ViewHolder {
        View userId;
        TextView txtFriendIndicator;
        ImageView imgAvatar;
    }
}
