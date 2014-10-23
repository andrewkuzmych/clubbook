package com.nl.clubbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nl.clubbook.R;
import com.nl.clubbook.datasource.CheckIn;
import com.nl.clubbook.datasource.User;
import com.nl.clubbook.helper.ImageHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Andrew on 6/2/2014.
 */
public class FriendsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<User> mFriends;

    private String mCheckIn;
    private String mNotCheckIn;

    public FriendsAdapter(Context context, List<User> friends) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mFriends = friends;

        mCheckIn = context.getString(R.string.checked_in_double_dots);
        mNotCheckIn = context.getString(R.string.not_checked_in);
    }

    @Override
    public int getCount() {
        return mFriends.size();
    }

    @Override
    public User getItem(int position) {
        return mFriends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FriendItemHolder holder;

        if (row == null) {
            row = mInflater.inflate(R.layout.item_list_friends, parent, false);

            holder = new FriendItemHolder();
            holder.imgUserAvatar = (ImageView) row.findViewById(R.id.imgAvatar);
            holder.txtUsername = (TextView) row.findViewById(R.id.txtUsername);
            holder.txtCheckIn = (TextView) row.findViewById(R.id.txtCheckedIn);
            holder.txtCheckedInPlace = (TextView) row.findViewById(R.id.txtCheckedInPlace);

            row.setTag(holder);
        } else {
            holder = (FriendItemHolder) row.getTag();
        }

        fillRow(holder, mFriends.get(position));

        return row;
    }

    public void updateData(List<User> friends) {
        if(friends == null) {
            return;
        }

        mFriends = friends;
        notifyDataSetChanged();
    }

    private void fillRow(FriendItemHolder holder, User user) {
        holder.txtUsername.setText(user.getName());

        if (user.getAvatar() != null) {
            String imageUrl = ImageHelper.getUserListAvatar(user.getAvatar());

            holder.imgUserAvatar.setTag(imageUrl);
            Picasso.with(mContext).load(imageUrl).error(R.drawable.ic_avatar_unknown).into(holder.imgUserAvatar);
        }

        CheckIn checkIn = user.getLastCheckIn();
        if(checkIn == null) {
            return;
        }

        if(!checkIn.isActive()) {
            holder.txtCheckedInPlace.setVisibility(View.GONE);
            holder.txtCheckIn.setText(mNotCheckIn);
        } else {
            holder.txtCheckedInPlace.setText(checkIn.getClubName());
            holder.txtCheckedInPlace.setVisibility(View.VISIBLE);

            holder.txtCheckIn.setText(mCheckIn);
        }
    }

    static class FriendItemHolder {
        ImageView imgUserAvatar;
        TextView txtUsername;
        TextView txtCheckIn;
        TextView txtCheckedInPlace;
    }
}
