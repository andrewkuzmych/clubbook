package com.nl.clubbook.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
            holder.distance = (TextView) row.findViewById(R.id.distance_text);
            holder.distance.setTypeface(typeface_bold);

            row.setTag(holder);
        } else {
            holder = (ClubItemHolder) row.getTag();
        }

        ClubDto club = data[position];
        String distance = LocationCheckinHelper.formatDistance(context, club.getDistance());
        holder.distance.setText(distance);
        holder.club_title.setText(club.getTitle());
        holder.club_id.setText(club.getId());

        String image_url = ImageHelper.GenarateUrl(club.getAvatar(), "w_300,h_300,c_fit");

        // checkin button
        holder.checkin = (Button) row.findViewById(R.id.checkin);
        holder.checkin.setTag(club);
        // if we checked in this this club set related style
        if (LocationCheckinHelper.isCheckinHere(club)) {
            UiHelper.changeCheckinState(context, holder.checkin, false);
        } else {
            UiHelper.changeCheckinState(context, holder.checkin, true);
        }
        // can we check in this club
        if (LocationCheckinHelper.canCheckinHere(club)) {
            holder.checkin.setEnabled(true);
        } else {
            holder.checkin.setEnabled(false);
        }

        holder.checkin.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                final ClubDto club = (ClubDto) view.getTag();
                if (LocationCheckinHelper.isCheckinHere(club)) {
                    LocationCheckinHelper.checkout(context, new CheckInOutCallbackInterface() {
                        @Override
                        public void onCheckInOutFinished(boolean result) {
                            // Do something when download finished
                            if (result)
                                UiHelper.changeCheckinState(context, view, true);
                        }
                    });
                } else {
                    LocationCheckinHelper.checkin(context, club, new CheckInOutCallbackInterface() {
                        @Override
                        public void onCheckInOutFinished(boolean isUserCheckin) {
                            // check if checkin was successful
                            if (isUserCheckin) {
                                // open club details and pass club_id parameter
                                ClubFragment fragment = new ClubFragment(null, club.getId());
                                FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                                FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();
                                mFragmentTransaction.replace(R.id.frame_container, fragment);
                                mFragmentTransaction.addToBackStack(null);
                                mFragmentTransaction.commit();
                            }
                        }
                    });
                }
            }
        });

        holder.avatar.setTag(image_url);
        imageLoader.displayImage(image_url, holder.avatar, options, animateFirstListener);

        return row;
    }

    static class ClubItemHolder {
        ImageView avatar;
        TextView club_title;
        TextView club_id;
        TextView distance;
        Button checkin;
    }

}
