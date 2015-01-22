package com.nl.clubbook.model.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.nl.clubbook.utils.ConvertUtils;

import org.json.JSONObject;

/**
 * Created by odats on 05/08/2014
 */
public class UserPhoto implements Parcelable {

    private String id;
    private String url;
    private boolean isAvatar;

    public UserPhoto() {
    }

    public UserPhoto(Parcel in) {
        id = in.readString();
        url = in.readString();
        isAvatar = ConvertUtils.intToBoolean(in.readInt());
    }

    public UserPhoto(JSONObject rawData) {
        id = rawData.optString("_id");
        url = rawData.optString("url");
        isAvatar = rawData.optBoolean("profile");
    }

    public boolean getIsAvatar() {
        return isAvatar;
    }

    public void setIsAvatar(boolean isAvatar) {
        this.isAvatar = isAvatar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
        dest.writeString(url);
        dest.writeInt(ConvertUtils.booleanToInt(isAvatar));
    }

    public static final Parcelable.Creator<UserPhoto> CREATOR = new Parcelable.Creator<UserPhoto>() {

        public UserPhoto createFromParcel(Parcel in) {
            return new UserPhoto(in);
        }

        public UserPhoto[] newArray(int size) {
            return new UserPhoto[size];
        }
    };
}
