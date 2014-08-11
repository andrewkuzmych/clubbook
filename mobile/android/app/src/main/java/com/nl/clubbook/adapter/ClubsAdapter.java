package com.nl.clubbook.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.fragment.ClubFragment;
import com.nl.clubbook.helper.CheckInOutCallbackInterface;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.helper.UiHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by Andrew on 5/27/2014.
 */
public class ClubsAdapter extends BaseAdapter {

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();

    private LayoutInflater mInflater;
    private Context mContext;
    private List<ClubDto> mClubs = null;

    public ClubsAdapter(Context context, List<ClubDto> data) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mClubs = data;

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
    public int getCount() {
        return mClubs.size();
    }

    @Override
    public Object getItem(int position) {
        return mClubs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.getLong(mClubs.get(position).getId(), -1);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ClubItemHolder holder;

        if (row == null) {
            row = mInflater.inflate(R.layout.club_list_item, parent, false);

            holder = new ClubItemHolder();
            holder.avatar = (ImageView) row.findViewById(R.id.imgAvatar);
            holder.clubTitle = (TextView) row.findViewById(R.id.txtClubTitle);
            holder.distance = (TextView) row.findViewById(R.id.distance_text);
            holder.peopleCount = (TextView) row.findViewById(R.id.people_count);
            holder.checkIn = (TextView) row.findViewById(R.id.txtCheckIn);

            row.setTag(holder);
        } else {
            holder = (ClubItemHolder) row.getTag();
        }

        fillRow(holder, mClubs.get(position));

        return row;
    }

    public void updateData(List<ClubDto> data) {
        if(data == null) {
            return;
        }

        mClubs = data;
        notifyDataSetChanged();
    }

    private void fillRow(ClubItemHolder holder, ClubDto club) {
        String distance = LocationCheckinHelper.formatDistance(mContext, club.getDistance());
        holder.distance.setText(distance);

        holder.peopleCount.setText(String.valueOf(club.getActiveCheckins()));
        holder.clubTitle.setText(club.getTitle());

        holder.checkIn.setTag(club);
        // if we checked in this this club set related style
        if (LocationCheckinHelper.isCheckinHere(club)) {
            UiHelper.changeCheckinState(mContext, holder.checkIn, false);
        } else {
            UiHelper.changeCheckinState(mContext, holder.checkIn, true);
        }
        // can we check in this club
        if (LocationCheckinHelper.canCheckinHere(club)) {
            holder.checkIn.setEnabled(true);
        } else {
            holder.checkIn.setEnabled(false);
        }

        holder.checkIn.setOnClickListener(getBtnCheckInClickListener());

        //load image
        String image_url = ImageHelper.getClubListAvatar(club.getAvatar());
        holder.avatar.setTag(image_url);
        imageLoader.displayImage(image_url, holder.avatar, options, animateFirstListener);
    }

    private View.OnClickListener getBtnCheckInClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(final View view) {

                final ClubDto club = (ClubDto) view.getTag();
                if (LocationCheckinHelper.isCheckinHere(club)) {
                    LocationCheckinHelper.checkout(mContext, new CheckInOutCallbackInterface() {
                        @Override
                        public void onCheckInOutFinished(boolean result) {
                            // Do something when download finished
                            if (result) {
                                UiHelper.changeCheckinState(mContext, view, true);
                            }
                        }
                    });
                } else {
                    LocationCheckinHelper.checkin(mContext, club, new CheckInOutCallbackInterface() {
                        @Override
                        public void onCheckInOutFinished(boolean isUserCheckIn) {
                            // check if checkin was successful
                            if (isUserCheckIn) {
                                // open club details and pass club_id parameter
                                //TODO fix this
                                ClubFragment fragment = new ClubFragment(null, club.getId());
                                FragmentManager fragmentManager = ((MainActivity) mContext).getSupportFragmentManager();
                                FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();
                                mFragmentTransaction.replace(R.id.frame_container, fragment);
                                mFragmentTransaction.addToBackStack(null);
                                mFragmentTransaction.commit();
                            }
                        }
                    });
                }
            }
        };

        return listener;
    }

    static class ClubItemHolder {
        ImageView avatar;
        TextView clubTitle;
        TextView distance;
        TextView peopleCount;
        TextView friendsCount;
        TextView checkIn;
    }

}
