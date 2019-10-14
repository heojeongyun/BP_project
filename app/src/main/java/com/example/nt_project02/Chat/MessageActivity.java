package com.example.nt_project02.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nt_project02.NotificationModel;
import com.example.nt_project02.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageActivity extends AppCompatActivity {
    private String destinationUid;
    private Button button;
    private EditText editText;



    private String userNick;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String uid;
    private String chatRoomUid;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private UserModel userModel;
    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy.MM.dd HH:mm");

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent data=getIntent();

        uid=FirebaseAuth.getInstance().getCurrentUser().getUid(); // 어플 현재 이용자 아이디
        userModel=data.getParcelableExtra("destination_UserModel");
        destinationUid=userModel.getUid(); // 상대방 아이디





        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
       /* recyclerView.setHasFixedSize(true);


        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        chatList=new ArrayList<>();
        mAdapter = new MessageAdapter(chatList,MessageActivity.this,nick);
        recyclerView.setAdapter(mAdapter);*/


/*        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");*/


    /*    ChatData chat=new ChatData();
        chat.setNickname(nick);
        chat.setMsg("hi");
        myRef.setValue(chat);*/

       /* myRef.addChildEventListener(new ChildEventListener() {
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
        });*/





       // destinationUid=getIntent().getStringExtra("destinationUid");
        button=(Button)findViewById(R.id.messageActivity_Button);
        editText=(EditText)findViewById(R.id.messageActivity_editText);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   String msg=editText.getText().toString();

                if (msg != null) {
                    ChatData chat=new ChatData();
                    chat.setNickname(nick);
                    chat.setMsg(msg);
                    myRef.push().setValue(chat);

                }*/

             /*Toast.makeText(getApplicationContext(),destinationUid,Toast.LENGTH_LONG).show();*/
             ChatModel chatModel=new ChatModel();
             chatModel.users.put(uid,true);
             chatModel.users.put(destinationUid,true);

             if(chatRoomUid==null){
                 button.setEnabled(false);
                 FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void aVoid) {
                         checkChatRoom();
                     }
                 });


             }else{

                 ChatModel.Comment comment=new ChatModel.Comment();
                 comment.uid=uid;
                 comment.message=editText.getText().toString();
                 comment.timestamp= ServerValue.TIMESTAMP;
                 FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         sendGcm();
                         editText.setText("");

                     }
                 });
             }



            }
        });

        checkChatRoom();








    }

    void sendGcm(){
        Gson gson=new Gson();




        //보내는 사람 닉네임 가져오기
        FirebaseFirestore.getInstance().collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userNick=document.get("nick").toString();
                    } else {

                    }
                } else {

                }
            }
        });

        NotificationModel notificationModel=new NotificationModel();
        notificationModel.to=userModel.getPushToken();
        notificationModel.notification.title=userNick;
        notificationModel.notification.text=editText.getText().toString();
        notificationModel.data.title=userNick;
        notificationModel.data.text=editText.getText().toString();

        RequestBody requestBody=RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationModel));


        Request request=new Request.Builder()
                .header("Content-Type","application/json")
                .addHeader("Authorization","key=AIzaSyBjcVeOPoTAHTd7wCz7xzadu1ofOTGLAL4")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient=new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });

    }




    void checkChatRoom(){
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null){
                    ChatModel newRoom=new ChatModel();
                    newRoom.users.put(uid,true);
                    newRoom.users.put(destinationUid,true);

                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(newRoom).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });
                    return;
                }


                for(DataSnapshot item : dataSnapshot.getChildren()){
                    ChatModel chatModel =item.getValue(ChatModel.class);
                    if(chatModel.users.containsKey(destinationUid)&& chatModel.users.size()==2){
                        chatRoomUid=item.getKey();
                        button.setEnabled(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<ChatModel.Comment> comments;




        public RecyclerViewAdapter() {
            comments=new ArrayList<>();


            getMessageList();



        }

        void getMessageList(){

            databaseReference=FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments");

            valueEventListener=databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    comments.clear();
                    Map<String,Object> readUsersMap=new HashMap<>();

                    for(DataSnapshot item:dataSnapshot.getChildren()){
                        String key=item.getKey();
                        ChatModel.Comment comment=item.getValue(ChatModel.Comment.class);
                        comment.readUsers.put(uid,true);

                        readUsersMap.put(key,comment);
                        comments.add(comment);


                    }

                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").updateChildren(readUsersMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            //메세지가 갱신
                            notifyDataSetChanged();

                            recyclerView.scrollToPosition(comments.size()-1);

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat,parent,false);



            return new MessageViewHolder(view);


        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder messageViewHolder=((MessageViewHolder)holder);


            /*if (comments.get(position).message_image.)*/

            //내가 보내 메세지
            if(comments.get(position).uid.equals(uid)){

                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                setReadCounter(position,messageViewHolder.textView_readCounter_left);



                //상대방이 보낸 메세지
            }else
                {

              /*  Glide.with(holder.itemView.getContext())
                        .load(userModel.imageurl)
                        .apply(new RequestOptions().circleCrop())
                        .into(messageViewHolder.imageView_profile);*/
                messageViewHolder.textView_name.setText(userModel.getNick());
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                setReadCounter(position,messageViewHolder.textView_readCounter_right);


            }

            long unixTime=(long) comments.get(position).timestamp;
            Date date=new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time=simpleDateFormat.format(date);
            messageViewHolder.textView_timestamp.setText(time);


        }

        void setReadCounter(final int position, final TextView textView){
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String,Boolean> users=(Map<String,Boolean>) dataSnapshot.getValue();

                    int count=users.size() -comments.get(position).readUsers.size();
                    if(count>0){
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(String.valueOf(count));
                    }else{
                        textView.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder{
            public TextView textView_message;
            public TextView textView_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;
            public TextView textView_readCounter_left;
            public TextView textView_readCounter_right;
            public ImageView message_image;

            public MessageViewHolder(View view){

                super(view);
                textView_message=view.findViewById(R.id.TextView_msg);
                textView_name=(TextView) view.findViewById(R.id.TextView_nickname);
                imageView_profile=(ImageView) view.findViewById(R.id.profile_Image);
                linearLayout_destination=(LinearLayout) view.findViewById(R.id.linearlayout_destination);
                linearLayout_main=(LinearLayout)view.findViewById(R.id.linearlayout_main);
                textView_timestamp=(TextView) view.findViewById(R.id.textview_timestamp);
                textView_readCounter_left=(TextView) view.findViewById(R.id.messageItem_textview_readCounter_left);
                textView_readCounter_right=(TextView)view.findViewById(R.id.messageItem_textview_readCounter_right);
                message_image=(ImageView)view.findViewById(R.id.message_image);
            }

        }
    }

    @Override
    public void onBackPressed(){

        databaseReference.removeEventListener(valueEventListener);
        finish();
        overridePendingTransition(R.anim.fromleft,R.anim.toright);

    }
}
