package com.nl.clubbook.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nl.clubbook.R;

public class NotificationsFragment extends Fragment {
	
	public NotificationsFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
         
        return rootView;
    }
}
