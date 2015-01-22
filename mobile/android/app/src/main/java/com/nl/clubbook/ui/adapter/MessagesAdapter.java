package com.nl.clubbook.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nl.clubbook.R;
import com.nl.clubbook.model.data.Chat;
import com.nl.clubbook.model.data.ChatMessage;
import com.nl.clubbook.model.data.User;
import com.nl.clubbook.helper.ImageHelper;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Andrew on 6/2/2014.
 */
public class MessagesAdapter extends BaseAdapter {

    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("dd/MM/yy");

    private Context mContext;
    private List<Chat> mChats = null;
    private LayoutInflater mInflater;

    public MessagesAdapter(Context context, List<Chat> chats) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mChats = chats;
    }

    @Override
    public int getCount() {
        return mChats.size();
    }

    @Override
    public Chat getItem(int position) {
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
            holder.txtLastChatTime = (TextView) row.findViewById(R.id.txtLastChatTime);

            row.setTag(holder);
        } else {
            holder = (MessageItemHolder) row.getTag();
        }

        Chat messageItem = mChats.get(position);
        fillView(holder, messageItem);

        return row;
    }

    public void updateData(List<Chat> newChats) {
        if(newChats == null) {
            return;
        }

        mChats = newChats;
        notifyDataSetChanged();
    }

    private void fillView(MessageItemHolder holder, Chat messageItem) {
        if(messageItem == null) {
            return;
        }

        User receiver = messageItem.getReceiver();

        holder.txtUsername.setText(receiver.getName());
        holder.txtUsername.setTag(receiver.getId());

        List<ChatMessage> conversation = messageItem.getConversation();
        if(conversation != null && !conversation.isEmpty()) {
            ChatMessage lastChatMessage = conversation.get(0);

            if(lastChatMessage != null) {
                holder.txtLastMessage.setText(lastChatMessage.getFormatMessage(mContext));
                holder.txtLastChatTime.setText(mDateFormat.format(lastChatMessage.getTime()));
            }
        }

        int unreadMessageCount = messageItem.getUnreadMessages();
        if(unreadMessageCount == 0) {
            holder.txtUnreadMessagesCount.setVisibility(View.INVISIBLE);
        } else {
            holder.txtUnreadMessagesCount.setVisibility(View.VISIBLE);
        }
        holder.txtUnreadMessagesCount.setText("" + messageItem.getUnreadMessages());

        String avatarUrl = receiver.getAvatar();
        if(avatarUrl != null && avatarUrl.length() != 0) {
            String imageUrl = ImageHelper.getUserListAvatar(avatarUrl);
            Picasso.with(mContext).load(imageUrl).error(R.drawable.ic_avatar_unknown).into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setTag("");
        }
    }

    static class MessageItemHolder {
        ImageView imgAvatar;
        TextView txtUsername;
        TextView txtLastMessage;
        TextView txtUnreadMessagesCount;
        TextView txtLastChatTime;
    }

}
