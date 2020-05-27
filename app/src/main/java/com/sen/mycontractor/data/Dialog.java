package com.sen.mycontractor.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.stfalcon.chatkit.commons.models.IDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jessie on 10/4/2017.
 */

public class Dialog implements IDialog<Message>, Parcelable {

    private String dialogId;
    private String dialogPhoto;
    private String dialogName;
    private ArrayList<User> users;
    private ArrayList<String> usersUIds;
    private Message lastMessage;
    private String messageId;
    private String text;
    private Date createdAt;
    private String date;
    private String userId;
    private String userName;
    private String userPhotoUrl;
    private boolean online;
    private String imageUrl;
    private String voiceUrl;
    private int duration;
    private int unreadCount;
    private Date dateForCreatedAt;

    @Override
    public String toString() {
        return "Values are " + dialogId + dialogName + dialogPhoto + users+usersUIds +
                lastMessage +messageId + text +createdAt+ date + userId + userName + userPhotoUrl +
                online + imageUrl + voiceUrl + duration + unreadCount;
    }

    public Dialog(String dialogId, String dialogName, String photo,
                  ArrayList<User> users, Message lastMessage, int unreadCount) {
        this.dialogId = dialogId;
        this.dialogName = dialogName;
        this.dialogPhoto = photo;
        this.users = users;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
        this.usersUIds = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            this.usersUIds.add(users.get(i).getId());
        }
        this.messageId = lastMessage.getId();
        this.text = lastMessage.getText();
        this.createdAt = lastMessage.getCreatedAt();
        this.date = new SimpleDateFormat("yyyy MMM d, EEE 'at' h:mm:ss a", Locale.getDefault()).format(createdAt);
        this.userId = lastMessage.getUserId();
        this.userName = lastMessage.getUserName();
        this.userPhotoUrl = lastMessage.getUserPhotoUrl();
        this.online = lastMessage.isOnline();
        this.imageUrl = lastMessage.getImageUrl();
        this.voiceUrl = lastMessage.getVoiceUrl();
        this.duration = lastMessage.getDuration();
        this.unreadCount = unreadCount;
    }

    public Dialog(String dialogId, String dialogName, String dialogPhoto, ArrayList<String> usersUIds
            , String messageId, String text, String date, String userId, String name, String photoUrl,
                  boolean online, String imageUrl, String voiceUrl, int duration, int unreadCount) {
        this.dialogId = dialogId;
        this.dialogName = dialogName;
        this.dialogPhoto = dialogPhoto;
        this.usersUIds = usersUIds;
        this.lastMessage = new Message(dialogId, messageId, text, date, userId, name, photoUrl, online, imageUrl, voiceUrl, duration);
        this.messageId = messageId;
        this.text = text;
        this.date = date;
        try {
            this.createdAt = new SimpleDateFormat("yyyy MMM d, EEE 'at' h:mm:ss a", Locale.getDefault())
                    .parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        this.userId = userId;
        this.userName = name;
        this.userPhotoUrl = photoUrl;
        this.online = online;
        this.imageUrl = imageUrl;
        this.voiceUrl = voiceUrl;
        this.duration = duration;
        this.unreadCount = unreadCount;
    }

    public Dialog() {

    }

    protected Dialog(Parcel in) {
        dialogId = in.readString();
        dialogPhoto = in.readString();
        dialogName = in.readString();
        usersUIds = in.createStringArrayList();
        messageId = in.readString();
        text = in.readString();
        date = in.readString();
        userId = in.readString();
        userName = in.readString();
        userPhotoUrl = in.readString();
        online = in.readByte() != 0;
        imageUrl = in.readString();
        voiceUrl = in.readString();
        duration = in.readInt();
        unreadCount = in.readInt();
    }

    public static final Creator<Dialog> CREATOR = new Creator<Dialog>() {
        @Override
        public Dialog createFromParcel(Parcel in) {
            return new Dialog(in);
        }

        @Override
        public Dialog[] newArray(int size) {
            return new Dialog[size];
        }
    };

    @Override
    public String getId() {
        return dialogId;
    }

    public void setId(String dialogid) {
        this.dialogId = dialogid;
    }

    @Override
    public String getDialogName() {
        return dialogName;
    }

    public void setDialogName(String dialogName) {
        this.dialogName = dialogName;
    }

    @Override
    public String getDialogPhoto() {
        return dialogPhoto;
    }

    public void setDialogPhoto(String dialogPhoto) {
        this.dialogPhoto = dialogPhoto;
    }

    @Override
    @Exclude
    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
        this.usersUIds = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            this.usersUIds.add(users.get(i).getId());
        }
    }

    public ArrayList<String> getUsersUIds() {
        return usersUIds;
    }

    public void setUsersUIds(ArrayList<String> usersUIds) {
        this.usersUIds = usersUIds;
    }

    @Override
    @Exclude
    public Message getLastMessage() {
        Message message = new Message();
        message.setDialogId(dialogId);
        message.setId(messageId);
        message.setUser(new User(userId, userName, userPhotoUrl, online));
        message.setCreatedAt(getCreatedAt());
        return lastMessage == null ? message : lastMessage;
    }

    @Override
    @Exclude
    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
        this.dialogId = lastMessage.getDialogId();
        this.messageId = lastMessage.getId();
        this.text = lastMessage.getText();
        this.date = lastMessage.getDate();
        this.userId = lastMessage.getUserId();
        this.userName = lastMessage.getUserName();
        this.userPhotoUrl = lastMessage.getUserPhotoUrl();
        this.online = lastMessage.isOnline();
        this.imageUrl = lastMessage.getImageUrl();
        this.voiceUrl = lastMessage.getVoiceUrl();
        this.duration = lastMessage.getDuration();
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Exclude
    public Date getCreatedAt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM d, EEE 'at' h:mm:ss a", Locale.getDefault());
        try {
            dateForCreatedAt = sdf.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return createdAt == null ? dateForCreatedAt : createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        this.date = new SimpleDateFormat("yyyy MMM d, EEE 'at' h:mm:ss a", Locale.getDefault()).format(createdAt);

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        try {
            this.createdAt = new SimpleDateFormat("yyyy MMM d, EEE 'at' h:mm:ss a", Locale.getDefault())
                    .parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String photoUrl) {
        this.userPhotoUrl = photoUrl;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getImageUrl() {
        return imageUrl == null ? null : imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVoiceUrl() {
        return voiceUrl == null ? null : voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(dialogId);
        parcel.writeString(dialogPhoto);
        parcel.writeString(dialogName);
        parcel.writeStringList(usersUIds);
        parcel.writeString(messageId);
        parcel.writeString(text);
        parcel.writeString(date);
        parcel.writeString(userId);
        parcel.writeString(userName);
        parcel.writeString(userPhotoUrl);
        parcel.writeByte((byte) (online ? 1 : 0));
        parcel.writeString(imageUrl);
        parcel.writeString(voiceUrl);
        parcel.writeInt(duration);
        parcel.writeInt(unreadCount);
    }
}
