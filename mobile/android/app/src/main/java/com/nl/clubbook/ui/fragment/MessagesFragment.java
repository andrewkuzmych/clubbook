package com.nl.clubbook.ui.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.nl.clubbook.R;
import com.nl.clubbook.model.ClubbookPreferences;
import com.nl.clubbook.ui.adapter.MessagesAdapter;
import com.nl.clubbook.model.data.Chat;
import com.nl.clubbook.model.httpclient.HttpClientManager;
import com.nl.clubbook.model.data.User;
import com.nl.clubbook.utils.L;
import com.nl.clubbook.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends BaseRefreshFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private MessagesAdapter mAdapter;
    private ChatFragment mChatFragment;

    private ActionMode mActionMode;
    private boolean mIsActionModeActive;
    private Chat mChatToDelete;

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
                receiver.getName()
        );
        openFragment(mChatFragment, ChatFragment.class);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(!mIsActionModeActive) {
            Toolbar toolBar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            mActionMode = toolBar.startActionMode(new ActionModeCallBack());
        }

        if(mAdapter.getCurrentSelectedPosition() == position) {
            mActionMode.finish();
            return true;
        }

        mChatToDelete = mAdapter.getItem(position);
        mAdapter.setSelection(view, position);

        return true;
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

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && mActionMode != null && mIsActionModeActive) {
                    mActionMode.finish();

                    return true;
                }

                return false;
            }
        });

        ListView listMessages = (ListView)view.findViewById(R.id.listMessages);
        mAdapter = new MessagesAdapter(getActivity(), new ArrayList<Chat>());
        listMessages.setAdapter(mAdapter);
        listMessages.setOnItemClickListener(this);
        listMessages.setOnItemLongClickListener(this);
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

        final ClubbookPreferences preferences = ClubbookPreferences.getInstance(getActivity().getBaseContext());
        String userId = preferences.getUserId();
        String accessToken = preferences.getAccessToken();

        if(isPullToRefreshRefreshed) {
            mSwipeRefreshLayout.setRefreshing(true);
        } else {
            view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        HttpClientManager.getInstance().getConversations(userId, accessToken,
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

    private void doDeleteConversations() {
        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        if(mChatToDelete == null) {
            return;
        }

        User receiver = mChatToDelete.getReceiver();
        String receiverId = receiver.getId();

        if(TextUtils.isEmpty(receiverId)) {
            return;
        }

        final ClubbookPreferences preferences = ClubbookPreferences.getInstance(getActivity().getBaseContext());
        String userId = preferences.getUserId();
        String accessToken = preferences.getAccessToken();

        mSwipeRefreshLayout.setRefreshing(true);

        HttpClientManager.getInstance().deleteConversation(userId, receiverId, accessToken,
                new HttpClientManager.OnResultReady() {

                    @Override
                    public void onReady(Object result, boolean failed) {
                        View view = getView();

                        if (isDetached() || getActivity() == null || view == null) {
                            L.i("fragment_is_detached");
                            return;
                        }

                        mSwipeRefreshLayout.setRefreshing(false);

                        if (failed) {
                            showToast(R.string.something_went_wrong_please_try_again);
                            return;
                        }

                        mAdapter.deleteItem(mChatToDelete);
                        mActionMode.finish();

                        if (mAdapter.getCount() == 0) {
                            view.findViewById(R.id.txtNoMessages).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.listMessages).setVisibility(View.GONE);
                        }
                    }
                });
    }

    private class ActionModeCallBack implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            mIsActionModeActive = true;

            actionMode.getMenuInflater().inflate(R.menu.menu_delete, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if(menuItem.getItemId() == R.id.menuDelete) {
                doDeleteConversations();
                return true;
            }


            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mIsActionModeActive = false;
            mAdapter.removeSelection();
        }
    }
}
