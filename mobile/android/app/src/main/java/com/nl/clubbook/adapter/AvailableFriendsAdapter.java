package com.nl.clubbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.datasource.UserDto;

import java.util.List;

/**
 * Created by Volodymyr on 07.10.2014.
 */
public class AvailableFriendsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<UserDto> mAvailableFriends;

    public AvailableFriendsAdapter(Context context, List<UserDto> availableFriends) {
        mInflater = LayoutInflater.from(context);
        mAvailableFriends = availableFriends;
    }

    @Override
    public int getCount() {
        return mAvailableFriends.size();
    }

    @Override
    public UserDto getItem(int position) {
        return mAvailableFriends.get(position);
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
            row = mInflater.inflate(R.layout.item_list_available_friends, null);
            holder = new ViewHolder();
            holder.imgAvatar = (ImageView) row.findViewById(R.id.imgAvatar);
            holder.txtName = (TextView) row.findViewById(R.id.txtName);
            holder.imgAddFriend = (ImageView) row.findViewById(R.id.imgAddFriend);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        fillRow(holder, mAvailableFriends.get(position));

        return row;
    }

    private void fillRow(ViewHolder holder, UserDto user) {
        //TODO

    }

    private class ViewHolder {
        ImageView imgAvatar;
        TextView txtName;
        ImageView imgAddFriend;
    }
}
