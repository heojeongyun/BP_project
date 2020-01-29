package com.example.nt_project02.Chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nt_project02.NotificationModel;
import com.example.nt_project02.R;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
    private Button Image_Button;

    private static final int GALLERY_PICK=1;


    private String username;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String uid;
    private String chatRoomUid;
    private String ImageUrl;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy.MM.dd HH:mm");

    private UserModel destinationUserModel;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    int peopleCount=0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent data=getIntent();

        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();//어플 현재 이용자 아이디
        destinationUid=data.getStringExtra("destination_Uid"); // 상대방 아이디





        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);







       // destinationUid=getIntent().getStringExtra("destinationUid");
        button=(Button)findViewById(R.id.messageActivity_Button);
        editText=(EditText)findViewById(R.id.messageActivity_editText);
        Image_Button=(Button)findViewById(R.id.messageActivity_picture);

        Image_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);


                startActivityForResult(Intent.createChooser(intent,"SELECT IMAGE"), GALLERY_PICK);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                 comment.IsImage=false;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode==GALLERY_PICK && resultCode==RESULT_OK){
            final Uri imageUri=data.getData();


            final StorageReference ref = FirebaseStorage.getInstance().getReference().child("MessageImages").child(imageUri.toString());
            UploadTask uploadTask = ref.putFile(imageUri);




            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        ImageUrl= task.getResult().toString();


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
                            comment.message=ImageUrl;
                            comment.IsImage=true;
                            comment.timestamp= ServerValue.TIMESTAMP;
                            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    sendGcm();
                                    /*editText.setText("");*/
                                    ImageUrl=null; //초기화

                                }
                            });
                        }
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });


        }
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
                        username=document.get("name").toString();
                    } else {

                    }
                } else {

                }
            }
        });

        NotificationModel notificationModel=new NotificationModel();
        notificationModel.to=destinationUserModel.getPushToken();
        notificationModel.notification.title=username;
        notificationModel.notification.text=editText.getText().toString();
        notificationModel.data.title=username;
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

  /*  void sendFCM(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()){
                            Log.w("FCM Log","getInstanceID failed",task.getException());
                            return;
                        }

                        //String token=task.getResult().getToken();
                        String token=destinationUserModel.pushToken;
                        Log.d("FCM Log","FCM 토큰:"+token);
                        Toast.makeText(MessageActivity.this,token,Toast.LENGTH_LONG).show();
                    }
                });
    }*/




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
            comments = new ArrayList<>();


            FirebaseFirestore.getInstance().collection("users").document(destinationUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    destinationUserModel=document.toObject(UserModel.class);
                    getMessageList();

                }
            });

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
                        ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                        ChatModel.Comment comment_motify = item.getValue(ChatModel.Comment.class);
                        comment_motify.readUsers.put(uid, true);

                        readUsersMap.put(key, comment_motify);
                        comments.add(comment_origin);
                    }

                    if(comments.size()>=1) {
                        if (!comments.get(comments.size() - 1).readUsers.containsKey(uid)) {


                            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments")
                                    .updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    notifyDataSetChanged();
                                    recyclerView.scrollToPosition(comments.size() - 1);
                                }
                            });
                        } else {
                            notifyDataSetChanged();
                            recyclerView.scrollToPosition(comments.size() - 1);
                        }
                        //메세지가 갱신
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public int getItemViewType(int position) {
            if(!comments.get(position).IsImage){
                return 0;
            }else{
                return 1;
            }


        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


            if(viewType==0){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat,parent,false);

                return new MessageViewHolder(view);
            }else{
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat2,parent,false);

                return new ImageViewHolder(view);
            }



        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {




            long unixTime=(long) comments.get(position).timestamp;
            Date date=new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time=simpleDateFormat.format(date);

            if (!comments.get(position).IsImage){
                MessageViewHolder messageViewHolder=((MessageViewHolder)holder);

                messageViewHolder.textView_timestamp.setText(time);
                //내가 보낸 메세지
                if(comments.get(position).uid.equals(uid)){

                    messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                    messageViewHolder.textView_message.setBackgroundResource(R.drawable.lastrightbubble);
                    messageViewHolder.textView_message.setText(comments.get(position).message);
                    messageViewHolder.textView_message.setTextSize(25);
                    messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                    //messageViewHolder.textView_message.setGravity(Gravity.RIGHT);
                    //messageViewHolder.message_image.setVisibility(View.INVISIBLE);
                    setReadCounter(position,messageViewHolder.textView_readCounter_left);



                    //상대방이 보낸 메세지
                }else
                {

                Glide.with(holder.itemView.getContext())
                        .load(destinationUserModel.imageurl)
                        .apply(new RequestOptions().circleCrop())
                        .into(messageViewHolder.imageView_profile);
                    messageViewHolder.textView_name.setText(destinationUserModel.getNick());
                    messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                    messageViewHolder.textView_message.setBackgroundResource(R.drawable.lastleftbubble);
                    messageViewHolder.textView_message.setText(comments.get(position).message);
                    messageViewHolder.textView_message.setTextSize(25);
                    messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                    //setReadCounter(position,messageViewHolder.textView_readCounter_right);


                }

            }else if(comments.get(position).IsImage){
                //내가 보내 사진
                ImageViewHolder imageViewHolder=((ImageViewHolder)holder);
                imageViewHolder.textView_timestamp.setText(time);
                if(comments.get(position).uid.equals(uid)){

                    //imageViewHolder.textView_message.setText(comments.get(position).message);
                    imageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                    imageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                    //imageViewHolder.textView_message.setVisibility(View.INVISIBLE);
                    Glide.with(holder.itemView.getContext()).load(comments.get(position).message)
                            .into(imageViewHolder.message_image);
                    setReadCounter(position,imageViewHolder.textView_readCounter_left);



                    //상대방이 보낸 사진
                }else
                {

                Glide.with(holder.itemView.getContext())
                        .load(destinationUserModel.imageurl)
                        .apply(new RequestOptions().circleCrop())
                        .into(imageViewHolder.imageView_profile);
                    imageViewHolder.textView_name.setText(destinationUserModel.getNick());
                    imageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                    imageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                    //imageViewHolder.textView_message.setVisibility(View.INVISIBLE);
                    Glide.with(holder.itemView.getContext()).load(comments.get(position).message)
                            .into(imageViewHolder.message_image);
                    setReadCounter(position,imageViewHolder.textView_readCounter_right);




                }
            }







        }

        void setReadCounter(final int position, final TextView textView){
            if (peopleCount == 0) {


                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Boolean> users = (Map<String, Boolean>) dataSnapshot.getValue();
                        peopleCount = users.size();
                        int count = peopleCount - comments.get(position).readUsers.size();
                        if (count > 0) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(count));
                        } else {
                            textView.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else{
                int count = peopleCount - comments.get(position).readUsers.size();
                if (count > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(count));
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }
            }

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
            //public ImageView message_image;

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
                //message_image=(ImageView)view.findViewById(R.id.message_image);
            }

        }
        private class ImageViewHolder extends RecyclerView.ViewHolder{
            //public TextView textView_message;
            public TextView textView_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;
            public TextView textView_readCounter_left;
            public TextView textView_readCounter_right;
            public ImageView message_image;

            public ImageViewHolder(View view){

                super(view);
                //textView_message=view.findViewById(R.id.TextView_msg);
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

        if(valueEventListener!=null) {
            databaseReference.removeEventListener(valueEventListener);

        }
        finish();
        overridePendingTransition(R.anim.fromleft,R.anim.toright);

    }
}
