package com.example.nt_project02.CustomData;

public class MemberInfo {
    //수정
    private String uid;
    private String name;
    private String sex;
    private String city;
    private String user_kind;



    public MemberInfo(String uid,String name, String sex, String city, String user_kind) {
        this.uid=uid;
        this.name = name;
        this.sex = sex;
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
