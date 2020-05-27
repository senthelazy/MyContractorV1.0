package com.sen.mycontractor.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.stfalcon.chatkit.commons.models.IUser;

/**
 * Created by Sen on 2017/8/22.
 */

public class Customer implements Parcelable {
    private String mFullName, mEmail, mPhone, mUid, mPassword, mPersonalPhotoUrl, mStatus,mLocation = "";


    protected Customer(Parcel in) {
        this.mFullName = in.readString();
        this.mEmail = in.readString();
        this.mPhone = in.readString();
        this.mUid = in.readString();
        this.mPassword = in.readString();
        this.mPersonalPhotoUrl = in.readString();
        this.mStatus = in.readString();
        this.mLocation=in.readString();

    }

    public Customer(String fullName, String email, String phone, String uid,
                    String password, String personalPhotoUrl, String status,String mLocation) {
        this.mFullName = fullName;
        this.mEmail = email;
        this.mPhone = phone;
        this.mUid = uid;
        this.mPassword = password;
        this.mPersonalPhotoUrl = personalPhotoUrl;
        this.mStatus = status;
        this.mLocation=mLocation;
    }

    public Customer() {

    }






    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String fullName) {
        mFullName = fullName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getID() {
        return mUid;
    }

    public void setID(String ID) {
        this.mUid = ID;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getPersonalPhotoUrl() {
        return mPersonalPhotoUrl;
    }

    public void setPersonalPhotoUrl(String personalPhotoUrl) {
        mPersonalPhotoUrl = personalPhotoUrl;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        this.mStatus = status;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mFullName);
        parcel.writeString(mEmail);
        parcel.writeString(mPhone);
        parcel.writeString(mUid);
        parcel.writeString(mPassword);
        parcel.writeString(mPersonalPhotoUrl);
        parcel.writeString(mStatus);
        parcel.writeString(mLocation);
    }


}
