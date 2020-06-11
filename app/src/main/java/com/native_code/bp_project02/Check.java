package com.native_code.bp_project02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Check extends AppCompatActivity {

    private EditText password_edittext;
    private Button confirm_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        password_edittext = (EditText) findViewById(R.id.password_edittext);
        confirm_button=(Button) findViewById(R.id.confirm_button);

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input=password_edittext.getText().toString();

                if(input.equals("#051051")){
                    Intent intent = new Intent(getApplicationContext(), Native_Register.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"010-3507-1161로 문의해 주세요.",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
