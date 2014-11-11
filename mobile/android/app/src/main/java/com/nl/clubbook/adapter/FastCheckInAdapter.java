package com.nl.clubbook.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.datasource.Club;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by Volodymyr on 11.11.2014.
 */
public class FastCheckInAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Club> mClubs;

    public FastCheckInAdapter(Context context, List<Club> clubs) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mClubs = clubs;
    }

    @Override
    public int getCount() {
        return mClubs.size();
    }

    @Override
    public Club getItem(int position) {
        return mClubs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if(row == null) {
            row = mInflater.inflate(R.layout.item_list_fast_check_in, null);
            holder = new ViewHolder();

            holder.imgAvatar = (ImageView) row.findViewById(R.id.imgAvatar);
            holder.txtCheckIn = (TextView) row.findViewById(R.id.txtCheckedIn);
            holder.txtClubName = (TextView) row.findViewById(R.id.txtClubName);
            holder.txtDistance = (TextView) row.findViewById(R.id.txtDistance);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        fillRow(holder, mClubs.get(position));

        return row;
    }

    public void updateData(@Nullable List<Club> newClubs) {
        if(newClubs == null) {
            mClubs.clear();
        } else {
            mClubs = newClubs;
        }

        notifyDataSetChanged();
    }

    private void fillRow(ViewHolder holder, Club club) {
        String clubName = club.getTitle();
        holder.txtClubName.setText(clubName != null ? clubName : "");

        String distance = LocationCheckinHelper.formatDistance(mContext, club.getDistance());
        holder.txtDistance.setText(distance);

        String photoUrl = club.getAvatar();
        if(!TextUtils.isEmpty(photoUrl)) {
            Picasso.with(mContext).load(photoUrl).error(R.drawable.ic_club_avatar_default).into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.drawable.ic_club_avatar_default);
        }
    }

    private class ViewHolder {
        ImageView imgAvatar;
        TextView txtClubName;
        TextView txtDistance;
        TextView txtCheckIn;
    }
}