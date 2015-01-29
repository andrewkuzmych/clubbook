package com.nl.clubbook.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.model.data.Place;
import com.nl.clubbook.model.data.ClubWorkingHours;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.utils.L;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Volodymyr on 15.08.2014.
 */
public class ClubInfoFragment extends BaseFragment {

    private Place mPlace;

    public static Fragment newInstance(Place place) {
        ClubInfoFragment fragment = new ClubInfoFragment();

        fragment.setPlace(place);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_club_info, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sendScreenStatistic(R.string.club_screen_android);

        initTarget();
        initView();
    }

    private void setPlace(Place place) {
        mPlace = place;
    }

    private void initView() {
        View view = getView();
        if(view == null || isDetached() || getActivity() == null) {
            return;
        }

        fillView(view);
    }

    private void fillView(@NotNull View view) {
        if(mPlace == null) {
            view.findViewById(R.id.parallaxScrollView).setVisibility(View.GONE);
            return;
        }

        List<String> photos = mPlace.getPhotos();
        if(photos != null && !photos.isEmpty()) {
            ImageView imgClubPhoto = (ImageView) view.findViewById(R.id.imgClubPhoto);
            String url = photos.get(0);
            Picasso.with(getActivity()).load(url).error(R.drawable.ic_club_avatar_default).into(imgClubPhoto);
        }

        String info = mPlace.getInfo();
        if(!setTextToTextView(view, info, R.id.txtAbout)) {
            view.findViewById(R.id.dividerInfo).setVisibility(View.GONE);
            view.findViewById(R.id.txtLabelAbout).setVisibility(View.GONE);
        }

        fillLocationHolder(view, mPlace);
        fillClubRequirementsHolder(view, mPlace);
        fillContactHolder(view, mPlace);
        fillWorkingHoursHolder(view, mPlace);
    }

    private void fillContactHolder(View view, Place place) {
        boolean result = false;

        if(fillTextViewHolder(view, place.getPhone(), R.id.txtPhone, R.id.txtPhone)) {
            result = true;
        }

        if(fillTextViewHolder(view, place.getWebsite(), R.id.txtWebsite, R.id.txtWebsite)) {
            result = true;
        }

        if(fillTextViewHolder(view, place.getEmail(), R.id.txtEmail, R.id.txtEmail)) {
            result = true;
        }

        if(!result) {
            view.findViewById(R.id.holderContacts).setVisibility(View.GONE);
            view.findViewById(R.id.holderClubRequirements).setVisibility(View.GONE);
        }
    }

    private void fillLocationHolder(View view, Place place) {
        boolean result = false;

        TextView txtLocation = (TextView) view.findViewById(R.id.txtLocation);
        String address = place.getAddress();
        if(address != null && address.length() > 0) {
            result = true;
            txtLocation.setText(address != null ? address : "");
        } else {
            txtLocation.setVisibility(View.INVISIBLE);
        }

        String distance = LocationCheckinHelper.formatDistance(getActivity().getApplicationContext(), place.getDistance());
        TextView txtDistance = (TextView) view.findViewById(R.id.txtDistance);
        if(distance != null && distance.length() > 0) {
            result = true;
            txtDistance.setText(distance);
        } else {
            txtDistance.setVisibility(View.GONE);
        }

        if(!result) {
            view.findViewById(R.id.holderLocation).setVisibility(View.GONE);
            view.findViewById(R.id.dividerLocation).setVisibility(View.GONE);
        }
    }

    private void fillClubRequirementsHolder(View view, Place place) {
        boolean result = false;

        if(fillTextViewHolder(view, place.getAgeRestriction(), R.id.txtAgeRestriction, R.id.holderAgeRestriction)) {
            result = true;
        }

        if(fillTextViewHolder(view, place.getCapacity(), R.id.txtCapacity, R.id.holderCapacity)) {
            result = true;
        }

        if(!result) {
            view.findViewById(R.id.holderRequirements).setVisibility(View.GONE);
            view.findViewById(R.id.holderClubRequirements).setVisibility(View.GONE);
        }
    }

    private void fillWorkingHoursHolder(View view, Place place) {
        boolean result = false;

        //fill today working hours
        TextView txtHours = (TextView) view.findViewById(R.id.txtHours);
        ClubWorkingHours workHours = place.getTodayWorkingHours();
        if(workHours != null) {
            result = true;

            boolean isClosed = false;

            TextView txtOpenStatus = (TextView) view.findViewById(R.id.txtOpenStatus);
            String status = workHours.getStatus();
            if(ClubWorkingHours.STATUS_OPENED.equalsIgnoreCase(status)) {
                txtOpenStatus.setTextColor(getResources().getColor(R.color.green));
                txtOpenStatus.setText(R.string.open);
            } else {
                isClosed = true;
                txtOpenStatus.setTextColor(getResources().getColor(R.color.red_light));
                txtOpenStatus.setText(R.string.closed_display);
            }

            String startTime = workHours.getStartTime();
            String endTime = workHours.getEndTime();

            if(!isClosed && startTime != null && endTime != null) {
                StringBuilder todayHoursToDisplay = new StringBuilder();
                todayHoursToDisplay.append(getString(R.string.hours));
                todayHoursToDisplay.append(" ");
                todayHoursToDisplay.append(!startTime.isEmpty() ? startTime + " - " : "");
                todayHoursToDisplay.append(endTime);

                txtHours.setText(todayHoursToDisplay.toString());
            } else {
                txtHours.setVisibility(View.INVISIBLE);
            }
        } else {
            txtHours.setVisibility(View.INVISIBLE);
        }

        //fill week working hours
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        int calendarDayOfWeekIndex =  calendar.get(Calendar.DAY_OF_WEEK);

        LinearLayout holderWorkingHours = (LinearLayout)view.findViewById(R.id.holderWorkingHours);
        List<ClubWorkingHours> workingHours = place.getWorkingHours();
        if(workingHours != null && !workingHours.isEmpty()) {
            result = true;
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            for (ClubWorkingHours clubWorkHours : workingHours) {
                View row;
                if(calendarDayOfWeekIndex - 1 == clubWorkHours.getDay()) {
                    row = inflater.inflate(R.layout.view_club_work_hours_bold, null);
                } else {
                    row = inflater.inflate(R.layout.view_club_work_hours, null);
                }

                String dayName = getDayNameByDayNumber(clubWorkHours.getDay());
                TextView txtDayName = (TextView) row.findViewById(R.id.txtDayName);
                txtDayName.setText(dayName != null ? dayName : "");

                TextView txtWorkHours = (TextView) row.findViewById(R.id.txtWorkHours);
                if(ClubWorkingHours.STATUS_OPENED.equals(clubWorkHours.getStatus())) {
                    String startTime = clubWorkHours.getStartTime();
                    String endTime = clubWorkHours.getEndTime();

                    txtWorkHours.setText(!TextUtils.isEmpty(startTime) ? startTime + " - " : "");
                    txtWorkHours.append(endTime != null ? endTime : "");
                } else {
                    txtWorkHours.setText(getString(R.string.closed));
                }

                holderWorkingHours.addView(row);
            }
        }

        if(!result) {
            holderWorkingHours.setVisibility(View.GONE);
            view.findViewById(R.id.dividerHours).setVisibility(View.GONE);
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
