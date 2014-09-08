package com.nl.clubbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nl.clubbook.R;
import com.nl.clubbook.datasource.ChatDto;
import com.nl.clubbook.datasource.ChatMessageDto;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.helper.ImageHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by Andrew on 6/2/2014.
 */
public class MessagesAdapter extends BaseAdapter {

    private Context mContext;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();
    private List<ChatDto> mChats = null;
    private LayoutInflater mInflater;

    public MessagesAdapter(Context context, List<ChatDto> chats) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mChats = chats;

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_avatar_missing)
                .showImageForEmptyUri(R.drawable.ic_avatar_missing)
                .showImageOnFail(R.drawable.ic_avatar_unknown)
                .cacheInMemory()
                .cacheOnDisc()
                .build();
    }

    @Override
    public int getCount() {
        return mChats.size();
    }

    @Override
    public ChatDto getItem(int position) {
        return mChats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MessageItemHolder holder;

        if (row == null) {
            row = mInflater.inflate(R.layout.item_list_message, null);

            holder = new MessageItemHolder();
            holder.imgAvatar = (ImageView) row.findViewById(R.id.imgAvatar);
            holder.txtUsername = (TextView) row.findViewById(R.id.txtUsername);
            holder.txtLastMessage = (TextView) row.findViewById(R.id.txtLastMessage);
            holder.txtUnreadMessagesCount = (TextView) row.findViewById(R.id.txtUnreadMessagesCount);

            row.setTag(holder);
        } else {
            holder = (MessageItemHolder) row.getTag();
        }

        ChatDto messageItem = mChats.get(position);
        fillView(holder, messageItem);

        return row;
    }

    public void updateData(List<ChatDto> newChats) {
        if(newChats == null) {
            return;
        }

        mChats = newChats;
        notifyDataSetChanged();
    }

    private void fillView(MessageItemHolder holder, ChatDto messageItem) {
        if(messageItem == null) {
            return;
        }

        UserDto receiver = messageItem.getReceiver();

        holder.txtUsername.setText(receiver.getName());
        holder.txtUsername.setTag(receiver.getId());

        List<ChatMessageDto> conversation = messageItem.getConversation();
        if(conversation != null && !conversation.isEmpty()) {
            holder.txtLastMessage.setText(conversation.get(0).getFormatMessage(mContext));
        }

        int unreadMessageCount = messageItem.getUnreadMessages();
        if(unreadMessageCount == 0) {
            holder.txtUnreadMessagesCount.setVisibility(View.GONE);
        } else {
            holder.txtUnreadMessagesCount.setVisibility(View.VISIBLE);
        }
        holder.txtUnreadMessagesCount.setText("" + messageItem.getUnreadMessages());

        String avatarUrl = receiver.getAvatar();
        if(avatarUrl != null && avatarUrl.length() != 0) {
            String imageUrl = ImageHelper.getUserListAvatar(avatarUrl);
            imageLoader.displayImage(imageUrl, holder.imgAvatar, options, animateFirstListener);
            holder.imgAvatar.setTag(imageUrl);
        } else {
            holder.imgAvatar.setTag("");
        }
    }

    static class MessageItemHolder {
        ImageView imgAvatar;
        TextView txtUsername;
        TextView txtLastMessage;
        TextView txtUnreadMessagesCount;
    }

}
