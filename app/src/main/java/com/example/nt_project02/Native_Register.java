package com.example.nt_project02;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class Native_Register extends AppCompatActivity {

    String region;
    String user_kind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_register);

        user_kind="현지인";

        Spinner region_spinner=(Spinner) findViewById(R.id.native_spinner);


        region_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                region=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button submit_button=(Button)findViewById(R.id.submit_button);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileUpdate();
            }
        });

    }

    private void profileUpdate() {



        String nick = ((EditText) findViewById(R.id.native_confirm)).getText().toString();
        String hash = ((EditText) findViewById(R.id.hash_tag)).getText().toString();
        String self_info = ((EditText) findViewById(R.id.tv_information)).getText().toString();
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();





        if (nick.length() > 0 && hash.length()>0 && self_info.length()>0 &&region.length()>0) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Native_MemberInfo native_memberInfo = new Native_MemberInfo(uid,nick,region,hash,self_info,user_kind);

            if (user != null) {
                db.collection("users").document(user.getUid()).set(native_memberInfo,SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startToast("현지인 정보 등록 성공.");
                                finish();
                                MystartActivity(MainActivity.class);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startToast("현지인 정보 등록 실패.");

                    }
                });

            }

        } else {
            startToast("회원정보를 입력해주세요.");
        }


    }

    private void startToast(String msg){

        Toast.makeText(Native_Register.this, msg,
                Toast.LENGTH_SHORT).show();
    }

    private void MystartActivity(Class c){
        Intent intent=new Intent(this,c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
