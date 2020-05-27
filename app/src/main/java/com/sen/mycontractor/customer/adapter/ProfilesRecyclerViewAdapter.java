package com.sen.mycontractor.customer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.sen.mycontractor.R;
import com.sen.mycontractor.common.ChatPage;
import com.sen.mycontractor.customer.widget.FilterImageView;
import com.sen.mycontractor.data.Dialog;
import com.sen.mycontractor.data.Message;
import com.sen.mycontractor.data.Technician;
import com.sen.mycontractor.data.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by Jessie on 10/14/2017.
 */

public class ProfilesRecyclerViewAdapter extends RecyclerView.Adapter<ProfilesRecyclerViewAdapter.ContractorViewHolder> {
    private List<Technician> technicians;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private DatabaseReference databaseRef;
    private FirebaseUser mUser;
    private Activity activity;
    private Dialog existDialog;


    public ProfilesRecyclerViewAdapter(Activity activity, List<Technician> technicians) {
        this.activity = activity;
        this.technicians = technicians;
        databaseRef = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public ContractorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_profile, parent, false);
        ContractorViewHolder cvh = new ContractorViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(ContractorViewHolder holder, final int position) {
        holder.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        holder.emailIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.phoneIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + technicians.get(position).getPhone()));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    activity.getApplicationContext().startActivity(callIntent);

                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });
        holder.contractorPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + technicians.get(position).getPhone()));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    activity.getApplicationContext().startActivity(callIntent);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });
        holder.chatMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.chatMessagesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseRef.child("Dialogs").child(technicians.get(position).getUid()).orderByChild("userId").equalTo(technicians.get(position).getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        Dialog dialog = ds.getValue(Dialog.class);
                                        existDialog = new Dialog(dialog.getId(), dialog.getDialogName(),
                                                dialog.getDialogPhoto(), dialog.getUsersUIds(), dialog.getMessageId(), dialog.getText()
                                                , dialog.getDate(), dialog.getUserId(), dialog.getUserName(), dialog.getUserPhotoUrl(), dialog.isOnline(),
                                                dialog.getImageUrl(), dialog.getVoiceUrl(), dialog.getDuration(), dialog.getUnreadCount());
                                    }
                                    ChatPage.open(activity, existDialog);

                                } else {
                                    databaseRef.child("Dialogs").child(technicians.get(position).getUid()).orderByChild("userId").equalTo(mUser.getUid())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                            Dialog dialog = ds.getValue(Dialog.class);
                                                            Log.i("dates:", dialog.getCreatedAt() + " and " + dialog.getDate());
                                                            existDialog = new Dialog(dialog.getId(), dialog.getDialogName(),
                                                                    dialog.getDialogPhoto(), dialog.getUsersUIds(), dialog.getMessageId(), dialog.getText()
                                                                    , dialog.getDate(), dialog.getUserId(), dialog.getUserName(), dialog.getUserPhotoUrl(), dialog.isOnline(),
                                                                    dialog.getImageUrl(), dialog.getVoiceUrl(), dialog.getDuration(), dialog.getUnreadCount());
                                                        }
                                                        ChatPage.open(activity, existDialog);
                                                    } else {
                                                        ArrayList<User> users = new ArrayList<>();
                                                        User SenderUser = new User(technicians.get(position).getUid(), technicians.get(position).getFullName(),
                                                                technicians.get(position).getPersonalPhotoUrl(), true);
                                                        final String messageId = Long.toString(UUID.randomUUID().getLeastSignificantBits());
                                                        final String dialogId = Long.toString(UUID.randomUUID().getLeastSignificantBits());
                                                        final Message welcomeMessage = new Message(dialogId, messageId, SenderUser, "Hello,how is it going?");
                                                        User receiverUser = new User(mUser.getUid(), mUser.getDisplayName(),
                                                                mUser.getPhotoUrl().toString(), true);
                                                        users.add(receiverUser);
                                                        users.add(SenderUser);
                                                        final Dialog senderDialog = new Dialog(dialogId, mUser.getDisplayName(),
                                                                mUser.getPhotoUrl().toString(), users, welcomeMessage, 1);
                                                        databaseRef.child("Dialogs").child(technicians.get(position).getUid()).child(dialogId).setValue(senderDialog);
                                                        final Dialog ReceiverDialog = new Dialog(dialogId, technicians.get(position).getFullName(),
                                                                technicians.get(position).getPersonalPhotoUrl(), users, welcomeMessage, 1);
                                                        databaseRef.child("Dialogs").child(mUser.getUid()).child(dialogId).setValue(ReceiverDialog);
                                                        databaseRef.child("Messages").child(messageId).setValue(welcomeMessage);
                                                        ChatPage.open(activity, ReceiverDialog);

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
        });

        holder.contractorName.setText(technicians.get(position).getFullName());
        holder.contractorPhone.setText(technicians.get(position).getPhone());
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer())
                .build();
        imageLoader.displayImage(technicians.get(position).getPersonalPhotoUrl(),
                new ImageViewAware(holder.contractorPhoto), options);
        holder.companyName.setText(technicians.get(position).getCompanyName());
        holder.email.setText(technicians.get(position).getEmail());
        holder.cityName.setText(technicians.get(position).getCityName());
    }

    @Override
    public int getItemCount() {
        return technicians.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ContractorViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView contractorName, companyName, contractorPhone, email, chatMessages, cityName;
        FilterImageView contractorPhoto, emailIcon, phoneIcon, chatMessagesIcon;


        ContractorViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.singleContractorCv);
            contractorName = (TextView) itemView.findViewById(R.id.contractor_name);
            contractorPhone = (TextView) itemView.findViewById(R.id.phone);
            contractorPhoto = (FilterImageView) itemView.findViewById(R.id.contractor_photo);
            companyName = (TextView) itemView.findViewById(R.id.company_name);
            email = (TextView) itemView.findViewById(R.id.email);
            emailIcon = (FilterImageView) itemView.findViewById(R.id.email_icon);
            phoneIcon = (FilterImageView) itemView.findViewById(R.id.phone_icon);
            chatMessagesIcon = (FilterImageView) itemView.findViewById(R.id.chat_messages_Icon);
            cityName = (TextView) itemView.findViewById(R.id.city_name);
            chatMessages = (TextView) itemView.findViewById(R.id.chat_messages);

        }


    }


}
