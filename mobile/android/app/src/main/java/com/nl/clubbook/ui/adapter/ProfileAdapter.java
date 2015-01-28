package com.nl.clubbook.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.model.data.User;
import com.nl.clubbook.helper.ImageHelper;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by Andrew on 6/2/2014.
 */
public class ProfileAdapter extends BaseAdapter {
    public static final int MODE_DEFAULT = 7777;
    public static final int MODE_YESTERDAY_CHECKED_IN = 8888;

    private Context mContext;
    private List<User> mUsers;
    private LayoutInflater mInflater;
    private int mMode = MODE_DEFAULT;


    public ProfileAdapter(Context context, List<User> users, int mode) {
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
    public User getItem(int position) {
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
            row = mInflater.inflate(R.layout.item_grid_profile, parent, false);

            holder = new ViewHolder();
            holder.imgAvatar = (ImageView) row.findViewById(R.id.imgAvatar);
            holder.txtFriendIndicator = (TextView) row.findViewById(R.id.txtFriendIndicator);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        if(mMode == MODE_YESTERDAY_CHECKED_IN) {
            if (position == (mUsers.size() - 1)) {
                row.setPadding(0, 0, 0, (int) mContext.getResources().getDimension(R.dimen.grid_user_bottom_padding));
            } else {
                row.setPadding(0, 0, 0, 0);
            }
        }

        fillView(holder, mUsers.get(position));

        return row;
    }

    public void updateData(@Nullable List<User> newUsers) {
        if(newUsers == null) {
            mUsers.clear();
        } else {
            mUsers = newUsers;
        }

        notifyDataSetChanged();
    }

    public List<User> getUsers() {
        return mUsers;
    }

    private void fillView(ViewHolder holder, User item) {
        String imageUrl = ImageHelper.getUserListAvatar(item.getAvatar());
        Picasso.with(mContext).load(imageUrl).error(R.drawable.ic_avatar_unknown).into(holder.imgAvatar);

        if(User.STATUS_FRIEND.equalsIgnoreCase(item.getFriendStatus())) {
            holder.txtFriendIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.txtFriendIndicator.setVisibility(View.GONE);
        }
    }

    static class ViewHolder {
        TextView txtFriendIndicator;
        ImageView imgAvatar;
    }
}