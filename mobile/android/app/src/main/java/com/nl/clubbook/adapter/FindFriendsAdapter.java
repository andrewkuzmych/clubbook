package com.nl.clubbook.adapter;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nl.clubbook.R;
import com.nl.clubbook.datasource.CheckIn;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.fragment.dialog.ProgressDialog;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Volodymyr on 10.10.2014.
 */
public class FindFriendsAdapter extends BaseAdapter {

    private Context mContext;
    private Fragment mFragment;
    private LayoutInflater mInflater;
    private List<UserDto> mUsers;

    public FindFriendsAdapter(Context context, Fragment fragment, List<UserDto> users) {
        mContext = context;
        mFragment = fragment;
        mInflater = LayoutInflater.from(context);
        mUsers = users;
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public UserDto getItem(int position) {
        return mUsers.get(position);
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
            row = mInflater.inflate(R.layout.item_list_find_friends, null);
            holder = new ViewHolder();

            holder.imgAvatar = (ImageView) row.findViewById(R.id.imgAvatar);
            holder.txtAddFriend = (TextView) row.findViewById(R.id.txtAddFriend);
            holder.txtCheckedIn = (TextView) row.findViewById(R.id.txtCheckedIn);
            holder.txtCheckedInPlace = (TextView) row.findViewById(R.id.txtCheckedInPlace);
            holder.txtUsername = (TextView) row.findViewById(R.id.txtUsername);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        fillView(holder, mUsers.get(position));

        return row;
    }

    private void fillView(ViewHolder holder, UserDto user) {
        holder.txtUsername.setText(user.getName());
        holder.txtUsername.setTag(user.getId());

        if (user.getAvatar() != null) {
            String imageUrl = ImageHelper.getUserListAvatar(user.getAvatar());

            Picasso.with(mContext).load(imageUrl).error(R.drawable.ic_avatar_unknown).into(holder.imgAvatar);
        }

        CheckIn checkIn = user.getLastCheckIn();
        if(checkIn != null) {
            if(!checkIn.isActive()) {
                holder.txtCheckedInPlace.setVisibility(View.GONE);
                holder.txtCheckedIn.setText(R.string.not_checked_in);
            } else {
                holder.txtCheckedInPlace.setText(checkIn.getClubName());
                holder.txtCheckedInPlace.setVisibility(View.VISIBLE);

                holder.txtCheckedIn.setText(R.string.checked_in_double_dots);
            }
        }

        String friendStatus = user.getFriendStatus();
        int btnMode;
        if(UserDto.STATUS_FRIEND.equalsIgnoreCase(friendStatus)) {
            holder.txtAddFriend.setVisibility(View.GONE);
            btnMode = -1;
        } else if(UserDto.STATUS_RECEIVE_REQUEST.equalsIgnoreCase(friendStatus)) {
            holder.txtAddFriend.setVisibility(View.VISIBLE);
            holder.txtAddFriend.setText(R.string.accept_request_);
            btnMode = BtnAddFriendModes.MODE_ACCEPT;
        } else if(UserDto.STATUS_SENT_REQUEST.equalsIgnoreCase(friendStatus)) {
            holder.txtAddFriend.setVisibility(View.VISIBLE);
            holder.txtAddFriend.setText(R.string.cancel_request_);
            btnMode = BtnAddFriendModes.MODE_CANCEL;
        } else {
            holder.txtAddFriend.setVisibility(View.VISIBLE);
            holder.txtAddFriend.setText(R.string.add_friend_);
            btnMode = BtnAddFriendModes.MODE_ADD;
        }

        holder.txtAddFriend.setTag(user.getId());
        holder.txtAddFriend.setOnClickListener(getOnClickListener(btnMode));
    }

    private View.OnClickListener getOnClickListener(final int mode) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object tagId = view.getTag();
                if(tagId == null) {
                    return;
                }

                String profileId = (String) tagId;
                TextView txtAddFriend = (TextView) view;

                if(mode == BtnAddFriendModes.MODE_ADD) {
                    onAddFriendsClicked(txtAddFriend, profileId);
                } else if(mode == BtnAddFriendModes.MODE_CANCEL){
                    onCancelFriendRequestClicked(txtAddFriend, profileId);
                } else {
                    onAcceptFriendsRequestClicked(txtAddFriend, profileId);
                }
            }
        };
    }

    private void onAddFriendsClicked(final TextView txtAddFriend, String profileId) {
        if(!NetworkUtils.isOn(mContext)) {
            showToast(R.string.no_connection);
            return;
        }

        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();

        showProgress(mContext.getString(R.string.loading));

        DataStore.addFriendRequest(user.get(SessionManager.KEY_ID), profileId, user.get(SessionManager.KEY_ACCESS_TOCKEN),
                new DataStore.OnResultReady() {

                    @Override
                    public void onReady(Object result, boolean failed) {
                        hideProgress();
                        if (failed) {
                            showToast(R.string.something_went_wrong_please_try_again);
                        } else {
                            txtAddFriend.setText(R.string.cancel_request_);
                            txtAddFriend.setOnClickListener(getOnClickListener(BtnAddFriendModes.MODE_CANCEL));
                        }
                    }
                });
    }

    private void onAcceptFriendsRequestClicked(final TextView txtAddFriend, String profileId) {
        if(!NetworkUtils.isOn(mContext)) {
            showToast(R.string.no_connection);
            return;
        }

        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();

        showProgress(mContext.getString(R.string.loading));

        DataStore.acceptFriendRequest(user.get(SessionManager.KEY_ID), profileId, user.get(SessionManager.KEY_ACCESS_TOCKEN),
                new DataStore.OnResultReady() {
                    @Override
                    public void onReady(Object result, boolean failed) {
                        hideProgress();
                        if (failed) {
                            showToast(R.string.something_went_wrong_please_try_again);
                        } else {
                            txtAddFriend.setVisibility(View.GONE);
                            txtAddFriend.setOnClickListener(null);
                        }
                    }
                });
    }

    private void onCancelFriendRequestClicked(final TextView txtAddFriend, String profileId) {
        if(!NetworkUtils.isOn(mContext)) {
            showToast(R.string.no_connection);
            return;
        }

        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();

        showProgress(mContext.getString(R.string.canceling));

        DataStore.cancelFriendRequest(user.get(SessionManager.KEY_ID), profileId, user.get(SessionManager.KEY_ACCESS_TOCKEN),
                new DataStore.OnResultReady() {

                    @Override
                    public void onReady(Object result, boolean failed) {
                        hideProgress();
                        if (failed) {
                            showToast(R.string.something_went_wrong_please_try_again);
                        } else {
                            txtAddFriend.setText(R.string.add_friend);
                            txtAddFriend.setOnClickListener(getOnClickListener(BtnAddFriendModes.MODE_ADD));
                        }
                    }
                });
    }

    private void showToast(int messageRes) {
        Toast.makeText(mContext, messageRes, Toast.LENGTH_SHORT).show();
    }

    protected void showProgress(String message) {
        Fragment progressDialog = ProgressDialog.newInstance(null, message);
        FragmentTransaction fTransaction = mFragment.getChildFragmentManager().beginTransaction();
        fTransaction.add(progressDialog, ProgressDialog.TAG);
        fTransaction.commitAllowingStateLoss();
    }

    protected void hideProgress() {
        DialogFragment progressDialog = (DialogFragment) mFragment.getChildFragmentManager().findFragmentByTag(ProgressDialog.TAG);
        if(progressDialog != null) {
            progressDialog.dismissAllowingStateLoss();
        }
    }

    private interface BtnAddFriendModes {
        final int MODE_ADD = 33;
        final int MODE_ACCEPT = 55;
        final int MODE_CANCEL = 77;
    }

    private class ViewHolder {
        ImageView imgAvatar;
        TextView txtUsername;
        TextView txtCheckedIn;
        TextView txtCheckedInPlace;
        TextView txtAddFriend;
    }
}
