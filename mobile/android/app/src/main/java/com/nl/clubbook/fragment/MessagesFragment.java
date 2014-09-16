package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.nl.clubbook.R;
import com.nl.clubbook.adapter.MessagesAdapter;
import com.nl.clubbook.datasource.ChatDto;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.utils.L;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessagesFragment extends BaseRefreshFragment implements AdapterView.OnItemClickListener {

    private MessagesAdapter mAdapter;
    private ChatFragment mChatFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_messages, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sendScreenStatistic(R.string.messages_screen_android);

        initActionBarTitle(getString(R.string.messages));
        initView();
        loadData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
            actionBar.setIcon(R.drawable.icon_play);
            actionBar.setTitle(R.string.messages);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView txtUsername = (TextView) view.findViewById(R.id.txtUsername);
        ImageView imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
        String userId = txtUsername.getTag().toString();
        String userName = txtUsername.getText().toString();
        String userPhotoUrl = (String)imgAvatar.getTag();

        mChatFragment = (ChatFragment)ChatFragment.newInstance(MessagesFragment.this, userId, userName, userPhotoUrl);
        openFragment(mChatFragment, ChatFragment.class);
    }

    @Override
    protected void loadData() {
        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();

        mSwipeRefreshLayout.setRefreshing(true);

        DataStore.getConversations(user.get(SessionManager.KEY_ID), user.get(SessionManager.KEY_ACCESS_TOCKEN),
                new DataStore.OnResultReady() {

                    @Override
                    public void onReady(Object result, boolean failed) {
                        View view = getView();
                        if(isDetached() || getActivity() == null || view == null) {
                            L.i("fragment_is_detached");
                            return;
                        }

                        mSwipeRefreshLayout.setRefreshing(false);

                        if (failed) {
                            if (mAdapter.getCount() == 0){
                                view.findViewById(R.id.txtNoMessages).setVisibility(View.VISIBLE);
                            }

                            showToast(R.string.something_went_wrong_please_try_again);
                            return;
                        }

                        List<ChatDto> chats = (List<ChatDto>) result;
                        mAdapter.updateData(chats);

                        if(chats.size() == 0) {
                            view.findViewById(R.id.txtNoMessages).setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        ListView listMessages = (ListView)view.findViewById(R.id.listMessages);
        mAdapter = new MessagesAdapter(getActivity(), new ArrayList<ChatDto>());
        listMessages.setAdapter(mAdapter);
        listMessages.setOnItemClickListener(this);
    }

    public ChatFragment getChatFragment() {
        return mChatFragment;
    }
}
