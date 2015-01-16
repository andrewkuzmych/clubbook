package com.nl.clubbook.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nl.clubbook.R;


/**
 * Created by Volodymyr on 15.01.2015.
 */
public class ShareContentDialog extends DialogFragment {

    public static final String TAG = ShareContentDialog.class.getSimpleName();

    private AdapterView.OnItemClickListener mOnItemClickListener;

    public static final Fragment newInstance(AdapterView.OnItemClickListener onItemClickListener) {
        ShareContentDialog shareContentDialog = new ShareContentDialog();
        shareContentDialog.setOnItemClickListener(onItemClickListener);

        return shareContentDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        Context context = getActivity().getBaseContext();
        View view = LayoutInflater.from(context).inflate(R.layout.view_dialog_share_content, null);

        ListView listItems = (ListView) view.findViewById(R.id.list);
        ShareContentAdapter adapter = new ShareContentAdapter(context, getItems());
        listItems.setAdapter(adapter);
        listItems.setOnItemClickListener(mOnItemClickListener);

        dialog.setView(view);

        return dialog.create();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private class ShareContentAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private ShareContentItem[] mItems;

        public ShareContentAdapter(Context context, ShareContentItem[] items) {
            mInflater = LayoutInflater.from(context);
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public ShareContentItem getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder;

            if(row == null) {
                row = mInflater.inflate(R.layout.item_list_share_content, null);
                holder = new ViewHolder();

                holder.icon = (ImageView) row.findViewById(R.id.imgItemIcon);
                holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);

                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            ShareContentItem item = mItems[position];
            holder.icon.setImageResource(item.iconRes);
            holder.txtTitle.setText(item.title);

            return row;
        }

        private class ViewHolder {
            public ImageView icon;
            public TextView txtTitle;
        }
    }

    private ShareContentItem[] getItems() {
        String[] titles = getResources().getStringArray(R.array.share_content_title);
        ShareContentItem[] items = new ShareContentItem[titles.length];

        for(int i = 0; i < items.length; i++) {
            ShareContentItem item = new ShareContentItem();
            item.title = titles[i];
            item.iconRes = icons[i];

            items[i] = item;
        }

        return items;
    }

    private class ShareContentItem {
        public String title;
        public int iconRes;
    }

    private final int[] icons = new int[] {
            R.drawable.ic_camera_alt_grey,
            R.drawable.ic_photo_grey,
            R.drawable.ic_my_location_grey
    };
}
