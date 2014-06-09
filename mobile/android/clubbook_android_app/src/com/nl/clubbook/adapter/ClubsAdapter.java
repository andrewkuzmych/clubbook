package com.nl.clubbook.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nl.clubbook.R;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * Created by Andrew on 5/27/2014.
 */
public class ClubsAdapter extends ArrayAdapter<ClubDto> {
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();

    Context context;
    int layoutResourceId;
    ClubDto data[] = null;

    public ClubsAdapter(Context context, int layoutResourceId, ClubDto[] data) {
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
        ClubItemHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/TITILLIUMWEB-REGULAR.TTF");
            Typeface typeface_bold = Typeface.createFromAsset(context.getAssets(), "fonts/TITILLIUMWEB-BOLD.TTF");

            holder = new ClubItemHolder();
            holder.avatar = (ImageView) row.findViewById(R.id.avatar);
            holder.club_title = (TextView) row.findViewById(R.id.club_title);
            holder.club_title.setTypeface(typeface_bold);
            holder.club_id = (TextView) row.findViewById(R.id.club_id);
            holder.club_address= (TextView) row.findViewById(R.id.club_address);
            holder.club_address.setTypeface(typeface);
            holder.distance = (TextView) row.findViewById(R.id.distance);
            holder.distance.setTypeface(typeface_bold);


            row.setTag(holder);
        } else {
            holder = (ClubItemHolder) row.getTag();
        }

        ClubDto club = data[position];
        String distance = LocationCheckinHelper.calculateDistance(context, club.getDistance());
        holder.distance.setText(distance);
        holder.club_title.setText(club.getTitle());
        holder.club_id.setText(club.getId());
        holder.club_address.setText(club.getAddress());

        String image_url = ImageHelper.GenarateUrl(club.getAvatar(), "w_300,h_300,c_fit");

        holder.avatar.setTag(image_url);
        imageLoader.displayImage(image_url, holder.avatar, options, animateFirstListener);


        return row;
    }

    public ClubDto getPrizeById(String id)
    {
        ClubDto result = null;
        for(int i=0; i < data.length; i++)
        {
            if(data[i].getId().equalsIgnoreCase(id))
            {
                result = data[i];
                break;
            }
        }

        return result;

    }

    static class ClubItemHolder {
        // ImageView category;
        ImageView avatar;
        TextView club_title;
        TextView club_id;
        TextView club_address;
        TextView distance;
    }

}
