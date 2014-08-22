package com.nl.clubbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.datasource.ChatMessageDto;
import com.nl.clubbook.helper.ImageHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 6/6/2014.
 */
public class ChatAdapter extends ArrayAdapter<ChatMessageDto> {

    private static final int TYPE_MESSAGE = 0;
    private static final int TYPE_DRINK = 1;
    private static final int TYPE_SMILE = 2;

    private static final int TYPE_COUNT = 3;

    private Context mContext;
    private LayoutInflater mInflater;
    private List<ChatMessageDto> mMessages = new ArrayList<ChatMessageDto>();

    protected ImageLoader mImageLoader;
    protected DisplayImageOptions mOptions;

    public ChatAdapter(Context context, int textViewResourceId, List<ChatMessageDto> messages) {
        super(context, textViewResourceId);

        mContext = context;
        mMessages = messages;
        mInflater = LayoutInflater.from(context);

        mImageLoader = ImageLoader.getInstance();
        mOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_avatar_missing)
                .showImageForEmptyUri(R.drawable.ic_avatar_missing)
                .showImageOnFail(R.drawable.ic_avatar_unknown)
                .cacheInMemory()
                .cacheOnDisc()
                .build();
    }

    @Override
    public void add(ChatMessageDto object) {
        mMessages.add(object);
        super.add(object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        ChatMessageDto message = getItem(position);

        int type = getItemViewType(position);
        if (type == TYPE_DRINK) {
            row = initDrinkRow(message);
        } else {
            row = initMessageRow(message);
        }

        return row;
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public ChatMessageDto getItem(int index) {
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
        ChatMessageDto chatMessageDto = mMessages.get(position);
        String type = chatMessageDto.getType();

        if (ChatMessageDto.TYPE_MESSAGE.equalsIgnoreCase(type)) {
            return TYPE_MESSAGE;
        } else if(ChatMessageDto.TYPE_DRINK.equalsIgnoreCase(type)) {
            return TYPE_DRINK;
        } else {
            return TYPE_SMILE;
        }
    }

    private View initMessageRow(ChatMessageDto message) {
        View row;

        if(message.getIsMyMessage()) {
            row = mInflater.inflate(R.layout.item_chat_left, null);
        } else {
            row = mInflater.inflate(R.layout.item_chat_right, null);
        }

        fillRow(row, message);

        return row;
    }

    private View initDrinkRow(ChatMessageDto message) {
        View row;

        if(message.getIsMyMessage()) {
            row = mInflater.inflate(R.layout.item_chat_drink_left, null);
        } else {
            row = mInflater.inflate(R.layout.item_chat_drink_right, null);
        }

        fillRow(row, message);

        return row;
    }

    private void fillRow(View row, ChatMessageDto message) {
        ImageView imgAvatar = (ImageView) row.findViewById(R.id.imgAvatar);
        TextView txtChatMessage = (TextView) row.findViewById(R.id.txtChatMessage);

        String avatarString = message.getUserFromAvatar();
        String avatarUrl = ImageHelper.getUserListAvatar(avatarString);
        if(avatarUrl != null && avatarUrl.length() > 0) {
            mImageLoader.displayImage(message.getUserFromAvatar(), imgAvatar, mOptions);
        }

        String chatMessage = message.getFormatMessage(mContext);
        txtChatMessage.setText(chatMessage);
    }

}
