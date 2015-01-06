package com.nl.clubbook.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.activity.YesterdayUsersGridActivity;
import com.nl.clubbook.datasource.Place;
import com.nl.clubbook.helper.ImageHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by User on 06.01.2015.
 */
public class YesterdayPlacesAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Place> mPlaces;
    private String mCheckedIn;
    private String mFriends;

    public YesterdayPlacesAdapter(Context context, List<Place> places) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mPlaces = places;

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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ClubItemHolder holder;

        if (row == null) {
            row = mInflater.inflate(R.layout.item_list_yesterday_place, parent, false);

            holder = new ClubItemHolder();
            holder.imgAvatar = (ImageView) row.findViewById(R.id.imgAvatar);
            holder.txtClubName = (TextView) row.findViewById(R.id.txtClubName);
            holder.txtCheckedInCount = (TextView) row.findViewById(R.id.txtCheckedInCount);
            holder.txtFriendsCount = (TextView) row.findViewById(R.id.txtFriendsCount);
            holder.txtView = (TextView) row.findViewById(R.id.txtView);

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

    private void fillRow(ClubItemHolder holder, final Place place) {
        holder.txtCheckedInCount.setText(place.getActiveCheckIns() + "\n" + mCheckedIn);
        holder.txtFriendsCount.setText(place.getActiveFriendsCheckIns() + "\n" + mFriends);

        holder.txtClubName.setText(place.getTitle());

        //load image
        String imageUrl = ImageHelper.getClubListAvatar(place.getAvatar());
        holder.imgAvatar.setTag(imageUrl);
        Picasso.with(mContext).load(imageUrl).error(R.drawable.ic_club_avatar_default).into(holder.imgAvatar);

        holder.txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, YesterdayUsersGridActivity.class);
                intent.putExtra(YesterdayUsersGridActivity.EXTRA_CLUB_ID, place.getId());
                mContext.startActivity(intent);
            }
        });
    }

    static class ClubItemHolder {
        ImageView imgAvatar;
        TextView txtClubName;
        TextView txtCheckedInCount;
        TextView txtFriendsCount;
        TextView txtView;
    }
}
