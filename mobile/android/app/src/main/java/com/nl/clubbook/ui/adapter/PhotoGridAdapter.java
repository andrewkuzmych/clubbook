package com.nl.clubbook.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nl.clubbook.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Volodymyr on 27.01.2015.
 */
public class PhotoGridAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<String> mUrls;

    public PhotoGridAdapter(Context context, List<String> urls) {
        mInflater = LayoutInflater.from(context);
        mUrls = urls;
    }

    @Override
    public int getCount() {
        return mUrls.size();
    }

    @Override
    public String getItem(int position) {
        return mUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        ViewHolder holder;

        if(item == null) {
            item = mInflater.inflate(R.layout.item_grid_photo, null);
            holder = new ViewHolder();

            holder.imgPhoto = (ImageView) item.findViewById(R.id.imgPhoto);

            item.setTag(holder);
        } else {
            holder = (ViewHolder) item.getTag();
        }

        Picasso.with(mInflater.getContext()).load(mUrls.get(position)).into(holder.imgPhoto);

        return item;
    }

    private class ViewHolder {
        ImageView imgPhoto;
    }
}
