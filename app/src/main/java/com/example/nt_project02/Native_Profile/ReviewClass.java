package com.example.nt_project02.Native_Profile;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nt_project02.R;


public class ReviewClass extends AppCompatActivity { // 후기를 담는 부분인데 보충이 필요할 것 같다.

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

    public void onButton1Clicked(View view) {

            Toast.makeText(getApplicationContext(), "리뷰 등록이 되었습니다.", Toast.LENGTH_LONG).show();
        }
    }

