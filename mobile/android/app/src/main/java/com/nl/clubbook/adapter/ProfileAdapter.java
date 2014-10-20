package com.nl.clubbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.datasource.CheckInUser;
import com.nl.clubbook.helper.ImageHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Andrew on 6/2/2014.
 */
public class ProfileAdapter extends BaseAdapter {
    public static final int MODE_GRID = 7777;
    public static final int MODE_LIST = 8888;

    private Context mContext;
    private List<CheckInUser> mUsers;
    private LayoutInflater mInflater;
    private int mMode = MODE_GRID;


    public ProfileAdapter(Context context, List<CheckInUser> users, int mode) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mUsers = users;
        mMode = mode;
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

    private void fillView(ViewHolder holder, CheckInUser item) {
        String imageUrl = ImageHelper.getUserListAvatar(item.getAvatarUrl());
        Picasso.with(mContext).load(imageUrl).error(R.drawable.ic_avatar_unknown).into(holder.imgAvatar);

        if(item.isFriend()) {
            holder.txtFriendIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.txtFriendIndicator.setVisibility(View.GONE);
        }

        holder.userId.setTag(item.getId());
    }

    static class ViewHolder {
        View userId;
        TextView txtFriendIndicator;
        ImageView imgAvatar;
    }
}
