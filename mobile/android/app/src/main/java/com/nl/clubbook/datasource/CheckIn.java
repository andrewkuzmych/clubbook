package com.nl.clubbook.datasource;

import android.os.Parcel;
import android.os.Parcelable;

import com.nl.clubbook.utils.ConvertUtils;

/**
 * Created by Volodymyr on 14.08.2014.
 */
public class CheckIn implements Parcelable {

    private String id;
    private boolean isActive;
    private String clubId;
    private String clubAddress;
    private String clubName;

    public CheckIn() {
    }

    public CheckIn(Parcel in) {
        id = in.readString();
        isActive = ConvertUtils.intToBoolean(in.readInt());
        clubId = in.readString();
        clubAddress = in.readString();
        clubName = in.readString();
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getClubAddress() {
        return clubAddress;
    }

    public void setClubAddress(String clubAddress) {
        this.clubAddress = clubAddress;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(ConvertUtils.booleanToInt(isActive));
        dest.writeString(clubId);
        dest.writeString(clubAddress);
        dest.writeString(clubName);
    }

    public static final Creator<CheckIn> CREATOR = new Creator<CheckIn>() {

        @Override
        public CheckIn createFromParcel(Parcel source) {
            return new CheckIn(source);
        }

        @Override
        public CheckIn[] newArray(int size) {
            return new CheckIn[0];
        }
    };
}

