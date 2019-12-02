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

public class LoginActivity extends AppCompatActivity {



    //파이어 베이스 계정객체 선언
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 초기화 Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.CheckButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoPasswordResetbutton).setOnClickListener(onClickListener);
        findViewById(R.id.signUp_Activity_Button).setOnClickListener(onClickListener);
    }


    // 뒤로가기 버튼
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        //뒤로가기 버튼 눌렀을 때 어플 종료
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }


    //버튼 눌렀을 떄
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //로그인 버튼
                case R.id.CheckButton:
                    login();
                    break;
                //패스워드 리셋 버튼
                case R.id.gotoPasswordResetbutton:
                    MystartActivity(PasswordResetActivity.class);
                    break;
                //회원가입 버튼
                case R.id.signUp_Activity_Button:
                    MystartActivity(Sign_UpActivity.class);
                    break;
            }


        }


    };

    //로그인 메서드
    private void login() {


        String email = ((EditText) findViewById(R.id.NameEditText)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();


        //이메일,패스워드 입력 확인
        if (email.length() > 0 && password.length() > 0) {


            //파이어베이스 로그인
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //FirebaseUser user = mAuth.getCurrentUser();
                                startToast("로그인 성공");
                                MystartActivity(MainActivity.class);

                            } else {


                                //로그인 오류시
                                if (task.getException() != null) {
                                    startToast(task.getException().toString());
                                }
                            }

                            // ...
                        }
                    });

        } else {
            //이메일,패스워드 칸 빈칸일 시
            startToast("이메일 또는 비밀번호를 입력해주세요.");
        }


    }



    //토스트 메세지 메서드
    private void startToast(String msg){

        Toast.makeText(LoginActivity.this, msg,
                Toast.LENGTH_SHORT).show();
    }

    //액티비티 이동 메서드
    private void MystartActivity(Class c){
        Intent intent=new Intent(this,c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}

