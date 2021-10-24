package com.bilocker.utils;

import com.bilocker.model.User;

public class CurrentUser {

    private static CurrentUser instance = null;
    private User currUser;

    public static CurrentUser getInstance() {

        if(instance == null){
            instance = new CurrentUser();
        }
        return instance;
    }

    private CurrentUser(){}

    public User getCurrUser() {
        return currUser;
    }

    public void setCurrUser(User currUser) {
        this.currUser = currUser;
    }
}
