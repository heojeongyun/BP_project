package com.example.nt_project02.Chat;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
public class UserModel implements Parcelable {
    public String birthday;
    public Integer bookmarks_number;
    public List<String> bookmarks;
    public String name;
    public String phonenumber;
    public String pushToken;
    public String region;
    public String sex;
    public String uid;
    public String user_kind;
    public String imageurl;
    public String hashtag;
    public List<String> requests;

    public UserModel(Parcel parcel) {
        birthday=parcel.readString();
        bookmarks_number=parcel.readInt();
        bookmarks = new ArrayList<String>();
        parcel.readList(bookmarks, UserModel.class.getClassLoader());
        name=parcel.readString();
        phonenumber=parcel.readString();
        pushToken=parcel.readString();
        region=parcel.readString();
        sex=parcel.readString();
        uid=parcel.readString();
        user_kind=parcel.readString();
        imageurl=parcel.readString();
        hashtag=parcel.readString();
        requests = new ArrayList<String>();
        parcel.readList(requests, UserModel.class.getClassLoader());
    }
    public UserModel(){
    }
    /*
      public UserModel(String uid, String nick, String region, String hash, String self_info, String name, String sex, String phone, String city, String user_kind) {
          this.uid = uid;
          this.nick = nick;
          this.region = region;
          this.hash = hash;
          this.self_info = self_info;
          this.name = name;
          this.sex = sex;
          this.phone = phone;
          this.city = city;
          this.user_kind = user_kind;
      }
  */
    public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel parcel) {
            return new UserModel(parcel);
        }
        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.birthday);
        dest.writeInt(this.bookmarks_number);
        dest.writeList(this.bookmarks);
        dest.writeString(this.name);
        dest.writeString(this.phonenumber);
        dest.writeString(this.pushToken);
        dest.writeString(this.region);
        dest.writeString(this.sex);
        dest.writeString(this.uid);
        dest.writeString(this.user_kind);
        dest.writeString(this.imageurl);
        dest.writeString(this.hashtag);
        dest.writeList(this.requests);
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public Integer getBookmarks_number() {
        return bookmarks_number;
    }
    public void setBookmarks_number(Integer bookmarks_number) {
        this.bookmarks_number = bookmarks_number;
    }
    public List<String> getBookmarks() {
        return bookmarks;
    }
    public void setBookmarks(List<String> bookmarks) {
        this.bookmarks = bookmarks;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhonenumber() {
        return phonenumber;
    }
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
    public String getPushToken() {
        return pushToken;
    }
    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }
    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getUser_kind() {
        return user_kind;
    }
    public void setUser_kind(String user_kind) {
        this.user_kind = user_kind;
    }
    public String getImageurl() {
        return imageurl;
    }
    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
    public String getHashtag() {
        return hashtag;
    }
    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public List<String> getRequests() {
        return requests;
    }

    public void setRequests(List<String> requests) {
        this.requests = requests;
    }

    public static Creator<UserModel> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "birthday='" + birthday + '\'' +
                ", bookmarks_number=" + bookmarks_number +
                ", bookmarks=" + bookmarks +
                ", name='" + name + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", pushToken='" + pushToken + '\'' +
                ", region='" + region + '\'' +
                ", sex='" + sex + '\'' +
                ", uid='" + uid + '\'' +
                ", user_kind='" + user_kind + '\'' +
                ", imageurl='" + imageurl + '\'' +
                ", hashtag='" + hashtag + '\'' +
                ", requests=" + requests +
                '}';
    }
}