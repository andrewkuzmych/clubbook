package com.nl.clubbook.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nl.clubbook.R;
import com.nl.clubbook.model.ClubbookPreferences;
import com.nl.clubbook.ui.activity.MainActivity;
import com.nl.clubbook.ui.adapter.ChatAdapter;
import com.nl.clubbook.model.data.BaseChatMessage;
import com.nl.clubbook.model.data.Chat;
import com.nl.clubbook.model.data.ChatMessage;
import com.nl.clubbook.model.httpclient.HttpClientManager;
import com.nl.clubbook.model.data.Location;
import com.nl.clubbook.model.data.User;
import com.nl.clubbook.ui.fragment.dialog.ShareContentDialog;
import com.nl.clubbook.helper.LocationCheckinHelper;
import com.nl.clubbook.utils.CalendarUtils;
import com.nl.clubbook.utils.KeyboardUtils;
import com.nl.clubbook.utils.L;
import com.nl.clubbook.utils.MapUtils;
import com.nl.clubbook.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 6/8/2014.
 */
public class ChatFragment extends BaseInnerFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final int MODE_OPEN_FROM_CHAT_LIST = 9999;
    public static final int MODE_OPEN_FROM_PROFILE = 2222;

    private static final String ARG_USER_ID = "ARG_USER_ID";
    private static final String ARG_USER_NAME = "ARG_USER_NAME";
    private static final String ARG_MODE = "ARG_MODE";

    private ChatAdapter mAdapter;
    private EditText mEditMessage;
    private String mUserToId;
    private String mUserFromId;
    private String mAccessToken;
    private Chat mChat;
    private int mMode = MODE_OPEN_FROM_CHAT_LIST;

    public static Fragment newInstance(Fragment targetFragment, int mode, String userId, String userName) {
        Fragment fragment = new ChatFragment();
        fragment.setTargetFragment(targetFragment, 0);

        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        args.putString(ARG_USER_NAME, userName);
        args.putInt(ARG_MODE, mode);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_chat, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sendScreenStatistic(R.string.chat_screen_android);

        initTarget();

        handleArgs();
        initView();
        loadConversation();
    }

    @Override
    public void onStart() {
        super.onStart();

        ClubbookPreferences session = ClubbookPreferences.getInstance();
        session.setConversationListener(mUserToId + "_" + mUserFromId);
    }

    @Override
    public void onStop() {
        super.onStop();

        ClubbookPreferences session = ClubbookPreferences.getInstance();
        session.setConversationListener(null);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            handleArgs();
        }
    }

    @Override
    public void onDestroyView() {
        View view = getView();
        if(view != null) {
            KeyboardUtils.closeKeyboard(getActivity(), mEditMessage);
        }

        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtLike:
                sendLikeProfileMessage();
                break;
            case R.id.txtSend:
                sendTextMessage();
                break;
            case R.id.imgShareContent:
                onShareContentClicked();
                break;
            case R.id.imgAvatar:
                onUserProfileClicked();
                break;
            case R.id.imgLocation:
                onLocationClicked(v);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case ShareContentDialog.Position.TAKE_PHOTO:
                onTakePhotoClicked();
                break;
            case ShareContentDialog.Position.CHOOSE_EXISTING_PHOTO:
                onChoosePhotoClicked();
                break;
            case ShareContentDialog.Position.SHARE_LOCATION:
                sendShareLocationMessage();
        }
    }

    private void handleArgs() {
        Bundle args = getArguments();
        if(args == null) {
            return;
        }

        mMode = args.getInt(ARG_MODE, MODE_OPEN_FROM_CHAT_LIST);
        mUserToId = args.getString(ARG_USER_ID);
        String userName = args.getString(ARG_USER_NAME);

        initActionBarTitle(userName != null ? userName : "");
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        mUserFromId = getCurrentUserId();
        mAccessToken = getSession().getUserDetails().get(ClubbookPreferences.KEY_ACCESS_TOCKEN);

        mEditMessage = (EditText) view.findViewById(R.id.editMessage);
        mEditMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendTextMessage();
                }
                return true;
            }
        });

        view.findViewById(R.id.txtSend).setOnClickListener(ChatFragment.this);
        view.findViewById(R.id.txtLike).setOnClickListener(ChatFragment.this);
        view.findViewById(R.id.imgShareContent).setOnClickListener(ChatFragment.this);
    }

    public void receiveComment(ChatMessage message) {
        mAdapter.add(message);

        HttpClientManager.getInstance().readMessages(
                mChat.getCurrentUser().getId(),
                mChat.getReceiver().getId(),
                mAccessToken,
                new HttpClientManager.OnResultReady() {
                    @Override
                    public void onReady(Object result, boolean failed) {
                    }
                }
        );
    }

    private void sendMessage(String type, String message, String lat, String lon) {
        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        if(!isSendingMessagesEnabled()) {
            Toast.makeText(getActivity(), R.string.you_cannot_send_three_messages_without_reply, Toast.LENGTH_SHORT).show();
            return;
        }

        HttpClientManager.getInstance().sendMessage(mUserFromId, mUserToId, message, type, mAccessToken, lat, lon, new HttpClientManager.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
            }
        });

        ChatMessage myNewMessage = new ChatMessage();
        myNewMessage.setMsg(message);
        myNewMessage.setType(type);
        myNewMessage.setIsMyMessage(true);
        myNewMessage.setUserFrom(mChat.getCurrentUser().getId());
        myNewMessage.setUserFromName(mChat.getCurrentUser().getName());
        myNewMessage.setUserFromAvatar(mChat.getCurrentUser().getAvatar());
        myNewMessage.setTime(System.currentTimeMillis());

        if(lat != null && lon != null) {
            android.location.Location currentLocation = LocationCheckinHelper.getInstance().getCurrentLocation();

            Location location = new Location();
            location.setLat(currentLocation.getLatitude());
            location.setLon(currentLocation.getLongitude());

            myNewMessage.setLocation(location);
        }

        mAdapter.add(myNewMessage);
        mEditMessage.setText("");

        View view = getView();
        if(view != null) {
            view.findViewById(R.id.txtNoMessages).setVisibility(View.GONE);
        }
    }

    private void sendTextMessage() {
        String message = mEditMessage.getText().toString();
        if(TextUtils.isEmpty(message)) {
            return;
        }

        sendMessage(ChatMessage.Types.TYPE_MESSAGE, message, null, null);
    }

    private void sendLikeProfileMessage() {
        String userName = mChat.getCurrentUser().getName();
        String formatMessage = (userName != null ? userName : "") + " " + getString(R.string.likes_the_profile);

        sendMessage(ChatMessage.Types.TYPE_SMILE, formatMessage, null, null);
    }

    private void sendShareLocationMessage() {
        android.location.Location currentLocation = LocationCheckinHelper.getInstance().getCurrentLocation();

        sendMessage(ChatMessage.Types.TYPE_LOCATION, "", "" + currentLocation.getLatitude(), "" + currentLocation.getLongitude());
    }

    private void onShareContentClicked() {
        Fragment shareContentDialog = ShareContentDialog.newInstance(ChatFragment.this);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(shareContentDialog, ShareContentDialog.TAG);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private boolean isSendingMessagesEnabled() {
        int maxSendMessagesCountWithoutReply = 3;

        List<BaseChatMessage> messages = mAdapter.getMessages();

        if(messages == null || messages.isEmpty()) {
            return true;
        }

        int sendMessagesCount = 0;
        for(BaseChatMessage baseMessage : messages) {
            if(baseMessage instanceof ChatMessage) {
                ChatMessage chatMessage = (ChatMessage) baseMessage;

                if(chatMessage.getIsMyMessage()) {
                    sendMessagesCount++;

                    if(sendMessagesCount == (maxSendMessagesCountWithoutReply + 1)) {
                        break;
                    }
                } else {
                    return true;
                }
            }
        }

        if(sendMessagesCount >= maxSendMessagesCountWithoutReply) {
            return false;
        } else {
            return true;
        }
    }

    private void onUserProfileClicked() {
        User receiver = mChat.getReceiver();
        if (receiver == null) {
            return;
        }

        KeyboardUtils.closeKeyboard(getActivity(), mEditMessage);

        if (mMode == MODE_OPEN_FROM_PROFILE) {
            closeFragment();
        } else {
            Fragment fragment = ProfileFragment.newInstance(ChatFragment.this, receiver, ProfileFragment.OPEN_FROM_CHAT);
            openFragment(fragment, ProfileFragment.class);
        }
    }

    private void onLocationClicked(View view) {
        if(view.getTag() == null) {
            return;
        }

        Location location = (Location) view.getTag();
        MapUtils.showLocationOnGoogleMapApp(getActivity(), location.getLat(), location.getLon());
    }

    private void onTakePhotoClicked() {
        //TODO
    }

    private void onChoosePhotoClicked() {
        //TODO
    }

    private void loadConversation() {
        final View view = getView();
        if(view == null) {
            return;
        }

        if(!NetworkUtils.isOn(getActivity())) {
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
            showToast(R.string.no_connection);
            return;
        }

        setLoading(true);

        HttpClientManager.getInstance().getConversation(getActivity(), mUserFromId, mUserToId, mAccessToken, new HttpClientManager.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (isDetached() || getActivity() == null || getActivity().isFinishing()) {
                    return;
                }

                if (failed) {
                    if (mAdapter == null || mAdapter.getCount() == 0) {
                        view.findViewById(R.id.txtNoMessages).setVisibility(View.VISIBLE);
                    }

                    showToast(R.string.something_went_wrong_please_try_again);
                    view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    return;
                }

                mChat = (Chat) result;

                List<BaseChatMessage> baseChatMessages = getChatsMessages(mChat.getConversation());

                mAdapter = new ChatAdapter(getActivity(), R.layout.item_chat_left, baseChatMessages, ChatFragment.this);
                ListView listChat = (ListView) view.findViewById(R.id.listChat);
                listChat.setAdapter(mAdapter);
                listChat.setSelection(mChat.getConversation().size());

                if (baseChatMessages.isEmpty()) {
                    view.findViewById(R.id.txtNoMessages).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.txtNoMessages).setVisibility(View.GONE);
                }

                HttpClientManager.getInstance().readMessages(
                        mChat.getCurrentUser().getId(),
                        mChat.getReceiver().getId(),
                        mAccessToken,
                        new HttpClientManager.OnResultReady() {
                            @Override
                            public void onReady(Object result, boolean failed) {
                                if (isDetached() || getActivity() == null) {
                                    return;
                                }

                                if (failed) {
                                    L.i("readMessages failed");
                                    return;
                                }

                                Activity activity = getActivity();
                                if (activity != null && activity instanceof MainActivity) {
                                    MainActivity mainActivity = (MainActivity) activity;
                                    mainActivity.updateMessagesCount();
                                }
                            }
                        });

                setLoading(false);
            }
        });
    }

    //TODO move this operation to background
    private List<BaseChatMessage> getChatsMessages(List<ChatMessage> chatMessages) {
        long previousTime = 0;
        int dayTimeInMilliseconds = CalendarUtils.getDayTimeInMilliseconds();
        List<BaseChatMessage> baseChatMessages = new ArrayList<BaseChatMessage>();
        for(ChatMessage message : chatMessages) {
            long timeWithoutHours = message.getTimeWithoutHours();
            if(timeWithoutHours > previousTime + dayTimeInMilliseconds) {
                BaseChatMessage baseMessage = new BaseChatMessage();
                baseMessage.setTimeWithoutHours(timeWithoutHours);
                baseMessage.setDateObject(true);

                baseChatMessages.add(baseMessage);
                baseChatMessages.add(message);
                previousTime = timeWithoutHours;
            } else {
                baseChatMessages.add(message);
            }
        }

        return baseChatMessages;
    }

    private void setLoading(boolean isLoading) {
        View view = getView();
        if(view == null) {
            return;
        }

        if(isLoading) {
            view.findViewById(R.id.listChat).setVisibility(View.GONE);
            view.findViewById(R.id.holderNewMessage).setVisibility(View.GONE);
            view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.listChat).setVisibility(View.VISIBLE);
            view.findViewById(R.id.holderNewMessage).setVisibility(View.VISIBLE);
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }
}