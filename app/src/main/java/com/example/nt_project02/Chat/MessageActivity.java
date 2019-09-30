package com.example.nt_project02.Chat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nt_project02.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private String destinationUid;
    private Button button;
    private EditText editText;



    private String TAG="sw";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<ChatData> chatList;
    private String nick="nick1";
    private FirebaseDatabase database;
    private DatabaseReference myRef;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);




        recyclerView = (RecyclerView) findViewById(R.id.recyclerView );

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        chatList=new ArrayList<>();
        mAdapter = new MessageAdapter(chatList,MessageActivity.this,nick);
        recyclerView.setAdapter(mAdapter);


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");


    /*    ChatData chat=new ChatData();
        chat.setNickname(nick);
        chat.setMsg("hi");
        myRef.setValue(chat);*/

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatData chat=dataSnapshot.getValue(ChatData.class);
                ((MessageAdapter) mAdapter).addChat(chat);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





       // destinationUid=getIntent().getStringExtra("destinationUid");
        button=(Button)findViewById(R.id.messageActivity_Button);
        editText=(EditText)findViewById(R.id.messageActivity_editText);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=editText.getText().toString();

                if (msg != null) {
                    ChatData chat=new ChatData();
                    chat.setNickname(nick);
                    chat.setMsg(msg);
                    myRef.push().setValue(chat);

                }

            }
        });








    }
}
