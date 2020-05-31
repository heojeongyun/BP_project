package com.native_code.bp_project02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MemberTypeActivity extends AppCompatActivity {

    //private String user_kind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_type);

        Button TravelerButton=(Button) findViewById(R.id.TravelerButton);
        TravelerButton.setOnClickListener(new View.OnClickListener() { //여행자 선택시
            @Override
            public void onClick(View v) {
                //user_kind="여행자";
                Intent intent = new Intent(getApplicationContext(), MemberActivity.class);
                //intent.putExtra("user_kind", "여행자");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        Button NativeButton=(Button) findViewById(R.id.NativeButton);
        NativeButton.setOnClickListener(new View.OnClickListener() { //현지인 선택시
            @Override
            public void onClick(View v) {
                //user_kind="여행자";
                Intent intent = new Intent(getApplicationContext(), Native_Register.class);
                //intent.putExtra("user_kind", "여행자");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

    }

    //뒤로가기 버튼 비활성화
    public void onBackPressed() {
        //super.onBackPressed();
    }


}
