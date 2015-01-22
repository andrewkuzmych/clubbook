package com.nl.clubbook.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.model.data.Place;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.helper.UiHelper;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by Volodymyr on 11.11.2014.
 */
public class FastCheckInAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Place> mPlaces;
    private View.OnClickListener mOnBtnCheckInClicked;

    public FastCheckInAdapter(Context context, List<Place> places, View.OnClickListener onBtnCheckInClicked) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mPlaces = places;

        mOnBtnCheckInClicked = onBtnCheckInClicked;
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
            holder.txtCheckIn = (TextView) row.findViewById(R.id.txtCheckIn);
            holder.txtClubName = (TextView) row.findViewById(R.id.txtClubName);
            holder.txtDistance = (TextView) row.findViewById(R.id.txtDistance);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        fillRow(holder, mPlaces.get(position));

        return row;
    }

    public void updateData(@Nullable List<Place> newPlaces) {
        if(newPlaces == null) {
            mPlaces.clear();
        } else {
            mPlaces = newPlaces;
        }

        notifyDataSetChanged();
    }

    private void fillRow(ViewHolder holder, Place place) {
        String clubName = place.getTitle();
        holder.txtClubName.setText(clubName != null ? clubName : "");

        String distance = LocationCheckinHelper.formatDistance(mContext, place.getDistance());
        holder.txtDistance.setText(distance);

        String photoUrl = place.getAvatar();
        if(!TextUtils.isEmpty(photoUrl)) {
            Picasso.with(mContext).load(ImageHelper.getClubListAvatar(photoUrl)).error(R.drawable.ic_club_avatar_default).into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.drawable.ic_club_avatar_default);
        }

        //init CheckIn button
        if(LocationCheckinHelper.getInstance().isCheckInHere(place)) {
            UiHelper.changeCheckInState(mContext, holder.txtCheckIn, true);
        } else {
            UiHelper.changeCheckInState(mContext, holder.txtCheckIn, false);
        }
        setCheckInTxtPadding(mContext, holder.txtCheckIn);

        holder.txtCheckIn.setTag(place);
        holder.txtCheckIn.setOnClickListener(mOnBtnCheckInClicked);
    }

    public static void setCheckInTxtPadding(Context context, View view) {
        view.setPadding(
                (int)context.getResources().getDimension(R.dimen.btn_check_in_left_right_padding_fast_check_in),
                0,
                (int)context.getResources().getDimension(R.dimen.btn_check_in_left_right_padding_fast_check_in),
                0
        );
    }

    private class ViewHolder {
        ImageView imgAvatar;
        TextView txtClubName;
        TextView txtDistance;
        TextView txtCheckIn;
    }
}
