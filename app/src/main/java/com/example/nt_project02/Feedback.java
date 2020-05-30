package com.example.nt_project02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Feedback extends AppCompatActivity {

    private String feedback_kind;
    private String feedback_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        Spinner region_spinner=(Spinner) findViewById(R.id.feedback_spinner);
        region_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                feedback_kind=parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        EditText feedback_EditText=(EditText) findViewById(R.id.feedback_EditText);
        Button submit_button=(Button)findViewById(R.id.feedback_submit_button);






        FirebaseFirestore db= FirebaseFirestore.getInstance();

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                feedback_content=feedback_EditText.getText().toString();

                if (feedback_content.length() > 0 && feedback_kind.length()>0) {
                    //파이어스토어에 넣을 피드백 데이터
                    Map<String, Object> feedback = new HashMap<>();
                    feedback.put("kind", feedback_kind);
                    feedback.put("content", feedback_content);

                    db.collection("feedback")
                            .add(feedback)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    startToast("소중한 의견 감사합니다.");
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }else{
                    startToast("정보를 입력해주세요");
                }
            }
        });



    }

    private void startToast(String msg){
        Toast.makeText(Feedback.this, msg,
                Toast.LENGTH_SHORT).show();
    }
}
