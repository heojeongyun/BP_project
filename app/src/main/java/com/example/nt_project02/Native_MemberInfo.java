package com.example.nt_project02;

public class Native_MemberInfo {

    String nick;
    String region;
    String hash;
    String self_info;

    public Native_MemberInfo(String nick, String region, String hash, String self_info) {
        this.nick = nick;
        this.region = region;
        this.hash = hash;
        this.self_info = self_info;
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
}
