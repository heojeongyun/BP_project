package com.example.nt_project02;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MemberActivity extends AppCompatActivity {
    private static final String TAG="MemberInfoActivity";
    RadioGroup sex_rg,user_kind_rg;
    String sex,user_kind,city;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init2);



        findViewById(R.id.CheckButton).setOnClickListener(onClickListener);
        sex_rg=(RadioGroup)findViewById(R.id.SexRadioGroup);
        user_kind_rg=(RadioGroup)findViewById(R.id.User_Kind);
        Spinner city_spinner=(Spinner) findViewById(R.id.City_Spinner);


        sex_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.male :
                        sex="남자";
                        break;
                    case R.id.female :
                        sex="여자";
                        break;
                }

            }
        });

        user_kind_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.Native :
                        user_kind="현지인";
                        break;
                    case R.id.Traveler :
                        user_kind="여행자";
                        break;
                }

            }
        });

        city_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.CheckButton:
                    profileUpdate();
                    break;

            }


        }


    };


    private void profileUpdate() {



        String name = ((EditText) findViewById(R.id.tv_name)).getText().toString();
        String phone = ((EditText) findViewById(R.id.tv_phone)).getText().toString();
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();





        if (name.length() > 0 && phone.length()>0 && sex.length()>0 &&city.length()>0&&user_kind.length()>0) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            MemberInfo memberInfo = new MemberInfo(uid,name,sex,phone,city,user_kind);

            if (user != null) {
                db.collection("users").document(user.getUid()).set(memberInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startToast("회원정보 등록 성공.");
                                        finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startToast("회원정보를 등록 실패.");
                        Log.w(TAG, "Error writing document", e);
                    }
                });

            }

        } else {
            startToast("회원정보를 입력해주세요.");
        }


    }




    private void startToast(String msg){

        Toast.makeText(MemberActivity.this, msg,
                Toast.LENGTH_SHORT).show();
    }



}
