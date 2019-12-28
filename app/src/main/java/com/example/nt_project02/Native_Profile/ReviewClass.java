package com.example.nt_project02.Native_Profile;

import android.graphics.drawable.Drawable;

public class ReviewClass { // 후기를 담는 부분인데 보충이 필요할 것 같다.

    private Drawable image;

    private String text;


    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
