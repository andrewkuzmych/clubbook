package com.nl.clubbook.datasource;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Volodymyr on 15.08.2014.
 */
public class ClubWorkingHours implements Parcelable {

    public static final String STATUS_OPENED = "opened";
    public static final String STATUS_CLOSED = "closed";

    private String id;
    private String status;
    private String startTime;
    private String endTime;
    private int day;

    public ClubWorkingHours(){
    }

    public ClubWorkingHours(Parcel in) {
        id = in.readString();
        status = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        day = in.readInt();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(status);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeInt(day);
    }

    public static final Creator<ClubWorkingHours> CREATOR = new Creator<ClubWorkingHours>() {
        @Override
        public ClubWorkingHours createFromParcel(Parcel source) {
            return new ClubWorkingHours(source);
        }

        @Override
        public ClubWorkingHours[] newArray(int size) {
            return new ClubWorkingHours[size];
        }
    };
}
