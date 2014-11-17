package com.nl.clubbook.helper;

import com.nl.clubbook.datasource.User;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Volodymyr on 17.11.2014.
 */
public class SingleUsersHolder {

    private List<User> mUsers = new ArrayList<User>();

    private static SingleUsersHolder sInstance;

    public static SingleUsersHolder getInstance() {
        if(sInstance == null) {
            sInstance = new SingleUsersHolder();
        }

        return sInstance;
    }

    private SingleUsersHolder() {
    }

    @NotNull
    public List<User> getUsers() {
        return mUsers;
    }

    public void setUsers(@Nullable List<User> users) {
        if(users == null) {
            mUsers.clear();
            return;
        }

        this.mUsers = users;
    }
}
