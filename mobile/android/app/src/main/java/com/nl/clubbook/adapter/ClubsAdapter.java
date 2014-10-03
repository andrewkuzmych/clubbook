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
import com.nl.clubbook.datasource.ClubWorkingHoursDto;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Andrew on 5/27/2014.
 */
public class ClubsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<ClubDto> mClubs = null;
    private String mCheckedIn;
    private String mFriends;

    public ClubsAdapter(Context context, List<ClubDto> data) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mClubs = data;

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
            holder.txtStatus = (TextView) row.findViewById(R.id.txtStatus);

            row.setTag(holder);
        } else {
            holder = (ClubItemHolder) row.getTag();
        }

        fillRow(holder, mClubs.get(position));

        return row;
    }

    public void updateData(List<ClubDto> data) {
        if (data == null) {
            mClubs.clear();
        } else {
            mClubs = data;
        }

        notifyDataSetChanged();
    }

    private void fillRow(ClubItemHolder holder, ClubDto club) {
        String distance = LocationCheckinHelper.formatDistance(mContext, club.getDistance());
        holder.txtDistance.setText(distance);

        holder.txtCheckedInCount.setText(club.getActiveCheckIns() + "\n" + mCheckedIn);
        holder.txtFriendsCount.setText(club.getActiveFriendsCheckIns() + "\n" + mFriends);

        holder.txtClubName.setText(club.getTitle());
        holder.txtClubName.setTag(club.getId());

        ClubWorkingHoursDto todayWorkingHours = club.getTodayWorkingHours();
        if(todayWorkingHours != null && ClubWorkingHoursDto.STATUS_OPENED.equalsIgnoreCase(todayWorkingHours.getStatus())) {
            holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.green));
            holder.txtStatus.setText(R.string.open);
        } else {
            holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.red_light));
            holder.txtStatus.setText(R.string.closed_display);
        }

        //load image
        String imageUrl = ImageHelper.getClubListAvatar(club.getAvatar());
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
