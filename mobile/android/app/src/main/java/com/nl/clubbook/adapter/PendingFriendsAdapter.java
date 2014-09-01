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
import com.nl.clubbook.helper.ImageHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by Volodymyr on 26.08.2014.
 */
public class PendingFriendsAdapter extends BaseAdapter {

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private ImageLoadingListener mAnimateFirstListener = new SimpleImageLoadingListener();
    private LayoutInflater mInflater;
    private List<UserDto> mUsers;
    private View.OnClickListener mOnClickListener;

    public PendingFriendsAdapter(Context context, List<UserDto> users, View.OnClickListener onClickListener) {
        mInflater = LayoutInflater.from(context);
        mUsers = users;
        mOnClickListener = onClickListener;

        mImageLoader = ImageLoader.getInstance();
        mOptions = new DisplayImageOptions.Builder()
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

        UserDto user = mUsers.get(position);
        fillRow(holder, user);

        return row;
    }

    public void updateData(List<UserDto> newUsers) {
        if(newUsers == null) {
            mUsers.clear();
        } else {
            mUsers = newUsers;
        }

        notifyDataSetChanged();
    }

    private void fillRow(ViewHolder holder, UserDto user) {
        holder.txtName.setText(user.getName());
        holder.txtName.setTag(user.getId());

        if (user.getAvatar() != null) {
            String imageUrl = ImageHelper.getUserListAvatar(user.getAvatar());

            holder.imgAvatar.setTag(imageUrl);
            mImageLoader.displayImage(imageUrl, holder.imgAvatar, mOptions, mAnimateFirstListener);
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
