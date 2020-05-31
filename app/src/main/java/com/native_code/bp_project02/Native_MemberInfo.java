package com.native_code.bp_project02;

public class Native_MemberInfo {
    String uid;
    String name;
    String phonenumber;
    String hashtag;
    String user_kind;
    String region;
    Integer bookmarks_number;

    public Native_MemberInfo(String uid, String name, String phonenumber, String hashtag, String user_kind, String region, Integer bookmarks_number) {
        this.uid = uid;
        this.name = name;
        this.phonenumber = phonenumber;
        this.hashtag = hashtag;
        this.user_kind = user_kind;
        this.region = region;
        this.bookmarks_number = bookmarks_number;
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
    public String getHashtag() { return hashtag;
    }
    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
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
    public Integer getBookmarks_number() {
        return bookmarks_number;
    }
    public void setBookmarks_number(Integer bookmarks_number) {
        this.bookmarks_number = bookmarks_number;
    }
}
