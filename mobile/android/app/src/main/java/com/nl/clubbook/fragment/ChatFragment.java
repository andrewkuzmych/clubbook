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
import com.nl.clubbook.utils.KeyboardUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * Created by Andrew on 6/8/2014.
 */
public class ChatFragment extends BaseInnerFragment {

    private static final String ARG_USER_ID = "ARG_USER_ID";
    private static final String ARG_USER_NAME = "ARG_USER_NAME";

    private TextView userName;
    private ImageView userAvatar;
    private ListView chat_list;
    private ChatAdapter adapter;
    private EditText inputText;
    private String mUserToId;
    private String mUserNameTo;
    private String mUserFromId; // current user id
    private String mAccessToken; // current user id
    private ChatDto chatDto;

    protected ImageLoader imageLoader;
    protected DisplayImageOptions options;
    protected ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handleArgs();
        initView();
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

    private void handleArgs() {
        Bundle args = getArguments();
        if(args == null) {
            return;
        }

        mUserToId = args.getString(ARG_USER_ID);
        mUserNameTo = args.getString(ARG_USER_NAME);

        initActionBarTitle(mUserNameTo != null ? mUserNameTo : "");
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        mUserFromId = getCurrentUserId();
        mAccessToken = getSession().getUserDetails().get(SessionManager.KEY_ACCESS_TOCKEN);

        userName = (TextView) view.findViewById(R.id.chatUserName);
        userAvatar = (ImageView) view.findViewById(R.id.chatUserAvatar);

        chat_list = (ListView) view.findViewById(R.id.chatList);

        // send message input
        inputText = (EditText) view.findViewById(R.id.messageInput);
        view.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
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

        view.findViewById(R.id.sendDrinkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessageTemp("drink");
            }
        });
        view.findViewById(R.id.sendSmileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessageTemp("smile");
            }
        });

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_avatar_missing)
                .showImageForEmptyUri(R.drawable.ic_avatar_missing)
                .showImageOnFail(R.drawable.ic_avatar_unknown)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        fillConversation();
    }

    public void receiveComment(ChatMessageDto message) {
        adapter.add(message);

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

        adapter.add(chatMessageDto);
    }

    private void sendMessage() {
        String accessToken = getSession().getUserDetails().get(SessionManager.KEY_ACCESS_TOCKEN);
        String input = inputText.getText().toString();

        if (!input.equals("")) {
            DataStore.chat(mUserFromId, mUserToId, input, "message", accessToken, new DataStore.OnResultReady() {
                @Override
                public void onReady(Object result, boolean failed) {

                }
            });

            adapter.add(new ChatMessageDto(inputText.getText().toString()));
            inputText.setText("");
        }
    }

    private void fillConversation() {
        DataStore.getConversation(mUserFromId, mUserToId, mAccessToken, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    return;
                }

                chatDto = (ChatDto) result;

                // set user name
                userName.setText(chatDto.getReceiver().getName());
                // set avatar
                String image_url = ImageHelper.getProfileImage(chatDto.getReceiver().getAvatar());
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
                                ((MainActivity) getActivity()).updateMessagesCount();
                            }
                        });
            }
        });
    }
}