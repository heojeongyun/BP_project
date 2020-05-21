package com.example.nt_project02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nt_project02.CustomData.UserModel;
import com.example.nt_project02.CustomData.ReviewData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class ReviewActivity extends AppCompatActivity {

    private String destinationUid;
    private UserModel destination_userModel;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private TextView review_nickTV;
    private ImageView review_profile_Image;
    private EditText review_edit;
    private Button register_button;
    private float mRating = 0;
    private String TAG="ReviewActivity";

    private UserModel current_userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_sign_up);

        review_nickTV = (TextView) findViewById(R.id.review_nickTV);
        review_profile_Image = (ImageView) findViewById(R.id.review_profile_Image);
        review_edit = (EditText) findViewById(R.id.review_edit);
        register_button = (Button) findViewById(R.id.register_button);


        Intent data = getIntent();
        destination_userModel = data.getParcelableExtra("destination_UserModel");
        destinationUid = destination_userModel.getUid();

        review_nickTV.setText(destination_userModel.getName());

        // 이미지 URL을 통해 이미지를 불러오는 작업

        if (destination_userModel.getImageurl() != null) {
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

    private void RegisterReview() {

        //현재 여행자 UID 불러오기
        String CurrentUser_Uid = FirebaseAuth.getInstance().getUid();
        //현재 여행자 정보 가져오기
        db.collection("users")
                .whereEqualTo("uid", CurrentUser_Uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                current_userModel = document.toObject(UserModel.class);
                                Log.e(TAG,"current:"+current_userModel);
                                String Content = review_edit.getText().toString();
                                String name=current_userModel.getName();
                                String imageurl=current_userModel.getImageurl();

                                RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar1);


                                mRating=ratingBar.getRating();


                                ReviewData reviewData = new ReviewData(current_userModel.uid, destinationUid, mRating, Content, name, imageurl);


                                Date date = new Date();


                                db.collection("review").document(date.toString()).set(reviewData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(),"리뷰 작성 완료",Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {


                                    }
                                });


                            }
                        } else {
                            Log.d("Profile_Traver_data", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }


}
