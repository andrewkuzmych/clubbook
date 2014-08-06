package com.nl.clubbook.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.adapter.ChatAdapter;
import com.nl.clubbook.datasource.ChatDto;
import com.nl.clubbook.datasource.ChatMessageDto;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.SessionManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * Created by Andrew on 6/8/2014.
 */
public class ChatFragment extends BaseFragment {

    private TextView userName;
    private ImageView userAvatar;
    private ListView chat_list;
    private ChatAdapter adapter;
    private EditText inputText;
    private String user_to;
    private String user_name_to;
    private String user_from;
    private ChatDto chatDto;

    protected ImageLoader imageLoader;
    protected DisplayImageOptions options;
    protected ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();

    public ChatFragment(BaseFragment provoiusFregment, String user_id, String user_name) {
        super(provoiusFregment);
        this.user_to = user_id;
        this.user_name_to = user_name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        getActivity().setTitle("chat");

        user_from = getCurrentUserId();

        userName = (TextView) rootView.findViewById(R.id.chatUserName);
        userAvatar = (ImageView) rootView.findViewById(R.id.chatUserAvatar);

        chat_list = (ListView) rootView.findViewById(R.id.chatList);

        // send message input
        inputText = (EditText) rootView.findViewById(R.id.messageInput);
        rootView.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        // Setup our input methods. Enter key on the keyboard or pushing the send button
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        rootView.findViewById(R.id.sendDrinkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessageTemp("drink");
            }
        });
        rootView.findViewById(R.id.sendSmileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessageTemp("smile");
            }
        });

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_list_image)
                .showImageForEmptyUri(R.drawable.default_list_image)
                .showImageOnFail(R.drawable.default_list_image)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        fillConversation();

        return rootView;
    }

    public void receiveComment(ChatMessageDto message) {
        adapter.add(message);

        DataStore.read_messages(chatDto.getCurrentUser().getId(), chatDto.getReceiver().getId(), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    return;
                }
            }
        });
    }

    private void sendMessageTemp(String type) {
        DataStore.chat(user_from, user_to, "", type, new DataStore.OnResultReady() {
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

        adapter.add(chatMessageDto);
    }

    private void sendMessage() {
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            DataStore.chat(user_from, user_to, input, "message", new DataStore.OnResultReady() {
                @Override
                public void onReady(Object result, boolean failed) {

                }
            });

            adapter.add(new ChatMessageDto(inputText.getText().toString()));
            inputText.setText("");
        }
    }

    private void fillConversation() {
        DataStore.get_conversation(user_from, user_to, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    return;
                }

                chatDto = (ChatDto) result;

                // set user name
                userName.setText(chatDto.getReceiver().getName());
                // set avatar
                String image_url = ImageHelper.getUserChatAvatar(chatDto.getReceiver().getAvatar());
                imageLoader.displayImage(image_url, userAvatar, options, animateFirstListener);
                // display chat messages

                // chat messages

                adapter = new ChatAdapter(getActivity().getApplicationContext(), R.layout.chat_item, chatDto.getConversation());
                chat_list.setAdapter(adapter);

                chat_list.setSelection(chatDto.getConversation().size());

                inputText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(inputText, InputMethodManager.SHOW_IMPLICIT);

                // make conversation between 2 people as read
                DataStore.read_messages(chatDto.getCurrentUser().getId(), chatDto.getReceiver().getId(), new DataStore.OnResultReady() {
                    @Override
                    public void onReady(Object result, boolean failed) {
                        if (failed) {
                            return;
                        }
                        ((MainActivity) getActivity()).updateMessagesCount();
                    }
                });
            }
        });
    }

    @Override
    public void backButtonWasPressed() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        // hide keyboard
        imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);

        ((MainActivity) getActivity()).setCurrentFragment(previousFragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (((MainActivity) getActivity()).getDrawerToggle().isDrawerIndicatorEnabled()) {
            ((MainActivity) getActivity()).getDrawerToggle().setDrawerIndicatorEnabled(false);
            ((MainActivity) getActivity()).getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        SessionManager session = new SessionManager(getActivity().getApplicationContext());
        session.setConversationListner(user_to + "_" + user_from);
    }

    @Override
    public void onStop() {
        super.onStop();
        SessionManager session = new SessionManager(getActivity().getApplicationContext());
        session.setConversationListner(null);
    }
}