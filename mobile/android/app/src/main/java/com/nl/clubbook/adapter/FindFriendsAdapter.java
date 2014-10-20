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
import com.nl.clubbook.datasource.User;
import com.nl.clubbook.fragment.dialog.ProgressDialog;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Volodymyr on 10.10.2014.
 */
public class FindFriendsAdapter extends BaseAdapter {

    private Context mContext;
    private Fragment mFragment;
    private LayoutInflater mInflater;
    private List<User> mUsers;

    private int mBtnAddFriendPadding;

    public FindFriendsAdapter(Context context, Fragment fragment, List<User> users) {
        mContext = context;
        mFragment = fragment;
        mInflater = LayoutInflater.from(context);
        mUsers = users;

        mBtnAddFriendPadding = (int) context.getResources().getDimension(R.dimen.btn_add_friend_padding);
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public User getItem(int position) {
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

    private void fillView(ViewHolder holder, User user) {
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
        if(User.STATUS_FRIEND.equalsIgnoreCase(friendStatus)) {
            holder.txtAddFriend.setVisibility(View.GONE);
            btnMode = -1;
        } else if(User.STATUS_RECEIVE_REQUEST.equalsIgnoreCase(friendStatus)) {
            holder.txtAddFriend.setVisibility(View.VISIBLE);
            initAddFriendBtn(
                    holder.txtAddFriend,
                    R.string.accept_request_,
                    R.drawable.bg_btn_green
            );
            btnMode = BtnAddFriendModes.MODE_ACCEPT;
        } else if(User.STATUS_SENT_REQUEST.equalsIgnoreCase(friendStatus)) {
            holder.txtAddFriend.setVisibility(View.GONE);
            btnMode = -1;
        } else {
            holder.txtAddFriend.setVisibility(View.VISIBLE);
            initAddFriendBtn(
                    holder.txtAddFriend,
                    R.string.add_friend_,
                    R.drawable.bg_btn_violet_dark
            );
            btnMode = BtnAddFriendModes.MODE_ADD;
        }

        holder.txtAddFriend.setTag(user);
        holder.txtAddFriend.setOnClickListener(getOnClickListener(btnMode));
    }

    private void initAddFriendBtn(TextView txtAddFriend, int textRes, int bgRes) {
        txtAddFriend.setText(textRes);
        txtAddFriend.setBackgroundResource(bgRes);
        txtAddFriend.setPadding(mBtnAddFriendPadding, 0, mBtnAddFriendPadding, 0);
    }

    private View.OnClickListener getOnClickListener(final int mode) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object tagUser = view.getTag();
                if(tagUser == null) {
                    return;
                }

                User user = (User) tagUser;
                TextView txtAddFriend = (TextView) view;

                if(mode == BtnAddFriendModes.MODE_ADD) {
                    onAddFriendsClicked(txtAddFriend, user);
                } else {
                    onAcceptFriendsRequestClicked(txtAddFriend, user);
                }
            }
        };
    }

    private void onAddFriendsClicked(final TextView txtAddFriend, final User user) {
        if(!NetworkUtils.isOn(mContext)) {
            showToast(R.string.no_connection);
            return;
        }

        SessionManager session = SessionManager.getInstance();
        String userId = session.getUserId();
        String accessToken = session.getAccessToken();

        showProgress(mContext.getString(R.string.loading));

        DataStore.addFriendRequest(userId, user.getId(), accessToken,
                new DataStore.OnResultReady() {

                    @Override
                    public void onReady(Object result, boolean failed) {
                        hideProgress();
                        if (failed) {
                            showToast(R.string.something_went_wrong_please_try_again);
                        } else {
                            user.setFriendStatus(User.STATUS_FRIEND);
                            txtAddFriend.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void onAcceptFriendsRequestClicked(final TextView txtAddFriend, final User user) {
        if(!NetworkUtils.isOn(mContext)) {
            showToast(R.string.no_connection);
            return;
        }

        SessionManager session = SessionManager.getInstance();
        String userId = session.getUserId();
        String accessToken = session.getAccessToken();

        showProgress(mContext.getString(R.string.loading));

        DataStore.acceptFriendRequest(userId, user.getId(), accessToken,
                new DataStore.OnResultReady() {
                    @Override
                    public void onReady(Object result, boolean failed) {
                        hideProgress();
                        if (failed) {
                            showToast(R.string.something_went_wrong_please_try_again);
                        } else {
                            user.setFriendStatus(User.STATUS_FRIEND);
                            txtAddFriend.setVisibility(View.GONE);
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
    }

    private class ViewHolder {
        ImageView imgAvatar;
        TextView txtUsername;
        TextView txtCheckedIn;
        TextView txtCheckedInPlace;
        TextView txtAddFriend;
    }
}
