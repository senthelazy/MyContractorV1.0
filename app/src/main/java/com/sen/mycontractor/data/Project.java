package com.sen.mycontractor.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sen on 2017/8/22.
 */

public class Project implements Parcelable {
    private String location = "";
    private List<String> imagesOriginalUri = new ArrayList<String>();
    private List<String> imagesThumbnailUri = new ArrayList<String>();
    private String videoUrl = "";
    private String category = "";
    private String subCategory = "";
    private String jobDescription = "";
    private int mID = 0;
    private String CustomerUid = "";
    private String createdAt;
    private List<String> originalLastPathSegment=new ArrayList<String>();
    private List<String> thumbnailLastPathSegment=new ArrayList<String>();
    private String videoLastPathSegment;

    public Project(Parcel in) {
        location = in.readString();
        in.readStringList(imagesOriginalUri);
        in.readStringList(imagesThumbnailUri);
        videoUrl = in.readString();
        category = in.readString();
        subCategory = in.readString();
        jobDescription = in.readString();
        mID = in.readInt();
        CustomerUid = in.readString();
        createdAt=in.readString();
        in.readStringList(originalLastPathSegment);
        in.readStringList(thumbnailLastPathSegment);
        videoLastPathSegment=in.readString();
    }

    public Project() {

    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void setID(int ID) {
        this.mID = ID;
    }

    public int getID() {

        return mID;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getOriginalLastPathSegment() {
        return originalLastPathSegment;
    }

    public void setOriginalLastPathSegment(List<String> originalLastPathSegment) {
        this.originalLastPathSegment = originalLastPathSegment;
    }

    public List<String> getThumbnailLastPathSegment() {
        return thumbnailLastPathSegment;
    }

    public void setThumbnailLastPathSegment(List<String> thumbnailLastPathSegment) {
        this.thumbnailLastPathSegment = thumbnailLastPathSegment;
    }

    public String getVideoLastPathSegment() {
        return videoLastPathSegment;
    }

    public void setVideoLastPathSegment(String videoLastPathSegment) {
        this.videoLastPathSegment = videoLastPathSegment;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(location);
        parcel.writeStringList(imagesOriginalUri);
        parcel.writeStringList(imagesThumbnailUri);
        parcel.writeString(videoUrl);
        parcel.writeString(category);
        parcel.writeString(subCategory);
        parcel.writeString(jobDescription);
        parcel.writeInt(mID);
        parcel.writeString(CustomerUid);
        parcel.writeString(createdAt);
        parcel.writeStringList(originalLastPathSegment);
        parcel.writeStringList(thumbnailLastPathSegment);
        parcel.writeString(videoLastPathSegment);

    }



    public String getLocation() {
        return location;
    }

    public List<String> getImagesOriginalUri() {
        return imagesOriginalUri;
    }

    public void setImagesOriginalUri(ArrayList<String> imagesOriginalUri) {
        this.imagesOriginalUri = imagesOriginalUri;
    }

    public List<String> getImagesThumbnailUri() {
        return imagesThumbnailUri;
    }

    public void setImagesThumbnailUri(ArrayList<String> imagesThumbnailUri) {
        this.imagesThumbnailUri = imagesThumbnailUri;
    }


    public String getCustomerUid() {
        return CustomerUid;
    }

    public void setCustomerUid(String customerUid) {
        this.CustomerUid = customerUid;
    }

    public String getVideoUrl() {
        return videoUrl;
    }


    public String getCategory() {
        return category;
    }


    public String getSubCategory() {
        return subCategory;
    }

    public void setLocation(String mLocation) {
        this.location = mLocation;
    }


    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobDescription() {
        return jobDescription;
    }

}
