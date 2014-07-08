package com.nl.clubbook.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    private TextView countryName;
    private List<ChatMessageDto> countries = new ArrayList<ChatMessageDto>();
    private LinearLayout wrapper;

    @Override
    public void add(ChatMessageDto object) {
        countries.add(object);
        super.add(object);
    }

    public ChatAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public int getCount() {
        return this.countries.size();
    }

    public ChatMessageDto getItem(int index) {
        return this.countries.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.chat_item, parent, false);
        }

        wrapper = (LinearLayout) row.findViewById(R.id.wrapper);

        ChatMessageDto coment = getItem(position);

        countryName = (TextView) row.findViewById(R.id.comment);

        countryName.setText(coment.getMsg());

        countryName.setBackgroundResource(coment.getIsMyMessage() ? R.drawable.bubble_green : R.drawable.bubble_yellow);
        wrapper.setGravity(coment.getIsMyMessage() ? Gravity.RIGHT : Gravity.LEFT);

        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
