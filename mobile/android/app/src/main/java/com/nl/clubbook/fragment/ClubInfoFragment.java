package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.PhotoPagerAdapter;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.ClubWorkingHoursDto;
import com.nl.clubbook.datasource.JSONConverter;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.ui.view.ViewPagerBulletIndicatorView;
import com.nl.clubbook.utils.L;
import com.nl.clubbook.utils.UIUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Volodymyr on 15.08.2014.
 */
public class ClubInfoFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private static final String ARG_JSON_CLUB = "ARG_JSON_CLUB";

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();

    private ViewPagerBulletIndicatorView mBulletIndicator;

    public static Fragment newInstance(String jsonClub) {
        Fragment fragment = new ClubInfoFragment();

        Bundle args = new Bundle();
        args.putString(ARG_JSON_CLUB, jsonClub);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_club_info, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initLoader();
        initView();
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public void onPageSelected(int position) {
        mBulletIndicator.setSelectedView(position);
    }

    private void initView() {
        View view = getView();
        if(view == null || isDetached() || getActivity() == null) {
            return;
        }

        Bundle args = getArguments();
        if(args == null) {
            L.i("args = null");
            return;
        }

        String jsonClub = args.getString(ARG_JSON_CLUB);
        ClubDto club = JSONConverter.newClub(jsonClub);
        if(club == null) {
            L.i("club = null");
            return;
        }

        fillView(view, club);
    }

    private void initLoader() {
        mImageLoader = ImageLoader.getInstance();
        mOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_club_avatar_default)
                .showImageOnFail(R.drawable.ic_club_avatar_default)
                .cacheInMemory()
                .cacheOnDisc()
                .build();
    }

    private void fillView(View view, ClubDto club) {
        initViewPager(view, club.getPhotos());

        String info = club.getInfo();
        if(!setTextToTextView(view, info, R.id.txtInfo)) {
            view.findViewById(R.id.dividerInfo).setVisibility(View.GONE);
        }

        fillClubRequirementsHolder(view, club);
        fillContactHolder(view, club);
        fillWorkingHoursHolder(view, club);

        String avatarUrl = club.getAvatar();
        if(avatarUrl != null && !avatarUrl.isEmpty()) {
            UIUtils.loadPhotoToActionBar((ActionBarActivity)getActivity(), ImageHelper.getUserListAvatar(avatarUrl));
        }
    }

    private void fillContactHolder(View view, ClubDto club) {
        boolean result = false;

        if(fillTextViewHolder(view, club.getAddress(), R.id.txtLocation, R.id.txtLocation)) {
            result = true;
        }

        if(fillTextViewHolder(view, club.getPhone(), R.id.txtPhone, R.id.txtPhone)) {
            result = true;
        }

        if(fillTextViewHolder(view, club.getWebsite(), R.id.txtWebsite, R.id.txtWebsite)) {
            result = true;
        }

        if(fillTextViewHolder(view, club.getEmail(), R.id.txtEmail, R.id.txtEmail)) {
            result = true;
        }

        if(!result) {
            view.findViewById(R.id.holderContacts).setVisibility(View.GONE);
            view.findViewById(R.id.holderClubRequirements).setVisibility(View.GONE);
        }
    }

    private void fillClubRequirementsHolder(View view, ClubDto club) {
        boolean result = false;

        if(fillTextViewHolder(view, club.getAgeRestriction(), R.id.txtAgeRestriction, R.id.holderAgeRestriction)) {
            result = true;
        }

        if(fillTextViewHolder(view, club.getDressCode(), R.id.txtDressCode, R.id.holderDressCode)) {
            result = true;
        }

        if(fillTextViewHolder(view, club.getCapacity(), R.id.txtCapacity, R.id.holderCapacity)) {
            result = true;
        }

        if(!result) {
            view.findViewById(R.id.holderRequirements).setVisibility(View.GONE);
            view.findViewById(R.id.dividerContacts).setVisibility(View.GONE);
        }
    }

    private void fillWorkingHoursHolder(View view, ClubDto club) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        int calendarDayOfWeekIndex =  calendar.get(Calendar.DAY_OF_WEEK);

        LinearLayout holderWorkingHours = (LinearLayout)view.findViewById(R.id.holderWorkingHours);
        List<ClubWorkingHoursDto> workingHours = club.getWorkingHours();
        if(workingHours != null && !workingHours.isEmpty()) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            for (ClubWorkingHoursDto clubWorkHours : workingHours) {
                View row;
                if(calendarDayOfWeekIndex - 1 == clubWorkHours.getDay()) {
                    row = inflater.inflate(R.layout.view_club_work_hours_bold, null);
                } else {
                    row = inflater.inflate(R.layout.view_club_work_hours, null);
                }

                String dayName = getDayNameByDayNumber(clubWorkHours.getDay());
                TextView txtDayName = (TextView) row.findViewById(R.id.txtDayName);
                txtDayName.setText(dayName != null ? dayName : dayName);

                TextView txtWorkHours = (TextView) row.findViewById(R.id.txtWorkHours);
                if(ClubWorkingHoursDto.STATUS_OPENED.equals(clubWorkHours.getStatus())) {
                    String startTime = clubWorkHours.getStartTime();
                    String endTime = clubWorkHours.getEndTime();

                    txtWorkHours.setText(!TextUtils.isEmpty(startTime) ? startTime + " - " : "");
                    txtWorkHours.append(endTime != null ? endTime : "");
                } else {
                    txtWorkHours.setText(getString(R.string.closed));
                }

                holderWorkingHours.addView(row);
            }
        } else {
            holderWorkingHours.setVisibility(View.GONE);
        }
    }

    private boolean fillTextViewHolder(View view, String text, int textViewId, int textViewHolderId) {
        if(setTextToTextView(view, text, textViewId)) {
            return true;
        } else {
            view.findViewById(textViewHolderId).setVisibility(View.GONE);
            return false;
        }
    }

    private boolean setTextToTextView(View view, String text, int textViewId) {
        if(text != null && text.length() != 0) {
            TextView txtInfo = (TextView) view.findViewById(textViewId);
            txtInfo.setText(text);
            return true;
        } else {
            view.findViewById(textViewId).setVisibility(View.GONE);
            return false;
        }
    }

    private void initViewPager(View view, List<String> photos) {
        if(photos == null) {
            L.i("photos == null");
            return;
        }
        mBulletIndicator = (ViewPagerBulletIndicatorView)view.findViewById(R.id.indicatorPhotos);
        mBulletIndicator.setBulletViewCount(photos.size());

        ViewPager pagerImage = (ViewPager) view.findViewById(R.id.pagerPhoto);
        PhotoPagerAdapter adapter = new PhotoPagerAdapter(getChildFragmentManager(), photos, mImageLoader, mOptions, animateFirstListener);
        pagerImage.setAdapter(adapter);

        pagerImage.setOnPageChangeListener(this);
    }

    private String getDayNameByDayNumber(int day) {
        String result;
        switch (day) {
            case 1:
                result = getString(R.string.monday);
                break;
            case 2:
                result = getString(R.string.tuesday);
                break;
            case 3:
                result = getString(R.string.wednesday);
                break;
            case 4:
                result = getString(R.string.thursday);
                break;
            case 5:
                result = getString(R.string.friday);
                break;
            case 6:
                result = getString(R.string.saturday);
                break;
            case 0:
                result = getString(R.string.sunday);
                break;
            default:
                result = getString(R.string.monday);
                break;
        }

        return result;
    }
}
