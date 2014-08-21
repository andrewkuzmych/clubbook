package com.nl.clubbook.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;

import java.util.ArrayList;

/**
 * Created by Andrew on 5/21/2014.
 */
public class CropOptionAdapter extends BaseAdapter {
    private ArrayList<CropOption> mOptions;
    private LayoutInflater mInflater;

    public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
        mOptions = options;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mOptions.size();
    }

    @Override
    public Object getItem(int position) {
        return mOptions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup group) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            row = mInflater.inflate(R.layout.item_list_crop_chooser, null);
            holder = new ViewHolder();

            holder.imgAppIcon = (ImageView) row.findViewById(R.id.imgAppIcon);
            holder.txtAppName = (TextView) row.findViewById(R.id.txtAppName);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        CropOption item = mOptions.get(position);
        fillRow(holder, item);

        return row;
    }

    private void fillRow(ViewHolder holder, CropOption item) {
        if(item == null) {
            return;
        }

        holder.imgAppIcon.setImageDrawable(item.icon);
        holder.txtAppName.setText(item.title);
    }

    private class ViewHolder {
        ImageView imgAppIcon;
        TextView txtAppName;
    }
}