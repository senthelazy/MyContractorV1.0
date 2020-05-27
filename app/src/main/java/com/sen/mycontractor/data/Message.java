package com.sen.mycontractor.data;

import android.net.ParseException;

import com.google.firebase.database.Exclude;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jessie on 10/4/2017.
 */

public class Message implements IMessage,
        MessageContentType.Image, /*this is for default image messages implementation*/
        MessageContentType /*and this one is for custom content type (in this case - voice message)*/ {

    private String dialogId;
    private String messageId;
    private String text;
    private Date createdAt;
    private String date;
    private User user;
    private String userId;
    private String userName;
    private String userPhotoUrl;
    private boolean online;
    private Image image;
    private String imageUrl;
    private Voice voice;
    private String voiceUrl;
    private int duration;
    private Date dateForCreatedAt;
    private String status;

    public Message() {
    }

    public Message(String dialogId, String messageId, User user, String text) {
        this(dialogId, messageId, user, text, new Date());
    }

    public Message(String dialogId, String messageId, User user, String text, Date createdAt) {
        this.dialogId = dialogId;
        this.messageId = messageId;
        this.text = text;
        this.user = user;
        this.userId = user.getId();
        this.userName = user.getName();
        this.userPhotoUrl = user.getAvatar();
        this.online = user.isOnline();
        /*this.imageUrl="";
        image=new Image(imageUrl);
        this.voiceUrl="";
        this.duration=0;
        voice=new Voice(voiceUrl,duration);
        */
        this.createdAt = createdAt;
        this.date = new SimpleDateFormat("yyyy MMM d, EEE 'at' h:mm:ss a", Locale.getDefault()).format(createdAt);
        this.dateForCreatedAt=createdAt;
    }

    public Message(String dialogId, String messageId, String text, String date, String userId, String name, String photoUrl
            , boolean online, String imageUrl, String voiceUrl, int duration) {
        this.dialogId = dialogId;
        this.messageId = messageId;
        this.text = text;
        this.date = date;
        try {
            this.createdAt = new SimpleDateFormat("yyyy MMM d, EEE 'at' h:mm:ss a", Locale.getDefault())
                    .parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        this.user=new User(userId,name,photoUrl,online);
        this.userId = userId;
        this.userName = name;
        this.userPhotoUrl = photoUrl;
        this.online = online;
        this.imageUrl = imageUrl;
        image=new Image(imageUrl);
        this.voiceUrl = voiceUrl;
        this.duration = duration;
        voice=new Voice(voiceUrl,duration);
        this.dateForCreatedAt=createdAt;
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    @Override
    public String getId() {
        return messageId;
    }

    public void setId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    @Exclude
    public Date getCreatedAt() {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy MMM d, EEE 'at' h:mm:ss a", Locale.getDefault());
        try {
            dateForCreatedAt = sdf.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return createdAt==null?dateForCreatedAt:createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    @Exclude
    public User getUser() {
        return user==null?new User(userId,userName,userPhotoUrl,online):user;
    }

    public void setUser(User user) {
        this.user = user;
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

    @Override
    public String getImageUrl() {
        return image==null?null:image.url;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        image=new Image(imageUrl);
    }

    public String getVoiceUrl() {
        return voice==null?null:voice.url;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
        voice=new Voice(voiceUrl,duration);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Exclude
    public String getStatus() {
        return "Sent";
    }

    public void setStatus(String status) {
        this.status=status;
    }

    public void setImage(Image image){
        this.image=image;
    }

    public void setVoice(Voice voice){
        this.voice=voice;
    }

    public static class Image {

        private String url;

        public Image(String url) {
            this.url = url;
        }
    }

    public static class Voice {

        private String url;
        private int duration;

        public Voice(String url, int duration) {
            this.url = url;
            this.duration = duration;
        }

        public String getUrl() {
            return url;
        }

        public int getDuration() {
            return duration;
        }
    }
}
