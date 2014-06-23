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
import android.widget.ListView;
import android.widget.TextView;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.adapter.ChatAdapter;
import com.nl.clubbook.datasource.Chat;
import com.nl.clubbook.datasource.Comment;
import com.nl.clubbook.datasource.Conversation;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.helper.SessionManager;

/**
 * Created by Andrew on 6/8/2014.
 */
public class ChatFragment extends BaseFragment {

    private String username;
    private ListView chat_list;
    private ChatAdapter adapter;
    private EditText inputText;
    private String user_to;
    private String user_name_to;
    private String user_from;
    Chat chat;

    public ChatFragment(BaseFragment provoiusFregment, String user_id, String user_name) {
        super(provoiusFregment);
        this.user_to = user_id;
        this.user_name_to = user_name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        getActivity().setTitle(user_name_to);
        inputText = (EditText) rootView.findViewById(R.id.messageInput);
        rootView.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        user_from = getCurrentUserId();

        chat_list = (ListView) rootView.findViewById(R.id.chatList);
        adapter = new ChatAdapter(getActivity().getApplicationContext(), R.layout.chat_item);
        chat_list.setAdapter(adapter);


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

        addConversation();
        return rootView;
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

    public void addComment(String message) {
        adapter.add(new Comment(true, message));
    }

    private void sendMessage() {
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            DataStore.chat(user_from, user_to, input, new DataStore.OnResultReady() {
                @Override
                public void onReady(Object result, boolean failed) {

                }
            });

            adapter.add(new Comment(false, inputText.getText().toString()));
            inputText.setText("");
        }
    }

    private void addConversation() {
        DataStore.get_conversation(user_from, user_to, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    return;
                }

                chat = (Chat) result;

                for (int i = 0; i < chat.getConversation().size(); i++) {
                    Conversation conf = chat.getConversation().get(i);
                    if (conf.getUser_from().equalsIgnoreCase(user_from))
                        adapter.add(new Comment(false, conf.getMsg()));
                    else
                        adapter.add(new Comment(true, conf.getMsg()));
                }

                chat_list.setSelection(chat.getConversation().size());

                inputText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(inputText, InputMethodManager.SHOW_IMPLICIT);
                DataStore.read_messages(chat.getChatId(), user_from, new DataStore.OnResultReady() {
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
}