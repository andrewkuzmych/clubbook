package com.nl.clubbook.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.ConversationShort;
import com.nl.clubbook.fragment.SelectedClubFragment;
import com.nl.clubbook.helper.CheckInOutCallbackInterface;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.helper.UiHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * Created by Andrew on 6/2/2014.
 */
public class MessagesAdapter extends ArrayAdapter<ConversationShort> {
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();

    Context context;
    int layoutResourceId;
    ConversationShort data[] = null;

    public MessagesAdapter(Context context, int layoutResourceId, ConversationShort[] data) {
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

        ConversationShort con = data[position];
        holder.user_name.setText(con.getUser_name());
        holder.user_name.setTag(con.getUser_id());
        holder.con_id.setText(con.getId());
        holder.user_message.setText(con.getLast_message());
        int unread_count = con.getUnread_messages();
        if(unread_count == 0) {
            holder.user_message_count.setVisibility(View.GONE);
        } else {
            holder.user_message_count.setVisibility(View.VISIBLE);
        }

        holder.user_message_count.setText(String.valueOf(con.getUnread_messages()));

        if(con.getUser_photo() != null) {
            String image_url = ImageHelper.GenarateUrl(con.getUser_photo(), "w_300,h_300,c_fit");
            holder.user_avatar.setTag(image_url);
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
