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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 6/2/2014.
 */
public class ProfileAdapter extends ArrayAdapter<UserDto> {
    private Context context;
    private int layoutResourceId;
    private List<UserDto> data = new ArrayList<UserDto>();
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();


    public ProfileAdapter(Context context, int layoutResourceId,
                          List<UserDto> data) {
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
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            holder.userId = (TextView) row.findViewById(R.id.user_id);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        UserDto item = data.get(position);
        holder.imageTitle.setText(item.getName());

        String image_url = ImageHelper.GenarateUrl(item.getAvatar(), "w_100,h_100,c_thumb,g_face");

        holder.image.setTag(image_url);
        holder.userId.setText(item.getId());
        imageLoader.displayImage(image_url, holder.image, options, animateFirstListener);

        return row;
    }

    static class ViewHolder {
        TextView imageTitle;
        TextView userId;
        ImageView image;
    }
}
