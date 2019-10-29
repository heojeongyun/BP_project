package com.example.nt_project02;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Sign_UpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // 초기화 Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.signUpButton).setOnClickListener(onClickListener);

    }








    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                    case R.id.signUpButton:
                        signUp();
                        break;


        }
    }



    };

    private void signUp(){

        String email=((EditText) findViewById(R.id.NameEditText)).getText().toString();
        String password=((EditText) findViewById(R.id.passwordEditText)).getText().toString();
        String passwordCheck=((EditText) findViewById(R.id.passwordCheckEditText)).getText().toString();

        if(email.length()>0 && password.length()>0 && passwordCheck.length()>0){
            if(password.equals(passwordCheck)){
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // 회원가입 성공 했을 떄 UI.
                                    //Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    startToast("회원가입 성공");
                                    MystartActivity(MemberActivity.class);

                                } else {
                                    // 회원가입 실패 했을 떄 UI.
                                    //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    if(task.getException()!=null){
                                        startToast(task.getException().toString());
                                    }

                                }

                                // ...
                            }
                        });

            }else{
                startToast("비밀번호가 일치하지 않습니다.");
            }


        }else{
            startToast("이메일 또는 비밀번호를 입력해주세요.");
        }
    }




    private void startToast(String msg){

        Toast.makeText(Sign_UpActivity.this, msg,
                Toast.LENGTH_SHORT).show();
    }

    private void MystartActivity(Class c){
        Intent intent=new Intent(this,c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
