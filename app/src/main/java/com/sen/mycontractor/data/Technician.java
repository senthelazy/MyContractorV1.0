package com.sen.mycontractor.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sen on 2017/8/22.
 */

public class Technician implements Parcelable {
    private String mFullName, mCompanyName, mCityName, mEmail, mPhone, mUid,
            mPassword, mPersonalPhotoUrl, mStatus, mBusinessLicenseUrl, mInsuranceUrl;

    protected Technician(Parcel in) {
        this.mFullName = in.readString();
        this.mCompanyName = in.readString();
        this.mCityName = in.readString();
        this.mEmail = in.readString();
        this.mPhone = in.readString();
        this.mUid = in.readString();
        this.mPassword = in.readString();
        this.mPersonalPhotoUrl = in.readString();
        this.mStatus = in.readString();
        this.mBusinessLicenseUrl = in.readString();
        this.mInsuranceUrl = in.readString();
    }

    public Technician(String fullName, String companyName, String cityName, String email, String phone, String uid,
                      String password, String personalPhotoUrl, String status, String businessLicenseUrl, String insuranceUrl) {
        this.mFullName = fullName;
        this.mCompanyName = companyName;
        this.mCityName = cityName;
        this.mEmail = email;
        this.mPhone = phone;
        this.mUid = uid;
        this.mPassword = password;
        this.mPersonalPhotoUrl = personalPhotoUrl;
        this.mStatus = status;
        this.mBusinessLicenseUrl = businessLicenseUrl;
        this.mInsuranceUrl = insuranceUrl;
    }

    public Technician() {

    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        this.mUid = uid;
    }


    public static final Creator<Technician> CREATOR = new Creator<Technician>() {
        @Override
        public Technician createFromParcel(Parcel in) {
            return new Technician(in);
        }

        @Override
        public Technician[] newArray(int size) {
            return new Technician[size];
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
        this.mFullName = fullName;
    }

    public String getCompanyName() {
        return mCompanyName;
    }

    public void setCompanyName(String companyName) {
        this.mCompanyName = companyName;
    }


    public String getCityName() {
        return mCityName;
    }

    public void setCityName(String cityName) {
        this.mCityName = cityName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        this.mPhone = phone;
    }


    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public String getPersonalPhotoUrl() {
        return mPersonalPhotoUrl;
    }

    public void setPersonalPhotoUrl(String personalPhotoUrl) {
        this.mPersonalPhotoUrl = personalPhotoUrl;
    }

    public String getBusinessLicenseUrl() {
        return mBusinessLicenseUrl;
    }

    public void setBusinessLicenseUrl(String businessLicenseUrl) {
        this.mBusinessLicenseUrl = businessLicenseUrl;
    }

    public String getInsuranceUrl() {
        return mInsuranceUrl;
    }

    public void setInsuranceUrl(String insuranceUrl) {
        this.mInsuranceUrl = insuranceUrl;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        this.mStatus = status;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mFullName);
        parcel.writeString(mCompanyName);
        parcel.writeString(mCityName);
        parcel.writeString(mEmail);
        parcel.writeString(mPhone);
        parcel.writeString(mUid);
        parcel.writeString(mPassword);
        parcel.writeString(mPersonalPhotoUrl);
        parcel.writeString(mStatus);
        parcel.writeString(mBusinessLicenseUrl);
        parcel.writeString(mInsuranceUrl);
    }
}
