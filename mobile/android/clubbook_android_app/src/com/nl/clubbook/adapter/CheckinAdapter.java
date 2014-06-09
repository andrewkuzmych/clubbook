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

                    //final SessionManager session = new SessionManager(context.getApplicationContext());
                    //HashMap<String, String> user = session.getUserDetails();

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
                                // Do something when download finished
                                if(result) {
/*
                                    Intent in = new Intent(context.getApplicationContext(), ClubActivity.class);
                                    in.putExtra("club_id", club.getId());
                                    context.startActivity(in);

*/
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

                                    //SelectedClubFragment fragment = new SelectedClubFragment(club.getId());
                                    //FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
                                    //FragmentTransaction mFragmentTransaction  = fragmentManager.beginTransaction();
                                    //mFragmentTransaction.addToBackStack(null);
                                    //mFragmentTransaction.replace(R.id.frame_container, fragment).commit();
                                }
                            }
                        });
                    }

                /*    LocationCheckinHelper.checkin(context, fragment_club, new CheckInOutCallbackInterface() {
                        @Override
                        public void onCheckInOutFinished(boolean result) {
                            // Do something when download finished
                            if(result) {
                                Intent in = new Intent(context.getApplicationContext(), ClubActivity.class);
                                in.putExtra("club_id", fragment_club.getId());
                                context.startActivity(in);
                            }
                        }
                    });*/


            }
        });
            //holder.distance = (TextView) row.findViewById(R.id.distance);
            //holder.distance.setTypeface(typeface_bold);
            row.setTag(holder);
        } else {
            holder = (ClubItemHolder) row.getTag();
        }

        ClubDto club = data[position];
     //   String distance = LocationHelper.calculateDistance(context, fragment_club.getDistance());
        //holder.distance.setText(distance);
        holder.club_title.setText(club.getTitle());
        holder.club_id.setText(club.getId());
        holder.club_address.setText(club.getAddress());
        holder.checkin.setTag(club);

        String image_url = ImageHelper.GenarateUrl(club.getAvatar(), "w_300,h_300,c_fit");
        holder.avatar.setTag(image_url);
        imageLoader.displayImage(image_url, holder.avatar, options, animateFirstListener);

        if(LocationCheckinHelper.isCheckinHere(context, club)) {
            UiHelper.changeCheckinState(context, holder.checkin, false);
        } else {
            UiHelper.changeCheckinState(context, holder.checkin, true);
        }

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
        Button checkin;
    }

}
