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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 6/2/2014.
 */
public class ProfileAdapter extends BaseAdapter {
    private List<UserDto> mUsers = new ArrayList<UserDto>();
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();
    private LayoutInflater mInflater;


    public ProfileAdapter(Context context, List<UserDto> users) {
        mInflater = LayoutInflater.from(context);
        this.mUsers = users;

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_list_image)
                .showImageForEmptyUri(R.drawable.default_list_image)
                .showImageOnFail(R.drawable.default_list_image)
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
            row = mInflater.inflate(R.layout.item_grid_profile, parent, false);
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

    private void fillView(ViewHolder holder, UserDto item) {
        String imageUrl = ImageHelper.getUserListAvatar(item.getAvatar());
        holder.imgAvatar.setTag(imageUrl);
        imageLoader.displayImage(imageUrl, holder.imgAvatar, options, animateFirstListener);

        holder.userId.setTag(item.getId());

        //TODO implement friends indicator
    }

    static class ViewHolder {
        View userId;
        TextView txtFriendIndicator;
        ImageView imgAvatar;
    }
}
