package com.nl.clubbook.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.Comment;
import com.nl.clubbook.datasource.Conversation;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.helper.SessionManager;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Andrew on 6/8/2014.
 */
public class ChatFragment  extends BaseFragment {

    private String username;
    private ListView chat_list;
    private ChatAdapter adapter;
    private EditText inputText;
    private String user_to;
    private String user_name_to;
    private String user_from;
    private ClubDto club;

    Pubnub pubnub = new Pubnub("pub-c-b0a0ffb6-6a0f-4907-8d4f-642e500c707a", "sub-c-f56b81f4-ed0a-11e3-8a10-02ee2ddab7fe", "", false);


    public ChatFragment(BaseFragment provoiusFregment, String user_id, String user_name, ClubDto club){
        super(provoiusFregment);
        this.user_to = user_id;
        this.user_name_to = user_name;
        this.club = club;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        getActivity().setTitle(user_name_to);
        inputText = (EditText)rootView.findViewById(R.id.messageInput);
        rootView.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        SessionManager session = new SessionManager(getActivity().getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        user_from = user.get(SessionManager.KEY_ID);

        chat_list =  (ListView)rootView.findViewById(R.id.chatList);
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
    public void onStart()
    {
        super.onStart();
        if (((MainActivity)getActivity()).getDrawerToggle().isDrawerIndicatorEnabled()) {
            ((MainActivity) getActivity()).getDrawerToggle().setDrawerIndicatorEnabled(false);
            ((MainActivity) getActivity()).getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        subscribeToChannel(user_to + "_" + user_from);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        pubnub.unsubscribe(user_to + "_" + user_from);
    }

    private void sendMessage() {
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            // Create our 'model', a Chat object
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
        // adapter.add(new Comment(true, "Hello bubbles!"));

        DataStore.get_conversation(user_from, user_to, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    return;
                }

                List<Conversation> conversations = (List<Conversation>) result;

                for(int i = 0; i < conversations.size(); i++)
                {
                    Conversation conf = conversations.get(i);
                    if (conf.getUser_from().equalsIgnoreCase(user_from))
                        adapter.add(new Comment(false, conf.getMsg()));
                    else
                        adapter.add(new Comment(true, conf.getMsg()));
                }
            }
        });
    }

    private void subscribeToChannel(String channel_name)
    {
        try {
            pubnub.subscribe(channel_name, new Callback() {

                        @Override
                        public void connectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : CONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void disconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        public void reconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void successCallback(String channel, final Object message) {
                            System.out.println("SUBSCRIBE : " + channel + " : "
                                    + message.getClass() + " : " + message.toString());
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.add(new Comment(true, message.toString()));
                                }
                            });

                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            System.out.println("SUBSCRIBE : ERROR on channel " + channel
                                    + " : " + error.toString());
                        }
                    }
            );
        } catch (PubnubException e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public void backButtonWasPressed() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);


        getActivity().setTitle(club.getTitle());
        ((MainActivity)getActivity()).setCurrentFragment(provoiusFregment);
    }
}