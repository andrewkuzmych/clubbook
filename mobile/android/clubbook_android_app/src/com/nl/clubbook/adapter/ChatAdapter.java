package com.nl.clubbook.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nl.clubbook.R;
import com.nl.clubbook.datasource.ChatMessageDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 6/6/2014.
 */
public class ChatAdapter extends ArrayAdapter<ChatMessageDto> {

    private static final int TYPE_MESSAGE = 0;
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_COUNT = 2;

    private TextView message;
    private ImageView chatMessageImage;
    private List<ChatMessageDto> chatMessageDtos = new ArrayList<ChatMessageDto>();
    private LinearLayout wrapper;

    @Override
    public void add(ChatMessageDto object) {
        chatMessageDtos.add(object);
        super.add(object);
    }

    public ChatAdapter(Context context, int textViewResourceId, List<ChatMessageDto> objects) {
        super(context, textViewResourceId, objects);
        this.chatMessageDtos = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        int type = getItemViewType(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (type == TYPE_MESSAGE) {
                row = inflater.inflate(R.layout.chat_item, parent, false);
            } else {
                row = inflater.inflate(R.layout.chat_item_image, parent, false);
            }
        }

        ChatMessageDto comment = getItem(position);

        if (type == TYPE_MESSAGE) {
            message = (TextView) row.findViewById(R.id.comment);
            message.setText(comment.getMsg());
            message.setBackgroundResource(comment.getIsMyMessage() ? R.drawable.bubble_green : R.drawable.bubble_yellow);

            wrapper = (LinearLayout) row.findViewById(R.id.chatMessageWrapper);
            wrapper.setGravity(comment.getIsMyMessage() ? Gravity.RIGHT : Gravity.LEFT);

        } else {
            message = (TextView) row.findViewById(R.id.chatCommentImage);
            chatMessageImage = (ImageView) row.findViewById(R.id.chatMessageImage);

            if (comment.getType().equalsIgnoreCase("smile")) {
                message.setText("Natali sent you a smile");
                chatMessageImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.icon_smile));
            } else if (comment.getType().equalsIgnoreCase("drink")) {
                message.setText("Natali invited you to a drink");
                chatMessageImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.icon_drink));
            }

        }

        return row;
    }

    @Override
    public int getCount() {
        return this.chatMessageDtos.size();
    }

    @Override
    public ChatMessageDto getItem(int index) {
        return this.chatMessageDtos.get(index);
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
        ChatMessageDto chatMessageDto = chatMessageDtos.get(position);
        if (chatMessageDto.getType().equalsIgnoreCase("message")) {
            return TYPE_MESSAGE;
        } else {
            return TYPE_IMAGE;
        }
    }

}
