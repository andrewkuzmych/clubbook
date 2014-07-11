package com.nl.clubbook.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
 * Created by Andrew on 6/2/2014.
 */
public class FriendsAdapter extends ArrayAdapter<UserDto> {
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();

    Context context;
    int layoutResourceId;
    List<UserDto> data = null;

    public FriendsAdapter(Context context, int layoutResourceId, List<UserDto> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        FriendItemHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new FriendItemHolder();
            holder.user_avatar = (ImageView) row.findViewById(R.id.user_avatar);
            holder.user_name = (TextView) row.findViewById(R.id.user_name);

            row.setTag(holder);
        } else {
            holder = (FriendItemHolder) row.getTag();
        }

        UserDto userDto = data.get(position);
        holder.user_name.setText(userDto.getName());
        holder.user_name.setTag(userDto.getId());

        if (userDto.getAvatar() != null) {
            String image_url = ImageHelper.generateUrl(userDto.getAvatar(), "w_300,h_300,c_fit");
            holder.user_avatar.setTag(image_url);
            imageLoader.displayImage(image_url, holder.user_avatar, options, animateFirstListener);
        }

        return row;
    }

    static class FriendItemHolder {
        ImageView user_avatar;
        TextView user_name;
    }
}
