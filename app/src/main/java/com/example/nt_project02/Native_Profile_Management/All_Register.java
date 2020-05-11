package com.example.nt_project02.Native_Profile_Management;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nt_project02.R;

public class All_Register extends AppCompatActivity {

    EditText introduction_EditText;
    TextView input_length;
    Button buttonEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);


        introduction_EditText = (EditText) findViewById(R.id.introduction_EditText);
        input_length = (TextView) findViewById(R.id.input_length);
        buttonEvent = (Button)findViewById(R.id.buttonEvent);




        InputFilter[] EditFilter = new InputFilter[1];
        EditFilter[0] = new InputFilter.LengthFilter(50);
        introduction_EditText.setFilters(EditFilter);


        EditText et = (EditText) findViewById(R.id.introduction_EditText);
        et.setHint("상세프로필에 등록될 소개글을 50자 내로 작성해 주세요.");


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














    }


}
