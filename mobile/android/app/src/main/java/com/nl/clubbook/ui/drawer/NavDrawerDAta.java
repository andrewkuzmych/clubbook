package com.nl.clubbook.ui.drawer;

import android.content.Context;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Volodymyr on 19.08.2014.
 */
public class NavDrawerDAta {

    public static final int CLUB_LIST_POSITION = 0;
    public static final int MESSAGES_POSITION = 1;
    public static final int FRIENDS_POSITION = 2;
    public static final int SETTINGS_POSITION = 3;

    public static final int DEFAULT_FRAGMENT_NUMBER = CLUB_LIST_POSITION;

    private NavDrawerDAta() {
    }

    public static List<NavDrawerItem> getNavDrawerItems(Context context) {
        List<NavDrawerItem> items = new ArrayList<NavDrawerItem>();

        for(int i = 0; i < DRAWER_TITLES_RESOURCES.length; i++) {
            NavDrawerItem item = new NavDrawerItem();

            item.setTitle(context.getString(DRAWER_TITLES_RESOURCES[i]));
            item.setIcon(DRAWER_ICONS[i]);
            item.setCount(0);

            items.add(item);
        }

        return items;
    }

    public static final int[] DRAWER_TITLES_RESOURCES = new int[] {
            R.string.club_list,
            R.string.messages,
            R.string.friends,
            R.string.settings
    };

    public static final int[] DRAWER_ICONS = new int[] {
            R.drawable.ic_club_list_nav_drawer,
            R.drawable.ic_messages_nav_drawer,
            R.drawable.ic_friends_nav_drawer,
            R.drawable.ic_settings_nav_drawer
    };
}
