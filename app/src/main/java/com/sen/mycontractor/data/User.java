package com.sen.mycontractor.data;

import com.stfalcon.chatkit.commons.models.IUser;

/**
 * Created by Jessie on 9/29/2017.
 */

public class User implements IUser {
    private String userId;
    private String name;
    private String photoUrl;
    private boolean online;

    public User(String userId, String name, String photoUrl, boolean online) {
        this.userId = userId;
        this.name = name;
        this.photoUrl = photoUrl;
        this.online = online;

    }


    public void setId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
    @Override
    public String getId() {
        return userId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return photoUrl;
    }

    public boolean isOnline() {
        return online;
    }
}

