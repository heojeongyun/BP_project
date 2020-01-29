package com.example.nt_project02;

public class Native_MemberInfo {

    String uid;
    String name;
    String phonenumber;
    String sex;
    String birthday;
    String user_kind;
    String region;

    public Native_MemberInfo(String uid, String name, String phonenumber, String sex, String birthday, String user_kind, String region) {
        this.uid = uid;
        this.name = name;
        this.phonenumber = phonenumber;
        this.sex = sex;
        this.birthday = birthday;
        this.user_kind = user_kind;
        this.region = region;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getUser_kind() {
        return user_kind;
    }

    public void setUser_kind(String user_kind) {
        this.user_kind = user_kind;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
