package com.sen.mycontractor.common;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sen.mycontractor.R;
import com.sen.mycontractor.data.Message;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public abstract class CommonMessagesActivity extends AppCompatActivity implements MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener {
    private static final int TOTAL_MESSAGES_COUNT=100;
    protected final String senderId = "0";
    private String receiverId;
    protected ImageLoader imageLoader;
    protected MessagesListAdapter<Message> messagesListAdapter;
    private Menu menu;
    private int selectionCount;
    private Date lastLoadedDate;
    private DatabaseReference mDatabaseRef;
    private FirebaseHelper mHelper;
    private Message welcomeMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader=new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                if(url!=null &&!url.equals("")) {
                    Picasso.with(CommonMessagesActivity.this).load(url).into(imageView);
                }
            }
        };
        mDatabaseRef= FirebaseDatabase.getInstance().getReference();
        mHelper=new FirebaseHelper(mDatabaseRef);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //messagesListAdapter.addToStart(MessagesFixtures.getTextMessage(), true);
        /*welcomeMessage=mHelper.getWelcomeMessage();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               messagesListAdapter.addToStart(welcomeMessage,true);
            }
        }, 1500);*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        getMenuInflater().inflate(R.menu.chat_actions_menu,menu);
        onSelectionChanged(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                messagesListAdapter.deleteSelectedMessages();
                break;
            case R.id.action_copy:
                messagesListAdapter.copySelectedMessagesText(this, getMessageStringFormatter(), true);
                ToastHelper.showToast(this, R.string.copied_message, true);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed();
        } else {
            messagesListAdapter.unselectAllItems();
        }
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
            loadMessages();
        }
    }

    @Override
    public void onSelectionChanged(int count) {
        this.selectionCount = count;
        menu.findItem(R.id.action_delete).setVisible(count > 0);
        menu.findItem(R.id.action_copy).setVisible(count > 0);
    }

    public void loadMessages() {
                //ArrayList<Message> messages = mHelper.getMessages();
                //ArrayList<Message> messages = mHelper.getMessages(lastLoadedDate);
                //lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt();
                //messagesListAdapter.addToEnd(messages, false);

    }

    private MessagesListAdapter.Formatter<Message> getMessageStringFormatter() {
        return new MessagesListAdapter.Formatter<Message>() {
            @Override
            public String format(Message message) {
                //String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                        //.format(message.getCreatedAt());
                String date=message.getDate();
                String text = message.getText();
                if (text == null) text = "[attachment]";

                return String.format(Locale.getDefault(), "%s: %s (%s)",
                        message.getUser().getName(), text, date);
            }
        };
    }
}
