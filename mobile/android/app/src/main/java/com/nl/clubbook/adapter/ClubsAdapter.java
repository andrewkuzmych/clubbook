package com.nl.clubbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Andrew on 5/27/2014.
 */
public class ClubsAdapter extends BaseAdapter {

    public static final int MODE_NEARBY = 1000;
    public static final int MODE_A_Z = 2000;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();

    private LayoutInflater mInflater;
    private Context mContext;
    private List<ClubDto> mClubs = null;
    private String mCheckedIn;
    private String mFriends;

    public ClubsAdapter(Context context, List<ClubDto> data) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mClubs = data;

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_club_avatar_default)
                .showImageForEmptyUri(R.drawable.ic_club_avatar_default)
                .showImageOnFail(R.drawable.ic_club_avatar_default)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        mCheckedIn = context.getString(R.string.checked_in);
        mFriends = context.getString(R.string.friends);
    }

    @Override
    public int getCount() {
        return mClubs.size();
    }

    @Override
    public Object getItem(int position) {
        return mClubs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ClubItemHolder holder;

        if (row == null) {
            row = mInflater.inflate(R.layout.item_list_club, parent, false);

            holder = new ClubItemHolder();
            holder.imgAvatar = (ImageView) row.findViewById(R.id.imgAvatar);
            holder.txtClubName = (TextView) row.findViewById(R.id.txtClubName);
            holder.txtDistance = (TextView) row.findViewById(R.id.txtDistance);
            holder.txtCheckedInCount = (TextView) row.findViewById(R.id.txtCheckedInCount);
            holder.txtFriendsCount = (TextView) row.findViewById(R.id.txtFriendsCount);

            row.setTag(holder);
        } else {
            holder = (ClubItemHolder) row.getTag();
        }

        fillRow(holder, mClubs.get(position));

        return row;
    }

    public void updateData(List<ClubDto> data, int sortMode) {
        if (data == null) {
            return;
        }

        mClubs = data;

        if (sortMode == MODE_NEARBY) {
            sortByDistance();
        } else {
            sortByName();
        }
    }

    public void sortByDistance() {
        Collections.sort(mClubs, new Comparator<ClubDto>() {
            @Override
            public int compare(ClubDto lhs, ClubDto rhs) {
                return Float.compare(lhs.getDistance(), rhs.getDistance());
            }
        });

        notifyDataSetChanged();
    }

    public void sortByName() {
        Collections.sort(mClubs, new Comparator<ClubDto>() {
            @Override
            public int compare(ClubDto lhs, ClubDto rhs) {
                return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
            }
        });

        notifyDataSetChanged();
    }

    private void fillRow(ClubItemHolder holder, ClubDto club) {
        String distance = LocationCheckinHelper.formatDistance(mContext, club.getDistance());
        holder.txtDistance.setText(distance);

        holder.txtCheckedInCount.setText(club.getActiveCheckIns() + "\n" + mCheckedIn);
        holder.txtFriendsCount.setText(club.getActiveFriendsCheckIns() + "\n" + mFriends);

        holder.txtClubName.setText(club.getTitle());
        holder.txtClubName.setTag(club.getId());

        //load image
        String imageUrl = ImageHelper.getClubListAvatar(club.getAvatar());
        holder.imgAvatar.setTag(imageUrl);
        imageLoader.displayImage(imageUrl, holder.imgAvatar, options, animateFirstListener);
    }

    static class ClubItemHolder {
        ImageView imgAvatar;
        TextView txtClubName;
        TextView txtDistance;
        TextView txtCheckedInCount;
        TextView txtFriendsCount;
    }

}
