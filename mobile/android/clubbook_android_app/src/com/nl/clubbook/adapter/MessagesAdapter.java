package com.nl.clubbook.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nl.clubbook.R;
import com.nl.clubbook.datasource.ChatDto;
import com.nl.clubbook.helper.ImageHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by Andrew on 6/2/2014.
 */
public class MessagesAdapter extends ArrayAdapter<ChatDto> {
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();

    Context context;
    int layoutResourceId;
    List<ChatDto> data = null;

    public MessagesAdapter(Context context, int layoutResourceId, List<ChatDto> data) {
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
        ConvShortItemHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/TITILLIUMWEB-REGULAR.TTF");
            Typeface typeface_bold = Typeface.createFromAsset(context.getAssets(), "fonts/TITILLIUMWEB-BOLD.TTF");

            holder = new ConvShortItemHolder();
            holder.user_avatar = (ImageView) row.findViewById(R.id.user_avatar);
            holder.user_name = (TextView) row.findViewById(R.id.user_name);
            holder.user_name.setTypeface(typeface_bold);
            holder.con_id = (TextView) row.findViewById(R.id.con_id);
            holder.user_message= (TextView) row.findViewById(R.id.user_message);
            holder.user_message.setTypeface(typeface);
            holder.user_message_count = (TextView) row.findViewById(R.id.user_message_count);
            holder.user_message_count.setTypeface(typeface_bold);

            row.setTag(holder);
        } else {
            holder = (ConvShortItemHolder) row.getTag();
        }

        ChatDto con = data.get(position);
        holder.user_name.setText(con.getReceiver().getName());
        holder.user_name.setTag(con.getReceiver().getId());
        holder.con_id.setText(con.getChatId());
        holder.user_message.setText(con.getConversation().get(0).getFormatMessage());

        int unread_count = con.getUnreadMessages();
        if(unread_count == 0) {
            holder.user_message_count.setVisibility(View.GONE);
        } else {
            holder.user_message_count.setVisibility(View.VISIBLE);
        }
        holder.user_message_count.setText(String.valueOf(con.getUnreadMessages()));

        if(con.getReceiver().getAvatar() != null) {
            String image_url = ImageHelper.getUserListAvatar(con.getReceiver().getAvatar());
            imageLoader.displayImage(image_url, holder.user_avatar, options, animateFirstListener);
        }

        return row;
    }

    static class ConvShortItemHolder {
        ImageView user_avatar;
        TextView user_name;
        TextView con_id;
        TextView user_message;
        TextView user_message_count;
    }

}
