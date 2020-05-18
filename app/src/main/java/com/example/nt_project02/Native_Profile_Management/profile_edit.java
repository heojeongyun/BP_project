package com.example.nt_project02.Native_Profile_Management;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nt_project02.CustomData.UserModel;
import com.example.nt_project02.MainActivity;
import com.example.nt_project02.MemberActivity;
import com.example.nt_project02.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class profile_edit extends AppCompatActivity {

    private EditText introduction_EditText;
    private TextView input_length;
    private Button buttonEvent;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String current_uid;
    private UserModel destination_userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        Intent data=getIntent();
        destination_userModel = data.getParcelableExtra("destination_UserModels");


        introduction_EditText = (EditText) findViewById(R.id.introduction_EditText);
        input_length = (TextView) findViewById(R.id.input_length);
        buttonEvent = (Button)findViewById(R.id.buttonEvent);
        ImageView profile_edit_ivUser=(ImageView) findViewById(R.id.profile_edit_ivUser);
        TextView profile_edit_nick_TextView=(TextView) findViewById(R.id.profile_edit_nick_TextView);


        //해당 현지인 이름 뷰에 설정
        profile_edit_nick_TextView.setText(destination_userModel.getName());

        //해당 현지인 사진 설정
        if (destination_userModel.getImageurl() != null) {
            Glide.with
                    (getApplicationContext())
                    .load(destination_userModel.getImageurl())
                    .apply(new RequestOptions().circleCrop())
                    .into(profile_edit_ivUser);
        }

        InputFilter[] EditFilter = new InputFilter[1];
        EditFilter[0] = new InputFilter.LengthFilter(50);
        introduction_EditText.setFilters(EditFilter);


        introduction_EditText.addTextChangedListener(new TextWatcher () {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = introduction_EditText.getText().toString();
                input_length.setText(input.length()+" / 50 글자 수");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });


        EditText et = (EditText) findViewById(R.id.introduction_EditText);
        et.setHint("상세프로필에 등록될 소개글을 50자 내로 작성해 주세요.");

        //현재 이용자 UID
        current_uid= FirebaseAuth.getInstance().getUid();

        buttonEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String introduction_text=introduction_EditText.getText().toString();
                Map<String, String> introduction = new HashMap<>();
                introduction.put("introduction",introduction_text);
                //소개글 파이어베이스 등록
                Registration(introduction);
            }
        });







    }

    private void Registration(Map<String,String> data){
        db.collection("users").document(current_uid).set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startToast("소개글 등록 성공.");

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                startToast("소개글 등록 실패.");
                //Log.w(TAG, "Error writing document", e);
            }
        });

    }

    private void startToast(String msg){

        Toast.makeText(profile_edit.this, msg,
                Toast.LENGTH_SHORT).show();
    }




}
