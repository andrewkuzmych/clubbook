package com.nl.clubbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.datasource.User;
import com.nl.clubbook.helper.ImageHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Volodymyr on 26.08.2014.
 */
public class PendingFriendsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<User> mUsers;
    private View.OnClickListener mOnClickListener;

    public PendingFriendsAdapter(Context context, List<User> users, View.OnClickListener onClickListener) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mUsers = users;
        mOnClickListener = onClickListener;
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if(row == null) {
            row = mInflater.inflate(R.layout.item_list_peding_friends, null);
            holder = new ViewHolder();

            holder.imgAvatar = (ImageView) row.findViewById(R.id.imgAvatar);
            holder.txtName = (TextView) row.findViewById(R.id.txtName);
            holder.imgAccept = (TextView) row.findViewById(R.id.txtAccept);
            holder.imgDecline = (TextView) row.findViewById(R.id.txtDecline);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        User user = mUsers.get(position);
        fillRow(holder, user);

        return row;
    }

    public void updateData(List<User> newUsers) {
        if(newUsers == null) {
            mUsers.clear();
        } else {
            mUsers = newUsers;
        }

        notifyDataSetChanged();
    }

    private void fillRow(ViewHolder holder, User user) {
        holder.txtName.setText(user.getName());

        if (user.getAvatar() != null) {
            String imageUrl = ImageHelper.getUserListAvatar(user.getAvatar());
            Picasso.with(mContext).load(imageUrl).error(R.drawable.ic_avatar_unknown).into(holder.imgAvatar);
        }

        holder.imgAccept.setTag(user.getId());
        holder.imgDecline.setTag(user.getId());

        holder.imgAccept.setOnClickListener(mOnClickListener);
        holder.imgDecline.setOnClickListener(mOnClickListener);
    }

    private class ViewHolder {
        ImageView imgAvatar;
        TextView txtName;
        TextView imgAccept;
        TextView imgDecline;
    }
}
