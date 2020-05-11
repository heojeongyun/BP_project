package com.example.nt_project02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_sign_up);


    }

    public void onButton1Clicked(View view){
        Toast.makeText(getApplicationContext(),"리뷰가 등록되었습니다.", Toast.LENGTH_LONG).show();
    }
}
