package com.example.nt_project02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nt_project02.Chat.UserModel;
import com.example.nt_project02.CustomData.ChatModel;
import com.example.nt_project02.CustomData.ReviewData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ReviewActivity extends AppCompatActivity {

    private String destinationUid;
    private UserModel destination_userModel;


    private TextView review_nickTV;
    private ImageView review_profile_Image;
    private EditText review_edit;
    private Button register_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_sign_up);

        review_nickTV = (TextView) findViewById(R.id.review_nickTV);
        review_profile_Image = (ImageView) findViewById(R.id.review_profile_Image);
        review_edit = (EditText) findViewById(R.id.review_edit);
        register_button = (Button) findViewById(R.id.register_button);





        Intent data=getIntent();
        destination_userModel=data.getParcelableExtra("destination_UserModel");
        destinationUid=destination_userModel.getUid();

        review_nickTV.setText(destination_userModel.getName());

        // 이미지 URL을 통해 이미지를 불러오는 작업

        if(destination_userModel.getImageurl()!=null) {
            Glide.with
                    (getApplicationContext())
                    .load(destination_userModel.getImageurl())
                    .apply(new RequestOptions().circleCrop())
                    .into(review_profile_Image);
        }

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterReview();
            }
        });

    }

    public void onButton1Clicked(View view){
        Toast.makeText(getApplicationContext(), "리뷰 등록이 되었습니다.", Toast.LENGTH_LONG).show();
    }
    private void RegisterReview(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //현재 여행자 UID 불러오기

        String CurrentUser_Uid= FirebaseAuth.getInstance().getUid();
        String Content=review_edit.getText().toString();
        ReviewData reviewData=new ReviewData(CurrentUser_Uid,destinationUid,1,Content);



       Date date=new Date();
       /* Map<String, ReviewData> review_map = new HashMap<>();
        review_map.put(CurrentUser_Uid, reviewData);*/

        db.collection("review").document(date.toString()).set(reviewData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


            }
        });





    }



}
