package com.sen.mycontractor.common;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;


import android.transition.Explode;
import android.transition.Slide;

import android.view.Gravity;
import android.view.View;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sen.mycontractor.R;

import com.sen.mycontractor.data.Dialog;
import com.sen.mycontractor.data.Message;
import com.sen.mycontractor.data.User;

import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChatPage extends CommonMessagesActivity implements MessageInput.InputListener, MessageInput.AttachmentsListener {

    public static void open(Activity context, Dialog dialog) {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context);
        Intent intent = new Intent(context, ChatPage.class);
        intent.putExtra("Dialog", dialog);
        context.startActivity(intent, options.toBundle());
    }

    @BindView(R.id.messagesList)
    MessagesList messageList;
    @BindView(R.id.input)
    MessageInput messageInput;
    private ArrayList<Message> messages = new ArrayList<>();
    private DatabaseReference mDatabaseRef;
    private FirebaseUser mUser;
    private Message message, lastMessage;
    private Dialog dialog;
    private String dialogName;
    private String dialogPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);
        ButterKnife.bind(this);
        setupWindowAnimations();
        dialog = getIntent().getParcelableExtra("Dialog");
        dialogName = dialog.getDialogName();
        dialogPhoto = dialog.getDialogPhoto();
        this.setTitle("Chatting with " + dialog.getDialogName() + "...");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        messageInput.setInputListener(this);
        messageInput.setAttachmentsListener(this);
        super.messagesListAdapter = new MessagesListAdapter<>(mUser.getUid(), super.imageLoader);
        super.messagesListAdapter.enableSelectionMode(this);
        //listenForNewMessage();

    }

    @Override
    public void onAddAttachments() {
        ToastHelper.showToast(this, "Voice and images is not available in beta version", false);
        //super.messagesListAdapter.addToStart(MessagesFixtures.getImageMessage(), true);
        //super.messagesListAdapter.addToStart(getImageMessage(image),true);
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        super.messagesListAdapter.addToStart(updateLastMessage(dialog.getId(), input.toString()), true);
        //super.messagesListAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadMessages();
    }


    public void loadMessages() {
        messages.clear();
        mDatabaseRef.child("Messages").orderByChild("dialogId").equalTo(dialog.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Message message = ds.getValue(Message.class);
                                lastMessage = new Message(message.getDialogId(), message.getId(), message.getText(), message.getDate(),
                                        message.getUserId(), message.getUserName(), message.getUserPhotoUrl(), message.isOnline(), message.getImageUrl()
                                        , message.getVoiceUrl(), message.getDuration());
                                messages.add(lastMessage);
                                Collections.sort(messages, new Comparator<Message>() {
                                    @Override
                                    public int compare(Message existMessage, Message newMessage) {
                                        if (existMessage.getCreatedAt() == null || newMessage.getCreatedAt() == null)
                                            return 0;
                                        return existMessage.getCreatedAt().compareTo(newMessage.getCreatedAt());
                                    }
                                });
                                Collections.reverse(messages);
                            }
                            addToChatList(messages);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }


    public void listenForNewMessage() {
        mDatabaseRef.child("Dialogs").child(mUser.getUid()).child(dialog.getId())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                      ChatPage.super.messagesListAdapter.update(lastMessage);
                       messageList.setAdapter(ChatPage.super.messagesListAdapter);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addToChatList(ArrayList<Message> historyMessages) {
        if (historyMessages != null) {
            super.messagesListAdapter.addToEnd(historyMessages, false);
        }
       /* super.messagesListAdapter.registerViewClickListener
                (R.id.messageUserAvatar, new MessagesListAdapter.OnMessageViewClickListener<Message>() {
                    @Override
                    public void onMessageViewClick(View view, Message message) {
                        //add personal profiles;
                    }
                });*/
        messageList.setAdapter(super.messagesListAdapter);
    }


    public Message updateLastMessage(String dialogId, String text) {
        String messageId = Long.toString(UUID.randomUUID().getLeastSignificantBits());
        User user = new User(mUser.getUid(), mUser.getDisplayName(), mUser.getPhotoUrl().toString(), true);
        message = new Message(dialogId, messageId, user, text);
        mDatabaseRef.child("Messages").child(messageId).setValue(message);
        dialog.setLastMessage(message);
        Map<String, Object> updateMessage = new HashMap<>();
        updateMessage.put("id", message.getDialogId());
        updateMessage.put("messageId", message.getId());
        updateMessage.put("text", message.getText());
        updateMessage.put("userId", message.getUserId());
        updateMessage.put("userName", message.getUserName());
        updateMessage.put("userPhotoUrl", message.getUserPhotoUrl());
        updateMessage.put("online", message.isOnline());
        updateMessage.put("date", message.getDate());
        for (int i = 0; i < dialog.getUsersUIds().size(); i++) {
            mDatabaseRef.child("Dialogs").child(dialog.getUsersUIds().get(i)).child(dialogId).updateChildren(updateMessage);
        }
        return message;
    }


    /*public Message getTextMessage(ArrayList<String> receivers,String text) {
        return new Message(uId, receivers, text);
    }*/

    /*public static Message getImageMessage(Message.Image image) {
        return new Message(getMessageId(), getUser(), image);
    }*/


    private void setupWindowAnimations() {
        Slide slideTransition = new Slide();
        slideTransition.setSlideEdge(Gravity.LEFT);
        slideTransition.setDuration(500);
        getWindow().setReenterTransition(slideTransition);
        getWindow().setExitTransition(slideTransition);
        getWindow().setAllowReturnTransitionOverlap(false);
        Explode enterTransition = new Explode();
        enterTransition.setDuration(500);
        getWindow().setEnterTransition(enterTransition);
    }
}
