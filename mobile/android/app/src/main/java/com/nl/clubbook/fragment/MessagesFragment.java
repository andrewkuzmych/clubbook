package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.nl.clubbook.R;
import com.nl.clubbook.adapter.MessagesAdapter;
import com.nl.clubbook.datasource.Chat;
import com.nl.clubbook.datasource.HttpClientManager;
import com.nl.clubbook.datasource.User;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.utils.L;
import com.nl.clubbook.utils.NetworkUtils;

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
        doRefresh(false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
            actionBar.setTitle(R.string.messages);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Chat chat = mAdapter.getItem(position);
        User receiver = chat.getReceiver();

        mChatFragment = (ChatFragment)ChatFragment.newInstance(
                MessagesFragment.this,
                ChatFragment.MODE_OPEN_FROM_CHAT_LIST,
                receiver.getId(),
                receiver.getName(),
                receiver.getAvatar()
        );
        openFragment(mChatFragment, ChatFragment.class);
    }

    @Override
    protected void loadData() {
        doRefresh(true);
    }

    public ChatFragment getChatFragment() {
        return mChatFragment;
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        ListView listMessages = (ListView)view.findViewById(R.id.listMessages);
        mAdapter = new MessagesAdapter(getActivity(), new ArrayList<Chat>());
        listMessages.setAdapter(mAdapter);
        listMessages.setOnItemClickListener(this);
    }

    private void doRefresh(boolean isPullToRefreshRefreshed) {
        final View view = getView();
        if(view == null) {
            return;
        }

        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        final SessionManager session = SessionManager.getInstance();
        final HashMap<String, String> user = session.getUserDetails();

        if(isPullToRefreshRefreshed) {
            mSwipeRefreshLayout.setRefreshing(true);
        } else {
            view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        HttpClientManager.getInstance().getConversations(user.get(SessionManager.KEY_ID), user.get(SessionManager.KEY_ACCESS_TOCKEN),
                new HttpClientManager.OnResultReady() {

                    @Override
                    public void onReady(Object result, boolean failed) {
                        if (isDetached() || getActivity() == null) {
                            L.i("fragment_is_detached");
                            return;
                        }

                        mSwipeRefreshLayout.setRefreshing(false);
                        view.findViewById(R.id.progressBar).setVisibility(View.GONE);

                        if (failed) {
                            if (mAdapter.getCount() == 0) {
                                view.findViewById(R.id.txtNoMessages).setVisibility(View.VISIBLE);
                            }

                            showToast(R.string.something_went_wrong_please_try_again);
                            return;
                        }

                        List<Chat> chats = (List<Chat>) result;
                        mAdapter.updateData(chats);

                        if (chats.size() == 0) {
                            view.findViewById(R.id.txtNoMessages).setVisibility(View.VISIBLE);
                        } else {
                            view.findViewById(R.id.txtNoMessages).setVisibility(View.GONE);
                        }
                    }
                });
    }
}
