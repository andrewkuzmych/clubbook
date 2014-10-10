package com.nl.clubbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.datasource.CheckInDto;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.helper.ImageHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Volodymyr on 10.10.2014.
 */
public class FindFriendsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<UserDto> mUsers;

    private String mCheckIn;
    private String mNotCheckIn;

    public FindFriendsAdapter(Context context, List<UserDto> users) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mUsers = users;

        mCheckIn = context.getString(R.string.checked_in_double_dots);
        mNotCheckIn = context.getString(R.string.not_checked_in);
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public UserDto getItem(int position) {
        return mUsers.get(position);
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
            row = mInflater.inflate(R.layout.item_list_find_friends, null);
            holder = new ViewHolder();

            holder.imgAvatar = (ImageView) row.findViewById(R.id.imgAvatar);
            holder.txtAddFriend = (TextView) row.findViewById(R.id.txtAddFriend);
            holder.txtCheckedIn = (TextView) row.findViewById(R.id.txtCheckedIn);
            holder.txtCheckedInPlace = (TextView) row.findViewById(R.id.txtCheckedInPlace);
            holder.txtUsername = (TextView) row.findViewById(R.id.txtUsername);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        fillView(holder, mUsers.get(position));

        return row;
    }

    private void fillView(ViewHolder holder, UserDto userDto) {
        holder.txtUsername.setText(userDto.getName());
        holder.txtUsername.setTag(userDto.getId());

        if (userDto.getAvatar() != null) {
            String imageUrl = ImageHelper.getUserListAvatar(userDto.getAvatar());

            Picasso.with(mContext).load(imageUrl).error(R.drawable.ic_avatar_unknown).into(holder.imgAvatar);
        }

        CheckInDto checkIn = userDto.getLastCheckIn();
        if(checkIn == null) {
            return;
        }

        if(!checkIn.isActive()) {
            holder.txtCheckedInPlace.setVisibility(View.GONE);
            holder.txtCheckedIn.setText(mNotCheckIn);
        } else {
            holder.txtCheckedInPlace.setText(checkIn.getClubName());
            holder.txtCheckedInPlace.setVisibility(View.VISIBLE);

            holder.txtCheckedIn.setText(mCheckIn);
        }
    }

    private class ViewHolder {
        ImageView imgAvatar;
        TextView txtUsername;
        TextView txtCheckedIn;
        TextView txtCheckedInPlace;
        TextView txtAddFriend;
    }
}
