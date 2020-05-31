package com.native_code.bp_project02.CustomData;

public class ReviewData {
    public String writer_Uid;
    public String destination_Uid;
    public float rating;
    public String mContent;
    public String name;
    public String imageurl;

    public ReviewData() {

    }

    public ReviewData(String writer_Uid, String destination_Uid, float rating, String mContent, String name, String imageurl) {
        this.writer_Uid = writer_Uid;
        this.destination_Uid = destination_Uid;
        this.rating = rating;
        this.mContent = mContent;
        this.name = name;
        this.imageurl = imageurl;
    }

    public String getWriter_Uid() {
        return writer_Uid;
    }

    public void setWriter_Uid(String writer_Uid) {
        this.writer_Uid = writer_Uid;
    }

    public String getDestination_Uid() {
        return destination_Uid;
    }

    public void setDestination_Uid(String destination_Uid) {
        this.destination_Uid = destination_Uid;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
