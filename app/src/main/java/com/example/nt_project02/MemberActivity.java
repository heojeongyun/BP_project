package com.example.nt_project02;


import android.content.Intent;
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
    RadioGroup sex_rg;
    String sex,user_kind,city;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_member_init2);

        user_kind="현지인";



        findViewById(R.id.submit_button).setOnClickListener(onClickListener);
        sex_rg=(RadioGroup)findViewById(R.id.sex_radioGroup);
        //user_kind_rg=(RadioGroup)findViewById(R.id.User_Kind);
        Spinner city_spinner=(Spinner) findViewById(R.id.traveler_spinner);


        //성별 라디오 버튼 체크시
        sex_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.man_radio :
                        sex="남자";
                        break;
                    case R.id.woman_radio :
                        sex="여자";
                        break;
                }

            }
        });

        user_kind="여행자";


        //사용자 종류 라디오 버튼 체크시
        /*user_kind_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
        });*/

        //도시 스피너 체크시
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

    //뒤로가기 버튼 눌렀을 때
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.submit_button:
                    profileUpdate();
                    break;

            }


        }


    };

    //프로필 업데이트 메서드
    private void profileUpdate() {



        //지역변수 선언
        String name = ((EditText) findViewById(R.id.traveler_name)).getText().toString();
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        String birthday_information = ((EditText) findViewById(R.id.birthday_information)).getText().toString();




        //각각의 정보가 빈칸인지 아닌지 확인
        if (name.length() > 0 && birthday_information.length()>0 && sex.length()>0 &&city.length()>0&&user_kind.length()>0) {
            //현재 유저 정보 가져오기
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            //MemberInfo 객체 선언
            MemberInfo memberInfo = new MemberInfo(uid,name,sex,birthday_information,city,user_kind);

            //현재 유저가 있다면
            if (user != null) {
                //파이어 스토어에 등록
                db.collection("users").document(user.getUid()).set(memberInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startToast("회원정보 등록 성공.");
                                finish();
                                MystartActivity(MainActivity.class);

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


    private void MystartActivity(Class c){
        Intent intent=new Intent(this,c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }



}