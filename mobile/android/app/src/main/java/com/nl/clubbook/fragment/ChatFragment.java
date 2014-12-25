package com.nl.clubbook.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nl.clubbook.R;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.adapter.ChatAdapter;
import com.nl.clubbook.datasource.BaseChatMessage;
import com.nl.clubbook.datasource.Chat;
import com.nl.clubbook.datasource.ChatMessage;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.User;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.utils.CalendarUtils;
import com.nl.clubbook.utils.KeyboardUtils;
import com.nl.clubbook.utils.L;
import com.nl.clubbook.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Andrew on 6/8/2014.
 */
public class ChatFragment extends BaseInnerFragment implements View.OnClickListener {

    public static final int MODE_OPEN_FROM_CHAT_LIST = 9999;
    public static final int MODE_OPEN_FROM_PROFILE = 2222;

    private static final String ARG_USER_ID = "ARG_USER_ID";
    private static final String ARG_USER_NAME = "ARG_USER_NAME";
    private static final String ARG_USER_PHOTO_URL = "ARG_USER_PHOTO_URL";
    private static final String ARG_MODE = "ARG_MODE";

    private ChatAdapter mAdapter;
    private EditText inputText;
    private String mUserToId;
    private String mUserFromId;
    private String mAccessToken;
    private Chat mChat;
    private int mMode = MODE_OPEN_FROM_CHAT_LIST;

    public static Fragment newInstance(Fragment targetFragment, int mode, String userId, String userName, String userPhotoUrl) {
        Fragment fragment = new ChatFragment();
        fragment.setTargetFragment(targetFragment, 0);

        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        args.putString(ARG_USER_NAME, userName);
        args.putString(ARG_USER_PHOTO_URL, userPhotoUrl);
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

        SessionManager session = SessionManager.getInstance();
        session.setConversationListener(mUserToId + "_" + mUserFromId);
    }

    @Override
    public void onStop() {
        super.onStop();

        SessionManager session = SessionManager.getInstance();
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
            KeyboardUtils.closeKeyboard(getActivity(), view.findViewById(R.id.messageInput));
        }

        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtLike:
                sendMessageTemp(ChatMessage.TYPE_SMILE, getString(R.string.likes_the_profile));
                break;
            case R.id.imgSendDrink:
                sendMessageTemp(ChatMessage.TYPE_DRINK, getString(R.string.invites_for_a_drink));
                break;
            case R.id.txtSend:
                sendMessage();
                break;
            case R.id.imgAvatar:
                onUserProfileClicked();
                break;
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
        String userPhotoUrl = args.getString(ARG_USER_PHOTO_URL);

        initActionBarTitle(userName != null ? userName : "");
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        mUserFromId = getCurrentUserId();
        mAccessToken = getSession().getUserDetails().get(SessionManager.KEY_ACCESS_TOCKEN);

        inputText = (EditText) view.findViewById(R.id.messageInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        view.findViewById(R.id.txtSend).setOnClickListener(ChatFragment.this);
        view.findViewById(R.id.imgSendDrink).setOnClickListener(ChatFragment.this);
        view.findViewById(R.id.txtLike).setOnClickListener(ChatFragment.this);
    }

    public void receiveComment(ChatMessage message) {
        mAdapter.add(message);

        DataStore.readMessages(
                mChat.getCurrentUser().getId(),
                mChat.getReceiver().getId(),
                mAccessToken,
                new DataStore.OnResultReady() {

                    @Override
                    public void onReady(Object result, boolean failed) {
                        if (failed) {
                            return;
                        }
                    }
                }
        );
    }

    private void sendMessageTemp(String type, String messages) {
        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        if(!isSendingMessagesEnabled()) {
            Toast.makeText(getActivity(), R.string.you_cannot_send_three_messages_without_reply, Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> userDetails = getSession().getUserDetails();
        String accessToken = userDetails.get(SessionManager.KEY_ACCESS_TOCKEN);
        String userName = userDetails.get(SessionManager.KEY_NAME);
        String formatMessage = (userName != null ? userName : "") + " " + messages;

        DataStore.chat(mUserFromId, mUserToId, formatMessage, type, accessToken, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {

            }
        });

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(type);
        chatMessage.setIsMyMessage(true);
        chatMessage.setUserFrom(mChat.getCurrentUser().getId());
        chatMessage.setUserFromName(mChat.getCurrentUser().getName());
        chatMessage.setUserFromAvatar(mChat.getCurrentUser().getAvatar());
        chatMessage.setMsg(formatMessage);

        mAdapter.add(chatMessage);

        getView().findViewById(R.id.txtNoMessages).setVisibility(View.GONE);
    }

    private void sendMessage() {
        if(!NetworkUtils.isOn(getActivity())) {
            showToast(R.string.no_connection);
            return;
        }

        if(!isSendingMessagesEnabled()) {
            Toast.makeText(getActivity(), R.string.you_cannot_send_three_messages_without_reply, Toast.LENGTH_SHORT).show();
            return;
        }

        String accessToken = getSession().getUserDetails().get(SessionManager.KEY_ACCESS_TOCKEN);
        String input = inputText.getText().toString().trim();

        if (!input.equals("")) {
            DataStore.chat(mUserFromId, mUserToId, input, ChatMessage.TYPE_MESSAGE, accessToken, new DataStore.OnResultReady() {
                @Override
                public void onReady(Object result, boolean failed) {

                }
            });

            ChatMessage myNewMessage = new ChatMessage();
            myNewMessage.setMsg(input);
            myNewMessage.setType(ChatMessage.TYPE_MESSAGE);
            myNewMessage.setIsMyMessage(true);
            myNewMessage.setUserFrom(mChat.getCurrentUser().getId());
            myNewMessage.setUserFromName(mChat.getCurrentUser().getName());
            myNewMessage.setUserFromAvatar(mChat.getCurrentUser().getAvatar());

            mAdapter.add(myNewMessage);
            inputText.setText("");

            getView().findViewById(R.id.txtNoMessages).setVisibility(View.GONE);
        }
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
        if(receiver == null) {
            return;
        }

        KeyboardUtils.closeKeyboard(getActivity(), getView().findViewById(R.id.messageInput));

        if(mMode == MODE_OPEN_FROM_PROFILE) {
            closeFragment();
        } else {
            Fragment fragment = ProfileFragment.newInstance(ChatFragment.this, receiver, ProfileFragment.OPEN_FROM_CHAT);
            openFragment(fragment, ProfileFragment.class);
        }
    }

    private void loadConversation() {
        if(!NetworkUtils.isOn(getActivity())) {
            getView().findViewById(R.id.progressBar).setVisibility(View.GONE);
            showToast(R.string.no_connection);
            return;
        }

        setLoading(true);

        DataStore.getConversation(getActivity(), mUserFromId, mUserToId, mAccessToken, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if(isDetached() || getActivity() == null || getActivity().isFinishing()) {
                    return;
                }

                View view = getView();
                if(view == null) {
                    return;
                }

                if (failed) {
                    if(mAdapter == null || mAdapter.getCount() == 0) {
                        view.findViewById(R.id.txtNoMessages).setVisibility(View.VISIBLE);
                    }

                    showToast(R.string.something_went_wrong_please_try_again);
                    view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    return;
                }

                mChat = (Chat) result;

                List<BaseChatMessage> baseChatMessages = getChatsMessages(mChat.getConversation());

                mAdapter = new ChatAdapter(getActivity().getApplicationContext(), R.layout.item_chat_left, baseChatMessages, ChatFragment.this);
                ListView listChat = (ListView) view.findViewById(R.id.listChat);
                listChat.setAdapter(mAdapter);
                listChat.setSelection(mChat.getConversation().size());

                if(baseChatMessages.isEmpty()) {
                    view.findViewById(R.id.txtNoMessages).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.txtNoMessages).setVisibility(View.GONE);
                }

                inputText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(inputText, InputMethodManager.SHOW_IMPLICIT);

                DataStore.readMessages(
                        mChat.getCurrentUser().getId(),
                        mChat.getReceiver().getId(),
                        mAccessToken,
                        new DataStore.OnResultReady() {
                            @Override
                            public void onReady(Object result, boolean failed) {
                                if(isDetached() || getActivity() == null) {
                                    return;
                                }

                                if (failed) {
                                    L.i("readMessages failed");
                                    return;
                                }

                                Activity activity = getActivity();
                                if(activity != null && activity instanceof MainActivity) {
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