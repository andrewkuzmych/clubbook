package com.nl.clubbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nl.clubbook.R;
import com.nl.clubbook.datasource.Place;
import com.nl.clubbook.datasource.ClubWorkingHours;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Andrew on 5/27/2014.
 */
public class PlacesAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<Place> mPlaces = null;
    private String mCheckedIn;
    private String mFriends;

    public PlacesAdapter(Context context, List<Place> data) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mPlaces = data;

        mCheckedIn = context.getString(R.string.checked_in);
        mFriends = context.getString(R.string.friends);
    }

    @Override
    public int getCount() {
        return mPlaces.size();
    }

    @Override
    public Place getItem(int position) {
        return mPlaces.get(position);
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
            holder.txtStatus = (TextView) row.findViewById(R.id.txtStatus);

            row.setTag(holder);
        } else {
            holder = (ClubItemHolder) row.getTag();
        }

        fillRow(holder, mPlaces.get(position));

        return row;
    }

    public void updateData(List<Place> data) {
        if (data == null) {
            mPlaces.clear();
        } else {
            mPlaces = data;
        }

        notifyDataSetChanged();
    }

    public void addData(List<Place> newPlaces) {
        if(newPlaces == null || newPlaces.isEmpty()) {
            return;
        }

        mPlaces.addAll(newPlaces);
        notifyDataSetChanged();
    }

    private void fillRow(ClubItemHolder holder, Place place) {
        String distance = LocationCheckinHelper.formatDistance(mContext, place.getDistance());
        holder.txtDistance.setText(distance);

        holder.txtCheckedInCount.setText(place.getActiveCheckIns() + "\n" + mCheckedIn);
        holder.txtFriendsCount.setText(place.getActiveFriendsCheckIns() + "\n" + mFriends);

        holder.txtClubName.setText(place.getTitle());

        ClubWorkingHours todayWorkingHours = place.getTodayWorkingHours();
        if(todayWorkingHours != null && ClubWorkingHours.STATUS_OPENED.equalsIgnoreCase(todayWorkingHours.getStatus())) {
            holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.green));
            holder.txtStatus.setText(R.string.open);
        } else {
            holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.red_light));
            holder.txtStatus.setText(R.string.closed_display);
        }

        //load image
        String imageUrl = ImageHelper.getClubListAvatar(place.getAvatar());
        holder.imgAvatar.setTag(imageUrl);
        Picasso.with(mContext).load(imageUrl).error(R.drawable.ic_club_avatar_default).into(holder.imgAvatar);
    }

    static class ClubItemHolder {
        ImageView imgAvatar;
        TextView txtClubName;
        TextView txtDistance;
        TextView txtCheckedInCount;
        TextView txtFriendsCount;
        TextView txtStatus;
    }

}
