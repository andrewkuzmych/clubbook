package com.nl.clubbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.datasource.BaseChatMessage;
import com.nl.clubbook.datasource.ChatMessage;
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

    private static final int TYPE_MESSAGE = 0;
    private static final int TYPE_DRINK = 1;
    private static final int TYPE_SMILE = 2;
    private static final int TYPE_DATE = 3;

    private static final int TYPE_COUNT = 4; //date item

    private LayoutInflater mInflater;
    private List<BaseChatMessage> mMessages;

    private Context mContext;
    private View.OnClickListener mUserProfileClickListener;
    private long mCurrentTimeWithoutHours;
    private long mDayTimeInMilliseconds;
    private String mToday;
    private String mYesterday;

    public ChatAdapter(Context context, int textViewResourceId, List<BaseChatMessage> messages, View.OnClickListener userProfileClickListener) {
        super(context, textViewResourceId);

        mContext = context;
        mMessages = messages;
        mInflater = LayoutInflater.from(context);

        mUserProfileClickListener = userProfileClickListener;

        mCurrentTimeWithoutHours = CalendarUtils.getCurrentTimeWithoutHours();
        mDayTimeInMilliseconds = CalendarUtils.getDayTimeInMilliseconds();
        mToday = context.getString(R.string.today);
        mYesterday = context.getString(R.string.yesterday);
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

        int type = getItemViewType(position);
        if (type == TYPE_DRINK) {
            return initDrinkRow((ChatMessage)message);
        } else {
            return initMessageRow((ChatMessage)message);
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

        if (ChatMessage.TYPE_MESSAGE.equalsIgnoreCase(type)) {
            return TYPE_MESSAGE;
        } else if(ChatMessage.TYPE_DRINK.equalsIgnoreCase(type)) {
            return TYPE_DRINK;
        } else {
            return TYPE_SMILE;
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

        fillRow(row, message);

        return row;
    }

    private View initDrinkRow(ChatMessage message) {
        View row;

        if(message.getIsMyMessage()) {
            row = mInflater.inflate(R.layout.item_chat_drink_right, null);
        } else {
            row = mInflater.inflate(R.layout.item_chat_drink_left, null);
        }

        fillRow(row, message);

        return row;
    }

    private void fillRow(View row, ChatMessage message) {
        ImageView imgAvatar = (ImageView) row.findViewById(R.id.imgAvatar);
        TextView txtChatMessage = (TextView) row.findViewById(R.id.txtChatMessage);

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
            imgAvatar.setTag(message.getUserFrom());
            imgAvatar.setOnClickListener(mUserProfileClickListener);
        } else {
            imgAvatar.setTag(null);
            imgAvatar.setOnClickListener(null);
        }
    }

}
