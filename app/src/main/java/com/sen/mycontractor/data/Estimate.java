package com.sen.mycontractor.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jessie on 9/15/2017.
 */

public class Estimate implements Parcelable {
    private String estimateId,mAmount, mTime, mOpinion, mTechName, mTechPhone,mTechId,mProjectId = "";

    protected Estimate(Parcel in) {
        estimateId=in.readString();
        mAmount = in.readString();
        mTime = in.readString();
        mOpinion = in.readString();
        mTechName = in.readString();
        mTechPhone = in.readString();
        mTechId=in.readString();
        mProjectId=in.readString();
    }

    public Estimate() {

    }

    public Estimate(String estimateId,String amount, String time, String opinion,String techName,String techPhone,String techId,String projectId) {
        this.estimateId=estimateId;
        this.mAmount = amount;
        this.mTime = time;
        this.mOpinion = opinion;
        this.mTechName=techName;
        this.mTechPhone=techPhone;
        this.mTechId=techId;
        this.mProjectId=projectId;

    }

    public static final Creator<Estimate> CREATOR = new Creator<Estimate>() {
        @Override
        public Estimate createFromParcel(Parcel in) {
            return new Estimate(in);
        }

        @Override
        public Estimate[] newArray(int size) {
            return new Estimate[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getAmount() {
        return mAmount;
    }

    public void setAmount(String mAmount) {
        this.mAmount = mAmount;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    public String getOpinion() {
        return mOpinion;
    }

    public void setOpinion(String mOpinion) {
        this.mOpinion = mOpinion;
    }

    public String getTechName() {
        return mTechName;
    }

    public void setTechName(String mTechName) {
        this.mTechName = mTechName;
    }

    public String getTechPhone() {
        return mTechPhone;
    }

    public String getTechId() {
        return mTechId;
    }

    public void setTechId(String mTechId) {
        this.mTechId = mTechId;
    }

    public void setTechPhone(String mTechPhone) {
        this.mTechPhone = mTechPhone;
    }

    public String getProjectId() {
        return mProjectId;
    }

    public void setProjectId(String mProjectId) {
        this.mProjectId = mProjectId;
    }

    public String getEstimateId() {
        return estimateId;
    }

    public void setEstimateId(String estimateId) {
        this.estimateId = estimateId;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(estimateId);
        parcel.writeString(mAmount);
        parcel.writeString(mTime);
        parcel.writeString(mOpinion);
        parcel.writeString(mTechName);
        parcel.writeString(mTechPhone);
        parcel.writeString(mTechId);
        parcel.writeString(mProjectId);
    }
}
