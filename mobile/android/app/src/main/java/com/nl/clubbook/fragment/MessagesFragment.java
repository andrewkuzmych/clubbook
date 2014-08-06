package com.nl.clubbook.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.adapter.MessagesAdapter;
import com.nl.clubbook.datasource.ChatDto;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.helper.SessionManager;

import java.util.HashMap;
import java.util.List;

public class MessagesFragment extends BaseFragment {
    ListView message_list;

    public MessagesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);
        message_list = (ListView) rootView.findViewById(R.id.message_list);
        loadData(true);
        return rootView;
    }

    public void loadData(final boolean loading) {
        final Context contextThis = getActivity();
        final BaseFragment thisInstance = this;

        final SessionManager session = new SessionManager(getActivity());
        final HashMap<String, String> user = session.getUserDetails();

        if (loading)
            ((BaseActivity) getActivity()).showProgress("Loading...");

        DataStore.get_conversations(user.get(SessionManager.KEY_ID), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    if (loading)
                        if(getActivity() != null)
                            ((BaseActivity) getActivity()).hideProgress(false);
                        else
                            Log.e("ERROR", "getActivity is null");
                    return;
                }

                if (loading)
                    ((BaseActivity) getActivity()).hideProgress(true);

                message_list.setAdapter(new MessagesAdapter(contextThis, R.layout.message_list_item, (List<ChatDto>) result));

                message_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        String user_id = view.findViewById(R.id.user_name).getTag().toString();
                        String user_title = ((TextView) view.findViewById(R.id.user_name)).getText().toString();
                        ChatFragment fragment = new ChatFragment(thisInstance, user_id, user_title);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();

                        mFragmentTransaction.addToBackStack(null);
                        mFragmentTransaction.replace(R.id.frame_container, fragment).commit();

                    }
                });
            }
        });
    }
}
