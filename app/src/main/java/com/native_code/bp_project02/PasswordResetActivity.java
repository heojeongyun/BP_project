package com.native_code.bp_project02;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        // 초기화 Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.SendButton).setOnClickListener(onClickListener);
    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.SendButton:
                    send();
                    break;
            }
        }


    };

    private void send() {

        String email = ((EditText) findViewById(R.id.NameEditText)).getText().toString();



        if (email.length() > 0) {

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startToast("이메일을 보냈습니다.");
                            } else {
                                startToast("이메일 보내기에 실패했습니다. 이메일을 다시 입력해주세요.");
                            }
                        }
                    });

        } else {
            startToast("이메일 입력해주세요.");
        }




    }






    private void startToast(String msg){

        Toast.makeText(PasswordResetActivity.this, msg,
                Toast.LENGTH_SHORT).show();
    }

}
