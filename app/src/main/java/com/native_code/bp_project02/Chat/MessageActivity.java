package com.native_code.bp_project02.Chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.native_code.bp_project02.CustomData.ChatModel;
import com.native_code.bp_project02.CustomData.UserModel;
import com.native_code.bp_project02.Fragment.Chatting_Fragment;
import com.native_code.bp_project02.GoogleMap_Drawing_Fragment;
import com.native_code.bp_project02.GoogleMap_Fragment;
import com.native_code.bp_project02.CustomData.NotificationModel;
import com.native_code.bp_project02.LoginActivity;
import com.native_code.bp_project02.R;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
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
    private static final int REQUEST_CONTENT =101 ;
    private String destinationUid;
    private Button button;
    private EditText editText;
    private Button Image_Button;

    private static final int GALLERY_PICK=1;


    private String username;

    private RecyclerView recyclerView;
    private FrameLayout frameLayout;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String uid;
    public static String chatRoomUid;
    private String ImageUrl;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM.dd HH:mm");
    private UserModel destinationUserModel;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    int peopleCount=0;

    private UserModel userModel;
    private String user_kind;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String user_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


    //지도 프래그먼트 선언
    private GoogleMap_Fragment googleMap_fragment;
    private GoogleMap_Drawing_Fragment googleMap_drawing_fragment;
    private Chatting_Fragment Chatting_Fragment;

    private String TAG="MessageActivity";
    public  String Content;
    private FragmentTransaction transaction;
    private InputMethodManager imm;
    public Button activtiy_message_MapButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        googleMap_fragment=new GoogleMap_Fragment();
        googleMap_drawing_fragment=new GoogleMap_Drawing_Fragment();

        Intent data=getIntent();
        chatRoomUid=null;

        uid=FirebaseAuth.getInstance().getCurrentUser().getUid(); //어플 현재 이용자 아이디
        destinationUid=data.getStringExtra("destination_Uid"); // 상대방 아이디




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


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        frameLayout=(FrameLayout)findViewById(R.id.container);

        Log.d(TAG,"deuid:"+destinationUid);
        Log.d(TAG,"uid:"+uid);




        button=(Button)findViewById(R.id.messageActivity_Button);
        editText=(EditText)findViewById(R.id.messageActivity_editText);
        Image_Button=(Button)findViewById(R.id.messageActivity_picture);
        Button FinishButton=(Button) findViewById(R.id.actvity_message_FinishButton);
        FinishButton.setVisibility(View.GONE);


        //키보드
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);


        FinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                //알림메세지
                final AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                builder.setTitle("매칭 종료");
                builder.setMessage("매칭을 종료 하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                databaseReference=FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments");
                                DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("users");
                                Map<String, Object> childUpdates = new HashMap<>();

                                //해당 채팅방 리스트에 보이지 않게 users 업데이트
                                childUpdates.put(uid, false);
                                childUpdates.put(destinationUid, false);
                                mDatabase.updateChildren(childUpdates);



                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                //현재 여행자 정보 파이어스토어 경로
                                DocumentReference Ref=db.collection("users").document(uid);
                                //현재 현지인 정보 파이어스토어경로
                                DocumentReference destination_Ref=db.collection("users").document(destinationUid);

                                //matching 리스트에서 삭제
                                Ref.update("matching",FieldValue.arrayRemove(destinationUid));
                                destination_Ref.update("matching",FieldValue.arrayRemove(uid));

                                //매칭 종료 메세지
                                ChatModel.Comment comment=new ChatModel.Comment();
                                comment.uid=uid;
                                comment.message="매칭을 종료했습니다.";
                                comment.IsImage=false;
                                comment.timestamp= ServerValue.TIMESTAMP;
                                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });

                                //액티비티 종료
                                finish();
                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.show();

            }
        });









        View.OnTouchListener remove =new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            //터치했을 때 프래그먼트 화면 없애기
                            removeFragment(googleMap_fragment);
                            //키보드 숨기
                            //imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                            break;
                        }
                    }
                    return false;
                }

        };

        recyclerView.setOnTouchListener(remove);
        editText.setOnTouchListener(remove);

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
             }



                else if(editText.getText().toString().equals("")) {
                 button.setEnabled(false);
             } else {
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
                editText.setText("");
                button.setEnabled(true);

            }

        });




        activtiy_message_MapButton=(Button)findViewById(R.id.activity_message_MapButton);


        Button activtiy_message_Map_Drawing_Button=(Button)findViewById(R.id.activtiy_message_Map_Drawing_Button);

        activtiy_message_Map_Drawing_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getSupportFragmentManager().beginTransaction().replace(R.id.container,googleMap_drawing_fragment).commit();
                //attachFragment(googleMap_drawing_fragment);
                startToast("업데이트 예정 입니다.");
            }
        });

        Button activity_message_Camera_Button=(Button)findViewById(R.id.activity_message_Camera_Button);

        activity_message_Camera_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"업데이트 예정 입니다.",Toast.LENGTH_SHORT).show();
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

        if(requestCode == REQUEST_CONTENT){
            if(resultCode== RESULT_OK){

                /*Content=data.getStringExtra("Content");
                Log.d(TAG,"확인:"+ Content);*/

                googleMap_fragment.onActivityResult(REQUEST_CONTENT,RESULT_OK,data);
            }

        }


    }

    void sendGcm(){
        Gson gson=new Gson();


        NotificationModel notificationModel=new NotificationModel();

        if(destinationUserModel != null) {
            notificationModel.to = destinationUserModel.getPushToken();
        }

        //notificationModel.notification.title=username;
        //notificationModel.notification.text=editText.getText().toString();
        notificationModel.data.title=username;
        notificationModel.data.text=editText.getText().toString();
        notificationModel.data.send_uid=uid;

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
                    //Log.e(TAG,"users:"+chatModel.users.values());
                    if(chatModel.users.containsKey(destinationUid) && chatModel.users.size()==2 ){
                        chatRoomUid=item.getKey();
                        Log.e(TAG,"chatRoomUid:"+chatRoomUid);
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
                    //getChatRoomState();
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

                    activtiy_message_MapButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            db.collection("users")
                                    .whereEqualTo("uid", user_uid)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value,
                                                            @Nullable FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Log.w(TAG, "Listen failed.", e);
                                                return;
                                            }

                                            for (QueryDocumentSnapshot doc : value) {

                                                if (doc != null) {
                                                    userModel = doc.toObject(UserModel.class);
                                                    user_kind = userModel.getUser_kind();
                                                    if (user_kind != null) {
                                                        if (user_kind.equals("여행자")) { //여행자인 경우 토스트 뜨게
                                                            startToast("현지인이 생성한 파란색 핀을 터치해 상세정보를 확인하세요!");
                                                        }
                                                    }
                                                    Log.e(TAG,"respone");



                                                }
                                            }
                                        }
                                    });
                            //getSupportFragmentManager().beginTransaction().replace(R.id.container,googleMap_fragment).commit();
                            attachFragment(googleMap_fragment);
                            recyclerView.scrollToPosition(comments.size() - 1);

                        }
                    });


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }

        void getChatRoomState(){

            databaseReference=FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("users");

            valueEventListener=databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    for(DataSnapshot item:dataSnapshot.getChildren()){

                        Log.e(TAG,"users:"+item.getValue());
                        if((Boolean) item.getValue()==false){
                            button.setEnabled(false);
                        }


                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }

        public int getItemViewType(int position) {

            if(!comments.get(position).IsImage){
                if(comments.get(position).uid.equals(uid)){
                    //내가 보내 메세지
                    return 2;
                }else{
                    //상대방이 보낸 메세지
                    return 0;
                }

            }else{
                //사진을 보냈을 때
                return 1;
            }


        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


            if(viewType==0){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat,parent,false);

                return new dMessageViewHolder(view);

            }else if(viewType==1){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat2,parent,false);

                return new ImageViewHolder(view);

            }else{
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat3,parent,false);

                return new mMessageViewHolder(view);
            }



        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {




            long unixTime=(long) comments.get(position).timestamp;
            Date date=new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time=simpleDateFormat.format(date);

            if (!comments.get(position).IsImage){

                //내가 보낸 메세지
                if(comments.get(position).uid.equals(uid)){

                    mMessageViewHolder m_messageViewHolder=((mMessageViewHolder) holder);

                    m_messageViewHolder.textView_timestamp.setText(time);

                    //messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                    m_messageViewHolder.textView_message.setBackgroundResource(R.drawable.lastrightbubble);
                    m_messageViewHolder.textView_message.setText(comments.get(position).message);
                    m_messageViewHolder.textView_message.setTextSize(17);
                    m_messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                    setReadCounter(position,m_messageViewHolder.textView_readCounter_left);



                    //상대방이 보낸 메세지
                }else
                {
                    dMessageViewHolder d_messageViewHolder=((dMessageViewHolder)holder);

                    d_messageViewHolder.textView_timestamp.setText(time);
                    if(destinationUserModel.imageurl !=null){
                        Glide.with(holder.itemView.getContext())
                                .load(destinationUserModel.imageurl)
                                .apply(new RequestOptions().circleCrop())
                                .into(d_messageViewHolder.imageView_profile);
                    }

                    d_messageViewHolder.textView_name.setText(destinationUserModel.getName());
                    d_messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                    d_messageViewHolder.textView_message.setBackgroundResource(R.drawable.lastleftbubble);
                    d_messageViewHolder.textView_message.setText(comments.get(position).message);
                    d_messageViewHolder.textView_message.setTextSize(17);
                    d_messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                    setReadCounter(position,d_messageViewHolder.textView_readCounter_right);


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

                    if(destinationUserModel.imageurl !=null){
                        Glide.with(holder.itemView.getContext())
                                .load(destinationUserModel.imageurl)
                                .apply(new RequestOptions().circleCrop())
                                .into(imageViewHolder.imageView_profile);
                    }
                    imageViewHolder.textView_name.setText(destinationUserModel.getName());
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

        private class dMessageViewHolder extends RecyclerView.ViewHolder{
            public TextView textView_message;
            public TextView textView_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;
            //public TextView textView_readCounter_left;
            public TextView textView_readCounter_right;
            //public ImageView message_image;

            public dMessageViewHolder(View view){

                super(view);
                textView_message=view.findViewById(R.id.TextView_msg);
                textView_name=(TextView) view.findViewById(R.id.TextView_nickname);
                imageView_profile=(ImageView) view.findViewById(R.id.profile_Image);
                linearLayout_destination=(LinearLayout) view.findViewById(R.id.linearlayout_destination);
                linearLayout_main=(LinearLayout)view.findViewById(R.id.linearlayout_main);
                textView_timestamp=(TextView) view.findViewById(R.id.textview_timestamp);
                //textView_readCounter_left=(TextView) view.findViewById(R.id.messageItem_textview_readCounter_left);
                textView_readCounter_right=(TextView)view.findViewById(R.id.messageItem_textview_readCounter_right);
                //message_image=(ImageView)view.findViewById(R.id.message_image);
            }

        }
        private class mMessageViewHolder extends RecyclerView.ViewHolder{
            public TextView textView_message;
            public TextView textView_name;
            public ImageView imageView_profile;
            //public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;
            public TextView textView_readCounter_left;
            //public TextView textView_readCounter_right;
            //public ImageView message_image;

            public mMessageViewHolder(View view){

                super(view);
                textView_message=view.findViewById(R.id.TextView_msg);
                textView_name=(TextView) view.findViewById(R.id.TextView_nickname);
                imageView_profile=(ImageView) view.findViewById(R.id.profile_Image);
                //linearLayout_destination=(LinearLayout) view.findViewById(R.id.linearlayout_destination);
                linearLayout_main=(LinearLayout)view.findViewById(R.id.linearlayout_main);
                textView_timestamp=(TextView) view.findViewById(R.id.textview_timestamp);
                textView_readCounter_left=(TextView) view.findViewById(R.id.messageItem_textview_readCounter_left);
                //textView_readCounter_right=(TextView)view.findViewById(R.id.messageItem_textview_readCounter_right);
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
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fromleft,R.anim.toright);

    }

    private void MystartActivity(Class c){
        Intent intent=new Intent(this,c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void attachFragment(Fragment fragment) {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        //키보드 숨기
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        transaction.commit();
    }

    private void removeFragment(Fragment fragment) {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void startToast (String msg){

        Toast.makeText(MessageActivity.this, msg,
                Toast.LENGTH_SHORT).show();
    }

}
