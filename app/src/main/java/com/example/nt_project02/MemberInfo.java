package com.example.nt_project02;

public class MemberInfo {

    private String uid;
    private String name;
    private String sex;
    private String birthday;
    private String city;
    private String user_kind;



    public MemberInfo(String uid,String name, String sex, String phone, String city, String user_kind) {
        this.uid=uid;
        this.name = name;
        this.sex = sex;
        this.birthday = phone;
        this.city = city;
        this.user_kind = user_kind;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return birthday;
    }

    public void setPhone(String phone) {
        this.birthday = phone;
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
}
