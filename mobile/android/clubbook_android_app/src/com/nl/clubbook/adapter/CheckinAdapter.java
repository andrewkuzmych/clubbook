package com.nl.clubbook.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.fragment.SelectedClubFragment;
import com.nl.clubbook.helper.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.HashMap;

/**
 * Created by Andrew on 6/2/2014.
 */
public class CheckinAdapter extends ArrayAdapter<ClubDto> {
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();
    private int CLUB_ID_KEY = 1;

    Context context;
    int layoutResourceId;
    ClubDto data[] = null;

    public CheckinAdapter(Context context, int layoutResourceId, ClubDto[] data) {
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

            holder.checkin= (Button)  row.findViewById(R.id.checkin);
            holder.checkin.setOnClickListener(new View.OnClickListener() {
                public void onClick(final View view) {
                    Toast.makeText(context, "this is " + view.getTag().toString(),
                            Toast.LENGTH_SHORT).show();

                    final ClubDto club = (ClubDto)view.getTag();

                    if(LocationCheckinHelper.isCheckinHere(context, club))
                    {
                        LocationCheckinHelper.checkout(context, new CheckInOutCallbackInterface() {
                            @Override
                            public void onCheckInOutFinished(boolean result) {
                                // Do something when download finished
                                if (result)
                                    UiHelper.changeCheckinState(context, view, true);
                            }
                        });

                    }
                    else
                    {
                        LocationCheckinHelper.checkin(context, club, new CheckInOutCallbackInterface() {
                            @Override
                            public void onCheckInOutFinished(boolean result) {
                                if(result) {
                                    SelectedClubFragment fragment = new SelectedClubFragment(null, club.getId());
                                    FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
                                    FragmentTransaction mFragmentTransaction  = fragmentManager.beginTransaction();

                                    if(fragment.isAdded())
                                        mFragmentTransaction.show(fragment);
                                    else {
                                        mFragmentTransaction.replace(R.id.frame_container, fragment);
                                        mFragmentTransaction.addToBackStack(null);
                                    }
                                    mFragmentTransaction.commit();

                                }
                            }
                        });
                    }
            }
        });
            row.setTag(holder);
        } else {
            holder = (ClubItemHolder) row.getTag();
        }

        ClubDto club = data[position];
        holder.club_title.setText(club.getTitle());
        holder.club_id.setText(club.getId());
        holder.club_address.setText(club.getAddress());
        String image_url = ImageHelper.GenarateUrl(club.getAvatar(), "w_300,h_300,c_fit");
        holder.avatar.setTag(image_url);
        imageLoader.displayImage(image_url, holder.avatar, options, animateFirstListener);

        holder.checkin.setTag(club);
        if(LocationCheckinHelper.isCheckinHere(context, club)) {
            UiHelper.changeCheckinState(context, holder.checkin, false);
        } else {
            UiHelper.changeCheckinState(context, holder.checkin, true);
        }

        return row;
    }

    static class ClubItemHolder {
        ImageView avatar;
        TextView club_title;
        TextView club_id;
        TextView club_address;
        Button checkin;
    }

}
