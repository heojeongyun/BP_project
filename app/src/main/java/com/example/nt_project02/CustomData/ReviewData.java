package com.example.nt_project02.CustomData;

public class ReviewData {
    public String writer_Uid;
    public String destination_Uid;
    public int rating;
    public String mContent;

    public ReviewData() {

    }

    public ReviewData(String writer_Uid, String destination_Uid, int rating, String mContent) {
        this.writer_Uid = writer_Uid;
        this.destination_Uid = destination_Uid;
        this.rating = rating;
        this.mContent = mContent;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }
}
