package com.nl.clubbook.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.datasource.BaseChatMessage;
import com.nl.clubbook.datasource.ChatMessage;
import com.nl.clubbook.datasource.Location;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.utils.CalendarUtils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Andrew on 6/6/2014.
 */
public class ChatAdapter extends ArrayAdapter<BaseChatMessage> {

    private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    private final SimpleDateFormat mDateMsgWithDay = new SimpleDateFormat("hh:mm aaa, d MMM", Locale.getDefault());
    private final SimpleDateFormat mDateMsgToday = new SimpleDateFormat("hh:mm aaa", Locale.getDefault());

    private static final int TYPE_MESSAGE = 0;
    private static final int TYPE_DRINK = 1;
    private static final int TYPE_SMILE = 2;
    private static final int TYPE_DATE = 3;
    private static final int TYPE_LOCATION = 4;
    private static final int TYPE_PHOTO = 5;

    private static final int TYPE_COUNT = 6; //date item

    private LayoutInflater mInflater;
    private List<BaseChatMessage> mMessages;

    private Context mContext;
    private View.OnClickListener mOnClickListener;
    private long mCurrentTimeWithoutHours;
    private long mDayTimeInMilliseconds;
    private String mToday;
    private String mYesterday;

    private int mLocationImageSize;

    public ChatAdapter(Context context, int textViewResourceId, List<BaseChatMessage> messages, View.OnClickListener onClickListener) {
        super(context, textViewResourceId);

        mContext = context;
        mMessages = messages;
        mInflater = LayoutInflater.from(context);

        mOnClickListener = onClickListener;

        mCurrentTimeWithoutHours = CalendarUtils.getCurrentTimeWithoutHours();
        mDayTimeInMilliseconds = CalendarUtils.getDayTimeInMilliseconds();
        mToday = context.getString(R.string.today);
        mYesterday = context.getString(R.string.yesterday);

        mLocationImageSize = (int) context.getResources().getDimension(R.dimen.size_location_image);
    }

    @Override
    public void add(BaseChatMessage object) {
        mMessages.add(object);
        super.add(object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseChatMessage message = getItem(position);
        if(message.isDateObject()) {
            return initDateRow(message);
        }

        ChatMessage chatMessage = (ChatMessage)message;
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_MESSAGE:
                return initMessageRow(chatMessage);
            case TYPE_LOCATION:
                return initLocationRow(chatMessage);
            case TYPE_DRINK:
                return initDrinkRow(chatMessage);

            default:
                return initMessageRow(chatMessage);
        }
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public BaseChatMessage getItem(int index) {
        return mMessages.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        BaseChatMessage baseMessage = mMessages.get(position);
        if(baseMessage.isDateObject()) {
            return TYPE_DATE;
        }

        ChatMessage chatMessage = (ChatMessage) baseMessage;
        String type = chatMessage.getType();

        if (ChatMessage.Types.TYPE_MESSAGE.equalsIgnoreCase(type)) {
            return TYPE_MESSAGE;
        } else if(ChatMessage.Types.TYPE_SMILE.equalsIgnoreCase(type)){
            return TYPE_SMILE;
        } else if(ChatMessage.Types.TYPE_LOCATION.equalsIgnoreCase(type))  {
            return TYPE_LOCATION;
        } else if(ChatMessage.Types.TYPE_PHOTO.equalsIgnoreCase(type)) {
            return TYPE_PHOTO;
        } else {
            return TYPE_DRINK;
        }
    }

    public List<BaseChatMessage> getMessages() {
        return mMessages;
    }

    private View initDateRow(BaseChatMessage dateMessage) {
        View row = mInflater.inflate(R.layout.item_date, null);

        TextView txtDate = (TextView) row.findViewById(R.id.txtDate);
        long messageTime = dateMessage.getTimeWithoutHours();
        if(messageTime == mCurrentTimeWithoutHours) {
            txtDate.setText(mToday);
        } else if(messageTime == mCurrentTimeWithoutHours - mDayTimeInMilliseconds) {
            txtDate.setText(mYesterday);
        } else {
            txtDate.setText(mFormat.format(messageTime));
        }

        return row;
    }

    private View initMessageRow(ChatMessage message) {
        View row;

        if(message.getIsMyMessage()) {
            row = mInflater.inflate(R.layout.item_chat_right, null);
        } else {
            row = mInflater.inflate(R.layout.item_chat_left, null);
        }

        fillMsgRow(row, message);

        return row;
    }

    private View initLocationRow(ChatMessage message) {
        View row;

        if(message.getIsMyMessage()) {
            row = mInflater.inflate(R.layout.item_chat_location_right, null);
        } else {
            row = mInflater.inflate(R.layout.item_chat_location_left, null);
        }

        fillLocationRow(row, message);

        return row;
    }

    private View initDrinkRow(ChatMessage message) {
        View row;

        if(message.getIsMyMessage()) {
            row = mInflater.inflate(R.layout.item_chat_drink_right, null);
        } else {
            row = mInflater.inflate(R.layout.item_chat_drink_left, null);
        }

        fillMsgRow(row, message);

        return row;
    }

    private void fillMsgRow(View row, ChatMessage message) {
        TextView txtDate = (TextView) row.findViewById(R.id.txtDate);
        ImageView imgAvatar = (ImageView) row.findViewById(R.id.imgAvatar);
        TextView txtChatMessage = (TextView) row.findViewById(R.id.txtChatMessage);

        long messageTime = message.getTime();
        if(messageTime < mCurrentTimeWithoutHours) {
            txtDate.setText(mDateMsgWithDay.format(messageTime));
        } else {
            txtDate.setText(mDateMsgToday.format(messageTime));
        }

        String avatarString = message.getUserFromAvatar();
        String avatarUrl = ImageHelper.getUserListAvatar(avatarString);
        if(avatarUrl != null && avatarUrl.length() > 0) {
            Picasso.with(mContext).load(avatarUrl).error(R.drawable.ic_avatar_unknown).into(imgAvatar);
        }

        String msg = message.getMsg();
        if(msg != null) {
            txtChatMessage.setText(msg.trim());
        }

        if(!message.getIsMyMessage()) {
            imgAvatar.setOnClickListener(mOnClickListener);
        } else {
            imgAvatar.setOnClickListener(null);
        }
    }

    private void fillLocationRow(View row, ChatMessage message) {
        TextView txtDate = (TextView) row.findViewById(R.id.txtDate);
        ImageView imgAvatar = (ImageView) row.findViewById(R.id.imgAvatar);
        ImageView imgLocation = (ImageView) row.findViewById(R.id.imgLocation);

        long messageTime = message.getTime();
        if(messageTime < mCurrentTimeWithoutHours) {
            txtDate.setText(mDateMsgWithDay.format(messageTime));
        } else {
            txtDate.setText(mDateMsgToday.format(messageTime));
        }

        String avatarString = message.getUserFromAvatar();
        String avatarUrl = ImageHelper.getUserListAvatar(avatarString);
        if(avatarUrl != null && avatarUrl.length() > 0) {
            Picasso.with(mContext).load(avatarUrl).error(R.drawable.ic_avatar_unknown).into(imgAvatar);
        }

        final Location location = message.getLocation();
        if(location != null) {
            String url = getLocationImageUrl(location);
            Picasso.with(mContext).load(url).into(imgLocation);

            imgLocation.setTag(location);
            imgLocation.setOnClickListener(mOnClickListener);
        } else {
            imgLocation.setTag(null);
            imgLocation.setOnClickListener(null);
        }

        if(!message.getIsMyMessage()) {
            imgAvatar.setOnClickListener(mOnClickListener);
        } else {
            imgAvatar.setOnClickListener(null);
        }
    }

    private String getLocationImageUrl(@NonNull Location location) {
        StringBuilder url = new StringBuilder();
        url.append("https://maps.googleapis.com/maps/api/staticmap?");
        url.append("&size=");
        url.append(mLocationImageSize);
        url.append("x");
        url.append(mLocationImageSize);
        url.append("&zoom=16&maptype=roadmap");
        url.append("&markers=color:red%7C");
        url.append(location.getLat());
        url.append(",");
        url.append(location.getLon());

        return url.toString();
    }
}
