package com.nl.clubbook.datasource;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Volodymyr on 14.08.2014.
 */
public class JSONConverter {

    private JSONConverter() {
    }

    public static List<UserDto> newFriendList(JSONArray jsonArrUsers) {
        if(jsonArrUsers == null) {
            return null;
        }

        List<UserDto> friends = new ArrayList<UserDto>();
        for (int i = 0; i < jsonArrUsers.length(); i++) {
            JSONObject jsonFriend = jsonArrUsers.optJSONObject(i);
            friends.add(newFriend(jsonFriend, true));
        }

        return friends;
    }

    public static UserDto newFriend(JSONObject jsonUser, boolean parseCheckIn) {
        if(jsonUser == null) {
            return null;
        }

        UserDto result = new UserDto();

        result.setId(jsonUser.optString("_id"));
        result.setFb_id(jsonUser.optString("fb_id"));
        result.setName(jsonUser.optString("name"));
        result.setEmail(jsonUser.optString("email"));
        result.setGender(jsonUser.optString("gender"));
        result.setDob(jsonUser.optString("dob_format"));
        result.setAge(jsonUser.optString("age"));
        result.setCountry(jsonUser.optString("country"));
        result.setBio(jsonUser.optString("bio"));

        JSONObject jsonAvatar = jsonUser.optJSONObject("avatar");
        if(jsonAvatar != null) {
            String url = jsonAvatar.optString("url");
            result.setAvatar(url);
        }

        if(parseCheckIn) {
            JSONArray jsonArrCheckIn = jsonUser.optJSONArray("jsonUser");
            if(jsonArrCheckIn != null) {
                for(int i = 0; i < jsonArrCheckIn.length(); i++) {
                    JSONObject jsonCheckIn = jsonArrCheckIn.optJSONObject(i);
                    CheckInDto checkIn = newCheckIn(jsonCheckIn);

                    if(checkIn != null) {
                        break;
                    }
                }
            }
        }

        //parse user's photos
        JSONArray photosJson = jsonUser.optJSONArray("photos");
        List<UserPhotoDto> photos = newUserPhotoList(photosJson);
        result.setPhotos(photos);

        return result;
    }

    public static List<UserPhotoDto> newUserPhotoList(JSONArray photosJson) {
        if(photosJson == null) {
            return null;
        }

        List<UserPhotoDto> photos = new ArrayList<UserPhotoDto>();
        for (int i = 0; i < photosJson.length(); i++) {
            UserPhotoDto photo = newUserPhoto(photosJson.optJSONObject(i));
            photos.add(photo);
        }

        return photos;
    }

    public static UserPhotoDto newUserPhoto(JSONObject jsonPhoto) {
        if(jsonPhoto == null) {
            return null;
        }

        UserPhotoDto photo = new UserPhotoDto();
        photo.setId(jsonPhoto.optString("_id"));
        photo.setUrl(jsonPhoto.optString("url"));
        photo.setIsAvatar(jsonPhoto.optBoolean("profile"));

        return photo;
    }

    public static CheckInDto newCheckIn(JSONObject jsonObject) {
        if(jsonObject == null) {
            return null;
        }

        CheckInDto checkIn = new CheckInDto();
        checkIn.setId(jsonObject.optString("id", ""));
        checkIn.setActive(jsonObject.optBoolean("active"));

        JSONObject jsonClub = jsonObject.optJSONObject("club");
        if(jsonClub != null) {
            checkIn.setClubAddress(jsonClub.optString("club_address", ""));
            checkIn.setClubId(jsonClub.optString("_id", ""));
            checkIn.setClubName(jsonClub.optString("club_name", ""));
        }

        return checkIn;
    }
}
