package com.example.nt_project02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MessageActivity extends AppCompatActivity {
    private String destinationUid;
    private Button button;
    private EditText editText;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference mDatabase;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

       // destinationUid=getIntent().getStringExtra("destinationUid");
        button=(Button)findViewById(R.id.messageActivity_Button);
        editText=(EditText)findViewById(R.id.messageActivity_editText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database = FirebaseDatabase.getInstance();
                myRef = database.getReference("message");

                mDatabase = FirebaseDatabase.getInstance().getReference();

                ChatModel chatModel=new ChatModel();
                chatModel.uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
                //chatModel.destinationUid=destinationUid;

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String value = dataSnapshot.getValue(String.class);

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                    }
                });


                mDatabase.child("chatrooms").push().setValue(chatModel);

            }
        });
    }
}
