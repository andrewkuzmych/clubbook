package com.nl.clubbook.ui.drawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nl.clubbook.R;
import com.nl.clubbook.adapter.NavDrawerItem;

import java.util.List;

public class NavDrawerListAdapter extends BaseAdapter {

    private List<NavDrawerItem> mItems;
    private LayoutInflater mInflater;

    public NavDrawerListAdapter(Context context, List<NavDrawerItem> items) {
        mInflater = LayoutInflater.from(context);
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if(row == null) {
            row = mInflater.inflate(R.layout.item_list_drawer, null);
            holder = new ViewHolder();

            holder.imgAvatar = (ImageView) row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
            holder.txtNumber = (TextView) row.findViewById(R.id.txtNumber);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        NavDrawerItem item = mItems.get(position);
        fillRow(holder, item);

        return row;
    }

    private void fillRow(ViewHolder holder, NavDrawerItem item) {
        holder.imgAvatar.setImageResource(item.getIcon());
        holder.txtTitle.setText(item.getTitle());

        int count = item.getCount();
        if(count > 0) {
            holder.txtNumber.setText("" + item.getCount());
            holder.txtNumber.setVisibility(View.VISIBLE);
        } else {
            holder.txtNumber.setVisibility(View.GONE);
        }
    }

    private class ViewHolder {
        ImageView imgAvatar;
        TextView txtTitle;
        TextView txtNumber;
    }
}
