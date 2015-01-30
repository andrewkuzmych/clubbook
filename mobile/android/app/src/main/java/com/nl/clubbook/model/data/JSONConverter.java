package com.nl.clubbook.model.data;

import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.utils.L;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Volodymyr on 14.08.2014.
 */
public class JSONConverter {

    public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss", Locale.getDefault());
    public static final SimpleDateFormat FORMAT_DATE_WITHOUT_HOURS = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private JSONConverter() {
    }

    public static List<User> newUsersList(@Nullable JSONArray jsonArrUsers, boolean parseCheckin) {
        if(jsonArrUsers == null) {
            return new ArrayList<User>();
        }

        List<User> friends = new ArrayList<User>();
        for (int i = 0; i < jsonArrUsers.length(); i++) {
            JSONObject jsonFriend = jsonArrUsers.optJSONObject(i);
            friends.add(newUser(jsonFriend, parseCheckin));
        }

        return friends;
    }

    public static User newUser(@Nullable JSONObject jsonUser, boolean parseCheckIn) {
        if(jsonUser == null) {
            return null;
        }

        User result = new User();

        result.setId(jsonUser.optString("_id"));
        result.setFbId(jsonUser.optString("fb_id"));
        result.setName(jsonUser.optString("name"));
        result.setEmail(jsonUser.optString("email"));
        result.setGender(jsonUser.optString("gender"));
        result.setBirthday(jsonUser.optString("dob_format"));
        result.setAge(jsonUser.optString("age"));
        result.setCountry(jsonUser.optString("country"));
        result.setAboutMe(jsonUser.optString("bio"));
        result.setFriendStatus(jsonUser.optString("friend_status", ""));
        result.setBlocked(jsonUser.optBoolean("is_blocked", false));

        JSONObject jsonAvatar = jsonUser.optJSONObject("avatar");
        if(jsonAvatar != null) {
            String url = jsonAvatar.optString("url");
            result.setAvatar(url);
        }

        if(parseCheckIn) {
            JSONArray jsonArrCheckIn = jsonUser.optJSONArray("checkin");
            if(jsonArrCheckIn != null) {
                for(int i = 0; i < jsonArrCheckIn.length(); i++) {
                    JSONObject jsonCheckIn = jsonArrCheckIn.optJSONObject(i);
                    CheckIn checkIn = newCheckIn(jsonCheckIn);

                    if(checkIn != null) {
                        result.setLastCheckIn(checkIn);
                        break;
                    }
                }
            }
        }

        //parse user's photos
        JSONArray photosJson = jsonUser.optJSONArray("photos");
        List<UserPhoto> photos = newUserPhotoList(photosJson);
        result.setPhotos(photos);

        return result;
    }

    public static List<UserPhoto> newUserPhotoList(@Nullable JSONArray photosJson) {
        if(photosJson == null) {
            return null;
        }

        List<UserPhoto> photos = new ArrayList<UserPhoto>();
        for (int i = 0; i < photosJson.length(); i++) {
            UserPhoto photo = newUserPhoto(photosJson.optJSONObject(i));
            photos.add(photo);
        }

        return photos;
    }

    public static UserPhoto newUserPhoto(@Nullable JSONObject jsonPhoto) {
        if(jsonPhoto == null) {
            return null;
        }

        UserPhoto photo = new UserPhoto();
        photo.setId(jsonPhoto.optString("_id"));
        photo.setUrl(jsonPhoto.optString("url"));
        photo.setIsAvatar(jsonPhoto.optBoolean("profile"));

        return photo;
    }

    public static CheckIn newCheckIn(@Nullable JSONObject jsonObject) {
        if(jsonObject == null) {
            return null;
        }

        CheckIn checkIn = new CheckIn();
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

    public static List<Place> newPlaceList(@Nullable JSONArray jsonArrPlaces) {
        if(jsonArrPlaces == null) {
            return new ArrayList<Place>();
        }

        List<Place> result = new ArrayList<Place>();
        for(int i = 0; i < jsonArrPlaces.length(); i++) {
            JSONObject jsonPlace = jsonArrPlaces.optJSONObject(i);
            Place place = newPlace(jsonPlace);

            if(place != null) {
                result.add(place);
            }
        }

        return result;
    }

    public static Place newPlace(@Nullable String strJsonClub) {
        if(strJsonClub == null) {
            return null;
        }

        Place place = null;

        try {
            JSONObject jsonPlace = new JSONObject(strJsonClub);
            place = newPlace(jsonPlace);
        } catch (JSONException e) {
            L.v("" + e);
        }

        return place;
    }

    public static Place newPlace(@Nullable JSONObject jsonPlace) {
        if(jsonPlace == null) {
            return null;
        }

        Place place = new Place();

        place.setId(jsonPlace.optString("id"));
        place.setTitle(jsonPlace.optString("club_name"));
        place.setPhone(jsonPlace.optString("club_phone"));
        place.setAddress(jsonPlace.optString("club_address"));
        place.setAvatar(jsonPlace.optString("club_logo"));
        place.setActiveCheckIns(jsonPlace.optInt("active_checkins"));
        place.setActiveFriendsCheckIns(jsonPlace.optInt("active_friends_checkins"));
        place.setInfo(jsonPlace.optString("club_info"));
        place.setAgeRestriction(jsonPlace.optString("club_age_restriction"));
        place.setDressCode(jsonPlace.optString("club_dress_code"));
        place.setCapacity(jsonPlace.optString("club_capacity"));
        place.setWebsite(jsonPlace.optString("club_site"));
        place.setEmail(jsonPlace.optString("club_email"));

        JSONObject jsonClubLocation = jsonPlace.optJSONObject("club_loc");
        if(jsonClubLocation != null) {
            place.setLon(jsonClubLocation.optDouble("lon"));
            place.setLat(jsonClubLocation.optDouble("lat"));
            place.setDistance(LocationCheckinHelper.getInstance().calculateDistance(place.getLat(), place.getLon()));
        }

        List<String> photos = new ArrayList<String>();
        JSONArray jsonArrPhotos = jsonPlace.optJSONArray("club_photos");
        if(jsonArrPhotos != null) {
            for (int i = 0; i < jsonArrPhotos.length(); i++) {
                photos.add(jsonArrPhotos.optString(i, ""));
            }
        }
        place.setPhotos(photos);

        List<ClubWorkingHours> workingHours = new ArrayList<ClubWorkingHours>();
        JSONArray jsonArrHours = jsonPlace.optJSONArray("club_working_hours");
        if(jsonArrHours != null) {
            for(int i = 0; i < jsonArrHours.length(); i++) {
                JSONObject jsonWorkHours = jsonArrHours.optJSONObject(i);
                ClubWorkingHours clubWorkHours = newClubWorkingHours(jsonWorkHours);
                if(clubWorkHours != null) {
                    workingHours.add(clubWorkHours);
                }
            }
        }
        place.setWorkingHours(workingHours);

        JSONObject jsonWorkingHours = jsonPlace.optJSONObject("club_today_working_hours");
        ClubWorkingHours todayWorkingHours = newClubWorkingHours(jsonWorkingHours);
        place.setTodayWorkingHours(todayWorkingHours);

        return place;
    }

    public static JSONObject newClub(@Nullable Place place) {
        if(place == null) {
            return null;
        }

        JSONObject jsonClub = new JSONObject();

        try {
            jsonClub.put("id", place.getId());
            jsonClub.put("club_name", place.getTitle());
            jsonClub.put("club_phone", place.getPhone());
            jsonClub.put("club_address", place.getAddress());
            jsonClub.put("club_logo", place.getAvatar());
            jsonClub.put("active_checkins", place.getActiveCheckIns());
            jsonClub.put("active_friends_checkins", place.getActiveFriendsCheckIns());
            jsonClub.put("club_info", place.getInfo());
            jsonClub.put("club_age_restriction", place.getAgeRestriction());
            jsonClub.put("club_dress_code", place.getDressCode());
            jsonClub.put("club_capacity", place.getCapacity());
            jsonClub.put("club_site", place.getWebsite());
            jsonClub.put("club_email", place.getEmail());

            JSONObject jsonLocation = new JSONObject();
            jsonLocation.put("lon", place.getLon());
            jsonLocation.put("lat", place.getLat());
            jsonClub.put("club_loc", jsonLocation);

            jsonClub.put("club_today_working_hours", newClubWorkingHours(place.getTodayWorkingHours()));

            JSONArray jsonArrPhotos = new JSONArray();
            List<String> photos = place.getPhotos();
            if(photos != null) {
                for (int i = 0; i < photos.size(); i++) {
                    jsonArrPhotos.put(i, photos.get(i));
                }
            }
            jsonClub.put("club_photos", jsonArrPhotos);

            JSONArray jsonArrHours = new JSONArray();
            List<ClubWorkingHours> workingHours = place.getWorkingHours();
            if(workingHours != null) {
                for (int i = 0; i < workingHours.size(); i++) {
                    ClubWorkingHours workHours = workingHours.get(i);
                    jsonArrHours.put(i, newClubWorkingHours(workHours));
                }
            }
            jsonClub.put("club_working_hours", jsonArrHours);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonClub;
    }

    public static ClubWorkingHours newClubWorkingHours(@Nullable JSONObject jsonClubWorkingHours) {
        if(jsonClubWorkingHours == null) {
            return null;
        }

        ClubWorkingHours result = new ClubWorkingHours();
        result.setId(jsonClubWorkingHours.optString("id", ""));
        result.setStatus(jsonClubWorkingHours.optString("status", ""));
        result.setStartTime(jsonClubWorkingHours.optString("start_time", ""));
        result.setEndTime(jsonClubWorkingHours.optString("end_time", ""));
        result.setDay(jsonClubWorkingHours.optInt("day", 0));

        return result;
    }

    public static JSONObject newClubWorkingHours(@Nullable ClubWorkingHours workingHours) throws JSONException {
        if(workingHours == null) {
            return null;
        }

        JSONObject jsonWorkingHours = new JSONObject();
        jsonWorkingHours.put("id", workingHours.getId());
        jsonWorkingHours.put("status", workingHours.getStatus());
        jsonWorkingHours.put("start_time", workingHours.getStartTime());
        jsonWorkingHours.put("end_time", workingHours.getEndTime());
        jsonWorkingHours.put("day", workingHours.getDay());
        jsonWorkingHours.put("id", workingHours.getId());

        return jsonWorkingHours;
    }

    public static Chat newChatDto(@NotNull JSONObject jsonChatDto, int timeZoneTime) {
        Chat result = new Chat();

        result.setChatId(jsonChatDto.optString("chat_id"));

        JSONArray jsonConversation = jsonChatDto.optJSONArray("conversation");
        List<ChatMessage> conversations = newChatMessagesList(jsonConversation, timeZoneTime);
        result.setConversation(conversations);

        JSONObject jsonCurrentUser = jsonChatDto.optJSONObject("current_user");
        User currentUser = newUser(jsonCurrentUser, false);
        result.setCurrentUser(currentUser);

        JSONObject jsonReceiver = jsonChatDto.optJSONObject("receiver");
        User receiverUser = newUser(jsonReceiver, false);
        result.setReceiver(receiverUser);

        result.setUnreadMessages(jsonChatDto.optInt("unread_messages"));

        return result;
    }

    public static List<ChatMessage> newChatMessagesList(@Nullable JSONArray jsonArray, int timeZomeTime) {
        if(jsonArray == null) {
            return null;
        }

        List<ChatMessage> result = new ArrayList<ChatMessage>();
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonChatMessage = jsonArray.optJSONObject(i);
            ChatMessage chatMessage = newChatMessage(jsonChatMessage, timeZomeTime);
            result.add(chatMessage);
        }

        return result;
    }

    public static ChatMessage newChatMessage(@NotNull JSONObject jsonChatMessage, int timeZoneTime) {
        ChatMessage result = new ChatMessage();

        result.setMsg(jsonChatMessage.optString("msg"));
        result.setType(jsonChatMessage.optString("type"));
        result.setUserFrom(jsonChatMessage.optString("from_who"));
        result.setRead(jsonChatMessage.optBoolean("read"));
        result.setIsMyMessage(jsonChatMessage.optBoolean("is_my_message"));
        result.setUserFromName(jsonChatMessage.optString("from_who_name"));

        JSONObject jsonLocation = jsonChatMessage.optJSONObject("location");
        if(jsonLocation != null) {
            double lat = jsonLocation.optDouble("lat");
            double lon = jsonLocation.optDouble("lon");

            Location location = new Location();
            location.setLon(lon);
            location.setLat(lat);

            result.setLocation(location);
        }

        String date = jsonChatMessage.optString("time");
        try {
            result.setTime(FORMAT_DATE.parse(date).getTime() + timeZoneTime);
            result.setTimeWithoutHours(FORMAT_DATE_WITHOUT_HOURS.parse(date).getTime());
        } catch (ParseException e) {
            L.i("" + e);
        }

        JSONObject jsonAvatar = jsonChatMessage.optJSONObject("from_who_avatar");
        if(jsonAvatar != null) {
            result.setUserFromAvatar(jsonAvatar.optString("url"));
        }

        return result;
    }
}
