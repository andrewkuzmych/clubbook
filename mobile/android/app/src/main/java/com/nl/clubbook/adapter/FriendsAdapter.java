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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by Andrew on 6/2/2014.
 */
public class FriendsAdapter extends BaseAdapter {

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private ImageLoadingListener mAnimateFirstListener = new SimpleImageLoadingListener();
    private LayoutInflater mInflater;
    private List<UserDto> mFriends;

    private String mCheckIn;
    private String mNotCheckIn;

    public FriendsAdapter(Context context, List<UserDto> friends) {
        mInflater = LayoutInflater.from(context);
        mFriends = friends;

        mImageLoader = ImageLoader.getInstance();
        mOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_avatar_missing)
                .showImageForEmptyUri(R.drawable.ic_avatar_missing)
                .showImageOnFail(R.drawable.ic_avatar_unknown)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        mCheckIn = context.getString(R.string.checked_in);
        mNotCheckIn = context.getString(R.string.not_checked_in);
    }

    @Override
    public int getCount() {
        return mFriends.size();
    }

    @Override
    public UserDto getItem(int position) {
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

    public void updateData(List<UserDto> friends) {
        if(friends == null) {
            return;
        }

        mFriends = friends;
        notifyDataSetChanged();
    }

    private void fillRow(FriendItemHolder holder, UserDto userDto) {
        holder.txtUsername.setText(userDto.getName());
        holder.txtUsername.setTag(userDto.getId());

        if (userDto.getAvatar() != null) {
            String image_url = ImageHelper.getUserListAvatar(userDto.getAvatar());

            holder.imgUserAvatar.setTag(image_url);
            mImageLoader.displayImage(image_url, holder.imgUserAvatar, mOptions, mAnimateFirstListener);
        }

        CheckInDto checkIn = userDto.getLastCheckIn();
        if(checkIn == null) {
            return;
        }

        if(checkIn.isActive()) {
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