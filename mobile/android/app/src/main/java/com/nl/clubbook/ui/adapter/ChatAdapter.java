package com.nl.clubbook.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.model.data.BaseChatMessage;
import com.nl.clubbook.model.data.ChatMessage;
import com.nl.clubbook.model.data.Location;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.utils.CalendarUtils;
import com.nl.clubbook.utils.CircleTransformation;
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

    private static final int TYPE_COUNT = 6; //date item

    private LayoutInflater mInflater;
    private List<BaseChatMessage> mMessages;

    private Context mContext;
    private View.OnClickListener mOnClickListener;
    private long mCurrentTimeWithoutHours;
    private long mDayTimeInMilliseconds;
    private String mToday;
    private String mYesterday;

    private int mImageSize;

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

        mImageSize = (int) context.getResources().getDimension(R.dimen.size_chat_image);
    }

    @Override
    public void add(BaseChatMessage object) {
        mMessages.add(object);
        super.add(object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        BaseChatMessage message = mMessages.get(position);
        int type = getItemViewType(position);
        switch (type) {
            case Type.TYPE_DATE:
                row = initDateRow(row, message);
                break;
            case Type.TYPE_MESSAGE:
                row = initMessageRow((ChatMessage) message);
                break;
            case Type.TYPE_LOCATION:
                row = initLocationRow((ChatMessage) message);
                break;
            case Type.TYPE_PHOTO:
                row = initPhotoRow((ChatMessage) message);
                break;
            case Type.TYPE_DRINK:
                row = initDrinkRow((ChatMessage) message);
                break;

            default:
                row = initMessageRow((ChatMessage) message);
        }

        return row;
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
            return Type.TYPE_DATE;
        }

        ChatMessage chatMessage = (ChatMessage) baseMessage;
        String type = chatMessage.getType();

        if (ChatMessage.Types.TYPE_MESSAGE.equalsIgnoreCase(type)) {
            return Type.TYPE_MESSAGE;
        } else if(ChatMessage.Types.TYPE_SMILE.equalsIgnoreCase(type)){
            return Type.TYPE_SMILE;
        } else if(ChatMessage.Types.TYPE_LOCATION.equalsIgnoreCase(type))  {
            return Type.TYPE_LOCATION;
        } else if(ChatMessage.Types.TYPE_PHOTO.equalsIgnoreCase(type)) {
            return Type.TYPE_PHOTO;
        } else {
            return Type.TYPE_DRINK;
        }
    }

    public List<BaseChatMessage> getMessages() {
        return mMessages;
    }

    private View initDateRow(View row, BaseChatMessage dateMessage) {
        DateViewHolder holder;

        if(row == null) {
            row = mInflater.inflate(R.layout.item_date, null);
            holder = new DateViewHolder();

            holder.txtDate = (TextView) row.findViewById(R.id.txtDate);

            row.setTag(holder);
        } else {
            holder = (DateViewHolder) row.getTag();
        }

        fillDateRow(holder, dateMessage);

        return row;
    }

    private View initMessageRow(ChatMessage message) {
        View row;

        if(message.getIsMyMessage()) {
            row = mInflater.inflate(R.layout.item_chat_message_right, null);
        } else {
            row = mInflater.inflate(R.layout.item_chat_message_left, null);
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

    private View initPhotoRow(ChatMessage message) {
        View row;

        if(message.getIsMyMessage()) {
            row = mInflater.inflate(R.layout.item_chat_photo_right, null);
        } else {
            row = mInflater.inflate(R.layout.item_chat_photo_left, null);
        }

        fillPhotoRow(row, message);

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

    private void fillDateRow(DateViewHolder holder, BaseChatMessage dateMessage) {
        long messageTime = dateMessage.getTimeWithoutHours();
        if(messageTime == mCurrentTimeWithoutHours) {
            holder.txtDate.setText(mToday);
        } else if(messageTime == mCurrentTimeWithoutHours - mDayTimeInMilliseconds) {
            holder.txtDate.setText(mYesterday);
        } else {
            holder.txtDate.setText(mFormat.format(messageTime));
        }
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
            Picasso.with(mContext).load(avatarUrl).transform(new CircleTransformation()).error(R.drawable.ic_avatar_unknown).into(imgAvatar);
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
            Picasso.with(mContext).load(avatarUrl).transform(new CircleTransformation()).error(R.drawable.ic_avatar_unknown).into(imgAvatar);
        }

        final Location location = message.getLocation();
        if(location != null) {
            String url = getLocationImageUrl(location);
            Picasso.with(mContext).load(url).into(imgLocation);

//            imgLocation.setTag(location); //TODO
            imgLocation.setOnClickListener(mOnClickListener);
        } else {
//            imgLocation.setTag(null);
            imgLocation.setOnClickListener(null);
        }

        if(!message.getIsMyMessage()) {
            imgAvatar.setOnClickListener(mOnClickListener);
        } else {
            imgAvatar.setOnClickListener(null);
        }
    }

    private void fillPhotoRow(View row, ChatMessage message) {
        TextView txtDate = (TextView) row.findViewById(R.id.txtDate);
        ImageView imgAvatar = (ImageView) row.findViewById(R.id.imgAvatar);
        ImageView imgPhoto = (ImageView) row.findViewById(R.id.imgPhoto);

        long messageTime = message.getTime();
        if(messageTime < mCurrentTimeWithoutHours) {
            txtDate.setText(mDateMsgWithDay.format(messageTime));
        } else {
            txtDate.setText(mDateMsgToday.format(messageTime));
        }

        String avatarString = message.getUserFromAvatar();
        String avatarUrl = ImageHelper.getUserListAvatar(avatarString);
        if(avatarUrl != null && avatarUrl.length() > 0) {
            Picasso.with(mContext).load(avatarUrl).transform(new CircleTransformation()).error(R.drawable.ic_avatar_unknown).into(imgAvatar);
        }

        String url = message.getUrl();
        if(!TextUtils.isEmpty(url)) {
            String generatedUrl = ImageHelper.getChatMessagePhotoUrl(url, mImageSize);
            Picasso.with(mContext).load(generatedUrl).into(imgPhoto);

            imgPhoto.setOnClickListener(mOnClickListener); //TODO
        } else {
            imgPhoto.setImageDrawable(null);
            imgPhoto.setOnClickListener(null);
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
        url.append(mImageSize);
        url.append("x");
        url.append(mImageSize);
        url.append("&zoom=16&maptype=roadmap");
        url.append("&markers=color:red%7C");
        url.append(location.getLat());
        url.append(",");
        url.append(location.getLon());

        return url.toString();
    }

    private class DateViewHolder {
        TextView txtDate;
    }

    private interface Type {
        public final int TYPE_MESSAGE = 0;
        public final int TYPE_DRINK = 1;
        public final int TYPE_SMILE = 2;
        public final int TYPE_DATE = 3;
        public final int TYPE_LOCATION = 4;
        public final int TYPE_PHOTO = 5;
    }
}
