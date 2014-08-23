package com.nl.clubbook.fragment;

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

import com.nl.clubbook.R;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.adapter.ChatAdapter;
import com.nl.clubbook.datasource.BaseChatMessage;
import com.nl.clubbook.datasource.ChatDto;
import com.nl.clubbook.datasource.ChatMessageDto;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.utils.CalendarUtils;
import com.nl.clubbook.utils.KeyboardUtils;
import com.nl.clubbook.utils.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 6/8/2014.
 */
public class ChatFragment extends BaseInnerFragment implements View.OnClickListener {

    private static final String ARG_USER_ID = "ARG_USER_ID";
    private static final String ARG_USER_NAME = "ARG_USER_NAME";

    private ChatAdapter mAdapter;
    private EditText inputText;
    private String mUserToId;
    private String mUserFromId;
    private String mAccessToken;
    private ChatDto chatDto;

    public static Fragment newInstance(Fragment targetFragment, String userId, String userName) {
        Fragment fragment = new ChatFragment();
        fragment.setTargetFragment(targetFragment, 0);

        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        args.putString(ARG_USER_NAME, userName);
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

        handleArgs();
        initView();
        loadConversation();
    }

    @Override
    public void onStart() {
        super.onStart();

        SessionManager session = new SessionManager(getActivity().getApplicationContext());
        session.setConversationListner(mUserToId + "_" + mUserFromId);
    }

    @Override
    public void onStop() {
        super.onStop();

        SessionManager session = new SessionManager(getActivity().getApplicationContext());
        session.setConversationListner(null);
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
                sendMessageTemp(ChatMessageDto.TYPE_SMILE);
                break;
            case R.id.imgSendDrink:
                sendMessageTemp(ChatMessageDto.TYPE_DRINK);
                break;
            case R.id.txtSend:
                sendMessage();
                break;
        }
    }

    private void handleArgs() {
        Bundle args = getArguments();
        if(args == null) {
            return;
        }

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

    public void receiveComment(ChatMessageDto message) {
        mAdapter.add(message);

        DataStore.readMessages(
                chatDto.getCurrentUser().getId(),
                chatDto.getReceiver().getId(),
                chatDto.getCurrentUser().getAccessToken(),
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

    private void sendMessageTemp(String type) {
        String accessToken = getSession().getUserDetails().get(SessionManager.KEY_ACCESS_TOCKEN);

        DataStore.chat(mUserFromId, mUserToId, "", type, accessToken, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {

            }
        });

        ChatMessageDto chatMessageDto = new ChatMessageDto();
        chatMessageDto.setType(type);
        chatMessageDto.setIsMyMessage(true);
        chatMessageDto.setUserFrom(chatDto.getCurrentUser().getId());
        chatMessageDto.setUserFromName(chatDto.getCurrentUser().getName());
        chatMessageDto.setUserFromAvatar(chatDto.getCurrentUser().getAvatar());

        mAdapter.add(chatMessageDto);
    }

    private void sendMessage() {
        String accessToken = getSession().getUserDetails().get(SessionManager.KEY_ACCESS_TOCKEN);
        String input = inputText.getText().toString().trim();

        if (!input.equals("")) {
            DataStore.chat(mUserFromId, mUserToId, input, ChatMessageDto.TYPE_MESSAGE, accessToken, new DataStore.OnResultReady() {
                @Override
                public void onReady(Object result, boolean failed) {

                }
            });

            ChatMessageDto myNewMessage = new ChatMessageDto();
            myNewMessage.setMsg(input);
            myNewMessage.setType(ChatMessageDto.TYPE_MESSAGE);
            myNewMessage.setIsMyMessage(true);
            myNewMessage.setUserFrom(chatDto.getCurrentUser().getId());
            myNewMessage.setUserFromName(chatDto.getCurrentUser().getName());
            myNewMessage.setUserFromAvatar(chatDto.getCurrentUser().getAvatar());

            mAdapter.add(myNewMessage);
            inputText.setText("");
        }
    }

    private void loadConversation() {
        setLoading(true);

        DataStore.getConversation(getActivity(), mUserFromId, mUserToId, mAccessToken, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if(isDetached() || getActivity() == null || getActivity().isFinishing()) {
                    return;
                }

                if (failed) {
                    return;
                }

                View view = getView();
                if(view == null) {
                    return;
                }

                chatDto = (ChatDto) result;

                List<BaseChatMessage> baseChatMessages = getChatsMessages(chatDto.getConversation());

                mAdapter = new ChatAdapter(getActivity().getApplicationContext(), R.layout.item_chat_left, baseChatMessages);
                ListView listChat = (ListView) view.findViewById(R.id.listChat);
                listChat.setAdapter(mAdapter);
                listChat.setSelection(chatDto.getConversation().size());

                inputText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(inputText, InputMethodManager.SHOW_IMPLICIT);

                String accessToken = getSession().getUserDetails().get(SessionManager.KEY_ACCESS_TOCKEN);
                DataStore.readMessages(
                        chatDto.getCurrentUser().getId(),
                        chatDto.getReceiver().getId(),
                        accessToken,
                        new DataStore.OnResultReady() {
                            @Override
                            public void onReady(Object result, boolean failed) {
                                if (failed) {
                                    L.i("readMessages failed");
                                    return;
                                }
                                ((MainActivity) getActivity()).updateMessagesCount();
                            }
                        });

                setLoading(false);
            }
        });
    }

    //TODO move this operation to background
    private List<BaseChatMessage> getChatsMessages(List<ChatMessageDto> chatMessages) {
        long previousTime = 0;
        int dayTimeInMilliseconds = CalendarUtils.getDayTimeInMilliseconds();
        List<BaseChatMessage> baseChatMessages = new ArrayList<BaseChatMessage>();
        for(ChatMessageDto message : chatMessages) {
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