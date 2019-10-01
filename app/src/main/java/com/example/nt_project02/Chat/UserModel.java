package com.example.nt_project02.Chat;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {

    //public String profieImageUrl;
    public String uid;
    public String nick;
    public String region;
    public String hash;
    public String self_info;
    public String name;
    public String sex;
    public String phone;
    public String city;
    public String user_kind;

    public UserModel(Parcel parcel) {

        uid=parcel.readString();
        name=parcel.readString();
        city=parcel.readString();
        nick=parcel.readString();
        self_info=parcel.readString();
        user_kind=parcel.readString();
        region=parcel.readString();
        sex=parcel.readString();
        phone=parcel.readString();
        hash=parcel.readString();

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
        dest.writeString(this.uid);
        dest.writeString(this.name);
        dest.writeString(this.city);
        dest.writeString(this.nick);
        dest.writeString(this.self_info);
        dest.writeString(this.user_kind);
        dest.writeString(this.region);
        dest.writeString(this.sex);
        dest.writeString(this.phone);
        dest.writeString(this.hash);


    }


    @Override
    public int describeContents() {
        return 0;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSelf_info() {
        return self_info;
    }

    public void setSelf_info(String self_info) {
        this.self_info = self_info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUser_kind() {
        return user_kind;
    }

    public void setUser_kind(String user_kind) {
        this.user_kind = user_kind;
    }

    public static Creator<UserModel> getCREATOR() {
        return CREATOR;
    }
}
