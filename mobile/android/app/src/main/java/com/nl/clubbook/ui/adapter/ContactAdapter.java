package com.nl.clubbook.ui.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nl.clubbook.R;
import com.nl.clubbook.model.data.Contact;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Volodymyr on 03.10.2014.
 */
public class ContactAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Contact> mContacts;

    public ContactAdapter(Context context, List<Contact> contacts) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mContacts = contacts;
    }

    @Override
    public int getCount() {
        return mContacts.size();
    }

    @Override
    public Contact getItem(int position) {
        return mContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mContacts.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if(row == null) {
            row = mInflater.inflate(R.layout.item_list_contact, null);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.imgAvatar);
            holder.txtName = (TextView) row.findViewById(R.id.txtName);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        fillRow(holder, mContacts.get(position));

        return row;
    }

    public void updateData(List<Contact> newContacts) {
        if(newContacts != null) {
            mContacts = newContacts;
        } else {
            mContacts.clear();
        }

        notifyDataSetChanged();
    }

    private void fillRow(ViewHolder holder, Contact contact) {
        String name = contact.getName();
        holder.txtName.setText(name != null ? name : "");

        Picasso.with(mContext).load(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contact.getId())).error(R.drawable.ic_avatar_missing).into(holder.image);
    }

    private class ViewHolder {
        ImageView image;
        TextView txtName;
    }
}
